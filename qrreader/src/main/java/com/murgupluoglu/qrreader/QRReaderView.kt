package com.murgupluoglu.qrreader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Rational
import android.view.TextureView
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner


/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRReaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr) {

    private lateinit var config: QRCameraConfiguration
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null

    lateinit var qrReaderListener: QRReaderListener

    inline fun setListener(crossinline listener: (qrCode: String, status: QRStatus) -> Unit) {
        this.qrReaderListener = object : QRReaderListener {
            override fun onReaded(qrCode: String, status: QRStatus) {
                listener(qrCode, status)
            }
        }
    }

    private fun buildUseCases() {

        //Preview
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(Rational(width, height))
            setTargetRotation(display.rotation)
            setLensFacing(config.lensFacing)
        }.build()

        preview = QRAutoFitPreviewBuilder.build(previewConfig, this@QRReaderView)
        //End - Preview

        //ImageAnalyze
        val analysisConfig = ImageAnalysisConfig.Builder().apply {
            setTargetAspectRatio(Rational(width, height))
            setLensFacing(config.lensFacing)
            // Use a worker thread for image analysis to prevent preview glitches
            val analyzerThread = HandlerThread("QRAnalyzer").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            // In our analysis, we care more about the latest image than analyzing *every* image
            setImageReaderMode(config.readerMode)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(display.rotation)
        }.build()

        imageAnalyzer = ImageAnalysis(analysisConfig)

        imageAnalyzer?.apply {
            analyzer = QRAnalyzer().apply {
                onFrameAnalyzed { qrCode, status, resultPoint, previewWidth, previewHeight, rotationDegrees ->
                    qrReaderListener.onReaded(qrCode, status)
                }
            }
        }
        //End - ImageAnalyze
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, conf: QRCameraConfiguration = QRCameraConfiguration()) {
        val permissionCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            this@QRReaderView.post {

                config = conf

                CameraX.unbindAll()
                buildUseCases()
                CameraX.bindToLifecycle(
                    lifecycleOwner,
                    preview,
                    imageAnalyzer
                )
            }
        } else {
            throw Exception("Camera permission not granted")
        }
    }

    fun enableTorch(enableTorch: Boolean) {
        preview?.enableTorch(enableTorch)
    }

    fun isTorchOn(): Boolean {
        return preview?.isTorchOn ?: false
    }
}