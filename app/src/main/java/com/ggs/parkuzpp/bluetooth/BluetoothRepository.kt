package com.ggs.parkuzpp.bluetooth

import android.content.Context

class BluetoothRepository(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "bluetooth_prefs",
            Context.MODE_PRIVATE
        )

    // SAVE NEW CAR

    fun saveCar(

        name: String,

        address: String
    ) {

        prefs.edit()

            .putString(
                address,
                name
            )

            .apply()
    }

    // GET ALL SAVED CARS

    fun getSavedCars():
            List<SavedBluetoothDevice> {

        return prefs.all.map {

            SavedBluetoothDevice(

                name = it.value.toString(),

                address = it.key
            )
        }
    }

    // CHECK IF DEVICE IS SAVED

    fun isSavedCar(
        address: String?
    ): Boolean {

        return prefs.contains(address)
    }

    // REMOVE ONE DEVICE

    fun removeCar(
        address: String
    ) {

        prefs.edit()

            .remove(address)

            .apply()
    }

    // REMOVE ALL DEVICES

    fun clearCars() {

        prefs.edit()
            .clear()
            .apply()
    }
}