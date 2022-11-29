package com.example.praypay

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.praypay.databinding.FragmentCartBinding
import com.example.praypay.databinding.FragmentScannerBinding
import com.example.praypay.product.Product
import com.example.praypay.product.ProductAdapter
import org.json.JSONException
import org.json.JSONObject

class Cart : Fragment() {

    companion object {
        fun newInstance() = Cart()
    }

    private lateinit var viewModel: CartViewModel

    private lateinit var binding: FragmentCartBinding
    private val productAdapter = ProductAdapter()
//    private lateinit var queue: RequestQueue


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)

        init()
        val queue = Volley.newRequestQueue(requireActivity())

        binding.btnPay.setOnClickListener {
            val url = "${Constants.API_SERVER}/users/${Constants.USER_ID}/purchases"
            val req = JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                {response->
                    Log.d(Constants.LOG_TAG, "Response from API: ${response.toString()}")
                    productAdapter.clear()
                    binding.tvTotal.text = ""
                    binding.btnPay.visibility = View.GONE
                },
                {err->
                    try {
                        val msg = JSONObject(String(err.networkResponse.data)).getString("msg")
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        Toast.makeText(requireActivity(), "Unknown error", Toast.LENGTH_SHORT).show()
                    }

                    Log.e(Constants.LOG_TAG, "${err.toString()}. Response code ${err.networkResponse.statusCode}. Message: ${String(err.networkResponse.data)}")
                }
            )
            queue.add(req)
        }

        val url = "${Constants.API_SERVER}/users/${Constants.USER_ID}/cart"
        val req = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {responseArray->
                Log.d(Constants.LOG_TAG, "Response from API: ${responseArray.toString()}")
                if (responseArray.length() == 0)
                    binding.btnPay.visibility = View.GONE
                else {
                    var total = 0.0
                    for (i in 0 until responseArray.length()) {
                        val item = responseArray.getJSONObject(i)
                        val price = item.getDouble(Constants.PRODUCT_PRICE).toFloat()
                        val cnt = item.getInt(Constants.PRODUCT_QUANTITY)
                        productAdapter.addProduct(product = Product(item.getString(Constants.PRODUCT_TITLE), price, cnt))
                        total += price * cnt
                    }
                    val totalString = "${getString(R.string.total)}: ${String.format("%.2f", total)} руб."
                    binding.tvTotal.text = totalString
                }
            },
            {err->
                Log.e(Constants.LOG_TAG, err.toString())
            }
        )
        queue.add(req)
//        return inflater.inflate(R.layout.fragment_cart, container, false)
        return binding.root
    }

    private fun init() {
        binding.apply {
            rcViewCart.layoutManager = LinearLayoutManager(requireActivity())
            rcViewCart.adapter = productAdapter
        }
    }

//    fun payClickListener(view: View) {
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        // TODO: Use the ViewModel
    }

}