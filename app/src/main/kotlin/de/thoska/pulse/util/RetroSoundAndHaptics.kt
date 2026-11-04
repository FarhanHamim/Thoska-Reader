package de.thoska.pulse.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Audio and Haptic feedback controller for the "THOSKA BOY" console.
 * Plays a classic 8-bit 2-tone "coin chime" and triggers physical vibration.
 */
object RetroSoundAndHaptics {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    /**
     * Plays a classic 2-tone retro 8-bit chime (like an arcade coin pickup)
     * and triggers haptic vibration when a card is successfully parsed.
     */
    fun playCoinChime(context: Context) {
        // Haptic feedback
        triggerHaptic(context, durationMs = 60)

        // 2-Tone 8-Bit Chime
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // First tone (B5 / ~987 Hz via TONE_DTMF_B)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_B, 70)
                delay(75)
                // Second high tone (E6 / ~1318 Hz via TONE_DTMF_3)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 140)
            } catch (_: Exception) {
                // Ignore audio failure on restricted devices
            }
        }
    }

    /**
     * Rapid 3-beep low-health tone played when card balance is < 5.00 €.
     */
    fun playLowHealthTone(context: Context) {
        triggerHaptic(context, durationMs = 80)
        CoroutineScope(Dispatchers.Default).launch {
            try {
                repeat(3) {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
                    delay(110)
                }
            } catch (_: Exception) {
                // Ignore audio failure
            }
        }
    }

    /**
     * Tactile button click haptic / short click sound.
     */
    fun playButtonClick(context: Context) {
        triggerHaptic(context, durationMs = 25)
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Exception) {}
    }

    private fun triggerHaptic(context: Context, durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
            // Ignore missing vibration hardware
        }
    }
}
