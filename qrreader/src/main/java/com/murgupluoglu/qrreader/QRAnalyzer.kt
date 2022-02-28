package com.murgupluoglu.qrreader

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRAnalyzer(val options: BarcodeScannerOptions) : ImageAnalysis.Analyzer {

    private val listeners =
        ArrayList<(qrStatus: Int, barcode: Barcode?, barcodes: List<Barcode>?, exception: Exception?) -> Unit>()

    fun onFrameAnalyzed(listener: (qrStatus: Int, barcode: Barcode?, barcodes: List<Barcode>?, exception: Exception?) -> Unit) =
        listeners.add(listener)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (listeners.isEmpty()) return

        imageProxy.image?.let { image ->

            val visionImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val detector = BarcodeScanning.getClient(options)

            detector.process(visionImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        listeners.forEach { it(QRStatus.Success, barcodes[0], barcodes, null) }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { error ->
                    listeners.forEach { it(QRStatus.Error, null, null, error) }
                    imageProxy.close()
                }
        }
    }
}