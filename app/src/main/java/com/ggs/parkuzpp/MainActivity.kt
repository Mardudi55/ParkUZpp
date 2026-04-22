package com.ggs.parkuzpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // TODO: Przenieść ten kod do testów
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword("test123@test.com", "123456")
            .addOnSuccessListener {
                println("REGISTER OK")
            }
            .addOnFailureListener { e ->
                println("REGISTER ERROR: ${e.message}")
            }

        setContent {
            // 1. Zmienna trzymająca stan motywu (żyje na samej górze apki)
            var isDarkTheme by remember { mutableStateOf(false) }

            // 2. Przekazujemy stan do globalnego motywu
            ParkUZTheme(darkTheme = isDarkTheme) {
                // 3. Przekazujemy stan i funkcję zmieniającą do nawigacji!
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
        }
    }
