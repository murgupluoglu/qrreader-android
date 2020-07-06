package com.murgupluoglu.androidqrreader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.murgupluoglu.qrreader.QRCameraConfiguration
import com.murgupluoglu.qrreader.QRReaderListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(application)


        qrCodeReaderView.setListener(object : QRReaderListener {
            override fun onRead(barcode: FirebaseVisionBarcode, barcodes: List<FirebaseVisionBarcode>) {
                qrTextView.text = barcode.displayValue
            }

            override fun onError(exception: Exception) {
                qrTextView.text = exception.toString()
            }
        })

        PermissionUtils.permission(PermissionConstants.CAMERA).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_BACK)
                qrCodeReaderView.startCamera(this@MainActivity, config)
            }

            override fun onDenied() {
                Log.e("MainActivity", "The camera permission must be granted in order to use this app")
            }
        }).request()


        torchButton.setOnClickListener {
            qrCodeReaderView.enableTorch(!qrCodeReaderView.isTorchOn())
        }
    }
}
