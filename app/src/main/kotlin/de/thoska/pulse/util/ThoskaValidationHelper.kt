package de.thoska.pulse.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object ThoskaValidationHelper {

    enum class ExpiryStatus {
        VALID,
        EXPIRING_SOON, // Within 14 days
        EXPIRED,
        UNKNOWN
    }

    private val DATE_PATTERN = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{2,4})")

    /**
     * Parses the trailing end date in validUntil (format dd.MM.yy or dd.MM.yyyy, e.g. "31.03.27" or "01.10.26 - 31.03.27")
     * and evaluates whether the card is expired, expiring within 14 days, or valid.
     */
    fun checkValidationExpiry(validUntil: String, referenceDate: Date = Date()): ExpiryStatus {
        if (validUntil.isBlank()) return ExpiryStatus.UNKNOWN

        val matcher = DATE_PATTERN.matcher(validUntil)
        var lastDateStr: String? = null
        while (matcher.find()) {
            lastDateStr = matcher.group()
        }

        if (lastDateStr == null) return ExpiryStatus.UNKNOWN

        return try {
            val formatStr = if (lastDateStr.length > 8) "dd.MM.yyyy" else "dd.MM.yy"
            val sdf = SimpleDateFormat(formatStr, Locale.GERMANY)
            sdf.isLenient = false
            val parsed = sdf.parse(lastDateStr) ?: return ExpiryStatus.UNKNOWN

            // Consider the expiry effective at the end of the day (23:59:59.999)
            val expiryTime = parsed.time + TimeUnit.DAYS.toMillis(1) - 1
            val diffMillis = expiryTime - referenceDate.time

            when {
                diffMillis < 0 -> ExpiryStatus.EXPIRED
                diffMillis <= TimeUnit.DAYS.toMillis(14) -> ExpiryStatus.EXPIRING_SOON
                else -> ExpiryStatus.VALID
            }
        } catch (_: Exception) {
            ExpiryStatus.UNKNOWN
        }
    }

    fun isLowBalance(balanceEur: Double): Boolean {
        return balanceEur < 5.00
    }
}

