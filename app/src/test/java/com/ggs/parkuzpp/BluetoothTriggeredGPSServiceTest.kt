package com.ggs.parkuzpp

import android.content.Context
import android.location.Location
import android.location.LocationManager
import java.util.function.Consumer
import androidx.test.core.app.ApplicationProvider
import com.ggs.parkuzpp.location.BluetoothTriggeredGPSService
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BluetoothTriggeredGPSServiceTest {
    private lateinit var context: Context
    private lateinit var mockLocationManager: LocationManager
    private lateinit var service: BluetoothTriggeredGPSService

    private fun makeLocation(lat: Double, lon: Double) =
        Location("test").apply {
            latitude = lat
            longitude = lon
        }

    private fun injectMockLocationManager() {
        UserTriggeredGPSService::class.java
            .getDeclaredField($$"locationManager$delegate")
            .apply { isAccessible = true }
            .set(service, lazy { mockLocationManager })
    }

    private fun injectMockFusedClient(mockFusedClient: FusedLocationProviderClient?) {
        UserTriggeredGPSService::class.java
            .getDeclaredField($$"fusedLocationClient$delegate")
            .apply { isAccessible = true }
            .set(service, lazy { mockFusedClient })
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        service = BluetoothTriggeredGPSService()
        injectMockLocationManager()
    }

    //
    //@Test
    //fun `something about requestLocation`(){
    //  //idk
    //}
    //
}
