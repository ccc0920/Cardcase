package com.team4.cardcase2.entity

import java.nio.ByteBuffer
import android.util.Base64

class Encoder {

    // 将整数加密成64位字符串
    fun encode(value: Int): String {
        val byteBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
        byteBuffer.putInt(value)
        val byteArray = byteBuffer.array()
        return Base64.encodeToString(byteArray, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    // 将64位字符串解码成整数
    fun decode(encoded: String): Int {
        val byteArray = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        return byteBuffer.int
    }
}