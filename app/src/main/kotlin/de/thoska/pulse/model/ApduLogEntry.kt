package de.thoska.pulse.model

data class ApduLogEntry(
    val step: String,
    val commandHex: String,
    val responseHex: String,
    val statusWord: String,
    val isSuccess: Boolean,
    val note: String = ""
)
