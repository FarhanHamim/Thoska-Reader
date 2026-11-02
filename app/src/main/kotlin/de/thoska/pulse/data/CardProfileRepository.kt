package de.thoska.pulse.data

import android.content.Context
import android.content.SharedPreferences
import de.thoska.pulse.model.CardProfile
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository managing student identity profiles bound to physical card hardware UIDs.
 * Starts completely blank (zero pre-seeded personal data).
 */
class CardProfileRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("thoska_card_profiles_v2", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROFILES = "key_profiles_json"

        fun normalizeUid(uid: String): String {
            return uid.replace(":", "").replace(" ", "").uppercase()
        }
    }

    /**
     * Retrieves all saved card profiles. Returns empty list if no card has been registered.
     */
    fun getAllProfiles(): List<CardProfile> {
        val jsonString = prefs.getString(KEY_PROFILES, null) ?: return emptyList()
        val list = mutableListOf<CardProfile>()
        try {
            val arr = JSONArray(jsonString)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    CardProfile(
                        cardUid = obj.getString("cardUid"),
                        holderName = obj.getString("holderName"),
                        matrikelNr = obj.getString("matrikelNr"),
                        libraryBarcode = obj.getString("libraryBarcode"),
                        validUntil = obj.getString("validUntil")
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    /**
     * Resolves a card profile dynamically by hardware UID.
     * Returns null if the card has never been registered before.
     */
    fun getProfileByUid(uid: String): CardProfile? {
        val normTarget = normalizeUid(uid)
        return getAllProfiles().firstOrNull { normalizeUid(it.cardUid) == normTarget }
    }

    /**
     * Saves or updates a student profile bound to the specified card UID.
     */
    fun saveProfile(profile: CardProfile) {
        val current = getAllProfiles().toMutableList()
        val normNew = normalizeUid(profile.cardUid)
        current.removeAll { normalizeUid(it.cardUid) == normNew }
        current.add(0, profile)

        val arr = JSONArray()
        for (p in current) {
            val obj = JSONObject().apply {
                put("cardUid", p.cardUid)
                put("holderName", p.holderName)
                put("matrikelNr", p.matrikelNr)
                put("libraryBarcode", p.libraryBarcode)
                put("validUntil", p.validUntil)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_PROFILES, arr.toString()).apply()
    }

    /**
     * Clears all saved profiles from storage.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
