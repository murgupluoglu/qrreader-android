package com.murgupluoglu.androidqrreader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.murgupluoglu.qrreader.QRCameraConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(this@MainActivity)

        qrCodeReaderView.setListener { qrCode, status ->
            qrTextView.text = qrCode
        }

        PermissionUtils.permission(PermissionConstants.CAMERA).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                qrCodeReaderView.startCamera(
                    this@MainActivity,
                    QRCameraConfiguration(lensFacing = CameraX.LensFacing.BACK)
                )
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
