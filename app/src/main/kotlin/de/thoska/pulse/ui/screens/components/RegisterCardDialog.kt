package de.thoska.pulse.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.ui.theme.BezelTealDeep
import de.thoska.pulse.ui.theme.BezelTealLight
import de.thoska.pulse.ui.theme.ButtonRed
import de.thoska.pulse.ui.theme.LcdGreenBg
import de.thoska.pulse.ui.theme.LcdGreenDeep

/**
 * Retro 8-bit dialog allowing guest card registration to link personal student details
 * to any newly tapped thoska card UID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCardDialog(
    cardUid: String,
    onSave: (name: String, matrikel: String, barcode: String, valid: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var matrikel by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var valid by remember { mutableStateOf("01.10.26 - 31.03.27") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BezelTealDeep, RoundedCornerShape(12.dp))
                .border(2.dp, BezelTealLight, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "REGISTER THOSKA CARD",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LcdGreenBg,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "UID: $cardUid",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LcdGreenBg.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace
            )

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = LcdGreenBg,
                unfocusedBorderColor = BezelTealLight,
                cursorColor = LcdGreenBg,
                focusedLabelColor = LcdGreenBg,
                unfocusedLabelColor = LcdGreenBg.copy(alpha = 0.7f)
            )

            val textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("FULL NAME") },
                singleLine = true,
                textStyle = textStyle,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = matrikel,
                onValueChange = { matrikel = it },
                label = { Text("MATRIKEL-NR") },
                singleLine = true,
                textStyle = textStyle,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("LIBRARY BARCODE (12 DIGITS)") },
                singleLine = true,
                textStyle = textStyle,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valid,
                onValueChange = { valid = it },
                label = { Text("VALIDITY PERIOD") },
                singleLine = true,
                textStyle = textStyle,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PixelButton(
                    text = "CANCEL",
                    onClick = onDismiss,
                    color = ButtonRed,
                    height = 34.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                PixelButton(
                    text = "SAVE PROFILE",
                    onClick = { onSave(name, matrikel, barcode, valid) },
                    color = LcdGreenBg,
                    height = 34.dp
                )
            }
        }
    }
}
