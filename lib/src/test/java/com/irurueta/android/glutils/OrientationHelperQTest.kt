package com.irurueta.android.glutils

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class OrientationHelperQTest {

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)

        val context = mockk<Context>()
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenDisplayRotationUnknownAndSdk29_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)

        val display = mockk<Display>()
        every { display.rotation }.returns(-1)

        val windowManager = mockk<WindowManager>()
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    private companion object {
        const val CAMERA_ID = "1"
    }
}