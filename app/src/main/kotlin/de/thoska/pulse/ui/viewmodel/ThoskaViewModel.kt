package de.thoska.pulse.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.thoska.pulse.data.CardProfileRepository
import de.thoska.pulse.data.ScanHistoryRepository
import de.thoska.pulse.model.ApduLogEntry
import de.thoska.pulse.model.CardProfile
import de.thoska.pulse.model.LcdPalette
import de.thoska.pulse.model.MensaBudgetPreset
import de.thoska.pulse.model.ScanRecord
import de.thoska.pulse.model.ThoskaCardData
import de.thoska.pulse.model.ThuringiaUniversity
import de.thoska.pulse.reader.AndroidIsoDepTransceiver
import de.thoska.pulse.reader.CardReadException
import de.thoska.pulse.reader.DesfireReader
import de.thoska.pulse.util.RetroSoundAndHaptics
import de.thoska.pulse.util.ThoskaCsvHelper
import de.thoska.pulse.util.ThoskaValidationHelper
import de.thoska.pulse.widget.ThoskaAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CardScanState {
    data object Idle : CardScanState
    data object Scanning : CardScanState
    data class Success(val cardData: ThoskaCardData) : CardScanState
    data class Error(val message: String, val logs: List<ApduLogEntry> = emptyList()) : CardScanState
}

enum class NfcStatus {
    ENABLED,
    DISABLED,
    NOT_SUPPORTED
}

enum class ConsoleScreenMode {
    BALANCE_FRONT,
    BARCODE_BACK,
    HISTORY_INVENTORY
}

class ThoskaViewModel(application: Application) : AndroidViewModel(application) {

    private val scanHistoryRepository = ScanHistoryRepository(application.applicationContext)
    val cardProfileRepository = CardProfileRepository(application.applicationContext)
    private val prefs = application.getSharedPreferences("thoska_pulse_preferences", Context.MODE_PRIVATE)

    val history: StateFlow<List<ScanRecord>> = scanHistoryRepository.historyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _scanState = MutableStateFlow<CardScanState>(CardScanState.Idle)
    val scanState: StateFlow<CardScanState> = _scanState.asStateFlow()

    private val _screenMode = MutableStateFlow(ConsoleScreenMode.BALANCE_FRONT)
    val screenMode: StateFlow<ConsoleScreenMode> = _screenMode.asStateFlow()

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage.asStateFlow()

    private val _showRegisterDialog = MutableStateFlow(false)
    val showRegisterDialog: StateFlow<Boolean> = _showRegisterDialog.asStateFlow()

    private val _editingProfile = MutableStateFlow<CardProfile?>(null)
    val editingProfile: StateFlow<CardProfile?> = _editingProfile.asStateFlow()

    private val _nfcStatus = MutableStateFlow(checkNfcStatus())
    val nfcStatus: StateFlow<NfcStatus> = _nfcStatus.asStateFlow()

    private val _activeApduLogs = MutableStateFlow<List<ApduLogEntry>>(emptyList())
    val activeApduLogs: StateFlow<List<ApduLogEntry>> = _activeApduLogs.asStateFlow()

    private val _showApduSheet = MutableStateFlow(false)
    val showApduSheet: StateFlow<Boolean> = _showApduSheet.asStateFlow()

    // D-Pad Feature 1: Mensa Budget Preset (D-Pad UP / DOWN)
    private val _budgetPreset = MutableStateFlow(MensaBudgetPreset.COFFEE)
    val budgetPreset: StateFlow<MensaBudgetPreset> = _budgetPreset.asStateFlow()

    // D-Pad Feature 2: LCD Palette Switcher (D-Pad LEFT / RIGHT)
    private val _lcdPalette = MutableStateFlow(
        LcdPalette.fromId(prefs.getString(KEY_LCD_PALETTE, LcdPalette.DMG_OLIVE.id))
    )
    val lcdPalette: StateFlow<LcdPalette> = _lcdPalette.asStateFlow()

    private var warningJob: Job? = null

    fun updateNfcStatus() {
        _nfcStatus.value = checkNfcStatus()
    }

    private fun checkNfcStatus(): NfcStatus {
        val adapter = NfcAdapter.getDefaultAdapter(getApplication())
        return when {
            adapter == null -> NfcStatus.NOT_SUPPORTED
            !adapter.isEnabled -> NfcStatus.DISABLED
            else -> NfcStatus.ENABLED
        }
    }

