package com.ggs.parkuzpp.bluetooth

import android.content.Context
import androidx.core.content.edit

class BluetoothRepository(
    context: Context
) {
    private val prefs = context.getSharedPreferences("bluetooth_prefs", Context.MODE_PRIVATE)

    /**
     * Save new car
     */
    fun saveCar(name: String, address: String) {
        prefs.edit { putString(address, name) }
    }

    /**
     * Get all saved cars
     */
    fun getSavedCars(): List<SavedBluetoothDevice> {
        return prefs.all.map {
            SavedBluetoothDevice(
                name = it.value.toString(),
                address = it.key
            )
        }
    }

    /**
     * Check if device is saved
     */
    fun isSavedCar(
        address: String?
    ): Boolean {

        return prefs.contains(address)
    }

    /**
     * Remove one device
     */
    fun removeCar(address: String) {
        prefs.edit { remove(address) }
    }

    /**
     * Remove all devices
     */
    fun clearCars() {
        prefs.edit { clear() }
    }
}