package de.thoska.pulse

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.thoska.pulse.ui.screens.DashboardScreen
import de.thoska.pulse.ui.theme.ThoskaPulseTheme
import de.thoska.pulse.ui.viewmodel.ThoskaViewModel

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private val viewModel: ThoskaViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            ThoskaPulseTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }

        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateNfcStatus()

        // Modern Reader Mode (NFC-A / ISO 14443-4, skip NDEF completely)
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val flags = NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                adapter.enableReaderMode(this, this, flags, null)
            } else {
                enableForegroundDispatch()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
        disableForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * ReaderCallback invoked on background thread when an NFC ISO 14443-4 Type A card is tapped.
     */
    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null) return
        val isoDep = IsoDep.get(tag) ?: return
        viewModel.onIsoDepDiscovered(isoDep)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.let {
                val isoDep = IsoDep.get(it)
                if (isoDep != null) {
                    viewModel.onIsoDepDiscovered(isoDep)
                }
            }
        }
    }

    private fun enableForegroundDispatch() {
        val adapter = nfcAdapter ?: return
        if (!adapter.isEnabled) return
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
        )
        val filters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
        val techList = arrayOf(
            arrayOf(IsoDep::class.java.name)
        )
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }
}
