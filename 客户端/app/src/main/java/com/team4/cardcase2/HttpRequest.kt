package com.team4.cardcase2

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class HttpRequest {

    private val client = OkHttpClient()

    fun sendPostRequest(url: String, authToken: String, jsonBody: String, callback: (String?, Exception?) -> Unit) {
        // 创建RequestBody
        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        // 创建Request
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .post(requestBody)
            .build()

        // 发送请求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理失败
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null, IOException("Unexpected code $it"))
                        return
                    }
                    // 处理成功，获取返回值
                    val responseBody = it.body?.string()
                    callback(responseBody, null)
                }
            }
        })
    }

    fun sendInfoRequest(url: String,jsonBody: String,callback: (String?, Exception?) -> Unit){
        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        // 创建Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null, IOException("Unexpected code $it"))
                        return
                    }
                    val responseBody = it.body?.string()
                    callback(responseBody, null)
                }
            }
        })


    }

    fun sendVerifyRequest(url: String,jsonBody: String,callback: (String?, Exception?) -> Unit){
        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        // 创建Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null, IOException("Unexpected code $it"))
                        return
                    }
                    val responseBody = it.body?.string()
                    callback(responseBody, null)
                }
            }
        })


    }

    fun sendLoginInfoRequest(url: String,jsonBody: String,callback: (String?, Exception?) -> Unit){
        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        // 创建Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null, IOException("Unexpected code $it"))
                        return
                    }
                    val responseBody = it.body?.string()
                    callback(responseBody, null)
                }
            }
        })


    }
}