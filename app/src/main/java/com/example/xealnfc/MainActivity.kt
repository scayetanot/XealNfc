package com.example.xealnfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.xealnfc.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: XealViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startNfcDetection(this.applicationContext)
    }
    override fun onResume() {
        super.onResume()
        viewModel.enableForegroundDispatch(this)
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
        //intent?.action is null on empoty tag
        viewModel.emptyNfcTagDetected()
    }
}