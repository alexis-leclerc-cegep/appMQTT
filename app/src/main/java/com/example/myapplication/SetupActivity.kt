package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class SetupActivity : AppCompatActivity() {
    val languages = arrayOf(
        Pair("Français", "fr"),
        Pair("English", "en"),
    ).sortedBy { it.first }

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        sharedPreferences = getDefaultSharedPreferences(this)

        Log.i("debug", "created1")
        val fullLanguages = languages.map{it.first}

        // Create an array adapter for the spinner
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            fullLanguages
        )
        val spinner = findViewById<Spinner>(R.id.spinner)

        spinner.adapter = adapter

        // Set the default value of the spinner
        val locale = Locale.getDefault().language
        val index = languages.indexOfFirst { it.second == locale }
        spinner.setSelection(index)

        val btnEnregistrer = findViewById<View>(R.id.btnSave)

        btnEnregistrer.setOnClickListener(View.OnClickListener {
            val position  = spinner.selectedItemPosition.toInt()
            if (getSelectedLanguage() != languages[position].second){
                // Si la locale est la même, ne rien faire
                val selectedLanguage = languages[position].second
                Log.i("debug", "selected language : $selectedLanguage")
                setAppLocale(this@SetupActivity, selectedLanguage)
                saveSelectedLanguage(selectedLanguage)
            }
        })


        Log.i("debug", "created")

    }
    private fun getSelectedLanguage(): String {
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    private fun saveSelectedLanguage(language: String) {
        val editor = sharedPreferences.edit()
        val config = resources.configuration

        editor.putString(KEY_SELECTED_LANGUAGE, language)
        editor.apply()
        editor.putString("language", language)
        editor.apply()

        val locale = Locale(language)
        Locale.setDefault(locale)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }


    private fun setAppLocale(activity: AppCompatActivity, language: String) {

        val resources = activity.resources
        val configuration = resources.configuration

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }


    companion object {
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_LANGUAGE = "en"
    }
}