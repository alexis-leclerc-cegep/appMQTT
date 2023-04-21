package com.example.myapplication;


import okhttp3.*
import java.io.IOException


public class HTTPHelper {

    private val client: OkHttpClient = OkHttpClient()

    fun run(url: String, json: String, callback: (String?) -> Unit){
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(mediaType, json)

        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

        val reponse: String

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
