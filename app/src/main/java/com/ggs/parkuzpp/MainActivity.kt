package com.ggs.parkuzpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ggs.parkuzpp.ui.theme.ParkUZTheme

// 🔥 DODANE (Firebase)
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 DODANE (Firebase test)
        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "status" to "dziala"
        )

        db.collection("test")
            .add(data)
            .addOnSuccessListener {
                println("FIREBASE OK")
            }
            .addOnFailureListener {
                println("FIREBASE ERROR")
            }

        enableEdgeToEdge()
        setContent {
            ParkUZTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParkUZTheme {
        Greeting("Android")
    }
}
