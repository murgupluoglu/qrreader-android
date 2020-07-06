package com.murgupluoglu.qrreader

import androidx.camera.core.CameraSelector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

data class QRCameraConfiguration(
        var lensFacing: Int = CameraSelector.LENS_FACING_BACK,

        var options: FirebaseVisionBarcodeDetectorOptions =
                FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.FORMAT_AZTEC)
                        .build()
)