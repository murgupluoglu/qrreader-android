package com.murgupluoglu.qrreader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor


/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRReaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PreviewView(context, attrs, defStyleAttr) {

    private lateinit var config: QRCameraConfiguration
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var mainExecutor: Executor
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector : CameraSelector
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var lifecycleOwner : LifecycleOwner
    private var camera : Camera? = null

    private lateinit var qrReaderListener: QRReaderListener

    fun setListener(listener : QRReaderListener) {
        qrReaderListener = listener
    }

    private fun buildUseCases() {

        cameraProviderFuture.addListener(Runnable {

            cameraSelector = CameraSelector.Builder().requireLensFacing(config.lensFacing).build()


            val rotation = display.rotation


            //Preview
            preview = Preview.Builder()
                    .setTargetName("Preview")
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(rotation)
                    .build()

            preview!!.setSurfaceProvider(previewSurfaceProvider)
            //End - Preview

            //ImageAnalyze
            imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetName("Analysis")
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(rotation)
                    .build()


            imageAnalyzer!!.setAnalyzer(mainExecutor, QRAnalyzer(rotation, config.options).apply {
                onFrameAnalyzed { qrStatus, barcode, barcodes, exeption ->
                    if(qrStatus == QRStatus.Success){
                        qrReaderListener.onRead(barcode!!, barcodes!!)
                    }else if(qrStatus == QRStatus.Error){
                        qrReaderListener.onError(exeption!!)
                    }
                }
            })
            //End - ImageAnalyze

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)

        }, mainExecutor)
    }

    fun startCamera(lifecycleowner: LifecycleOwner, conf: QRCameraConfiguration = QRCameraConfiguration()) {
        val permissionCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            this@QRReaderView.post {

                lifecycleOwner = lifecycleowner
                config = conf

                cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProvider = cameraProviderFuture.get()
                mainExecutor = ContextCompat.getMainExecutor(context)

                buildUseCases()

            }
        } else {
            throw Exception("Camera permission not granted")
        }
    }

    fun enableTorch(enableTorch: Boolean) {
        camera?.cameraControl?.enableTorch(enableTorch)
    }

    fun isTorchOn(): LiveData<Int>? {
        return camera?.cameraInfo?.torchState
    }

    fun isTorchAvailable() : Boolean{
        return camera?.cameraInfo?.hasFlashUnit() == true
    }
}