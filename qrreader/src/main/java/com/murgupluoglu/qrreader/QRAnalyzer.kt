package com.murgupluoglu.qrreader

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader

/*
*  Created by Mustafa Ürgüplüoğlu on 05.07.2019.
*  Copyright © 2019 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class QRAnalyzer : ImageAnalysis.Analyzer {

    var qrCodeReader: QRCodeReader = QRCodeReader()
    private val listeners = ArrayList<(qrCode: String, qrStatus : QRStatus, resultPoint : Array<ResultPoint>?, resultImageWidth : Int?, resultImageHeight : Int?, rotationDegrees: Int) -> Unit>()


    fun onFrameAnalyzed(listener: (qrCode: String, qrStatus : QRStatus, resultPoint : Array<ResultPoint>?, resultImageWidth : Int?, resultImageHeight : Int?, rotationDegrees: Int) -> Unit) = listeners.add(listener)


    override fun analyze(image: ImageProxy, rotationDegrees: Int) {

        if (listeners.isEmpty()) return

        val y = image.planes[0]
        val u = image.planes[1]
        val v = image.planes[2]

        val Yb = y.buffer.remaining()
        val Ub = u.buffer.remaining()
        val Vb = v.buffer.remaining()

        val data = ByteArray(Yb + Ub + Vb)
        y.buffer.get(data, 0, Yb)
        u.buffer.get(data, Yb, Ub)
        v.buffer.get(data, Yb + Ub, Vb)

        val lum = PlanarYUVLuminanceSource(
            data,
            image.width, image.height,
            0, 0,
            image.width, image.height,
            false
        )
        val hybBin = HybridBinarizer(lum)
        val bitmap = BinaryBitmap(hybBin)

        try {
            val result = qrCodeReader.decode(bitmap, null)
            val points = result.resultPoints
            listeners.forEach { it(result.text.toString(), QRStatus.Success(), points, image.width, image.height, rotationDegrees) }
        } catch (e: NotFoundException) {
            listeners.forEach { it("", QRStatus.NotFoundException(), null, null,null, rotationDegrees) }
        } catch (e: ChecksumException) {
            Log.d("ChecksumException", e.toString())
            listeners.forEach { it("", QRStatus.ChecksumException(), null, null, null, rotationDegrees) }
        } catch (e: FormatException) {
            Log.d("FormatException", e.toString())
            listeners.forEach { it("", QRStatus.FormatException(), null, null, null, rotationDegrees) }
        } finally {
            qrCodeReader.reset()
        }
    }
}