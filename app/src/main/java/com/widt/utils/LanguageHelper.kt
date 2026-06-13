package com.widt.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageHelper {

    private const val PREFS_NAME = "widt_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANG_VI = "vi"
    const val LANG_EN = "en"
    const val LANG_ZH = "zh"

    fun getSavedLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, LANG_VI) ?: LANG_VI
    }

    fun setLanguage(context: Context, langCode: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, langCode).apply()
        applyLanguage(context, langCode)
    }

    fun applyLanguage(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Call this in Activity.attachBaseContext() to apply saved language on startup.
     */
    fun applyLanguageOnStart(context: Context): Context {
        val langCode = getSavedLanguage(context)
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        val appContext = try { context.applicationContext } catch (e: Exception) { null }
        return (appContext ?: context).getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
