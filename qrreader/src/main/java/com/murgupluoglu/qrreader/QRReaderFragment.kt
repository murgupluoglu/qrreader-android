package com.murgupluoglu.qrreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRReaderFragment : Fragment(R.layout.fragment_qrreader) {

    private lateinit var config: QRCameraConfiguration
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var lifecycleOwner: LifecycleOwner
    private var camera: Camera? = null

    private lateinit var qrReaderListener: QRReaderListener
    private lateinit var previewView: PreviewView

    fun setListener(listener: QRReaderListener) {
        qrReaderListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        previewView = requireView().findViewById(R.id.previewView)
    }

    private fun buildUseCases() {

        cameraProviderFuture.addListener({

            cameraSelector = CameraSelector.Builder().requireLensFacing(config.lensFacing).build()


            val rotation = previewView.display.rotation

            //ImageAnalyze
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetName("Analysis")
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .build()


            imageAnalyzer!!.setAnalyzer(cameraExecutor, QRAnalyzer(config.options).apply {
                onFrameAnalyzed { qrStatus, barcode, barcodes, exception ->
                    if (qrStatus == QRStatus.Success) {
                        qrReaderListener.onRead(barcode!!, barcodes!!)
                    } else if (qrStatus == QRStatus.Error) {
                        qrReaderListener.onError(exception!!)
                    }
                }
            })
            //End - ImageAnalyze

            preview = Preview.Builder()
                .setTargetName("Preview")
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .build()

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview!!.setSurfaceProvider(previewView.surfaceProvider)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        config: QRCameraConfiguration = QRCameraConfiguration()
    ) {
        val permissionCamera =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            previewView.post {

                this.lifecycleOwner = lifecycleOwner
                this.config = config

                cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProvider = cameraProviderFuture.get()

                buildUseCases()

            }
        } else {
            throw Exception("Camera permission not granted")
        }
    }

    fun enableTorch(enableTorch: Boolean) {
        camera?.cameraControl?.enableTorch(enableTorch)
    }

    fun isTorchOn(): Boolean {
        if (isTorchAvailable()) {
            return camera?.cameraInfo?.torchState?.value == 1
        }
        return false
    }

    fun isTorchOnLiveData(): LiveData<Int>? {
        return camera?.cameraInfo?.torchState
    }

    fun isTorchAvailable(): Boolean {
        return camera?.cameraInfo?.hasFlashUnit() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}