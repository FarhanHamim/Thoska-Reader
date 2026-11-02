package de.thoska.pulse.model

data class CardProfile(
    val cardUid: String,
    val holderName: String,
    val matrikelNr: String,
    val libraryBarcode: String,
    val validUntil: String
)
