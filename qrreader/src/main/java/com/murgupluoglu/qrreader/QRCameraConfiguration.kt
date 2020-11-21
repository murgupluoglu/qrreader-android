package com.murgupluoglu.qrreader

import androidx.camera.core.CameraSelector
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

data class QRCameraConfiguration(
        var lensFacing: Int = CameraSelector.LENS_FACING_BACK,

        val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .build()
)