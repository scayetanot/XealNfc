package com.example.xealnfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.xealnfc.databinding.FragmentDetectNfcBinding
import kotlinx.coroutines.delay
import kotlin.time.measureTimedValue


class DetectNfcFragment: Fragment() {

    private lateinit var binding: FragmentDetectNfcBinding

    private val viewModel: XealViewModel by activityViewModels()

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
    }

    override fun onResume() {
        super.onResume()


     //   activity?.intent?.let {
      //      if(!it.action.equals("android.intent.action.MAIN")) {
        //        viewModel.emptyNfcTagDetected()
        //    }


                //var tagFromIntent: Tag? = it.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                //tagFromIntent?.let {
                //    val nfc = NfcA.get(it)
                //    val atqa: ByteArray = nfc.getAtqa()
                //    val sak: Short = nfc.getSak()
                //    nfc.connect()
                //}
           //}
      //  }
    }

    private fun subscribeToViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    XealViewModel.ViewState.NFC_DETECT_START -> {
                        binding.NfcSearchLayout.isVisible = true
                        binding.NfcRetryLayout.isVisible = false
                    }

                    XealViewModel.ViewState.NFC_DETECT_FAILED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                    }

                    XealViewModel.ViewState.NFC_NOT_SUPPORTED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                        Toast.makeText(requireContext(), getString(R.string.not_supported), Toast.LENGTH_LONG)
                            .show()
                    }

                    XealViewModel.ViewState.NFC_NOT_ENABLED -> {
                        binding.NfcSearchLayout.isVisible = false
                        binding.NfcRetryLayout.isVisible = true
                        Toast.makeText(requireContext(), getString(R.string.not_enable), Toast.LENGTH_LONG)
                            .show()
                    }

                    XealViewModel.ViewState.NFC_RETRY -> {
                        binding.NfcSearchLayout.isVisible = true
                        binding.NfcRetryLayout.isVisible = false
                        delay(1000)
                        viewModel.startNfcDetection(requireContext())
                    }

                    XealViewModel.ViewState.NavigateToHomePage -> {
                        val action = DetectNfcFragmentDirections.actionDetectNfcFragmentToHomeFragment(
                            "", ""
                        )
                        findNavController().navigate(action)
                    }
                    else -> Unit
                }
            }
        }
    }
}