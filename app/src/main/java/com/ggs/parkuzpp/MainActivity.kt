package com.ggs.parkuzpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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
            ParkUZTheme {
                AppNavigation()
            }
        }
    }
}