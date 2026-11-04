package de.thoska.pulse.model

import java.util.Locale

enum class MensaBudgetPreset(
    val id: String,
    val title: String,
    val shortName: String,
    val priceEur: Double
) {
    COFFEE(
        id = "COFFEE",
        title = "COFFEE / ESPRESSO (1.40 €)",
        shortName = "COFFEE",
        priceEur = 1.40
    ),
    WASHING(
        id = "WASHING",
        title = "WASHING MACHINE (2.00 €)",
        shortName = "WASHING",
        priceEur = 2.00
    ),
    CLASSIC(
        id = "CLASSIC",
        title = "CLASSIC MENSA DISH (2.60 €)",
        shortName = "MENSA DISH",
        priceEur = 2.60
    ),
    PREMIUM(
        id = "PREMIUM",
        title = "PREMIUM MENSA MEAL (3.80 €)",
        shortName = "PREMIUM MEAL",
        priceEur = 3.80
    );

    fun calculateAffordance(balanceEur: Double): Int {
        if (balanceEur < priceEur || priceEur <= 0.0) return 0
        return (balanceEur / priceEur).toInt()
    }

    fun formattedAffords(balanceEur: Double): String {
        val units = calculateAffordance(balanceEur)
        val priceStr = String.format(Locale.GERMANY, "%.2f", priceEur)
        return ">> AFFORDS: ${units}x $shortName (AT $priceStr €) <<"
    }

    companion object {
        fun next(current: MensaBudgetPreset): MensaBudgetPreset {
            val list = entries
            val idx = list.indexOf(current)
            return list[(idx + 1) % list.size]
        }

        fun prev(current: MensaBudgetPreset): MensaBudgetPreset {
            val list = entries
            val idx = list.indexOf(current)
            return list[(idx - 1 + list.size) % list.size]
        }
    }
}

