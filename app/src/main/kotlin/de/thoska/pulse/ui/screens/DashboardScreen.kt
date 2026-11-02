package de.thoska.pulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.thoska.pulse.ui.screens.components.ApduInspectorSheet
import de.thoska.pulse.ui.screens.components.LcdScreenContainer
import de.thoska.pulse.ui.screens.components.RegisterCardDialog
import de.thoska.pulse.ui.screens.components.ScrewIndent
import de.thoska.pulse.ui.screens.components.ShellFooterDetails
import de.thoska.pulse.ui.screens.components.ShellHeader
import de.thoska.pulse.ui.screens.components.TacticalControlsArea
import de.thoska.pulse.ui.theme.ShellOrange
import de.thoska.pulse.ui.theme.ShellOrangeDark
import de.thoska.pulse.ui.theme.ShellOrangeLight
import de.thoska.pulse.ui.viewmodel.CardScanState
import de.thoska.pulse.ui.viewmodel.ThoskaViewModel

/**
 * THOSKA BOY 1990s Handheld Console Main Activity Screen.
 * Handheld Casing: Matte Thoska Burnt Orange (#E05A2B)
 * Screen Bezel: EAH Slate Teal (#005A5B)
 * LCD Screen: Vintage Olive-Green Dot Matrix (#9BBC0F)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ThoskaViewModel,
    modifier: Modifier = Modifier
) {
    val scanState by viewModel.scanState.collectAsState()
    val screenMode by viewModel.screenMode.collectAsState()
    val nfcStatus by viewModel.nfcStatus.collectAsState()
    val history by viewModel.history.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()
    val showRegisterDialog by viewModel.showRegisterDialog.collectAsState()
    val activeLogs by viewModel.activeApduLogs.collectAsState()
    val showApduSheet by viewModel.showApduSheet.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activeCard = (scanState as? CardScanState.Success)?.cardData

    Scaffold(
        containerColor = ShellOrange
    ) { innerPadding ->
        // Complete Handheld Console Shell with Bevels and Screws
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ShellOrange)
                // Casing bevel borders: top-left highlight, bottom-right shadow
                .border(4.dp, ShellOrangeLight.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .border(2.dp, ShellOrangeDark, RoundedCornerShape(20.dp))
                .padding(10.dp)
        ) {
            // 4 Corner Screws on the Console Shell
            ScrewIndent(modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
            ScrewIndent(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
            ScrewIndent(modifier = Modifier.align(Alignment.BottomStart).padding(4.dp))
            ScrewIndent(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Casing Top Header: EAH Jena Logo & Debossed "THOSKA BOY™"
                ShellHeader()

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Olive-Green Dot-Matrix LCD Screen inside EAH Teal Bezel
                LcdScreenContainer(
                    screenMode = screenMode,
                    scanState = scanState,
                    nfcStatus = nfcStatus,
                    history = history,
                    warningMessage = warningMessage,
                    onRegisterCardClicked = { viewModel.openRegisterDialog() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Tactile 3D Controls
                // D-Pad: haptic feedback
                // Button A: Flip card (or warning if idle)
                // Button B: Toggle 5-slot scan history
                // SELECT: Eject card / reset to idle "NO CARTRIDGE" state
                // START: Simulate card tap
                TacticalControlsArea(
                    onDPadPress = { dir -> viewModel.onDirectionalPadPress(dir) },
                    onAButtonPressed = { viewModel.toggleFlipCard() },
                    onBButtonPressed = { viewModel.toggleHistory() },
                    onSelectPressed = { viewModel.ejectCard() },
                    onStartPressed = { viewModel.simulateScan() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Shell Footer: Dynamic Casing Details (Status / Slot when idle, student info when active)
                ShellFooterDetails(activeCard = activeCard)
            }
        }

        // Guest Card Registration Dialog
        if (showRegisterDialog && activeCard != null) {
            RegisterCardDialog(
                cardUid = activeCard.cardUid,
                onSave = { name, matrikel, barcode, valid ->
                    viewModel.saveCardRegistration(activeCard.cardUid, name, matrikel, barcode, valid)
                },
                onDismiss = { viewModel.closeRegisterDialog() }
            )
        }

        // APDU Log Inspector Bottom Sheet
        if (showApduSheet) {
            ApduInspectorSheet(
                logs = activeLogs,
                sheetState = sheetState,
                onDismiss = { viewModel.dismissApduSheet() }
            )
        }
    }
}
