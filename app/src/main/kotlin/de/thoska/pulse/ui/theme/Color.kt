package de.thoska.pulse.ui.theme

import androidx.compose.ui.graphics.Color

// THOSKA BOY 1990s Handheld Console Palette

// 1. Handheld Casing (Thoska Burnt Orange & Bevels)
val ShellOrange = Color(0xFFE05A2B)
val ShellOrangeLight = Color(0xFFF2784B)
val ShellOrangeDark = Color(0xFFB53D15)
val ShellOrangeShadow = Color(0xFF7A2508)

// 2. Bezel (EAH Jena Slate Teal)
val BezelTeal = Color(0xFF005A5B)
val BezelTealLight = Color(0xFF007577)
val BezelTealDark = Color(0xFF003839)
val BezelTealDeep = Color(0xFF002223)

// 3. Vintage Olive-Green Dot-Matrix LCD Palette (High-Contrast Pixel Ink)
val LcdGreenBg = Color(0xFF9BBC0F)       // Olive LCD matrix background
val LcdGreenMedium = Color(0xFF7A9E0A)   // Mid-tone green for HP bar background
val LcdGreenDark = Color(0xFF0E380E)     // Dark pixel green for labels
val LcdGreenDeep = Color(0xFF0A280A)     // Ultra-dark bold pixel ink for maximum contrast

// 4. Tactile Controls & Buttons
val DPadGrey = Color(0xFF3A3D42)
val DPadGreyLight = Color(0xFF555960)
val DPadGreyDark = Color(0xFF222428)

val ButtonRed = Color(0xFFD32F2F)
val ButtonRedLight = Color(0xFFFF5252)
val ButtonRedDark = Color(0xFF8E0000)

val StartSelectGrey = Color(0xFF6B7280)
val StartSelectGreyDark = Color(0xFF374151)

// 5. Casing Text & Logos
val CasingTextDebossed = Color(0xFF1A1C1E) // High-contrast solid dark charcoal for debossed headers
val CasingButtonLabel = Color(0xFF0F1115)  // High-contrast solid black/charcoal for physical button labels
val CasingTextWhite = Color(0xFFFFFFFF)    // High-contrast pure white for casing status
val CasingTealLogo = Color(0xFF005A5B)

// Material 3 Dark theme mapping
val PrimaryDark = LcdGreenDeep
val OnPrimaryDark = LcdGreenBg
val PrimaryContainerDark = LcdGreenMedium
val OnPrimaryContainerDark = LcdGreenDeep

val SecondaryDark = BezelTeal
val OnSecondaryDark = Color.White
val SecondaryContainerDark = BezelTealDark
val OnSecondaryContainerDark = Color.White

val TertiaryDark = ButtonRed
val OnTertiaryDark = Color.White
val TertiaryContainerDark = ButtonRedDark
val OnTertiaryContainerDark = Color.White

val BackgroundDark = ShellOrange
val OnBackgroundDark = Color.White
val SurfaceDark = BezelTeal
val OnSurfaceDark = LcdGreenDeep
val SurfaceVariantDark = BezelTealDark
val OnSurfaceVariantDark = LcdGreenBg
