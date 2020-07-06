package com.murgupluoglu.qrreader

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

interface QRReaderListener {
    fun onRead(barcode: FirebaseVisionBarcode, barcodes: List<FirebaseVisionBarcode>)
    fun onError(exception: Exception)
}