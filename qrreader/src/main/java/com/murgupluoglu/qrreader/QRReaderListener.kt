package com.murgupluoglu.qrreader

import com.google.mlkit.vision.barcode.Barcode

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

interface QRReaderListener {
    fun onRead(barcode: Barcode, barcodes: List<Barcode>)
    fun onError(exception: Exception)
}