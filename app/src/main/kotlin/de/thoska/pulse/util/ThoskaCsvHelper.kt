package de.thoska.pulse.util

import de.thoska.pulse.model.ScanRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ThoskaCsvHelper {

    const val CSV_HEADER = "Timestamp,CardUID,BalanceEUR,LastTransactionEUR"

    fun formatHistoryAsCsv(records: List<ScanRecord>): String {
        val sb = StringBuilder()
        sb.append(CSV_HEADER).append("\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        for (r in records) {
            val dateStr = sdf.format(Date(r.timestamp))
            val balStr = String.format(Locale.US, "%.2f", r.balanceEur)
            val txStr = String.format(Locale.US, "%.2f", r.lastTransactionEur)
            sb.append(dateStr).append(",")
                .append(r.cardUid).append(",")
                .append(balStr).append(",")
                .append(txStr).append("\n")
        }
        return sb.toString()
    }
}

