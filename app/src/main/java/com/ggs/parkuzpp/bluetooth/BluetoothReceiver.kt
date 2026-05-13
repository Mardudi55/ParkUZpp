package com.ggs.parkuzpp.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.ggs.parkuzpp.model.Coordinates
import com.ggs.parkuzpp.model.ParkSpot
import com.ggs.parkuzpp.model.ParkingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.Locale

class BluetoothReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (
            intent.action !=
            BluetoothDevice.ACTION_ACL_DISCONNECTED
        ) return

        val device =

            intent.getParcelableExtra<BluetoothDevice>(
                BluetoothDevice.EXTRA_DEVICE
            )

        val repository =
            BluetoothRepository(context)

        // IGNORE NON-SAVED DEVICES

        if (
            !repository.isSavedCar(
                device?.address
            )
        ) {

            Log.d(
                "BT",
                "Ignored device"
            )

            return
        }

        Log.d(
            "BT",
            "Saved car disconnected: ${device?.name}"
        )

        val gpsService =
            UserTriggeredGPSService(context)

        val parkingRepository =
            ParkingRepository()

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val location =
                    gpsService.getCurrentLocation()

                if (location != null) {

                    val spot =

                        ParkSpot(

                            active = true,

                            coordinates =

                                Coordinates(

                                    lat = location.latitude,

                                    lng = location.longitude
                                ),

                            label = getAddressLabel(
                                context,
                                location.latitude,
                                location.longitude
                            )
                        )

                    val result =

                        parkingRepository
                            .saveParkingSpot(
                                spot
                            )

                    if (
                        result.isSuccess
                    ) {

                        Log.d(
                            "BT",
                            "Parking saved successfully"
                        )

                    } else {

                        Log.e(
                            "BT",
                            "Failed to save parking"
                        )
                    }

                } else {

                    Log.e(
                        "BT",
                        "Location is null"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "BT",
                    "Error: ${e.message}"
                )
            }
        }
    }
    private fun getAddressLabel(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {

        return try {

            val geocoder = Geocoder(
                context,
                Locale.getDefault()
            )

            val addresses =

                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )

            if (
                !addresses.isNullOrEmpty()
            ) {

                val address = addresses[0]

                when {

                    !address.thoroughfare.isNullOrEmpty() -> {

                        if (
                            !address.featureName.isNullOrEmpty()
                        ) {

                            "${address.thoroughfare} ${address.featureName}"

                        } else {

                            address.thoroughfare
                        }
                    }

                    !address.locality.isNullOrEmpty() -> {

                        address.locality
                    }

                    else -> {

                        "Saved Parking"
                    }
                }

            } else {

                "Saved Parking"
            }

        } catch (e: Exception) {

            "Saved Parking"
        }
    }
}