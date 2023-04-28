package com.example.myapplication.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import android.widget.Toast
import com.example.myapplication.HTTPHelper
import com.example.myapplication.data.LoginRepository
import com.example.myapplication.data.Result

import com.example.myapplication.R
import com.example.myapplication.data.model.LoggedInUser
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, applicationContext: Context) {
        // can be launched in a separate asynchronous job

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
                    val loggedInUser = LoggedInUser(UUID.randomUUID().toString(), username, token)
                    val result = Result.Success(loggedInUser)
                    _loginResult.postValue( LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))

                    val context = applicationContext

                    val sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

                    val editor = sharedPreferences.edit()
                    Log.w("AndroidRuntime", result.data.token)

                    editor.putString("token", result.data.token)
                    editor.apply()
                } else {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 3
    }
}