package com.example.praypay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.praypay.databinding.FragmentScannerBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.json.JSONObject
import java.io.IOException


class Scanner : Fragment() {

    companion object {
        fun newInstance() = Scanner()
    }
    private lateinit var viewModel: ScannerViewModel
    private lateinit var binding: FragmentScannerBinding
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScannerBinding.inflate(inflater)
//        return inflater.inflate(R.layout.fragment_scanner, container, false)

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
                if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null){
                    Log.d(Constants.LOG_TAG, "Data from activity: ${result.data?.getStringExtra("name")}")
                }
                else {
                    Log.e(Constants.LOG_TAG, "Error from activity result: ${result.resultCode}")
                }
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(context, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScannerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(requireActivity().applicationContext).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(requireActivity().applicationContext, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Log.d(Constants.LOG_TAG, "Scanner has been closed")
//                Toast.makeText(activity?.applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue

                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    activity?.runOnUiThread {
                        cameraSource.stop()
                        Toast.makeText(activity?.applicationContext, "value- $scannedValue", Toast.LENGTH_SHORT).show()
                        val i = Intent(activity?.applicationContext, BuyProductActivity::class.java)
                        val productId = JSONObject(scannedValue).getInt("product_id")
                        Log.d(Constants.LOG_TAG, "Product ID: $productId")
                        i.putExtra("product_id", productId)
                        addProductLauncher.launch(i)
                    }
                }else
                {
//                    Log.d(Constants.LOG_TAG, "value - else")
//                    Toast.makeText(activity?.applicationContext, "value- else", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }
}