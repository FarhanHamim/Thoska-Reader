package de.thoska.pulse.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.model.ApduLogEntry
import de.thoska.pulse.ui.theme.BezelTeal
import de.thoska.pulse.ui.theme.BezelTealDark
import de.thoska.pulse.ui.theme.BezelTealDeep
import de.thoska.pulse.ui.theme.BezelTealLight
import de.thoska.pulse.ui.theme.ButtonRed
import de.thoska.pulse.ui.theme.LcdGreenBg
import de.thoska.pulse.ui.theme.LcdGreenDark
import de.thoska.pulse.ui.theme.LcdGreenDeep
import de.thoska.pulse.ui.theme.LcdGreenMedium

/**
 * THOSKA BOY Raw ISO 7816-4 APDU Inspector Sheet styled like a Game Boy debug cartridge tool.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApduInspectorSheet(
    logs: List<ApduLogEntry>,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BezelTealDeep,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Black.copy(alpha = 0.8f),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BezelTealLight, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(BezelTealDeep)
                .padding(16.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROM: APDU_TRACE.DUMP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LcdGreenBg,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                PixelButton(
                    text = "[ CLOSE ]",
                    onClick = onDismiss,
                    color = ButtonRed,
                    height = 32.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (logs.isEmpty()) {
                Text(
                    text = "* NO APDU FRAMES IN BUFFER *",
                    style = MaterialTheme.typography.bodySmall,
                    color = LcdGreenDark,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(logs) { index, log ->
                        RetroApduLogItem(index = index, log = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun RetroApduLogItem(
    index: Int,
    log: ApduLogEntry,
    modifier: Modifier = Modifier
) {
    val statusColor = if (log.isSuccess) LcdGreenBg else ButtonRed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BezelTealDark, RectangleShape)
            .border(1.5.dp, if (log.isSuccess) BezelTeal else ButtonRed, RectangleShape)
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("#%02d %s", index, log.step.uppercase()),
                    style = MaterialTheme.typography.labelLarge,
                    color = LcdGreenBg,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "SW: ${log.statusWord}",
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            if (log.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.note.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = LcdGreenMedium,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f), RectangleShape)
                    .border(1.dp, BezelTeal, RectangleShape)
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "TX: ${log.commandHex}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = LcdGreenBg
                    )

                    Text(
                        text = "RX: ${log.responseHex}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = statusColor
                    )
                }
            }
        }
    }
}
