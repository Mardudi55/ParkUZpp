package com.ggs.parkuzpp

import android.content.Context
import android.location.Location
import android.location.LocationManager
import java.util.function.Consumer
import androidx.test.core.app.ApplicationProvider
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.isNull
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GPSUtilsTest {
    private lateinit var context: Context
    private lateinit var mockLocationManager: LocationManager
    private lateinit var service: UserTriggeredGPSService

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

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockLocationManager = mock(LocationManager::class.java)
        service = UserTriggeredGPSService(context)
        injectMockLocationManager()
    }

    @Test
    fun `formatLocation rounds latitude and longitude to 5 decimal places`() = runTest {
        mockStatic(GoogleApiAvailability::class.java).use { mocked ->
            val mockApi = mock(GoogleApiAvailability::class.java)
            mocked.`when`<GoogleApiAvailability> { GoogleApiAvailability.getInstance() }
                .thenReturn(mockApi)
            `when`(mockApi.isGooglePlayServicesAvailable(any()))
                .thenReturn(ConnectionResult.SERVICE_MISSING)

            `when`(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .thenReturn(true)

            doAnswer { inv ->
                @Suppress("UNCHECKED_CAST")
                (inv.getArgument<Consumer<Location?>>(3))
                    .accept(makeLocation(52.123456789, 16.987654321))
            }.`when`(mockLocationManager)
                .getCurrentLocation(any(), isNull(), any(), any())

            val result = service.getCurrentLocation()

            assertNotNull(result)
            assertEquals(52.12346, result!!.latitude,  0.000001)
            assertEquals(16.98765, result.longitude,   0.000001)
        }
    }

    @Test
    fun `formatLocation returns null when location is null`() = runTest {
        mockStatic(GoogleApiAvailability::class.java).use { mocked ->
            val mockApi = mock(GoogleApiAvailability::class.java)
            mocked.`when`<GoogleApiAvailability> { GoogleApiAvailability.getInstance() }
                .thenReturn(mockApi)
            `when`(mockApi.isGooglePlayServicesAvailable(any()))
                .thenReturn(ConnectionResult.SERVICE_MISSING)

            `when`(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .thenReturn(false)
            `when`(mockLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                .thenReturn(false)

            val result = service.getCurrentLocation()
            assertNull(result)
        }
    }

}