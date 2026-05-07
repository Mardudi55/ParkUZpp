package com.ggs.parkuzpp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.ggs.parkuzpp.bluetooth.BluetoothController
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp
import androidx.core.content.edit

/**
 * The main entry point of the ParkUZ++ application.
 * Sets up the initial context, initializes Firebase, applies the user's preferred locale,
 * and launches the Jetpack Compose navigation graph.
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "settings"
        private const val PREF_LANG_KEY = "lang"
        private const val DEFAULT_LANG = "pl"
    }


    private lateinit var bluetoothController:
            BluetoothController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        bluetoothController =
            BluetoothController(this)

        bluetoothController.start()

        setContent {
            val prefs = remember { getSharedPreferences(PREFS_NAME, MODE_PRIVATE) }
            var currentLanguage by remember {
                mutableStateOf(prefs.getString(PREF_LANG_KEY, DEFAULT_LANG) ?: DEFAULT_LANG)
            }

            var isDarkTheme by remember { mutableStateOf(false) }

            val onLanguageChange: (String) -> Unit = { newLang ->
                prefs.edit { putString(PREF_LANG_KEY, newLang) }
                currentLanguage = newLang
                LocaleHelper.setLocale(this, newLang)
                // Recreate the activity to apply the new locale across the entire UI
                this.recreate()
            }

            ParkUZTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it },
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange
                )
            }
        }
    }

    /**
     * Overrides the base context to apply the saved locale before the activity is fully created.
     * This ensures that all UI components and context-aware elements use the correct language resources from the start.
     *
     * @param newBase The new base context for this wrapper.
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val lang = prefs.getString(PREF_LANG_KEY, DEFAULT_LANG) ?: DEFAULT_LANG
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }
}