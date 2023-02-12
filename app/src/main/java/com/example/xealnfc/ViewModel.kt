package com.example.xealnfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

    private var nfcAdapter: NfcAdapter? = null

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

    fun onPayNowClick() {
        _viewState.value = ViewState.Loading
        Handler(Looper.getMainLooper()).postDelayed({
            onPayNowSucceed()
        }, 5000)
    }

    fun onPayNowSucceed() {
        _viewState.value = ViewState.AddingAmountSuccess(selectedAmount.toString())
    }

    fun onPayNowFailed() {
        _viewState.value = ViewState.AddingAmountError
    }

    fun enableForegroundDispatch(activity: Activity) {
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()
        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addAction(NfcAdapter.ACTION_TAG_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType("text/plain")
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException(ex)
            }
        }
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    fun emptyNfcTagDetected() {
        Handler().removeCallbacksAndMessages(null)
        _viewState.value = ViewState.NavigateToHomePage
    }

     private fun checkForNfc(context: Context?){
         nfcAdapter = NfcAdapter.getDefaultAdapter(context)?.let { it }
         nfcAdapter?.let {
             when {
                 it.isEnabled -> {
                     Handler(Looper.getMainLooper()).postDelayed({
                         _viewState.value = ViewState.NFC_DETECT_FAILED
                     }, 10000)
                 }
                 else -> {
                     _viewState.value = ViewState.NFC_NOT_ENABLED
                 }
             }
         } ?: run {
             _viewState.value = ViewState.NFC_NOT_SUPPORTED
         }
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class AddingAmountSuccess(val amout: String): ViewState()
        object AddingAmountError: ViewState()
        object NavigateToHomePage: ViewState()
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