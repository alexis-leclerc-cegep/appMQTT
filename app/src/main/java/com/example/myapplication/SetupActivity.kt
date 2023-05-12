package com.example.myapplication

import android.content.res.Configuration
import android.os.Bundle
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class SetupActivity : AppCompatActivity() {
    val languages = arrayOf(
        Pair("Français", "fr"),
        Pair("English", "en"),
        Pair("Español", "es"),
        Pair("Deutsch", "de")
    ).sortedBy { it.first }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

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

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                val selectedLanguage = languages[position].second
                saveSelectedLanguage(selectedLanguage)
                setAppLocale(selectedLanguage)
                recreate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }
    private fun getSelectedLanguage(): String {
        val sharedPreferences = null
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    private fun saveSelectedLanguage(language: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SELECTED_LANGUAGE, language)
        editor.apply()
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}