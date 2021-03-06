package com.murgupluoglu.androidqrreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.google.mlkit.vision.barcode.Barcode
import com.murgupluoglu.qrreader.QRCameraConfiguration
import com.murgupluoglu.qrreader.QRReaderListener
import com.murgupluoglu.qrreader.QRReaderFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val qrCodeReader : QRReaderFragment by lazy {
        qrCodeReaderFragment as QRReaderFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(application)


        qrCodeReader.setListener(object : QRReaderListener {

            override fun onRead(barcode: Barcode, barcodes: List<Barcode>) {
                qrTextView.text = barcode.displayValue
            }

            override fun onError(exception: Exception) {
                qrTextView.text = exception.toString()
            }
        })

        PermissionUtils.permission(PermissionConstants.CAMERA).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_BACK)
                qrCodeReader.startCamera(this@MainActivity, config)
            }

            override fun onDenied() {
                Log.e("MainActivity", "The camera permission must be granted in order to use this app")
            }
        }).request()


        torchButton.setOnClickListener {
            qrCodeReader.enableTorch(!qrCodeReader.isTorchOn())
        }

        qrTextView?.setOnClickListener {
            val uriUrl: Uri = Uri.parse(qrTextView.text.toString())
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        }
    }
}
