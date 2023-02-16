package com.example.xealnfc

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException


class XealViewModel: ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.NFC_DETECT_START)
    val viewState: StateFlow<ViewState> = _viewState

    private var selectedAmount: Int = 0
    var userData: User = User("", 0.0, 0.0)


    private var nfcAdapter: NfcAdapter? = null
    private var nfcTag: Tag? = null

    fun resetData(){
        userData.name = ""
        userData.remainingAmount = 0.0
        userData.previousAmount = 0.0
    }
    fun startNfcDetection(activity: Activity) {
         viewModelScope.launch {
             checkForNfc(activity)
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
        userData.previousAmount = userData.remainingAmount
        userData.remainingAmount = userData.remainingAmount?.plus(selectedAmount)
        if(createNFCMessage()) {
            onPayNowSucceed()
        } else {
            userData.remainingAmount = userData.previousAmount
            onPayNowFailed()
        }
    }

    fun onPayNowSucceed() {
        _viewState.value = ViewState.AddingAmountSuccess(selectedAmount)
    }

    fun onPayNowFailed() {
        _viewState.value = ViewState.AddingAmountError
    }

    fun setUserData(name: String, currentAmount: Double = 0.0) {
        userData.name = name
        userData.remainingAmount = currentAmount
    }
    fun saveNfcTag(tag: Tag?) {
        nfcTag = tag
    }

    fun getNfcTag() = nfcTag

    fun createNFCMessage() : Boolean {
        var gson = Gson()
        var jsonString = gson.toJson(userData)
        var payload = jsonString.toString()
        val pathPrefix = "xeal.com:nfcxealtag"
        val nfcRecord = NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.toByteArray(), ByteArray(0), payload.toByteArray())
        val nfcMessage = NdefMessage(arrayOf(nfcRecord))
        nfcTag?.let {
            return  writeMessageToTag(nfcMessage, nfcTag)
        }
        return false
    }

    private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?): Boolean {
        try {
            val nDefTag = Ndef.get(tag)

            nDefTag?.let {
                it.connect()
                if (it.maxSize < nfcMessage.toByteArray().size) {
                    //Message too large to write to NFC tag
                    return false
                }
                if (it.isWritable) {
                    it.writeNdefMessage(nfcMessage)
                    it.close()
                    //Message is written to tag
                    return true
                } else {
                    return false
                }
            }

            val nDefFormatableTag = NdefFormatable.get(tag)

            nDefFormatableTag?.let {
                try {
                    it.connect()
                    it.format(nfcMessage)
                    it.close()
                    //The data is written to the tag
                    return true
                } catch (e: IOException) {
                    //Failed to format tag
                    return false
                }
            }
            //NDEF is not supported
            return false

        } catch (e: Exception) {
            Log.e(XealViewModel.javaClass.simpleName, "Error: " + e.message)
            //Write operation has failed
        }
        return false
    }

    fun readFromTag() {
        try {
            val nDefTag = Ndef.get(nfcTag)
            nDefTag?.let {
                it.connect()
                val nDefMessage = it.ndefMessage
                if(nDefMessage == null) {
                    _viewState.value= ViewState.NFC_TAG_READ
                } else {
                    val json = JSONObject(nDefMessage.records[0].payload.decodeToString())
                    userData.name = json.getString("name").toString()
                    userData.remainingAmount= json.getDouble("remainingAmount")
                    userData.remainingAmount= json.optDouble("previousAmount", 0.0)
                    _viewState.value= ViewState.NFC_TAG_READ
                }
            }
        } catch (e: Exception) {
            Log.e(XealViewModel.javaClass.simpleName, "Error: " + e.message)
        }


    }

    fun enableForegroundDispatch(activity: Activity, callback: NfcAdapter.ReaderCallback) {
        nfcAdapter?.let {
            val options = Bundle()
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            // Enable ReaderMode for all types of card and disable platform sounds
            it.enableReaderMode(
                activity,
                callback,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    fun disableForegroundDispatch(activity: Activity) {
        //nfcAdapter?.disableForegroundDispatch(activity)
        nfcAdapter?.let {
            it.disableReaderMode(activity)
        }
    }

    fun emptyNfcTagDetected() {
        _viewState.value = ViewState.EMPTY_NFC_TAG
    }

     private fun checkForNfc(activity: Activity){
         nfcAdapter = NfcAdapter.getDefaultAdapter(activity)?.let { it }
         nfcAdapter?.let {
             when {
                 it.isEnabled -> {}
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
        data class AddingAmountSuccess(val amout: Int): ViewState()
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
        object NFC_TAG_READ_ONLY : ViewState()
        object NFC_TAG_MESSAGE_ERROR : ViewState()
        object EMPTY_NFC_TAG : ViewState()
        object NFC_TAG_READ : ViewState()

    }

    companion object {
        val AMOUNT_10 = 10
        val AMOUNT_25 = 25
        val AMOUNT_50 = 50
    }

}