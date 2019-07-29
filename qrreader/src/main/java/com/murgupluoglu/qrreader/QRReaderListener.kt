package com.murgupluoglu.qrreader

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

interface QRReaderListener{
    fun onReaded(qrCode : String, status : QRStatus)
}