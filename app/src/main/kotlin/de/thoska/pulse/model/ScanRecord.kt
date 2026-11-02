package de.thoska.pulse.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ScanRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val cardUid: String,
    val balanceEur: Double,
    val lastTransactionEur: Double
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY)
            return sdf.format(Date(timestamp))
        }

    val formattedBalance: String
        get() = String.format("%.2f €", balanceEur)

    val formattedLastTransaction: String
        get() = String.format("%.2f €", lastTransactionEur)
}
