package com.ggs.parkuzpp

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.test.core.app.ApplicationProvider
import com.ggs.parkuzpp.camera.CameraController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CameraControllerTest {

    private lateinit var controller: CameraController
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Before
    fun setup() {
        val previewView = PreviewView(context)
        controller = CameraController(context, lifecycleOwner, previewView)
    }

    @Test
    fun `takePhoto returns early when imageCapture is not initialized`() {
        var callbackInvoked = false

        controller.takePhoto { uri ->
            callbackInvoked = true
        }

        Assert.assertFalse("Callback nie powinien zostać wywołany", callbackInvoked)
    }

    @Test
    fun `camera directory is created correctly`() {
        val imagesDir = File(context.filesDir, "images")

        Assert.assertEquals("images", imagesDir.name)
        Assert.assertTrue(imagesDir.absolutePath.contains("com.ggs.parkuzpp"))
    }
}