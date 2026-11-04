package de.thoska.pulse.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import de.thoska.pulse.model.ThuringiaUniversity

@Composable
fun UniversityLogo(
    university: ThuringiaUniversity,
    modifier: Modifier = Modifier
) {
    when (university) {
        ThuringiaUniversity.EAH_JENA -> EahJenaLogo(modifier)
        ThuringiaUniversity.FSU_JENA -> FsuJenaLogo(modifier)
        ThuringiaUniversity.TU_ILMENAU -> TuIlmenauLogo(modifier)
        ThuringiaUniversity.BAUHAUS_WEIMAR -> BauhausWeimarLogo(modifier)
        ThuringiaUniversity.UNI_ERFURT -> UniErfurtLogo(modifier)
        ThuringiaUniversity.FH_ERFURT -> FhErfurtLogo(modifier)
        ThuringiaUniversity.HS_SCHMALKALDEN -> HsSchmalkaldenLogo(modifier)
        ThuringiaUniversity.HS_NORDHAUSEN -> HsNordhausenLogo(modifier)
        ThuringiaUniversity.HFM_WEIMAR -> HfmWeimarLogo(modifier)
        ThuringiaUniversity.DHGE -> DhgeLogo(modifier)
    }
}

/**
 * 0. Neutral retro 8-bit cartridge/chip icon on Canvas.
 */
@Composable
fun ThoskaSystemLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Cartridge/ROM body
        val bodyPath = Path().apply {
            moveTo(0f, h * 0.15f)
            lineTo(w * 0.15f, 0f)
            lineTo(w * 0.85f, 0f)
            lineTo(w, h * 0.15f)
            lineTo(w, h * 0.85f)
            lineTo(0f, h * 0.85f)
            close()
        }
        drawPath(bodyPath, color = Color(0xFF262626))

        // Center ROM chip
        drawRect(
            color = Color(0xFF404040),
            topLeft = Offset(w * 0.25f, h * 0.22f),
            size = Size(w * 0.5f, h * 0.38f)
        )
        // Chip notch
        drawCircle(
            color = Color(0xFF262626),
            radius = w * 0.06f,
            center = Offset(w * 0.5f, h * 0.22f)
        )

        // Golden circuit pins at the bottom
        val pinCount = 5
        val pinWidth = w * 0.10f
        val gap = (w - (pinCount * pinWidth)) / (pinCount + 1)
        for (i in 0 until pinCount) {
            val px = gap + i * (pinWidth + gap)
            drawRect(
                color = Color(0xFFEAB308),
                topLeft = Offset(px, h * 0.85f),
                size = Size(pinWidth, h * 0.15f)
            )
        }
    }
}

/**
 * 1. EAH Jena - Stacked triangle pyramid in teal (#005A5B)
 */
@Composable
fun EahJenaLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

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
 * 2. FSU Jena - Classic deep blue crest / academic seal silhouette (#002F5D)
 */
@Composable
fun FsuJenaLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Crest Shield
        val crestPath = Path().apply {
            moveTo(w * 0.1f, 0f)
            lineTo(w * 0.9f, 0f)
            lineTo(w * 0.9f, h * 0.6f)
            cubicTo(w * 0.9f, h * 0.88f, w * 0.55f, h, w * 0.5f, h)
            cubicTo(w * 0.45f, h, w * 0.1f, h * 0.88f, w * 0.1f, h * 0.6f)
            close()
        }
        drawPath(crestPath, color = Color(0xFF002F5D))

        // Inner Academic Seal / Star
        val innerShield = Path().apply {
            moveTo(w * 0.22f, h * 0.12f)
            lineTo(w * 0.78f, h * 0.12f)
            lineTo(w * 0.78f, h * 0.55f)
            cubicTo(w * 0.78f, h * 0.78f, w * 0.52f, h * 0.88f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.48f, h * 0.88f, w * 0.22f, h * 0.78f, w * 0.22f, h * 0.55f)
            close()
        }
        drawPath(innerShield, color = Color(0xFF1E40AF), style = Stroke(width = w * 0.06f))

        // Seal Cross/Quill emblem
        drawLine(
            color = Color(0xFFFACC15),
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.5f, h * 0.72f),
            strokeWidth = w * 0.10f
        )
        drawLine(
            color = Color(0xFFFACC15),
            start = Offset(w * 0.32f, h * 0.38f),
            end = Offset(w * 0.68f, h * 0.38f),
            strokeWidth = w * 0.10f
        )
    }
}

/**
 * 3. TU Ilmenau - Pine green & orange technical gear/atom badge (#006633)
 */
