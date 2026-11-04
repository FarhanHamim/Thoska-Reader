package de.thoska.pulse.model

import androidx.compose.ui.graphics.Color

enum class LcdPalette(
    val id: String,
    val displayName: String,
    val backgroundColor: Color,
    val inkDeepColor: Color,
    val inkDarkColor: Color,
    val mediumColor: Color
) {
    DMG_OLIVE(
        id = "DMG_OLIVE",
        displayName = "DMG OLIVE",
        backgroundColor = Color(0xFF9BBC0F),
        inkDeepColor = Color(0xFF0A280A),
        inkDarkColor = Color(0xFF0E380E),
        mediumColor = Color(0xFF7A9E0A)
    ),
    POCKET_SILVER(
        id = "POCKET_SILVER",
        displayName = "POCKET SILVER",
        backgroundColor = Color(0xFFC4CFA1),
        inkDeepColor = Color(0xFF181C18),
        inkDarkColor = Color(0xFF2D332D),
        mediumColor = Color(0xFF8B956D)
    ),
    BACKLIT_CYAN(
        id = "BACKLIT_CYAN",
        displayName = "BACKLIT CYAN",
        backgroundColor = Color(0xFF00E5FF),
        inkDeepColor = Color(0xFF001F24),
        inkDarkColor = Color(0xFF00363A),
        mediumColor = Color(0xFF0097A7)
    );

    companion object {
        fun fromId(id: String?): LcdPalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DMG_OLIVE
        }

        fun next(current: LcdPalette): LcdPalette {
            val list = entries
            val idx = list.indexOf(current)
            return list[(idx + 1) % list.size]
        }

        fun prev(current: LcdPalette): LcdPalette {
            val list = entries
            val idx = list.indexOf(current)
            return list[(idx - 1 + list.size) % list.size]
        }
    }
}

