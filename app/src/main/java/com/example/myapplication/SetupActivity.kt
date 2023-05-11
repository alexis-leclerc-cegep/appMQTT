package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val locales = Locale.getAvailableLocales()

        val languageCodes = HashSet<String>()

        // Loop through each locale and add its language code to the set
        for (locale in locales) {
            languageCodes.add(locale.language)
        }

    // Convert the set to a sorted list
        val sortedLanguageCodes = ArrayList<String>(languageCodes).sorted()

        // Create an array adapter for the spinner
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            sortedLanguageCodes
        )
        val spinner = findViewById<Spinner>(R.id.spinner)

        spinner.adapter = adapter

    }
}