@Composable
fun TuIlmenauLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val c = Offset(w * 0.5f, h * 0.5f)

        // Pine green gear body
        drawCircle(color = Color(0xFF006633), radius = w * 0.44f, center = c)

        // 8 Gear Teeth
        val teeth = 8
        for (i in 0 until teeth) {
            val angle = (i * (360f / teeth)) * (Math.PI.toFloat() / 180f)
            val tx = c.x + (w * 0.44f) * Math.cos(angle.toDouble()).toFloat()
            val ty = c.y + (h * 0.44f) * Math.sin(angle.toDouble()).toFloat()
            drawCircle(color = Color(0xFF004D26), radius = w * 0.09f, center = Offset(tx, ty))
        }

        // Inner circle
        drawCircle(color = Color(0xFF0A280A), radius = w * 0.28f, center = c)

        // Orange Atom Core
        drawCircle(color = Color(0xFFF97316), radius = w * 0.14f, center = c)

        // Atom Orbit Ring
        drawCircle(color = Color(0xFFFFD54F), radius = w * 0.24f, center = c, style = Stroke(width = w * 0.05f))
    }
}

/**
 * 4. Bauhaus Weimar - Geometric square/circle/triangle block (#E3000B)
 */
@Composable
fun BauhausWeimarLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Dark Border
        drawRect(color = Color(0xFF111827), topLeft = Offset.Zero, size = Size(w, h))

        // Red Square
        drawRect(color = Color(0xFFE3000B), topLeft = Offset(w * 0.08f, h * 0.08f), size = Size(w * 0.40f, h * 0.40f))

        // Blue Circle
        drawCircle(color = Color(0xFF2563EB), radius = w * 0.20f, center = Offset(w * 0.72f, h * 0.28f))

        // Yellow/White Triangle at bottom
        val triPath = Path().apply {
            moveTo(w * 0.5f, h * 0.52f)
            lineTo(w * 0.90f, h * 0.92f)
            lineTo(w * 0.10f, h * 0.92f)
            close()
        }
        drawPath(triPath, color = Color(0xFFFBBF24))
    }
}

/**
 * 5. Uni Erfurt - Crimson red open book / spire emblem (#A51E37)
 */
@Composable
fun UniErfurtLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Crimson base background
        drawRect(color = Color(0xFFA51E37), topLeft = Offset.Zero, size = Size(w, h))

        // Twin Spire Silhouette in center
        val spires = Path().apply {
            // Left Spire
            moveTo(w * 0.35f, h * 0.08f)
            lineTo(w * 0.45f, h * 0.60f)
            lineTo(w * 0.25f, h * 0.60f)
            close()
            // Right Spire
            moveTo(w * 0.65f, h * 0.08f)
            lineTo(w * 0.75f, h * 0.60f)
            lineTo(w * 0.55f, h * 0.60f)
            close()
        }
        drawPath(spires, color = Color(0xFFFFFFFF))

        // Open Book Base
        val book = Path().apply {
            moveTo(w * 0.10f, h * 0.65f)
            cubicTo(w * 0.30f, h * 0.70f, w * 0.45f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.55f, h * 0.65f, w * 0.70f, h * 0.70f, w * 0.90f, h * 0.65f)
            lineTo(w * 0.90f, h * 0.88f)
            cubicTo(w * 0.70f, h * 0.92f, w * 0.55f, h * 0.85f, w * 0.50f, h * 0.95f)
            cubicTo(w * 0.45f, h * 0.85f, w * 0.30f, h * 0.92f, w * 0.10f, h * 0.88f)
            close()
        }
        drawPath(book, color = Color(0xFFFDE047))
    }
}

/**
 * 6. FH Erfurt - Bold green architectural pillar badge (#00854A)
 */
@Composable
fun FhErfurtLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dark background
        drawRect(color = Color(0xFF003D22), topLeft = Offset.Zero, size = Size(w, h))

        // Pediment / Roof beam
        drawRect(color = Color(0xFF00854A), topLeft = Offset(w * 0.12f, h * 0.12f), size = Size(w * 0.76f, h * 0.16f))

        // 3 Pillars
        val pillarW = w * 0.14f
        val pillarH = h * 0.48f
        val pY = h * 0.32f
        drawRect(color = Color(0xFF10B981), topLeft = Offset(w * 0.16f, pY), size = Size(pillarW, pillarH))
        drawRect(color = Color(0xFF10B981), topLeft = Offset(w * 0.43f, pY), size = Size(pillarW, pillarH))
        drawRect(color = Color(0xFF10B981), topLeft = Offset(w * 0.70f, pY), size = Size(pillarW, pillarH))

        // Pedestal / Base
        drawRect(color = Color(0xFF00854A), topLeft = Offset(w * 0.10f, h * 0.82f), size = Size(w * 0.80f, h * 0.12f))
    }
}

/**
 * 7. HS Schmalkalden - Dark cyan polygon/shield emblem (#005478)
 */
