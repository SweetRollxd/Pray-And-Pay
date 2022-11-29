package com.example.praypay

import android.app.PendingIntent
import android.content.ClipDescription
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject

class NfcReaderActivity : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_reader)
        Log.d("API Test", "Creating new activity")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null){
                Log.d(Constants.LOG_TAG, "Data from activity: ${result.data?.getStringExtra("name")}")
                setResult(RESULT_OK)
                finish()
            }
            else {
                Log.e(Constants.LOG_TAG, "Error from activity result: ${result.resultCode}")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("API Test", "Called onResume!")
        enableForegroundDispatch(this, this.nfcAdapter)
//        processIntent(intent)
        receiveNfcMessage(intent)
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch(this, this.nfcAdapter)
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("API Test", "Intent caught!")
        super.onNewIntent(intent)
        receiveNfcMessage(intent)
    }

    private fun receiveNfcMessage(intent: Intent?){
        val action = intent?.action
        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            with(parcelables) {
                val inNdefMessage = this?.get(0) as NdefMessage
                val inNdefRecords = inNdefMessage.records
                val ndefRecord_0 = inNdefRecords[0]
                Log.d("API Test", "Message: ${ndefRecord_0}")

                val i = Intent(this@NfcReaderActivity, BuyProductActivity::class.java)
                val productId = JSONObject(String(ndefRecord_0.payload)).getInt("product_id")
                Log.d(Constants.LOG_TAG, "Product ID: $productId")
                i.putExtra("product_id", productId)
                addProductLauncher.launch(i)

            }
        }
    }


    private fun enableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter){
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        filters[0] = IntentFilter()
        with(filters[0]){
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw java.lang.RuntimeException("Check your MIME type")
            }
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    private fun disableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter){
        adapter.disableForegroundDispatch(activity)
    }

}