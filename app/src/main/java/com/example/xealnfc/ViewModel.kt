package com.example.xealnfc

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel: ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.NFC_DETECT_START)
    val viewState: StateFlow<ViewState> = _viewState

    private var selectedAmount: Int = 0

     fun startNfcDetection(context: Context?) {
         viewModelScope.launch {
             checkForNfc(context)
         }
    }

    fun onDetectNfcRetry() {
        _viewState.value = ViewState.NFC_RETRY
    }

    fun onClickFor10() {
        _viewState.value = ViewState.SELECT_10
        selectedAmount = AMOUNT_10
    }

    fun onClickFor25() {
        _viewState.value = ViewState.SELECT_25
        selectedAmount = AMOUNT_25
    }

    fun onClickFor50() {
        _viewState.value = ViewState.SELECT_50
        selectedAmount = AMOUNT_50
    }

     private fun checkForNfc(context: Context?){
        val manager: NfcManager = context?.getSystemService(Context.NFC_SERVICE) as NfcManager
        val adapter: NfcAdapter = manager.getDefaultAdapter()

        if (adapter != null && adapter.isEnabled) {
            Handler(Looper.getMainLooper()).postDelayed({
                _viewState.value = ViewState.NFC_DETECT_FAILED
            }, 10000)
            _viewState.value = ViewState.NFC_DISCOVERING

            //Handler().removeCallbacksAndMessages(null)
        } else if (adapter != null && !adapter.isEnabled) {
            _viewState.value = ViewState.NFC_NOT_ENABLED
        } else {
            _viewState.value = ViewState.NFC_NOT_SUPPORTED
        }

         adapter?.let {
         //    val pendingIntent = PendingIntent.getActivity(
         //        context,
         //        0,
         //        context.Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
         //        0
         //    )
         //    it.enableForegroundDispatch(activity, pendingIntent, null, null);
         }
    }

    sealed class ViewState {
        object Empty: ViewState()
        data class Error(val message: String?) : ViewState()
        object Loading : ViewState()
        object SELECT_10 : ViewState()
        object SELECT_25 : ViewState()
        object SELECT_50 : ViewState()
        object NFC_RETRY : ViewState()
        object NFC_DETECT_FAILED : ViewState()
        object NFC_DETECT_START : ViewState()
        object NFC_NOT_SUPPORTED : ViewState()
        object NFC_NOT_ENABLED : ViewState()
        object NFC_DISCOVERING : ViewState()

    }

    companion object {
        val AMOUNT_10 = 10
        val AMOUNT_25 = 25
        val AMOUNT_50 = 50
    }


}