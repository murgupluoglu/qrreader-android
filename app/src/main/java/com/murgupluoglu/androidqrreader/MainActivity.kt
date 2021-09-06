package com.murgupluoglu.androidqrreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.google.mlkit.vision.barcode.Barcode
import com.murgupluoglu.qrreader.QRCameraConfiguration
import com.murgupluoglu.qrreader.QRReaderFragment
import com.murgupluoglu.qrreader.QRReaderListener


class MainActivity : AppCompatActivity() {

    private lateinit var qrCodeReader: QRReaderFragment
    private lateinit var qrTextView: TextView
    private lateinit var torchButton: Button
    private lateinit var switchCamera: ImageButton
    private val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_BACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(application)

        qrCodeReader =
            supportFragmentManager.findFragmentById(R.id.qrCodeReaderFragment) as QRReaderFragment
        qrTextView = findViewById(R.id.qrTextView)
        torchButton = findViewById(R.id.torchButton)
        switchCamera = findViewById(R.id.switchCamera)


        qrCodeReader.setListener(object : QRReaderListener {

            override fun onRead(barcode: Barcode, barcodes: List<Barcode>) {
                qrTextView.text = barcode.displayValue
            }

            override fun onError(exception: Exception) {
                qrTextView.text = exception.toString()
            }
        })

        PermissionUtils.permission(PermissionConstants.CAMERA)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {

                    qrCodeReader.startCamera(this@MainActivity, config)
                }

                override fun onDenied() {
                    Log.e(
                        "MainActivity",
                        "The camera permission must be granted in order to use this app"
                    )
                }
            }).request()


        torchButton.setOnClickListener {
            qrCodeReader.enableTorch(!qrCodeReader.isTorchOn())
        }

        qrTextView.setOnClickListener {
            val uriUrl: Uri = Uri.parse(qrTextView.text.toString())
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        }

        switchCamera.setOnClickListener {
            if (config.lensFacing == CameraSelector.LENS_FACING_BACK) {
                config.lensFacing = CameraSelector.LENS_FACING_FRONT
            } else {
                config.lensFacing = CameraSelector.LENS_FACING_BACK
            }
            qrCodeReader.startCamera(this@MainActivity, config)
        }
    }
}
