package de.thoska.pulse.ui.screens.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.model.LcdPalette
import de.thoska.pulse.model.MensaBudgetPreset
import de.thoska.pulse.model.ScanRecord
import de.thoska.pulse.ui.theme.BezelTeal
import de.thoska.pulse.ui.theme.BezelTealDark
import de.thoska.pulse.ui.theme.BezelTealDeep
import de.thoska.pulse.ui.theme.BezelTealLight
import de.thoska.pulse.ui.viewmodel.CardScanState
import de.thoska.pulse.ui.viewmodel.ConsoleScreenMode
import de.thoska.pulse.ui.viewmodel.NfcStatus
import de.thoska.pulse.util.ThoskaValidationHelper

/**
 * 1990s Dot-Matrix LCD Display nestled inside an EAH Slate-Teal bezel.
 * Supports dynamic retro palettes (DMG OLIVE, POCKET SILVER, BACKLIT CYAN).
 * Rendered with ultra-bold, high-contrast pixel typography.
 */
@Composable
fun LcdScreenContainer(
    screenMode: ConsoleScreenMode,
    scanState: CardScanState,
    nfcStatus: NfcStatus,
    history: List<ScanRecord>,
    warningMessage: String?,
    palette: LcdPalette,
    budgetPreset: MensaBudgetPreset,
    onRegisterCardClicked: () -> Unit,
    onEditProfileClicked: () -> Unit,
    onExportCsvClicked: () -> Unit,
    onClearHistoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Outer EAH Slate-Teal Screen Bezel
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                BezelTeal,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 32.dp, bottomEnd = 16.dp)
            )
            .border(
                3.dp,
                BezelTealLight.copy(alpha = 0.5f),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 32.dp, bottomEnd = 16.dp)
            )
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Bezel Header: Battery Indicator + Dot Matrix label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF1744), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF880000), RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BATTERY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "DOT MATRIX WITH STEREO SOUND",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFFCAD5D5),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Inner LCD Bevel Shadow Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(242.dp)
                    .background(BezelTealDeep, RoundedCornerShape(6.dp))
                    .border(2.5.dp, BezelTealDark, RoundedCornerShape(6.dp))
                    .padding(3.dp)
            ) {
                // The Dot-Matrix Screen with Active Retro Palette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.backgroundColor)
                        .padding(8.dp)
                ) {
                    // Warning Banner Overlay if Button A pressed without a locked card
                    if (warningMessage != null) {
                        LcdWarningBanner(message = warningMessage, palette = palette)
                    } else {
                        AnimatedContent(
                            targetState = screenMode,
                            transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(160)) },
                            label = "screenFlip"
                        ) { mode ->
                            when (mode) {
                                ConsoleScreenMode.BALANCE_FRONT -> {
                                    LcdFrontView(
                                        scanState = scanState,
                                        nfcStatus = nfcStatus,
                                        palette = palette,
                                        budgetPreset = budgetPreset
                                    )
                                }
                                ConsoleScreenMode.BARCODE_BACK -> {
                                    LcdBarcodeBackView(
                                        scanState = scanState,
                                        palette = palette,
                                        onRegisterCardClicked = onRegisterCardClicked,
                                        onEditProfileClicked = onEditProfileClicked
                                    )
                                }
                                ConsoleScreenMode.HISTORY_INVENTORY -> {
                                    LcdHistoryInventoryView(
                                        history = history,
                                        palette = palette,
                                        onExportCsvClicked = onExportCsvClicked,
                                        onClearHistoryClicked = onClearHistoryClicked
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen View 1: Main Balance View
 * In Idle state: shows strictly "THOSKA BOY OS v1.0" and blinking ">> INSERT THOSKA CARTRIDGE <<".
 * In Active state: displays balance, affordance calculation, battery-like HP bar, and low balance/expiry alerts.
 */
@Composable
private fun LcdFrontView(
    scanState: CardScanState,
    @Suppress("UNUSED_PARAMETER") nfcStatus: NfcStatus,
    palette: LcdPalette,
    budgetPreset: MensaBudgetPreset
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lcdBlink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )
    val isBlinkOn = blinkAlpha > 0.45f

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top LCD Status Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (scanState is CardScanState.Idle) Arrangement.Center else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "THOSKA BOY OS v1.0",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = palette.inkDeepColor,
                fontFamily = FontFamily.Monospace
            )
            if (scanState !is CardScanState.Idle) {
                Text(
                    text = if (scanState is CardScanState.Success) "CHIP:LOCKED" else "SLOT:EMPTY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Center Content based on Scan State
        when (scanState) {
            is CardScanState.Success -> {
                val card = scanState.cardData
                val balanceFormatted = card.formattedBalance
                val lastTxFormatted = card.formattedLastTransaction
                val profile = card.profile
                val isLowBal = ThoskaValidationHelper.isLowBalance(card.balanceEur)
                val expiryStatus = profile?.let { ThoskaValidationHelper.checkValidationExpiry(it.validUntil) }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Cardholder or Guest Header
                    Text(
                        text = if (profile != null) "${profile.university.shortName} | ${profile.holderName}" else "GUEST CARD [${card.cardUid.take(8)}]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDarkColor,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    // Low-Balance Warning Banner (flashing bold banner)
                    if (isLowBal) {
                        Text(
                            text = if (isBlinkOn) "! LOW CREDITS - VISIT AUFWERTER !" else "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = palette.inkDeepColor,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    } else if (expiryStatus == ThoskaValidationHelper.ExpiryStatus.EXPIRED ||
                        expiryStatus == ThoskaValidationHelper.ExpiryStatus.EXPIRING_SOON) {
                        // Validation Expiry Warning
                        Text(
                            text = "! VALIDATE CARD AT TERMINAL !",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = palette.inkDeepColor,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = "CASH OUT / BAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDarkColor,
                        fontFamily = FontFamily.Monospace
                    )

                    // Big Chunky Bold Numbers (e.g. 40,40 €)
                    Text(
                        text = balanceFormatted,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace
                    )

                    // Fork/Plate icon + LAST MEAL COST
                    Text(
                        text = "🍽 LAST: -$lastTxFormatted",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Horizontal Pixel Battery-like HP Bar
                    PixelHpBar(
                        percentage = ((card.balanceEur / 50.0).coerceIn(0.0, 1.0)).toFloat(),
                        palette = palette,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // D-Pad Budget Simulator Affordance line
                    Text(
                        text = budgetPreset.formattedAffords(card.balanceEur),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CardScanState.Scanning -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isBlinkOn) ">> READING NFC CHIP <<\nDO NOT MOVE CARD" else "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CardScanState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = ">> READ ERROR <<",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scanState.message.take(28),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDarkColor,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBlinkOn) "PRESS [START] TO RETRY" else "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            CardScanState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBlinkOn) ">> INSERT THOSKA CARTRIDGE <<" else "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = palette.inkDeepColor,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Navigation Line
        if (scanState !is CardScanState.Idle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "A:FLIP",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "B:LOG",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "START:TAP",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Screen View 2: High-contrast Library Barcode Pass or Guest Registration Prompt
 * Temporarily auto-boosts screen brightness to 100% (1.0f) for optical library scanner readability.
 */
@Composable
private fun LcdBarcodeBackView(
    scanState: CardScanState,
    palette: LcdPalette,
    onRegisterCardClicked: () -> Unit,
    onEditProfileClicked: () -> Unit
) {
    val card = (scanState as? CardScanState.Success)?.cardData
    val profile = card?.profile
    val context = LocalContext.current

    // Max Brightness Auto-Boost via DisposableEffect
    if (profile != null) {
        DisposableEffect(Unit) {
            val activity = context.findActivity()
            val window = activity?.window ?: return@DisposableEffect onDispose {}
            val layoutParams = window.attributes
            val originalBrightness = layoutParams.screenBrightness

            layoutParams.screenBrightness = 1.0f
            window.attributes = layoutParams

            onDispose {
                layoutParams.screenBrightness = originalBrightness
                window.attributes = layoutParams
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dynamic University Library Header
        val headerTitle = if (profile != null) {
            "${profile.university.shortName} LIBRARY PASS"
        } else {
            "THOSKA LIBRARY PASS"
        }

        Text(
            text = headerTitle,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = palette.inkDeepColor,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )

        if (profile != null) {
            // Crisp, pure white (#FFFFFF) quiet-zone padding box around barcode
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(Color.White, RoundedCornerShape(3.dp))
                    .border(2.dp, palette.inkDeepColor, RoundedCornerShape(3.dp))
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BarcodeCanvas(
                        code = profile.libraryBarcode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "[MAX BRIGHTNESS ACTIVE]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = profile.libraryBarcode,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = palette.inkDeepColor,
                fontFamily = FontFamily.Monospace
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.holderName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ID: ${profile.matrikelNr} | ${profile.validUntil}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDarkColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Button to edit existing profile
                Box(
                    modifier = Modifier
                        .background(palette.inkDeepColor, RoundedCornerShape(3.dp))
                        .clickable(onClick = onEditProfileClicked)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "[ EDIT PROFILE ]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.backgroundColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            // Unregistered Guest Card Prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "UNKNOWN GUEST CARD",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDeepColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "UID: ${card?.cardUid ?: "N/A"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDarkColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(palette.inkDeepColor, RoundedCornerShape(4.dp))
                        .clickable(onClick = onRegisterCardClicked)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "[ + REGISTER THIS CARD ]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.backgroundColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Text(
            text = "PRESS [A] TO RETURN",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
            color = palette.inkDarkColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Screen View 3: Game Boy 8-Bit Transaction History Log (Last 5 Scans) with CSV Export.
 */
@Composable
private fun LcdHistoryInventoryView(
    history: List<ScanRecord>,
    palette: LcdPalette,
    onExportCsvClicked: () -> Unit,
    onClearHistoryClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "» SCAN HISTORY LOG «",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = palette.inkDeepColor,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "(${history.size.coerceAtMost(5)}/5)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = palette.inkDarkColor,
                fontFamily = FontFamily.Monospace
            )
        }

        if (history.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "* NO SCANS LOGGED *\nTAP CARD TO LOG HISTORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.inkDarkColor,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                history.take(5).forEachIndexed { i, record ->
                    val bal = String.format("%.2f€", record.balanceEur).replace('.', ',')
                    val tx = String.format("-%.2f€", record.lastTransactionEur).replace('.', ',')
                    val shortDate = record.formattedDate.take(16)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${i + 1} $bal ($tx)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.inkDeepColor,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = shortDate.takeLast(8),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.inkDarkColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Action Row with [ EXPORT CSV ], [ CLEAR ], and B:CLOSE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(palette.inkDeepColor, RoundedCornerShape(3.dp))
                    .clickable(onClick = onExportCsvClicked)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "[ EXPORT CSV ]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.backgroundColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .background(palette.inkDeepColor, RoundedCornerShape(3.dp))
                    .clickable(onClick = onClearHistoryClicked)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "[ CLEAR ]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.backgroundColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "B:CLOSE",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                color = palette.inkDarkColor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Bold Warning Overlay on LCD when Button A is pressed without a locked card.
 */
@Composable
private fun LcdWarningBanner(message: String, palette: LcdPalette) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(palette.backgroundColor)
            .border(2.dp, palette.inkDeepColor, RectangleShape)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = ">> ATTENTION <<",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = palette.inkDeepColor,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = palette.inkDeepColor,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Pixel Battery-like HP Bar.
 */
@Composable
private fun PixelHpBar(
    percentage: Float,
    palette: LcdPalette,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val barHeight = size.height

        drawRect(color = palette.inkDeepColor, topLeft = Offset.Zero, size = Size(totalWidth, barHeight))
        drawRect(color = palette.mediumColor, topLeft = Offset(1.5f, 1.5f), size = Size(totalWidth - 3f, barHeight - 3f))

        val segments = 10
        val activeSegments = (percentage * segments).toInt().coerceIn(1, segments)
        val segWidth = (totalWidth - 4f) / segments

        for (i in 0 until activeSegments) {
            drawRect(
                color = palette.inkDeepColor,
                topLeft = Offset(2f + i * segWidth + 1f, 2.5f),
                size = Size(segWidth - 2f, barHeight - 5f)
            )
        }
    }
}

/**
 * High-contrast 1D Barcode Canvas.
 */
@Composable
private fun BarcodeCanvas(code: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barCount = 48
        val barWidth = size.width / (barCount * 1.5f)
        val barHeight = size.height

        val seed = code.hashCode().toLong()
        var curX = 0f
        for (i in 0 until barCount) {
            val bit = ((seed xor (i.toLong() * 31L)) shr (i % 32)) and 1L
            val isBlack = bit == 1L || i == 0 || i == barCount - 1 || i % 2 == 0
            val widthMultiplier = if ((i + code.length) % 5 == 0 || i % 7 == 0) 2f else 1f
            val w = barWidth * widthMultiplier

            if (isBlack) {
                drawRect(color = Color.Black, topLeft = Offset(curX, 0f), size = Size(w, barHeight))
            }
            curX += w + 1.2f
            if (curX >= size.width) break
        }
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
