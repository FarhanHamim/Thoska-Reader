package de.thoska.pulse

import de.thoska.pulse.reader.DesfireApduParser
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesfireApduParserTest {

    @Test
    fun `test select AID primary command APDU format`() {
        val expected = byteArrayOf(
            0x90.toByte(), 0x5A.toByte(), 0x00, 0x00, 0x03,
            0x5F.toByte(), 0x84.toByte(), 0x15,
            0x00
        )
        assertArrayEquals(expected, DesfireApduParser.SELECT_AID_PRIMARY)
        assertEquals("90 5A 00 00 03 5F 84 15 00", DesfireApduParser.toSpacedHex(DesfireApduParser.SELECT_AID_PRIMARY))
    }

    @Test
    fun `test select AID fallback command APDU format`() {
        val expected = byteArrayOf(
            0x90.toByte(), 0x5A.toByte(), 0x00, 0x00, 0x03,
            0x15, 0x84.toByte(), 0x5F.toByte(),
            0x00
        )
        assertArrayEquals(expected, DesfireApduParser.SELECT_AID_FALLBACK)
        assertEquals("90 5A 00 00 03 15 84 5F 00", DesfireApduParser.toSpacedHex(DesfireApduParser.SELECT_AID_FALLBACK))
    }

    @Test
    fun `test read balance file 01 command APDU format`() {
        val expected = byteArrayOf(
            0x90.toByte(), 0x6C.toByte(), 0x00, 0x00, 0x01,
            0x01,
            0x00
        )
        assertArrayEquals(expected, DesfireApduParser.READ_BALANCE_FILE_01)
        assertEquals("90 6C 00 00 01 01 00", DesfireApduParser.toSpacedHex(DesfireApduParser.READ_BALANCE_FILE_01))
    }

    @Test
    fun `test read settings file 01 command APDU format`() {
        val expected = byteArrayOf(
            0x90.toByte(), 0xF5.toByte(), 0x00, 0x00, 0x01,
            0x01,
            0x00
        )
        assertArrayEquals(expected, DesfireApduParser.READ_SETTINGS_FILE_01)
        assertEquals("90 F5 00 00 01 01 00", DesfireApduParser.toSpacedHex(DesfireApduParser.READ_SETTINGS_FILE_01))
    }

    @Test
    fun `test parse balance with standard 14,50 EUR card response`() {
        // 14500 in 4-byte little-endian: 0x000038A4 -> [0xA4, 0x38, 0x00, 0x00]
        // Trailing status word: 91 00 (DESFire OPERATION_OK)
        val mockResponse = byteArrayOf(
            0xA4.toByte(), 0x38.toByte(), 0x00, 0x00,
            0x91.toByte(), 0x00
        )

        assertTrue(DesfireApduParser.isSuccessResponse(mockResponse))
        assertEquals("9100", DesfireApduParser.extractStatusWord(mockResponse))

        val balance = DesfireApduParser.parseBalanceEur(mockResponse)
        assertEquals(14.50, balance, 0.001)
    }

    @Test
    fun `test parse balance with 2,50 EUR and ISO 9000 status`() {
        // 2500 in 4-byte little-endian: 0x000009C4 -> [0xC4, 0x09, 0x00, 0x00]
        val mockResponse = byteArrayOf(
            0xC4.toByte(), 0x09.toByte(), 0x00, 0x00,
            0x90.toByte(), 0x00
        )

        assertTrue(DesfireApduParser.isSuccessResponse(mockResponse))
        val balance = DesfireApduParser.parseBalanceEur(mockResponse)
        assertEquals(2.50, balance, 0.001)
    }

    @Test
    fun `test parse balance with 0,00 EUR`() {
        val mockResponse = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x91.toByte(), 0x00)
        val balance = DesfireApduParser.parseBalanceEur(mockResponse)
        assertEquals(0.00, balance, 0.001)
    }

    @Test
    fun `test parse balance with raw 4-byte payload without status word`() {
        // 1000 in little-endian -> 1.00 EUR
        val mockResponse = byteArrayOf(0xE8.toByte(), 0x03, 0x00, 0x00)
        val balance = DesfireApduParser.parseBalanceEur(mockResponse)
        assertEquals(1.00, balance, 0.001)
    }

    @Test(expected = IllegalStateException::class)
    fun `test parse balance throws on error status word`() {
        // Status 91 1C = File Not Found
        val errorResponse = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x91.toByte(), 0x1C.toByte())
        assertFalse(DesfireApduParser.isSuccessResponse(errorResponse))
        DesfireApduParser.parseBalanceEur(errorResponse)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test parse balance throws on truncated response`() {
        val truncated = byteArrayOf(0x01, 0x02)
        DesfireApduParser.parseBalanceEur(truncated)
    }

    @Test
    fun `test parse last transaction from file 01 settings bytes 12 to 15`() {
        // Create standard DESFire Value File settings response (17 bytes payload + 2 bytes status word = 19 bytes)
        // Bytes 0: File Type (0x03 = Value File)
        // Byte 1: Comm settings (0x00 = Plain)
        // Bytes 2..3: Access Rights (0xEE, 0xEE)
        // Bytes 4..7: Lower Limit (0)
        // Bytes 8..11: Upper Limit (1000000)
        // Bytes 12..15: Limited Credit / Last Transaction (3200 = 3.20 EUR -> [0x80, 0x0C, 0x00, 0x00])
        // Byte 16: Limited credit enabled (0x01)
        // Bytes 17..18: Status word 91 00
        val mockSettingsResponse = ByteArray(19)
        mockSettingsResponse[0] = 0x03 // Value File
        mockSettingsResponse[1] = 0x00 // Plain comm
        mockSettingsResponse[2] = 0xEE.toByte()
        mockSettingsResponse[3] = 0xEE.toByte()

        // Bytes 12..15: 3200 little endian (3.20 EUR mensa meal)
        mockSettingsResponse[12] = 0x80.toByte()
        mockSettingsResponse[13] = 0x0C.toByte()
        mockSettingsResponse[14] = 0x00
        mockSettingsResponse[15] = 0x00

        mockSettingsResponse[16] = 0x01 // Enabled
        mockSettingsResponse[17] = 0x91.toByte()
        mockSettingsResponse[18] = 0x00

        assertTrue(DesfireApduParser.isSuccessResponse(mockSettingsResponse))
        val mealCost = DesfireApduParser.parseLastTransactionEur(mockSettingsResponse)
        assertEquals(3.20, mealCost, 0.001)
    }

    @Test
    fun `test parse last transaction exactly 16 bytes payload`() {
        val payload = ByteArray(16)
        // Set bytes 12..15 to 4850 = 4.85 EUR mensa meal
        // 4850 = 0x000012F2 -> [0xF2, 0x12, 0x00, 0x00]
        payload[12] = 0xF2.toByte()
        payload[13] = 0x12.toByte()
        payload[14] = 0x00
        payload[15] = 0x00

        val mealCost = DesfireApduParser.parseLastTransactionEur(payload)
        assertEquals(4.85, mealCost, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test parse last transaction throws on short response`() {
        val shortResponse = ByteArray(14)
        DesfireApduParser.parseLastTransactionEur(shortResponse)
    }

    @Test
    fun `test hex utilities`() {
        val bytes = byteArrayOf(0x04, 0xA2.toByte(), 0x3F.toByte(), 0x15)
        assertEquals("04 A2 3F 15", DesfireApduParser.toSpacedHex(bytes))
        assertEquals("04:A2:3F:15", DesfireApduParser.toColonHex(bytes))

        val parsed = DesfireApduParser.hexToBytes("04 A2 3F 15")
        assertArrayEquals(bytes, parsed)

        val parsedNoSpace = DesfireApduParser.hexToBytes("04A23F15")
        assertArrayEquals(bytes, parsedNoSpace)
    }
}
