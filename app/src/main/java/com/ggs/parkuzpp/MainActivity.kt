package com.ggs.parkuzpp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.*


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            val prefs = remember { getSharedPreferences("settings", Context.MODE_PRIVATE) }
            var currentLanguage by remember {
                mutableStateOf(prefs.getString("lang", "pl") ?: "pl")
            }

            var isDarkTheme by remember { mutableStateOf(false) }

            val onLanguageChange: (String) -> Unit = { newLang ->
                prefs.edit().putString("lang", newLang).apply()
                currentLanguage = newLang
                LocaleHelper.setLocale(this, newLang)
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
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "pl") ?: "pl"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

}