@Composable
fun HsSchmalkaldenLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dark cyan shield
        val shield = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.30f)
            lineTo(w * 0.82f, h * 0.88f)
            lineTo(w * 0.5f, h)
            lineTo(w * 0.18f, h * 0.88f)
            lineTo(0f, h * 0.30f)
            close()
        }
        drawPath(shield, color = Color(0xFF005478))

        // Diagonal Stepped Chevron
        val chevron = Path().apply {
            moveTo(w * 0.20f, h * 0.35f)
            lineTo(w * 0.50f, h * 0.60f)
            lineTo(w * 0.80f, h * 0.35f)
            lineTo(w * 0.80f, h * 0.52f)
            lineTo(w * 0.50f, h * 0.78f)
            lineTo(w * 0.20f, h * 0.52f)
            close()
        }
        drawPath(chevron, color = Color(0xFF38BDF8))
    }
}

/**
 * 8. HS Nordhausen - Vibrant blue modern diamond/grid icon (#004B93)
 */
@Composable
fun HsNordhausenLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val c = Offset(w * 0.5f, h * 0.5f)

        // Diamond contour
        val diamond = Path().apply {
            moveTo(c.x, 0f)
            lineTo(w, c.y)
            lineTo(c.x, h)
            lineTo(0f, c.y)
            close()
        }
        drawPath(diamond, color = Color(0xFF004B93))

        // 4 grid sub-diamonds inside
        val innerW = w * 0.20f
        drawRect(color = Color(0xFF60A5FA), topLeft = Offset(c.x - innerW - 1f, c.y - innerW - 1f), size = Size(innerW, innerW))
        drawRect(color = Color(0xFF93C5FD), topLeft = Offset(c.x + 1f, c.y - innerW - 1f), size = Size(innerW, innerW))
        drawRect(color = Color(0xFF93C5FD), topLeft = Offset(c.x - innerW - 1f, c.y + 1f), size = Size(innerW, innerW))
        drawRect(color = Color(0xFF60A5FA), topLeft = Offset(c.x + 1f, c.y + 1f), size = Size(innerW, innerW))
    }
}

/**
 * 9. HfM Weimar - Retro 8-bit musical lyre / treble clef (#8B0000)
 */
@Composable
fun HfmWeimarLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background
        drawRect(color = Color(0xFF4A0000), topLeft = Offset.Zero, size = Size(w, h))

        // Lyre U-frame in Crimson
        val lyre = Path().apply {
            moveTo(w * 0.15f, h * 0.18f)
            lineTo(w * 0.25f, h * 0.18f)
            lineTo(w * 0.25f, h * 0.55f)
            cubicTo(w * 0.25f, h * 0.75f, w * 0.75f, h * 0.75f, w * 0.75f, h * 0.55f)
            lineTo(w * 0.75f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.58f)
            cubicTo(w * 0.85f, h * 0.85f, w * 0.15f, h * 0.85f, w * 0.15f, h * 0.58f)
            close()
        }
        drawPath(lyre, color = Color(0xFF8B0000))

        // Top Crossbar
        drawRect(color = Color(0xFFFBBF24), topLeft = Offset(w * 0.18f, h * 0.14f), size = Size(w * 0.64f, h * 0.09f))

        // 3 Lyre Strings
        val strW = w * 0.05f
        drawLine(color = Color(0xFFFDE68A), start = Offset(w * 0.38f, h * 0.23f), end = Offset(w * 0.38f, h * 0.68f), strokeWidth = strW)
        drawLine(color = Color(0xFFFDE68A), start = Offset(w * 0.50f, h * 0.23f), end = Offset(w * 0.50f, h * 0.72f), strokeWidth = strW)
        drawLine(color = Color(0xFFFDE68A), start = Offset(w * 0.62f, h * 0.23f), end = Offset(w * 0.62f, h * 0.68f), strokeWidth = strW)

        // Base Pedestal
        drawRect(color = Color(0xFFFBBF24), topLeft = Offset(w * 0.30f, h * 0.84f), size = Size(w * 0.40f, h * 0.10f))
    }
}

/**
 * 10. DHGE - Interlocking dual-square link badge (#003366)
 */
@Composable
fun DhgeLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Square 1: Top-Left (Gera)
        val sSize = w * 0.52f
        drawRect(
            color = Color(0xFF003366),
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(sSize, sSize),
            style = Stroke(width = w * 0.11f)
        )

        // Square 2: Bottom-Right (Eisenach)
        drawRect(
            color = Color(0xFF0284C7),
            topLeft = Offset(w * 0.40f, h * 0.40f),
            size = Size(sSize, sSize),
            style = Stroke(width = w * 0.11f)
        )

        // Center link overlap accent
        drawRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(w * 0.40f, h * 0.40f),
            size = Size(w * 0.20f, w * 0.20f)
        )
    }
}