    fun onIsoDepDiscovered(isoDep: IsoDep) {
        viewModelScope.launch {
            _scanState.value = CardScanState.Scanning
            _warningMessage.value = null
            try {
                val transceiver = AndroidIsoDepTransceiver(isoDep)
                val reader = DesfireReader(transceiver)
                val result = reader.readCard()

                if (result.isSuccess) {
                    val rawCardData = result.getOrThrow()
                    // Dynamically resolve cardholder profile from database
                    val matchedProfile = cardProfileRepository.getProfileByUid(rawCardData.cardUid)
                    val cardData = rawCardData.copy(profile = matchedProfile)

                    _scanState.value = CardScanState.Success(cardData)
                    _activeApduLogs.value = cardData.apduLogs
                    _screenMode.value = ConsoleScreenMode.BALANCE_FRONT

                    // Trigger 8-bit coin chime & vibration
                    RetroSoundAndHaptics.playCoinChime(getApplication())

                    // Low-balance alert audio check (< 5.00 €)
                    if (ThoskaValidationHelper.isLowBalance(cardData.balanceEur)) {
                        RetroSoundAndHaptics.playLowHealthTone(getApplication())
                    }

                    // Store scan in persistent local history
                    scanHistoryRepository.addRecord(
                        ScanRecord(
                            timestamp = cardData.timestamp,
                            cardUid = cardData.cardUid,
                            balanceEur = cardData.balanceEur,
                            lastTransactionEur = cardData.lastTransactionEur
                        )
                    )

                    // Refresh widget with latest scan
                    ThoskaAppWidget.updateAllWidgets(getApplication())
                } else {
                    val exception = result.exceptionOrNull()
                    val logs = (exception as? CardReadException)?.logs ?: emptyList()
                    _scanState.value = CardScanState.Error(
                        exception?.message ?: "Failed to read Thoska card",
                        logs
                    )
                    _activeApduLogs.value = logs
                }
            } catch (e: Exception) {
                _scanState.value = CardScanState.Error("Communication error: ${e.message}")
            }
        }
    }

    /**
     * Simulates scanning a card. Defaults to student card UID with customizable balance.
     */
    fun simulateScan(
        mockUid: String = "04:31:56:CA:6C:24:90",
        mockBalance: Double = 40.40,
        mockLastTx: Double = 1.40
    ) {
        viewModelScope.launch {
            _scanState.value = CardScanState.Scanning
            _warningMessage.value = null
            delay(250) // Short simulated NFC handshake delay

            val mockLogs = listOf(
                ApduLogEntry(
                    step = "Select AID 5F8415",
                    commandHex = "90 5A 00 00 03 5F 84 15 00",
                    responseHex = "91 00",
                    statusWord = "9100",
                    isSuccess = true,
                    note = "Mensa Application Selected"
                ),
                ApduLogEntry(
                    step = "Read Balance (File 0x01)",
                    commandHex = "90 6C 00 00 01 01 00",
                    responseHex = String.format("%02X %02X %02X %02X 91 00",
                        ((mockBalance * 1000).toInt() and 0xFF),
                        (((mockBalance * 1000).toInt() shr 8) and 0xFF),
                        (((mockBalance * 1000).toInt() shr 16) and 0xFF),
                        (((mockBalance * 1000).toInt() shr 24) and 0xFF)
                    ),
                    statusWord = "9100",
                    isSuccess = true,
                    note = String.format("Balance: %.2f €", mockBalance)
                ),
                ApduLogEntry(
                    step = "Read Settings / Last Tx (File 0x01)",
                    commandHex = "90 F5 00 00 01 01 00",
                    responseHex = "03 00 EE EE 00 00 00 00 00 50 C3 00 78 05 00 00 01 91 00",
                    statusWord = "9100",
                    isSuccess = true,
                    note = String.format("Last Transaction: %.2f €", mockLastTx)
                )
            )

            // Dynamically resolve profile from repository
            val matchedProfile = cardProfileRepository.getProfileByUid(mockUid)

            val cardData = ThoskaCardData(
                cardUid = mockUid,
                balanceEur = mockBalance,
                lastTransactionEur = mockLastTx,
                selectedAid = "5F8415",
                apduLogs = mockLogs,
                profile = matchedProfile
            )

            _scanState.value = CardScanState.Success(cardData)
            _activeApduLogs.value = mockLogs
            _screenMode.value = ConsoleScreenMode.BALANCE_FRONT

            // Trigger 8-bit coin chime & vibration
            RetroSoundAndHaptics.playCoinChime(getApplication())

            // Low-balance alert audio check (< 5.00 €)
            if (ThoskaValidationHelper.isLowBalance(cardData.balanceEur)) {
                RetroSoundAndHaptics.playLowHealthTone(getApplication())
            }

            scanHistoryRepository.addRecord(
                ScanRecord(
                    timestamp = cardData.timestamp,
                    cardUid = cardData.cardUid,
                    balanceEur = cardData.balanceEur,
                    lastTransactionEur = cardData.lastTransactionEur
                )
            )

            // Refresh widget with simulated scan
            ThoskaAppWidget.updateAllWidgets(getApplication())
        }
    }

    /**
     * SELECT [EJECT] Action:
     * Clears the currently active card from memory, resets all displayed data,
     * and returns the console immediately to the blank "NO CARTRIDGE" idle state.
     */
    fun ejectCard() {
        RetroSoundAndHaptics.playButtonClick(getApplication())
        _scanState.value = CardScanState.Idle
        _screenMode.value = ConsoleScreenMode.BALANCE_FRONT
        _warningMessage.value = null
        _showRegisterDialog.value = false
        _editingProfile.value = null
    }

