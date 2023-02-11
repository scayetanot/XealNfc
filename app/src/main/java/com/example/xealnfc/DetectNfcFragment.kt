package com.example.xealnfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.xealnfc.databinding.FragmentDetectNfcBinding
import kotlinx.coroutines.delay


class DetectNfcFragment: Fragment() {

    private lateinit var binding: FragmentDetectNfcBinding

    private val viewModel: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectNfcBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewState()
    }

    override fun onStart() {
        super.onStart()
     //   activity?.let {
     //       var tagFromIntent: Tag? = it.intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
     //       val n = NfcA.get(tagFromIntent)
     //   }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startNfcDetection(requireContext())
        activity?.intent?.let { receiveMessageFromDevice(it) }
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            with(parcelables) {
                val inNdefMessage = this?.get(0) as NdefMessage
                val inNdefRecords = inNdefMessage.records
                val ndefRecord_0 = inNdefRecords[0]

                val inMessage = String(ndefRecord_0.payload)
           //     tvIncomingMessage?.text = inMessage
            }
        }
    }

    private fun subscribeToViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    ViewModel.ViewState.NFC_DETECT_START -> {
                        binding.NfcSearchLayout.isVisible = true
                        binding.NfcRetryLayout.isVisible = false
                        //checkForNfc()
                    }

                    ViewModel.ViewState.NFC_DETECT_FAILED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                    }

                    ViewModel.ViewState.NFC_NOT_SUPPORTED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                        Toast.makeText(requireContext(), getString(R.string.not_supported), Toast.LENGTH_LONG)
                            .show()
                    }

                    ViewModel.ViewState.NFC_DISCOVERING -> {
                        activity?.let {
                            val nfcAdapter = NfcAdapter.getDefaultAdapter(it)

                            if (nfcAdapter != null && nfcAdapter.isEnabled) {
                                val pendingIntent = PendingIntent.getActivity(
                                    it, 0,
                                    Intent(it, it.javaClass)
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
                                )

                                val filters = arrayOfNulls<IntentFilter>(1)
                                val techList = arrayOf<Array<String>>()

                                filters[0] = IntentFilter()
                                with(filters[0]) {
                                    this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
                                    this?.addCategory(Intent.CATEGORY_DEFAULT)
                                    try {
                                        this?.addDataType("text/plain")
                                    } catch (ex: IntentFilter.MalformedMimeTypeException) {
                                        throw RuntimeException("Check your MIME type")
                                    }
                                }

                                nfcAdapter.enableForegroundDispatch(
                                    activity, pendingIntent,
                                    filters, null
                                )
                                //mShouldDisableForegroundDispatch = true
                                //handleNfcIntent(activity!!.intent)
                            }
                        }
                    }

                    ViewModel.ViewState.NFC_NOT_ENABLED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                        Toast.makeText(requireContext(), getString(R.string.not_enable), Toast.LENGTH_LONG)
                            .show()
                    }

                    ViewModel.ViewState.NFC_RETRY -> {
                        binding.NfcSearchLayout.isVisible = true
                        binding.NfcRetryLayout.isVisible = false
                        delay(1000)
                        viewModel.startNfcDetection(requireContext())
                    }

                    else -> Unit
                }
            }
        }
    }

   // private val isNfcSupported: Boolean =
       // this.nfcAdapter != null





}