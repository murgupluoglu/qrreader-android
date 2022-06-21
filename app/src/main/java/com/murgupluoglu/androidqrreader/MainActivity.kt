package com.murgupluoglu.androidqrreader

import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.CameraSelector
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.google.mlkit.vision.barcode.common.Barcode
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
                barcode.displayValue?.let {
                    qrTextView.text = it
                    fixTextView(qrTextView)
                }

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

        switchCamera.setOnClickListener {
            if (config.lensFacing == CameraSelector.LENS_FACING_BACK) {
                config.lensFacing = CameraSelector.LENS_FACING_FRONT
            } else {
                config.lensFacing = CameraSelector.LENS_FACING_BACK
            }
            qrCodeReader.startCamera(this@MainActivity, config)
        }
    }

    private fun fixTextView(tv: TextView) {
        if (tv.text is SpannableString) {
            val current = tv.text as SpannableString
            val spans = current.getSpans(0, current.length, URLSpan::class.java)
            for (span in spans) {
                val start = current.getSpanStart(span)
                val end = current.getSpanEnd(span)
                current.removeSpan(span)
                current.setSpan(
                    DefensiveURLSpan(span.url), start, end,
                    0
                )
            }
        }
    }

    class DefensiveURLSpan(private val mUrl: String) : URLSpan(mUrl) {
        override fun onClick(widget: View) {
            val uriUrl: Uri = Uri.parse(mUrl)
            val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(widget.context, uriUrl)
        }
    }

}
