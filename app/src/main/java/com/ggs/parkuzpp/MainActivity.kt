package com.ggs.parkuzpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        //FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
        //    PlayIntegrityAppCheckProviderFactory.getInstance()
        //)
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword("test123@test.com", "123456")
            .addOnSuccessListener {
                println("REGISTER OK")
            }
            .addOnFailureListener { e ->
                println("REGISTER ERROR: ${e.message}")
            }

        setContentView(R.layout.activity_main)
    }
}