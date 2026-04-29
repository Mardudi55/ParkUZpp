package com.ggs.parkuzpp.ui
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PasswordScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Zmień hasło", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Nowe hasło") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password.length < 6) {
                    Toast.makeText(context, "Min 6 znaków", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                user?.updatePassword(password)
                    ?.addOnSuccessListener {
                        isLoading = false
                        Toast.makeText(context, "Hasło zmienione", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                    ?.addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zmień hasło")
        }
    }
}
