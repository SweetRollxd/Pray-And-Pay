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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.praypay.databinding.FragmentAccountBinding
import com.example.praypay.product.Product
import org.json.JSONException
import org.json.JSONObject

class Account : Fragment() {

    companion object {
        fun newInstance() = Account()
    }

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel
    private lateinit var depositActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var historyActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)

        depositActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                refreshBalance()
            }
            else {
                Log.e(Constants.LOG_TAG, "Error from activity result: ${result.resultCode}")
            }

        }
//        depositActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//                result->
//            if (result.resultCode == AppCompatActivity.RESULT_OK){
//            }
//            else {
//                Log.e(Constants.LOG_TAG, "Error from activity result: ${result.resultCode}")
//            }
//
//        }

        binding.btnDeposit.setOnClickListener {
            val i = Intent(activity?.applicationContext, DepositActivity::class.java)
            depositActivityLauncher.launch(i)
        }

//        binding.btnHistory.setOnClickListener {
//            val i = Intent(activity?.applicationContext, DepositActivity::class.java)
//            historyActivityLauncher.launch(i)
//        }

        refreshBalance()

        return binding.root

    }

    private fun refreshBalance() {
        val queue = Volley.newRequestQueue(requireActivity())
        val url = "${Constants.API_SERVER}/users/${Constants.USER_ID}"
        val req = JsonObjectRequest(Request.Method.GET,
            url,
            null,
            {response->
                Log.d(Constants.LOG_TAG, "Response from API: ${response.toString()}")
                val userName = "${response.getString("firstname")} ${response.getString("surname")}"
                val balance = response.getString("balance") + " руб."
                binding.tvUsername.text = userName
                binding.tvBalance.text = balance
            },
            {err->
                Log.e(Constants.LOG_TAG, err.toString())
                try {
                    val msg = JSONObject(String(err.networkResponse.data)).getString("msg")
                    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Toast.makeText(requireActivity(), "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        )
        queue.add(req)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

}