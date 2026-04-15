package com.ggs.parkuzpp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ParkUZTheme {
                AuthScreen()
            }
        }
    }

}

@Composable
fun AuthScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var captchaChecked by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()


    if (isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Jesteś zalogowany 🎉", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                auth.signOut()


                email = ""
                password = ""
                captchaChecked = false
                message = ""

                isLoggedIn = false
            }) {
                Text("Wyloguj")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = captchaChecked,
                onCheckedChange = { captchaChecked = it }
            )
            Text("Nie jestem robotem")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!captchaChecked) {
                message = "Zaznacz captcha"
                return@Button
            }

            if (email.isEmpty() || password.isEmpty()) {
                message = "Uzupełnij dane"
                return@Button
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        message = "Rejestracja OK"
                    } else {
                        message = "Błąd: ${it.exception?.message}"
                    }
                }

        }) {
            Text("Rejestracja")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (!captchaChecked) {
                message = "Zaznacz captcha"
                return@Button
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        isLoggedIn = true
                    } else {
                        message = "Błąd logowania: ${it.exception?.message}"
                    }
                }

        }) {
            Text("Logowanie")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)
    }

}
