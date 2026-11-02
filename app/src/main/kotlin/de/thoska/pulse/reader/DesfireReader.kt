package de.thoska.pulse.reader

import de.thoska.pulse.model.ApduLogEntry
import de.thoska.pulse.model.ThoskaCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service orchestrating ISO 7816-4 wrapped APDU exchanges with a MIFARE DESFire
 * smart card for Thoska Mensa student card reading.
 */
class DesfireReader(
    private val transceiver: IsoDepTransceiver
) {

    /**
     * Reads card balance and last transaction details through raw ISO-DEP transceive calls.
     */
    suspend fun readCard(): Result<ThoskaCardData> = withContext(Dispatchers.IO) {
        val logs = mutableListOf<ApduLogEntry>()
        val rawUid = transceiver.getUid()
        val cardUid = rawUid?.let { DesfireApduParser.toColonHex(it) } ?: "UNKNOWN_UID"

        try {
            // Step 1: Select Application AID 0x5F8415
            var selectedAid = "5F8415"
            val selectCmdPrimary = DesfireApduParser.SELECT_AID_PRIMARY
            val selectRespPrimary = transceiver.transceive(selectCmdPrimary)
            val primarySuccess = DesfireApduParser.isSuccessResponse(selectRespPrimary)

            logs.add(
                ApduLogEntry(
                    step = "Select AID 5F8415",
                    commandHex = DesfireApduParser.toSpacedHex(selectCmdPrimary),
                    responseHex = DesfireApduParser.toSpacedHex(selectRespPrimary),
                    statusWord = DesfireApduParser.extractStatusWord(selectRespPrimary),
                    isSuccess = primarySuccess,
                    note = if (primarySuccess) "Mensa Application Selected" else "Primary AID rejected, attempting fallback"
                )
            )

            if (!primarySuccess) {
                // Fallback to reversed AID 0x15845F
                selectedAid = "15845F"
                val selectCmdFallback = DesfireApduParser.SELECT_AID_FALLBACK
                val selectRespFallback = transceiver.transceive(selectCmdFallback)
                val fallbackSuccess = DesfireApduParser.isSuccessResponse(selectRespFallback)

                logs.add(
                    ApduLogEntry(
                        step = "Select AID 15845F (Fallback)",
                        commandHex = DesfireApduParser.toSpacedHex(selectCmdFallback),
                        responseHex = DesfireApduParser.toSpacedHex(selectRespFallback),
                        statusWord = DesfireApduParser.extractStatusWord(selectRespFallback),
                        isSuccess = fallbackSuccess,
                        note = if (fallbackSuccess) "Mensa Application Selected (Fallback)" else "Fallback AID rejected"
                    )
                )

                if (!fallbackSuccess) {
                    return@withContext Result.failure(
                        CardReadException(
                            "Could not select Mensa application on card (Neither 5F8415 nor 15845F returned success).",
                            logs
                        )
                    )
                }
            }

            // Step 2: Read Balance (Value File 0x01)
            val balanceCmd = DesfireApduParser.READ_BALANCE_FILE_01
            val balanceResp = transceiver.transceive(balanceCmd)
            val balanceSuccess = DesfireApduParser.isSuccessResponse(balanceResp)

            logs.add(
                ApduLogEntry(
                    step = "Read Balance (File 0x01)",
                    commandHex = DesfireApduParser.toSpacedHex(balanceCmd),
                    responseHex = DesfireApduParser.toSpacedHex(balanceResp),
                    statusWord = DesfireApduParser.extractStatusWord(balanceResp),
                    isSuccess = balanceSuccess,
                    note = if (balanceSuccess) "Balance read successfully" else "Balance read failed"
                )
            )

            if (!balanceSuccess) {
                return@withContext Result.failure(
                    CardReadException("Failed to read balance from File 0x01: status ${DesfireApduParser.extractStatusWord(balanceResp)}", logs)
                )
            }

            val balanceEur = DesfireApduParser.parseBalanceEur(balanceResp)

            // Step 3: Read Last Transaction (File 0x01 Settings)
            val settingsCmd = DesfireApduParser.READ_SETTINGS_FILE_01
            val settingsResp = transceiver.transceive(settingsCmd)
            val settingsSuccess = DesfireApduParser.isSuccessResponse(settingsResp)

            logs.add(
                ApduLogEntry(
                    step = "Read Settings / Last Tx (File 0x01)",
                    commandHex = DesfireApduParser.toSpacedHex(settingsCmd),
                    responseHex = DesfireApduParser.toSpacedHex(settingsResp),
                    statusWord = DesfireApduParser.extractStatusWord(settingsResp),
                    isSuccess = settingsSuccess,
                    note = if (settingsSuccess) "File 0x01 settings read successfully" else "File settings read failed"
                )
            )

            val lastTransactionEur = if (settingsSuccess && settingsResp.size >= 16) {
                DesfireApduParser.parseLastTransactionEur(settingsResp)
            } else {
                0.0
            }

            val cardData = ThoskaCardData(
                cardUid = cardUid,
                balanceEur = balanceEur,
                lastTransactionEur = lastTransactionEur,
                selectedAid = selectedAid,
                apduLogs = logs
            )

            Result.success(cardData)
        } catch (e: Exception) {
            Result.failure(CardReadException("Error communicating with smart card: ${e.message}", logs, e))
        }
    }
}

class CardReadException(
    message: String,
    val logs: List<ApduLogEntry> = emptyList(),
    cause: Throwable? = null
) : Exception(message, cause)
