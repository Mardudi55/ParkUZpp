package com.ggs.parkuzpp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            // Wczytujemy zapisany język lub domyślnie "pl"
            val prefs = remember { getSharedPreferences("settings", Context.MODE_PRIVATE) }
            var currentLanguage by remember {
                mutableStateOf(prefs.getString("lang", "pl") ?: "pl")
            }

            var isDarkTheme by remember { mutableStateOf(false) }

            // Funkcja zmiany języka
            val onLanguageChange: (String) -> Unit = { newLang ->
                prefs.edit().putString("lang", newLang).apply()
                currentLanguage = newLang
                LocaleHelper.setLocale(this, newLang)
                this.recreate() // Odświeża całą aktywność z nowym językiem
            }

            ParkUZTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it },
                    currentLanguage = currentLanguage,      // Przekazujemy dalej
                    onLanguageChange = onLanguageChange     // Przekazujemy dalej
                )
            }
        }
    }

    // To jest kluczowe, żeby system Android wiedział, że język się zmienił przy starcie
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "pl") ?: "pl"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }
}