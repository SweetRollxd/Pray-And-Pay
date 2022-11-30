package com.example.praypay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.praypay.databinding.ActivityBuyProductBinding
import org.json.JSONException
import org.json.JSONObject

class BuyProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyProductBinding
    private var productId: Int? = null
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)
        productId = intent.getIntExtra("product_id", 0)
        if (productId == 0) {
            setResult(RESULT_CANCELED)
            finish()
        }
        val url = Constants.API_SERVER + "/products/$productId"

        val req = JsonObjectRequest(Request.Method.GET,
            url,
            null,
            {response->
                Log.d(Constants.LOG_TAG, "Response from API: ${response.toString()}")
                val productName = response.getString("description")
                val price = response.getString("price") + " руб."
                binding.tvName.text = productName
                binding.tvPrice.text = price
                val params = response.getJSONObject("params")
                for (key in params.keys()){
                    val param_string = "${Constants.PARAMS[key]}: ${params[key]}\n"
                    binding.tvParams.append(param_string)
                }
//                finish()
            },
            {err->
                try {
                    val msg = JSONObject(String(err.networkResponse.data)).getString("msg")
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
                }
                setResult(RESULT_CANCELED)
                finish()
            }
        )
        queue.add(req)
    }

    fun onAddClickListener(view: View){
        val url = Constants.API_SERVER + "/users/${Constants.USER_ID}/cart"
//        Log.d(Constants.LOG_TAG, "URL: $url")
        val data = JSONObject()
        data.put("product_id", productId)
        data.put("quantity", 1)
        val req = JsonObjectRequest(Request.Method.POST,
            url,
            data,
            {response->
                Log.d(Constants.LOG_TAG, "Response from API: ${response.toString()}")
                val productName = response.getString("description")
                val i = Intent()
//                Log.d(Constants.LOG_TAG, "Product name: $productName")
                i.putExtra("name", productName)
                setResult(RESULT_OK, i)
                finish()
            },
            {err->
                Log.e(Constants.LOG_TAG, err.toString())
                setResult(RESULT_CANCELED)
                finish()
            }
        )
        queue.add(req)

    }
}