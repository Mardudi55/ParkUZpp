package com.ggs.parkuzpp

import android.net.Uri
import com.ggs.parkuzpp.camera.CameraViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner

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
    fun `confirmPhoto emits uri to photoSaved flow`() = runTest {
        val mockUri = mock<Uri>()
        val emittedUris = mutableListOf<Uri>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.photoSaved.collect { emittedUris.add(it) }
        }

        viewModel.setCaptured(mockUri)
        viewModel.confirmPhoto()
        advanceUntilIdle()

        Assert.assertTrue(emittedUris.contains(mockUri))
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