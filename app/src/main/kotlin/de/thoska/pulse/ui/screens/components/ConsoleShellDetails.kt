package de.thoska.pulse.ui.screens.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.model.ThoskaCardData
import de.thoska.pulse.ui.theme.BezelTeal
import de.thoska.pulse.ui.theme.CasingTextDebossed
import de.thoska.pulse.ui.theme.CasingTextWhite
import de.thoska.pulse.ui.theme.ShellOrangeDark
import de.thoska.pulse.ui.theme.ShellOrangeShadow

/**
 * Top header of the Thoska Boy handheld casing:
 * Debossed retro "THOSKA BOY" title on top right + pixelated EAH Jena triangle logo.
 */
@Composable
fun ShellHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // EAH Jena Pixelated Triangle Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            EahJenaPixelLogo(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "EAH JENA",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = BezelTeal,
                fontFamily = FontFamily.Monospace
            )
        }

        // Debossed "THOSKA BOY" in retro bold gaming font
        Text(
            text = "THOSKA BOY™",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            color = CasingTextDebossed,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Pixelated EAH Jena Triangle Logo in EAH Teal (#005A5B).
 */
@Composable
fun EahJenaPixelLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Dark Outline
        val outerPath = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(outerPath, color = Color(0xFF101418))

        // Left Face (Highlight Teal)
        val leftFace = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.5f, h * 0.92f)
            lineTo(w * 0.08f, h * 0.92f)
            close()
        }
        drawPath(leftFace, color = Color(0xFF14B8A6))

        // Right Face (Shadow Teal)
        val rightFace = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.92f, h * 0.92f)
            lineTo(w * 0.5f, h * 0.92f)
            close()
        }
        drawPath(rightFace, color = Color(0xFF005A5B))
    }
}

/**
 * Dynamic Casing Footer Details:
 * When Idle (Pre-Tap): Displays "STATUS: WAITING FOR NFC..." and "SLOT: EMPTY". (Zero hardcoded personal info!)
 * When Card Loaded: Dynamically displays the resolved cardholder profile or guest UID info.
 */
@Composable
fun ShellFooterDetails(
    activeCard: ThoskaCardData?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val profile = activeCard?.profile

            val line1 = when {
                activeCard == null -> "STATUS: WAITING FOR NFC..."
                profile != null -> "VALID: ${profile.validUntil}"
                else -> "STATUS: GUEST CARD ACTIVE"
            }

            val line2 = when {
                activeCard == null -> "SLOT: EMPTY"
                profile != null -> "${profile.holderName} | ID: ${profile.matrikelNr}"
                else -> "UID: ${activeCard.cardUid}"
            }

            Text(
                text = line1,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = line2,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }

        // Speaker grille diagonal slots (///)
        SpeakerGrille(modifier = Modifier.size(46.dp, 26.dp))
    }
}

/**
 * Diagonal speaker grille slots in the plastic casing.
 */
@Composable
fun SpeakerGrille(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.rotate(-25f),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(ShellOrangeShadow, RoundedCornerShape(2.dp))
            )
        }
    }
}

/**
 * Subtle corner screw rivet indents in the handheld shell.
 */
@Composable
fun ScrewIndent(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(10.dp)
            .background(ShellOrangeShadow, CircleShape)
            .border(0.5.dp, ShellOrangeDark, CircleShape)
    ) {
        Canvas(modifier = Modifier.size(6.dp)) {
            val stroke = 1.dp.toPx()
            drawLine(Color(0xFF4A1807), Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), strokeWidth = stroke)
            drawLine(Color(0xFF4A1807), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = stroke)
        }
    }
}
