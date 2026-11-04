package de.thoska.pulse

import de.thoska.pulse.model.LcdPalette
import de.thoska.pulse.model.MensaBudgetPreset
import de.thoska.pulse.model.ScanRecord
import de.thoska.pulse.model.ThuringiaUniversity
import de.thoska.pulse.util.ThoskaCsvHelper
import de.thoska.pulse.util.ThoskaValidationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ThoskaExpansionFeaturesTest {

    @Test
    fun `test all 10 Thuringian Universities exist with expected metadata`() {
        assertEquals(10, ThuringiaUniversity.entries.size)

        val eah = ThuringiaUniversity.fromId("EAH_JENA")
        assertEquals("EAH JENA", eah.shortName)
        assertEquals("Ernst-Abbe-Hochschule Jena", eah.fullName)
        assertEquals(0xFF005A5BL, eah.primaryColorHex)

        val fsu = ThuringiaUniversity.fromId("FSU_JENA")
        assertEquals("FSU JENA", fsu.shortName)
        assertEquals(0xFF002F5DL, fsu.primaryColorHex)

        val ilmenau = ThuringiaUniversity.fromId("tu_ilmenau")
        assertEquals("TU ILMENAU", ilmenau.shortName)
        assertEquals(0xFF006633L, ilmenau.primaryColorHex)

        val bauhaus = ThuringiaUniversity.fromId("BAUHAUS_WEIMAR")
        assertEquals("BAUHAUS WEIMAR", bauhaus.shortName)
        assertEquals(0xFFE3000BL, bauhaus.primaryColorHex)

        val uniErfurt = ThuringiaUniversity.fromId("UNI_ERFURT")
        assertEquals("UNI ERFURT", uniErfurt.shortName)
        assertEquals(0xFFA51E37L, uniErfurt.primaryColorHex)

        val fhErfurt = ThuringiaUniversity.fromId("FH_ERFURT")
        assertEquals("FH ERFURT", fhErfurt.shortName)
        assertEquals(0xFF00854AL, fhErfurt.primaryColorHex)

        val schmalkalden = ThuringiaUniversity.fromId("HS_SCHMALKALDEN")
        assertEquals("HS SCHMALKALDEN", schmalkalden.shortName)
        assertEquals(0xFF005478L, schmalkalden.primaryColorHex)

        val nordhausen = ThuringiaUniversity.fromId("HS_NORDHAUSEN")
        assertEquals("HS NORDHAUSEN", nordhausen.shortName)
        assertEquals(0xFF004B93L, nordhausen.primaryColorHex)

        val hfm = ThuringiaUniversity.fromId("HFM_WEIMAR")
        assertEquals("HFM WEIMAR", hfm.shortName)
        assertEquals(0xFF8B0000L, hfm.primaryColorHex)

        val dhge = ThuringiaUniversity.fromId("DHGE")
        assertEquals("DHGE", dhge.shortName)
        assertEquals(0xFF003366L, dhge.primaryColorHex)

        // Unknown id fallback
        assertEquals(ThuringiaUniversity.EAH_JENA, ThuringiaUniversity.fromId("UNKNOWN_UNI"))
        assertEquals(ThuringiaUniversity.EAH_JENA, ThuringiaUniversity.fromId(null))
    }

    @Test
    fun `test Mensa Budget Simulator affordance math and cycling`() {
        val balance = 40.40

        // 40.40 / 1.40 = 28.857 -> 28 units
        assertEquals(28, MensaBudgetPreset.COFFEE.calculateAffordance(balance))
        assertEquals(">> AFFORDS: 28x COFFEE (AT 1,40 €) <<", MensaBudgetPreset.COFFEE.formattedAffords(balance))

        // 40.40 / 2.00 = 20 units
        assertEquals(20, MensaBudgetPreset.WASHING.calculateAffordance(balance))
        assertEquals(">> AFFORDS: 20x WASHING (AT 2,00 €) <<", MensaBudgetPreset.WASHING.formattedAffords(balance))

        // 40.40 / 2.60 = 15.538 -> 15 units
        assertEquals(15, MensaBudgetPreset.CLASSIC.calculateAffordance(balance))
        assertEquals(">> AFFORDS: 15x MENSA DISH (AT 2,60 €) <<", MensaBudgetPreset.CLASSIC.formattedAffords(balance))

        // 40.40 / 3.80 = 10.63 -> 10 units
        assertEquals(10, MensaBudgetPreset.PREMIUM.calculateAffordance(balance))
        assertEquals(">> AFFORDS: 10x PREMIUM MEAL (AT 3,80 €) <<", MensaBudgetPreset.PREMIUM.formattedAffords(balance))

        // Zero or small balance
        assertEquals(0, MensaBudgetPreset.COFFEE.calculateAffordance(0.50))

        // Cycling next/prev
        assertEquals(MensaBudgetPreset.WASHING, MensaBudgetPreset.next(MensaBudgetPreset.COFFEE))
        assertEquals(MensaBudgetPreset.CLASSIC, MensaBudgetPreset.next(MensaBudgetPreset.WASHING))
        assertEquals(MensaBudgetPreset.PREMIUM, MensaBudgetPreset.next(MensaBudgetPreset.CLASSIC))
        assertEquals(MensaBudgetPreset.COFFEE, MensaBudgetPreset.next(MensaBudgetPreset.PREMIUM))

        assertEquals(MensaBudgetPreset.PREMIUM, MensaBudgetPreset.prev(MensaBudgetPreset.COFFEE))
        assertEquals(MensaBudgetPreset.CLASSIC, MensaBudgetPreset.prev(MensaBudgetPreset.PREMIUM))
    }

    @Test
    fun `test LCD Palette switcher cycling and colors`() {
        assertEquals(3, LcdPalette.entries.size)
        assertEquals("DMG OLIVE", LcdPalette.DMG_OLIVE.displayName)
        assertEquals("POCKET SILVER", LcdPalette.POCKET_SILVER.displayName)
        assertEquals("BACKLIT CYAN", LcdPalette.BACKLIT_CYAN.displayName)

        assertEquals(LcdPalette.POCKET_SILVER, LcdPalette.next(LcdPalette.DMG_OLIVE))
        assertEquals(LcdPalette.BACKLIT_CYAN, LcdPalette.next(LcdPalette.POCKET_SILVER))
        assertEquals(LcdPalette.DMG_OLIVE, LcdPalette.next(LcdPalette.BACKLIT_CYAN))

        assertEquals(LcdPalette.BACKLIT_CYAN, LcdPalette.prev(LcdPalette.DMG_OLIVE))
        assertEquals(LcdPalette.POCKET_SILVER, LcdPalette.prev(LcdPalette.BACKLIT_CYAN))

        assertEquals(LcdPalette.DMG_OLIVE, LcdPalette.fromId("UNKNOWN"))
    }

    @Test
    fun `test low balance warning detection`() {
        assertTrue(ThoskaValidationHelper.isLowBalance(0.00))
        assertTrue(ThoskaValidationHelper.isLowBalance(1.40))
        assertTrue(ThoskaValidationHelper.isLowBalance(4.99))
        assertFalse(ThoskaValidationHelper.isLowBalance(5.00))
        assertFalse(ThoskaValidationHelper.isLowBalance(40.40))
    }

    @Test
    fun `test validation expiry parsing and status evaluation`() {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        val fixedRefDate = sdf.parse("15.03.2027")!!

        // Expired date
        val expired = ThoskaValidationHelper.checkValidationExpiry("01.10.2025 - 31.03.2026", referenceDate = fixedRefDate)
        assertEquals(ThoskaValidationHelper.ExpiryStatus.EXPIRED, expired)

        // Expiring within 14 days (31.03.2027 is 16 days after 15.03.2027, but 25.03.2027 is 10 days)
        val expiringSoon = ThoskaValidationHelper.checkValidationExpiry("01.10.26 - 25.03.27", referenceDate = fixedRefDate)
        assertEquals(ThoskaValidationHelper.ExpiryStatus.EXPIRING_SOON, expiringSoon)

        // Valid future date
        val valid = ThoskaValidationHelper.checkValidationExpiry("01.10.26 - 30.09.27", referenceDate = fixedRefDate)
        assertEquals(ThoskaValidationHelper.ExpiryStatus.VALID, valid)

        // Invalid string
        val unknown = ThoskaValidationHelper.checkValidationExpiry("INVALID_DATE_STRING", referenceDate = fixedRefDate)
        assertEquals(ThoskaValidationHelper.ExpiryStatus.UNKNOWN, unknown)
    }

    @Test
    fun `test offline CSV export formatting`() {
        val records = listOf(
            ScanRecord(
                timestamp = 1730746800000L, // 2024-11-04 19:00:00 UTC approx
                cardUid = "04:31:56:CA:6C:24:90",
                balanceEur = 40.40,
                lastTransactionEur = 1.40
            ),
            ScanRecord(
                timestamp = 1730743200000L,
                cardUid = "04:99:AA:BB:CC:DD:EE",
                balanceEur = 12.50,
                lastTransactionEur = 2.80
            )
        )

        val csv = ThoskaCsvHelper.formatHistoryAsCsv(records)
        val lines = csv.trim().lines()

        assertEquals(3, lines.size)
        assertEquals("Timestamp,CardUID,BalanceEUR,LastTransactionEUR", lines[0])
        assertTrue(lines[1].contains("04:31:56:CA:6C:24:90,40.40,1.40"))
        assertTrue(lines[2].contains("04:99:AA:BB:CC:DD:EE,12.50,2.80"))
    }
}

