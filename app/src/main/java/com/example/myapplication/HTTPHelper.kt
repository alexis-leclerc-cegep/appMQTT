package com.example.myapplication;


import okhttp3.*
import com.example.myapplication.HTTPHelper
import java.io.IOException


public class HTTPHelper {

    private val client: OkHttpClient = OkHttpClient()

    fun runSecu(url: String, token: String, callback: (String?) -> Unit) {

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failure")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                callback(responseBody)
            }

        })
    }


    fun run(url: String, json: String, callback: (String?) -> Unit){
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(mediaType, json)

        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failure")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                callback(responseBody)
            }

        })
    }
}
