package com.team4.cardcase2.entity

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix

class QRCodeGenerator {

    fun generateQRCode(text: String, width: Int = 200, height: Int = 200): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        return createBitmap(bitMatrix)
    }

    private fun createBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
            }
        }
        return bitmap
    }
}