    /**
     * Button A [FLIP CARD] Action:
     * - If no card is scanned: shows bold warning "NO CARD LOCKED - TAP THOSKA FIRST"
     * - If card is active: toggles between Front Balance View and Back Library Barcode Pass.
     */
    fun toggleFlipCard() {
        RetroSoundAndHaptics.playButtonClick(getApplication())
        val currentState = _scanState.value

        if (currentState !is CardScanState.Success) {
            showWarning("NO CARD LOCKED - TAP THOSKA FIRST")
            return
        }

        _warningMessage.value = null
        _screenMode.value = when (_screenMode.value) {
            ConsoleScreenMode.BALANCE_FRONT -> ConsoleScreenMode.BARCODE_BACK
            ConsoleScreenMode.BARCODE_BACK -> ConsoleScreenMode.BALANCE_FRONT
            ConsoleScreenMode.HISTORY_INVENTORY -> ConsoleScreenMode.BALANCE_FRONT
        }
    }

    /**
     * Button B [HISTORY] Action:
     * Toggles the compact 8-bit log overlay showing the last 5 scans.
     */
    fun toggleHistory() {
        RetroSoundAndHaptics.playButtonClick(getApplication())
        _warningMessage.value = null
        _screenMode.value = when (_screenMode.value) {
            ConsoleScreenMode.HISTORY_INVENTORY -> ConsoleScreenMode.BALANCE_FRONT
            else -> ConsoleScreenMode.HISTORY_INVENTORY
        }
    }

    private fun showWarning(msg: String) {
        warningJob?.cancel()
        _warningMessage.value = msg
        warningJob = viewModelScope.launch {
            delay(2400)
            _warningMessage.value = null
        }
    }

    fun openRegisterDialog(profile: CardProfile? = null) {
        RetroSoundAndHaptics.playButtonClick(getApplication())
        _editingProfile.value = profile
        _showRegisterDialog.value = true
    }

    fun closeRegisterDialog() {
        _showRegisterDialog.value = false
        _editingProfile.value = null
    }

    fun saveCardRegistration(
        cardUid: String,
        name: String,
        matrikel: String,
        barcode: String,
        valid: String,
        universityId: String = ThuringiaUniversity.EAH_JENA.id
    ) {
        val newProfile = CardProfile(
            cardUid = cardUid,
            holderName = name.ifBlank { "STUDENT" },
            matrikelNr = matrikel.ifBlank { "0000000" },
            libraryBarcode = barcode.ifBlank { "000000000000" },
            validUntil = valid.ifBlank { "01.10.26 - 31.03.27" },
            universityId = universityId
        )
        cardProfileRepository.saveProfile(newProfile)

        // If current card matches, update its attached profile
        val current = _scanState.value
        if (current is CardScanState.Success && CardProfileRepository.normalizeUid(current.cardData.cardUid) == CardProfileRepository.normalizeUid(cardUid)) {
            _scanState.value = CardScanState.Success(current.cardData.copy(profile = newProfile))
        }

        _showRegisterDialog.value = false
        _editingProfile.value = null
        RetroSoundAndHaptics.playCoinChime(getApplication())
    }

    /**
     * Interactive D-Pad Controller:
     * - UP/DOWN: Mensa Budget Preset cycling (COFFEE -> WASHING -> CLASSIC -> PREMIUM)
     * - LEFT/RIGHT: LCD Palette Switcher (DMG OLIVE <-> POCKET SILVER <-> BACKLIT CYAN)
     */
    fun onDirectionalPadPress(direction: String) {
        RetroSoundAndHaptics.playButtonClick(getApplication())
        when (direction.uppercase()) {
            "UP" -> {
                _budgetPreset.value = MensaBudgetPreset.prev(_budgetPreset.value)
            }
            "DOWN" -> {
                _budgetPreset.value = MensaBudgetPreset.next(_budgetPreset.value)
            }
            "LEFT" -> {
                val newPalette = LcdPalette.prev(_lcdPalette.value)
                _lcdPalette.value = newPalette
                prefs.edit().putString(KEY_LCD_PALETTE, newPalette.id).apply()
            }
            "RIGHT" -> {
                val newPalette = LcdPalette.next(_lcdPalette.value)
                _lcdPalette.value = newPalette
                prefs.edit().putString(KEY_LCD_PALETTE, newPalette.id).apply()
            }
        }
    }

    /**
     * Offline CSV Export via Storage Access Framework
     */
    fun exportHistoryToUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val csvContent = ThoskaCsvHelper.formatHistoryAsCsv(history.value)
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(csvContent.toByteArray(Charsets.UTF_8))
                }
                RetroSoundAndHaptics.playCoinChime(getApplication())
            } catch (_: Exception) {
                // Ignore file write exceptions
            }
        }
    }

    fun dismissApduSheet() {
        _showApduSheet.value = false
    }

    fun openApduSheet() {
        _showApduSheet.value = true
    }

    fun resetState() {
        _scanState.value = CardScanState.Idle
        _warningMessage.value = null
    }

    fun clearHistory() {
        scanHistoryRepository.clearHistory()
        RetroSoundAndHaptics.playButtonClick(getApplication())
        ThoskaAppWidget.updateAllWidgets(getApplication())
    }

    companion object {
        private const val KEY_LCD_PALETTE = "key_lcd_palette"
    }
}
