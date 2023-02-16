package com.example.xealnfc

import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.xealnfc.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: XealViewModel by viewModels()

    private val inWriteMode = false

    private val mNdef: Ndef? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startNfcDetection(this)
    }
    override fun onResume() {
        super.onResume()
        viewModel.enableForegroundDispatch(this, this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.disableForegroundDispatch(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (inWriteMode
            && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent?.action)) {
        //    viewModel.writeToTag(getIntent());
        }
        else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent?.action)) {
            viewModel.readFromTag();
        } else {
            viewModel.emptyNfcTagDetected()
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))

        tag?.let {
            viewModel.saveNfcTag(tag)
            viewModel.readFromTag()
        }
    }
}