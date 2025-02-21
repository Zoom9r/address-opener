package com.example.addressopener

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Оновлюємо заголовок вручну
        setTitle(R.string.title_activity_settings)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class SettingsFragment : androidx.preference.PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val languagePreference: ListPreference? = findPreference("language")

            languagePreference?.setOnPreferenceChangeListener { _, newValue ->
                val newLanguageCode = newValue as String

                // Отримуємо поточну мову
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                val currentLanguageCode = sharedPreferences.getString("language", "en") ?: "en"

                // Якщо мова не змінилася — нічого не робимо
                if (newLanguageCode == currentLanguageCode) {
                    return@setOnPreferenceChangeListener false
                }

                // Зберігаємо нову мову
                sharedPreferences.edit().putString("language", newLanguageCode).apply()

                // Оновлюємо локаль перед перезапуском
                LocaleHelper.applyLocale(requireContext(), newLanguageCode)

                // Перезапускаємо додаток
                val intent =
                    requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                requireActivity().finishAffinity() // Закриваємо всі активності
                startActivity(intent)

                true
            }
            // Обробка натискання на YouTube посилання
            val youtubePreference: Preference? = findPreference("youtube_link")
            youtubePreference?.setOnPreferenceClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley")
                )
                startActivity(intent)
                true
            }

        }


    }


}

