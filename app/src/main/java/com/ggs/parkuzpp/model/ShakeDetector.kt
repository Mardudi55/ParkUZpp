package com.ggs.parkuzpp.history

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastShakeTime = 0L

    private var shakeCount = 0

    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        if (event == null) return

        if (
            event.sensor.type !=
            Sensor.TYPE_ACCELEROMETER
        ) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration =
            sqrt(x * x + y * y + z * z)

        // STRONGER SHAKE REQUIRED

        if (acceleration > 20f) {

            val currentTime =
                System.currentTimeMillis()

            // IGNORE VERY FAST SENSOR SPAM

            if (
                currentTime - lastShakeTime > 300
            ) {

                lastShakeTime = currentTime

                shakeCount++

                // REQUIRE 2 SHAKES

                if (shakeCount >= 2) {

                    shakeCount = 0

                    onShake()
                }
            }

        } else {

            // RESET IF USER STOPS SHAKING

            val currentTime =
                System.currentTimeMillis()

            if (
                currentTime - lastShakeTime > 1500
            ) {

                shakeCount = 0
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}