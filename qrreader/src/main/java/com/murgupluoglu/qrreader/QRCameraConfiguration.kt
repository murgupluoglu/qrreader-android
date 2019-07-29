package com.murgupluoglu.qrreader

import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

data class QRCameraConfiguration(
    var lensFacing: CameraX.LensFacing = CameraX.LensFacing.BACK,

    var readerMode: ImageAnalysis.ImageReaderMode = ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE
)