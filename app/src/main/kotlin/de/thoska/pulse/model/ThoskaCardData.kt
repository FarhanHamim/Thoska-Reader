package de.thoska.pulse.model

data class ThoskaCardData(
    val cardUid: String,
    val balanceEur: Double,
    val lastTransactionEur: Double,
    val selectedAid: String,
    val timestamp: Long = System.currentTimeMillis(),
    val apduLogs: List<ApduLogEntry> = emptyList(),
    val profile: CardProfile? = null
) {
    val formattedBalance: String
        get() = String.format("%.2f €", balanceEur).replace('.', ',')

    val formattedLastTransaction: String
        get() = String.format("%.2f €", lastTransactionEur).replace('.', ',')

    val isRecognizedProfile: Boolean
        get() = profile != null
}
