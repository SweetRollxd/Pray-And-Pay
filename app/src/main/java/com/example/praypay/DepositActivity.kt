package com.example.praypay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class DepositActivity : AppCompatActivity() {
    private  lateinit var edAmount: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        edAmount = findViewById(R.id.edAmount)
    }

    fun sendDepositClickListener(view: View) {
        val queue = Volley.newRequestQueue(this)

        val url = "${Constants.API_SERVER}/users/${Constants.USER_ID}/deposit"
        val data = JSONObject()
        if (edAmount.text.toString() != "")
            data.put("amount", edAmount.text.toString().toDouble())
        val req = JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            {response->
                Log.d(Constants.LOG_TAG, "Response from API: ${response.toString()}")
                setResult(RESULT_OK)
                finish()
            },
            {err->
                try {
                    val msg = JSONObject(String(err.networkResponse.data)).getString("msg")
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
                }

                Log.e(Constants.LOG_TAG, "${err.toString()}. Response code ${err.networkResponse.statusCode}. Message: ${String(err.networkResponse.data)}")
            }
        )
        queue.add(req)
    }
}