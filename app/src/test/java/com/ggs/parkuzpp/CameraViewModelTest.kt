package com.ggs.parkuzpp

import android.content.Context
import android.net.Uri
import com.ggs.parkuzpp.camera.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [CameraViewModel].
 * Uses Robolectric for Android framework dependencies and Coroutines Test API
 * for handling asynchronous operations and flows.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CameraViewModelTest {

    private lateinit var viewModel: CameraViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CameraViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setCaptured updates lastCapturedUri`() {
        val mockUri = mock<Uri>()

        viewModel.setCaptured(mockUri)

        Assert.assertEquals(mockUri, viewModel.lastCapturedUri)
    }

    @Test
    fun `confirmPhoto emits signal to photoSaved flow`() = runTest {
        val mockUri = mock<Uri>()
        val mockContext = mock<Context>()
        var signalEmitted = false

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.photoSaved.collect { signalEmitted = true }
        }

        viewModel.setCaptured(mockUri)
        viewModel.confirmPhotoAndSave(mockContext)
        advanceUntilIdle()

        Assert.assertTrue("Event should be emitted to photoSaved flow", signalEmitted)
        job.cancel()
    }

    @Test
    fun `discardPhoto clears lastCapturedUri`() {
        val mockUri = mock<Uri>()
        viewModel.setCaptured(mockUri)
        viewModel.discardPhoto()

        Assert.assertNull(viewModel.lastCapturedUri)
    }
}