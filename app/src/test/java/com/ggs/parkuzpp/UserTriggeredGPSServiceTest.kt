package com.ggs.parkuzpp

import android.content.Context
import android.location.Location
import android.location.LocationManager
import java.util.function.Consumer
import androidx.test.core.app.ApplicationProvider
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
class UserTriggeredGPSServiceTest {
    private lateinit var context: Context
    private lateinit var mockLocationManager: LocationManager
    private lateinit var service: UserTriggeredGPSService

    // ── helpers ──────────────────────────────────────────────────────────────────────────────────

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

    // ── GMS path ─────────────────────────────────────────────────────────────────────────────────

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockLocationManager = mock(LocationManager::class.java)
        service = UserTriggeredGPSService(context)
        injectMockLocationManager()
    }

    // ── formatLocation ───────────────────────────────────────────────────────────────────────────

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

    // ── Android path ─────────────────────────────────────────────────────────────────────────────

    @Test
    fun `uses GPS provider when available`() = runTest {
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
                (inv.getArgument<Consumer<Location?>>(3)).accept(makeLocation(52.0, 16.0))
            }.`when`(mockLocationManager)
                .getCurrentLocation(eq(LocationManager.GPS_PROVIDER), isNull(), any(), any())

            val result = service.getCurrentLocation()

            assertNotNull(result)
            verify(mockLocationManager)
                .getCurrentLocation(eq(LocationManager.GPS_PROVIDER), isNull(), any(), any())
        }
    }

    @Test
    fun `falls back to NETWORK provider when GPS disabled`() = runTest {
        mockStatic(GoogleApiAvailability::class.java).use { mocked ->
            val mockApi = mock(GoogleApiAvailability::class.java)
            mocked.`when`<GoogleApiAvailability> { GoogleApiAvailability.getInstance() }
                .thenReturn(mockApi)
            `when`(mockApi.isGooglePlayServicesAvailable(any()))
                .thenReturn(ConnectionResult.SERVICE_MISSING)

            `when`(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .thenReturn(false)
            `when`(mockLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                .thenReturn(true)

            doAnswer { inv ->
                @Suppress("UNCHECKED_CAST")
                (inv.getArgument<Consumer<Location?>>(3)).accept(makeLocation(52.0, 16.0))
            }.`when`(mockLocationManager)
                .getCurrentLocation(eq(LocationManager.NETWORK_PROVIDER), isNull(), any(), any())

            val result = service.getCurrentLocation()

            assertNotNull(result)
            verify(mockLocationManager)
                .getCurrentLocation(eq(LocationManager.NETWORK_PROVIDER), isNull(), any(), any())
        }
    }

    @Test
    fun `falls back to lastKnownLocation when getCurrentLocation returns null`() = runTest {
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
                (inv.getArgument<Consumer<Location?>>(3)).accept(null) // fresh location null
            }.`when`(mockLocationManager)
                .getCurrentLocation(any(), isNull(), any(), any())

            val lastKnown = makeLocation(51.1, 17.0)
            `when`(mockLocationManager.getLastKnownLocation(any()))
                .thenReturn(lastKnown)

            val result = service.getCurrentLocation()

            assertNotNull(result)
            assertEquals(51.1, result!!.latitude, 0.00001)
            verify(mockLocationManager).getLastKnownLocation(any())
        }
    }

    @Test
    fun `returns null when no provider available`() = runTest {
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

    // ── GMS path ─────────────────────────────────────────────────────────────────────────────────

    @Test
    fun `uses GMS path when Play Services available`() = runTest {
        mockStatic(GoogleApiAvailability::class.java).use { mocked ->
            val mockApi = mock(GoogleApiAvailability::class.java)
            mocked.`when`<GoogleApiAvailability> { GoogleApiAvailability.getInstance() }
                .thenReturn(mockApi)
            `when`(mockApi.isGooglePlayServicesAvailable(any()))
                .thenReturn(ConnectionResult.SUCCESS)

            @Suppress("UNCHECKED_CAST")
            val mockTask = mock(Task::class.java) as Task<Location>
            val mockFusedClient = mock(FusedLocationProviderClient::class.java)

            `when`(mockFusedClient.getCurrentLocation(any<Int>(), isNull()))
                .thenReturn(mockTask)

            doAnswer { inv ->
                @Suppress("UNCHECKED_CAST")
                (inv.getArgument<OnSuccessListener<Location>>(0))
                    .onSuccess(makeLocation(52.5, 13.4))
                mockTask
            }.`when`(mockTask).addOnSuccessListener(any())

            `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

            injectMockFusedClient(mockFusedClient)

            val result = service.getCurrentLocation()

            assertNotNull(result)
            assertEquals(52.5, result!!.latitude, 0.00001)
        }
    }

    @Test
    fun `GMS failure listener returns null`() = runTest {
        mockStatic(GoogleApiAvailability::class.java).use { mocked ->
            val mockApi = mock(GoogleApiAvailability::class.java)
            mocked.`when`<GoogleApiAvailability> { GoogleApiAvailability.getInstance() }
                .thenReturn(mockApi)
            `when`(mockApi.isGooglePlayServicesAvailable(any()))
                .thenReturn(ConnectionResult.SUCCESS)


            @Suppress("UNCHECKED_CAST")
            val mockTask = mock(Task::class.java) as Task<Location>
            val mockFusedClient = mock(FusedLocationProviderClient::class.java)

            `when`(mockFusedClient.getCurrentLocation(any<Int>(), isNull()))
                .thenReturn(mockTask)
            `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)

            doAnswer { inv ->
                (inv.getArgument<OnFailureListener>(0))
                    .onFailure(Exception("GMS error"))
                mockTask
            }.`when`(mockTask).addOnFailureListener(any())

            injectMockFusedClient(mockFusedClient)

            val result = service.getCurrentLocation()
            assertNull(result)
        }
    }
}