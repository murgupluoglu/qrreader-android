package com.murgupluoglu.qrreader

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.lang.Exception


/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRAnalyzer(val displayRotation : Int, val options: FirebaseVisionBarcodeDetectorOptions) : ImageAnalysis.Analyzer {

    private fun degreesToFirebaseRotation(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException("Rotation must be 0, 90, 180, or 270.")
        }
    }

    private val listeners = ArrayList<(qrStatus: Int, barcode : FirebaseVisionBarcode?, barcodes : List<FirebaseVisionBarcode>?, exeption : Exception?) -> Unit>()


    fun onFrameAnalyzed(listener: (qrStatus: Int, barcode : FirebaseVisionBarcode?, barcodes : List<FirebaseVisionBarcode>?, exeption : Exception?) -> Unit) = listeners.add(listener)

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (listeners.isEmpty()) return

        imageProxy.image?.let { image ->
            val visionImage = FirebaseVisionImage.fromMediaImage(image, degreesToFirebaseRotation(displayRotation))

            val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

            detector.detectInImage(visionImage)
                    .addOnSuccessListener { barcodes ->

                        if(barcodes.isNotEmpty()){
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