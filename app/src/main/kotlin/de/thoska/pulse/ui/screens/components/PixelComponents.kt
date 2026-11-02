package de.thoska.pulse.ui.screens.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thoska.pulse.ui.theme.BezelTeal
import de.thoska.pulse.ui.theme.BezelTealDeep
import de.thoska.pulse.ui.theme.LcdGreenBg
import de.thoska.pulse.ui.theme.LcdGreenDeep

/**
 * 8-Bit Pixel Button with high-contrast inverted press state.
 */
@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LcdGreenDeep,
    enabled: Boolean = true,
    height: Dp = 36.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentBg = when {
        !enabled -> Color(0xFF1E2822)
        isPressed -> color
        else -> BezelTealDeep
    }

    val currentBorder = when {
        !enabled -> Color(0xFF333333)
        else -> color
    }

    val currentText = when {
        !enabled -> Color(0xFF666666)
        isPressed -> Color.White
        else -> color
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(height)
            .background(currentBg, RoundedCornerShape(4.dp))
            .border(1.5.dp, currentBorder, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = currentText,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}
