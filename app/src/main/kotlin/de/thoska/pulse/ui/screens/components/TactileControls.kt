package de.thoska.pulse.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.ui.theme.ButtonRed
import de.thoska.pulse.ui.theme.ButtonRedDark
import de.thoska.pulse.ui.theme.ButtonRedLight
import de.thoska.pulse.ui.theme.CasingButtonLabel
import de.thoska.pulse.ui.theme.CasingTextDebossed
import de.thoska.pulse.ui.theme.DPadGrey
import de.thoska.pulse.ui.theme.DPadGreyDark
import de.thoska.pulse.ui.theme.DPadGreyLight
import de.thoska.pulse.ui.theme.ShellOrangeDark
import de.thoska.pulse.ui.theme.ShellOrangeShadow
import de.thoska.pulse.ui.theme.StartSelectGrey
import de.thoska.pulse.ui.theme.StartSelectGreyDark

/**
 * Tactical Controls Area of the Thoska Boy Console:
 * - Left: Matte-grey D-Pad with 3D bevels and center indent.
 * - Right: Two angled glossy red buttons (B [HISTORY] & A [FLIP CARD]).
 * - Center Bottom: Rubbery angled SELECT and START pill buttons.
 */
@Composable
fun TacticalControlsArea(
    onDPadPress: (String) -> Unit,
    onAButtonPressed: () -> Unit,
    onBButtonPressed: () -> Unit,
    onSelectPressed: () -> Unit,
    onStartPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // D-Pad on Left and A/B Buttons on Right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Matte-grey Directional Pad
            DPadController(onPress = onDPadPress)

            // Angled Glossy Red A and B Buttons with debossed casing labels
            ActionButtons(
                onAPressed = onAButtonPressed,
                onBPressed = onBButtonPressed
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Select & Start Bar
        SelectStartController(
            onSelect = onSelectPressed,
            onStart = onStartPressed,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * 3D Matte-Grey D-Pad with directional triangles and center circle indent.
 */
@Composable
fun DPadController(
    onPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionUp = remember { MutableInteractionSource() }
    val interactionDown = remember { MutableInteractionSource() }
    val interactionLeft = remember { MutableInteractionSource() }
    val interactionRight = remember { MutableInteractionSource() }

    val sizeDp = 100.dp
    val armWidth = 34.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizeDp)
            // Circular base indent in the plastic shell
            .background(ShellOrangeShadow.copy(alpha = 0.45f), CircleShape)
            .padding(4.dp)
    ) {
        // Horizontal Arm
        Box(
            modifier = Modifier
                .width(sizeDp - 12.dp)
                .height(armWidth)
                .background(DPadGrey, RoundedCornerShape(3.dp))
                .border(1.5.dp, DPadGreyLight, RoundedCornerShape(3.dp))
        )

        // Vertical Arm
        Box(
            modifier = Modifier
                .width(armWidth)
                .height(sizeDp - 12.dp)
                .background(DPadGrey, RoundedCornerShape(3.dp))
                .border(1.5.dp, DPadGreyLight, RoundedCornerShape(3.dp))
        )

        // Center Indent Circle
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(DPadGreyDark, CircleShape)
                .border(1.dp, DPadGreyLight.copy(alpha = 0.5f), CircleShape)
        )

        // Up Button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(armWidth, (sizeDp - armWidth) / 2)
                .clickable(interactionSource = interactionUp, indication = null) { onPress("UP") }
        ) {
            Text(
                text = "▲",
                color = DPadGreyDark,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Down Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(armWidth, (sizeDp - armWidth) / 2)
                .clickable(interactionSource = interactionDown, indication = null) { onPress("DOWN") }
        ) {
            Text(
                text = "▼",
                color = DPadGreyDark,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Left Button
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size((sizeDp - armWidth) / 2, armWidth)
                .clickable(interactionSource = interactionLeft, indication = null) { onPress("LEFT") }
        ) {
            Text(
                text = "◀",
                color = DPadGreyDark,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Right Button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size((sizeDp - armWidth) / 2, armWidth)
                .clickable(interactionSource = interactionRight, indication = null) { onPress("RIGHT") }
        ) {
            Text(
                text = "▶",
                color = DPadGreyDark,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Angled Glossy Red A and B Buttons with high-contrast, bold casing labels:
 * "B [HISTORY]" and "A [FLIP CARD]".
 */
@Composable
fun ActionButtons(
    onAPressed: () -> Unit,
    onBPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Angled at -18 degrees like classic Game Boy layout
    Row(
        modifier = modifier
            .rotate(-18f)
            .background(ShellOrangeShadow.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // B Button (Left)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlossyRedButton(
                letter = "B",
                onClick = onBPressed
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "B [HISTORY]",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = CasingButtonLabel,
                fontFamily = FontFamily.Monospace
            )
        }

        // A Button (Right, slightly elevated in classic layout)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-8).dp)
        ) {
            GlossyRedButton(
                letter = "A",
                onClick = onAPressed
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "A [FLIP CARD]",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = CasingButtonLabel,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * 3D Glossy Red Round Button with tactile highlight and pressed depth.
 */
@Composable
private fun GlossyRedButton(
    letter: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    val buttonSize = 42.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(buttonSize)
            // Outer shadow bevel
            .background(
                Brush.verticalGradient(
                    colors = if (isPressed) listOf(ButtonRedDark, ButtonRed) else listOf(ButtonRedLight, ButtonRedDark)
                ),
                CircleShape
            )
            .border(1.5.dp, if (isPressed) ButtonRedDark else ButtonRedLight.copy(alpha = 0.8f), CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        Text(
            text = letter,
            color = Color.White.copy(alpha = 0.95f),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            ),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.offset(y = if (isPressed) 1.dp else (-1).dp)
        )
    }
}

/**
 * Rubbery Grey Angled SELECT and START Pill Buttons.
 */
@Composable
fun SelectStartController(
    onSelect: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // SELECT [EJECT] Button
        RubberyPillButton(
            label = "SELECT",
            subLabel = "[EJECT]",
            onClick = onSelect
        )

        // START Button (Simulates a card tap event!)
        RubberyPillButton(
            label = "START",
            subLabel = null,
            onClick = onStart
        )
    }
}

@Composable
private fun RubberyPillButton(
    label: String,
    subLabel: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .rotate(-25f)
                .width(46.dp)
                .height(14.dp)
                .background(
                    if (isPressed) StartSelectGreyDark else StartSelectGrey,
                    RoundedCornerShape(7.dp)
                )
                .border(1.5.dp, if (isPressed) Color.Black else StartSelectGreyDark, RoundedCornerShape(7.dp))
                .clickable(interactionSource = interaction, indication = null, onClick = onClick)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = CasingButtonLabel,
            fontFamily = FontFamily.Monospace
        )

        if (subLabel != null) {
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = CasingButtonLabel,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
