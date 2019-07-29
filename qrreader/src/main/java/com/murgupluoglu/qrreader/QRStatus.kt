package com.murgupluoglu.qrreader

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

sealed class QRStatus{
    class Success : QRStatus()
    class NotFoundException : QRStatus()
    class ChecksumException : QRStatus()
    class FormatException : QRStatus()
}