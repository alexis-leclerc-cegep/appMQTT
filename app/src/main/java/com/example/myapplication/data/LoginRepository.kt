package com.example.myapplication.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myapplication.data.model.LoggedInUser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

import com.example.myapplication.HTTPHelper
import org.json.JSONObject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    /*
    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        Log.w("HTTP", "Trying to login")
        val httpHelper = HTTPHelper()

        val API_URL: String = "http://172.16.6.110:6969"
        val route : String = "/login"

        val token: String

        val json = "{\"username\": \"$username\", \"password\": \"$password\"}"

        //var result: Result<LoggedInUser> = Result.Error(IOException("Error logging in"))

        httpHelper.run("$API_URL$route", json) { responseBody ->
            if (responseBody != null) {
                Log.w("HTTP", responseBody)
                val json = JSONObject(responseBody)
                val token = json.optString("token", "")
                if (token.isNotEmpty()) {
                    Log.w("HTTP", token)
                    val loggedInUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username, token)
                    val result = Result.Success(loggedInUser)
                } else {
                    val result = Result.Error(IOException("Error logging in"))
                }
            } else {
                val result = Result.Error(IOException("Error logging in"))
            }
        }

    }

     */


    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}