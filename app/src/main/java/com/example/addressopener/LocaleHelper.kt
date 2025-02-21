package com.example.addressopener

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {
    fun applyLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun applyLocale(base: Context): Context {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(base)
        val language = sharedPreferences.getString("language", "en") ?: "en"
        return applyLocale(base, language)
    }
}
