package de.thoska.pulse.data

import android.content.Context
import android.content.SharedPreferences
import de.thoska.pulse.model.ScanRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local persistent repository for storing Thoska Mensa card scan history.
 * Uses persistent SharedPreferences with JSON serialization, exposing a reactive StateFlow.
 */
class ScanHistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("thoska_pulse_history", Context.MODE_PRIVATE)

    private val _historyFlow = MutableStateFlow<List<ScanRecord>>(loadRecords())
    val historyFlow: StateFlow<List<ScanRecord>> = _historyFlow.asStateFlow()

    private fun loadRecords(): List<ScanRecord> {
        val jsonString = prefs.getString(KEY_RECORDS, null) ?: return emptyList()
        val list = mutableListOf<ScanRecord>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ScanRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        cardUid = obj.getString("cardUid"),
                        balanceEur = obj.getDouble("balanceEur"),
                        lastTransactionEur = obj.getDouble("lastTransactionEur")
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore parse errors on corrupt cache
        }
        return list.sortedByDescending { it.timestamp }
    }

    private fun saveRecords(records: List<ScanRecord>) {
        val jsonArray = JSONArray()
        for (r in records) {
            val obj = JSONObject().apply {
                put("id", r.id)
                put("timestamp", r.timestamp)
                put("cardUid", r.cardUid)
                put("balanceEur", r.balanceEur)
                put("lastTransactionEur", r.lastTransactionEur)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_RECORDS, jsonArray.toString()).apply()
        _historyFlow.value = records.sortedByDescending { it.timestamp }
    }

    fun addRecord(record: ScanRecord) {
        val current = _historyFlow.value.toMutableList()
        current.add(0, record)
        saveRecords(current)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_RECORDS).apply()
        _historyFlow.value = emptyList()
    }

    companion object {
        private const val KEY_RECORDS = "key_scan_records"
    }
}
