package com.ggs.parkuzpp

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.ggs.parkuzpp.location.BluetoothTriggeredGPSService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.isNull
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BluetoothTriggeredGPSServiceTest {

    private lateinit var service: BluetoothTriggeredGPSService
    private lateinit var mockFusedClient: FusedLocationProviderClient
    private lateinit var mockCallback: BluetoothTriggeredGPSService.LocationCallback

    private fun makeLocation(lat: Double, lon: Double) =
        Location("test").apply {
            latitude = lat
            longitude = lon
        }

    private fun taskWithLocation(location: Location?): Task<Location> =
        Tasks.forResult(location)

    private fun taskWithFailure(): Task<Location> =
        Tasks.forException(Exception("GPS error"))

    private fun injectMockFusedClient() {
        BluetoothTriggeredGPSService::class.java
            .getDeclaredField("fusedLocationClient")
            .apply { isAccessible = true }
            .set(service, mockFusedClient)
    }

    @Before
    fun setUp() {
        mockFusedClient = mock(FusedLocationProviderClient::class.java)
        mockCallback = mock(BluetoothTriggeredGPSService.LocationCallback::class.java)
        service = Robolectric.buildService(BluetoothTriggeredGPSService::class.java)
            .create()
            .get()

        Shadows.shadowOf(ApplicationProvider.getApplicationContext<Application>())
            .grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)

        injectMockFusedClient()
    }

    @Test
    fun `returns cached location immediately`() {
        val location = makeLocation(52.0, 21.0)
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithLocation(location))

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        verify(mockCallback).onLocationReceived(location)
        verify(mockCallback, never()).onLocationFailed()
    }

    @Test
    fun `cached location has correct coordinates`() {
        val location = makeLocation(52.2297, 21.0122)
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithLocation(location))

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        argumentCaptor<Location>().apply {
            verify(mockCallback).onLocationReceived(capture())
            assertEquals(52.2297, firstValue.latitude, 0.00001)
            assertEquals(21.0122, firstValue.longitude, 0.00001)
        }
    }

    @Test
    fun `requests fresh fix when no cached location`() {
        val freshLocation = makeLocation(52.0, 21.0)
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithLocation(null))
        `when`(mockFusedClient.getCurrentLocation(any<Int>(), isNull()))
            .thenReturn(taskWithLocation(freshLocation))

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        verify(mockCallback).onLocationReceived(freshLocation)
        verify(mockCallback, never()).onLocationFailed()
    }

    @Test
    fun `calls onLocationFailed when fresh fix returns null`() {
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithLocation(null))
        `when`(mockFusedClient.getCurrentLocation(any<Int>(), isNull()))
            .thenReturn(taskWithLocation(null))

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        verify(mockCallback).onLocationFailed()
        verify(mockCallback, never()).onLocationReceived(any<Location>())
    }

    @Test
    fun `calls onLocationFailed when fused client throws`() {
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithFailure())

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        verify(mockCallback).onLocationFailed()
        verify(mockCallback, never()).onLocationReceived(any<Location>())
    }

    @Test
    fun `fresh fix failure calls onLocationFailed`() {
        `when`(mockFusedClient.lastLocation).thenReturn(taskWithLocation(null))
        `when`(mockFusedClient.getCurrentLocation(any<Int>(), isNull()))
            .thenReturn(taskWithFailure())

        service.requestLocation(mockCallback)
        shadowOf(Looper.getMainLooper()).idle()

        verify(mockCallback).onLocationFailed()
        verify(mockCallback, never()).onLocationReceived(any<Location>())
    }
}
