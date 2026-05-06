package com.ggs.parkuzpp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ggs.parkuzpp.bluetooth.BluetoothController
import com.ggs.parkuzpp.bluetooth.BluetoothUtils
import com.ggs.parkuzpp.ui.AppNavigation
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothController:
            BluetoothController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        // TODO: Przenieść ten kod do testów
        /*val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword("test123@test.com", "123456")
            .addOnSuccessListener {
                println("REGISTER OK")
            }
            .addOnFailureListener { e ->
                println("REGISTER ERROR: ${e.message}")
            }

         */
        bluetoothController =
            BluetoothController(this)

        bluetoothController.start()

        setContent {

            var isDarkTheme by remember {
                mutableStateOf(false)
            }

            ParkUZTheme(
                darkTheme = isDarkTheme
            ) {

                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = {
                        isDarkTheme = it
                    }
                )
            }
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        bluetoothController.stop()
    }
}