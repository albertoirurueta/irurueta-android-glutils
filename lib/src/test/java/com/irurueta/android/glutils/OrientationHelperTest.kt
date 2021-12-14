package com.irurueta.android.glutils

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import com.irurueta.geometry.*
import com.irurueta.statistics.UniformRandomizer
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class OrientationHelperTest {

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun checkConstants() {
        assertEquals(0, OrientationHelper.ORIENTATION_0_DEGREES)
        assertEquals(90, OrientationHelper.ORIENTATION_90_DEGREES)
        assertEquals(180, OrientationHelper.ORIENTATION_180_DEGREES)
        assertEquals(270, OrientationHelper.ORIENTATION_270_DEGREES)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_90)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_180)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_270)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.R])
    @Test
    fun getCameraDisplayOrientationDegrees_whenDisplayRotationUnknownAndSdk30_returnsExpectedValue() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(-1)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation0_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation90_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation180_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation270_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation0_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation90_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation180_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation270_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation0New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation90New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation180New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraIdSensorOrientation270New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation0New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation90New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation180New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation270New_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.toViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation0_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation90_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(-90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation180_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(-180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation270_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID, result)
        assertEquals(-270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation0_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation90_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(-90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation180_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(-180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation270_returnsExpectedRotation() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = Rotation2D()
        OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics, result)
        assertEquals(-270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation0New_returnExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation90New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(-90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation180New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(-180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraIdSensorOrientation270New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, CAMERA_ID)
        assertEquals(-270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation0New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(0.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameracharacteristicsSensorOrientation90New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(-90.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation180New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(-180.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun fromViewCoordinatesRotation_whenCameraCharacteristicsSensorOrientation270New_returnsExpectedResult() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val result = OrientationHelper.fromViewCoordinatesRotation(context, cameraCharacteristics)
        assertEquals(-270.0, Math.toDegrees(result.theta), 0.0)
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation0AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation90AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation180AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation270AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation0AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation90AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation180AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation270AndIntrinsics_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation0AndIntrinsics_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation90AndIntrinsics_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation180AndIntrinsics_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation270AndIntrinsics_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientationUnknownAndIntrinsics_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenRotationAndIntrinsics_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val rotation = Rotation2D(angle)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            rotation,
            intrinsics,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            rotation,
            intrinsics,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation0AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot, toViewResult)

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation90AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot, toViewResult)

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation180AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot, toViewResult)

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation270AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot, toViewResult)

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation0AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation90AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation180AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation270AndPivot_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation0AndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation90AndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation180AndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation270AndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientationUnknownAndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenRotationAndPivot_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val rotation = Rotation2D(angle)

        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = ProjectiveTransformation2D()
        OrientationHelper.toViewCoordinatesTransformation(
            rotation,
            pivot,
            toViewResult
        )

        val fromViewResult = ProjectiveTransformation2D()
        OrientationHelper.fromViewCoordinatesTransformation(
            rotation,
            pivot,
            fromViewResult
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation0AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation90AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation180AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation270AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation0AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation90AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation180AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation270AndIntrinsicsNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation0AndIntrinsicsNew_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation90AndIntrinsicsNew_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation180AndIntrinsicsNew_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation270AndIntrinsicsNew_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            intrinsics
        )

        // check
        toViewResult.t = toViewResult.t
        fromViewResult.t = fromViewResult.t

        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientationUnknownAndIntrinsicsNew_areInverseTransformations() {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenRotationAndIntrinsicsNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val rotation = Rotation2D(angle)

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            rotation,
            intrinsics
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            rotation,
            intrinsics
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation0AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult =
            OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot)

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation90AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult =
            OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot)

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation180AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult =
            OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot)

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraIdSensorOrientation270AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult =
            OrientationHelper.toViewCoordinatesTransformation(context, CAMERA_ID, pivot)

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            CAMERA_ID,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation0AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation90AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation180AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenCameraCharacteristicsSensorOrientation270AndPivotNew_areInverseTransformations() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            context,
            cameraCharacteristics,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation0AndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation90AndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation180AndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientation270AndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenOrientationUnknownAndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesTransformation_whenRotationAndPivotNew_areInverseTransformations() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val rotation = Rotation2D(angle)

        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val toViewResult = OrientationHelper.toViewCoordinatesTransformation(
            rotation,
            pivot
        )

        val fromViewResult = OrientationHelper.fromViewCoordinatesTransformation(
            rotation,
            pivot
        )

        // check
        toViewResult.normalize()
        fromViewResult.normalize()

        val invToViewResult = toViewResult.inverseAndReturnNew() as ProjectiveTransformation2D
        val invFromViewResult = fromViewResult.inverseAndReturnNew() as ProjectiveTransformation2D

        invToViewResult.normalize()
        invFromViewResult.normalize()

        assertTrue(toViewResult.t.equals(invFromViewResult.t, SMALL_ABSOLUTE_ERROR))
        assertTrue(fromViewResult.t.equals(invToViewResult.t, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation0_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera, openGlCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation90_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera, openGlCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation180_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera, openGlCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation270_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera, openGlCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation0_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation90_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation180_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation270_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation0_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation90_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation180_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation270_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientationUnknown_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenRotation_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            viewRotation,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation0AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation90AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation180AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation270AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera, viewCamera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation0AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation90AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation180AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation270AndPivot_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation0AndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation90AndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation180AndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation270AndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientationUnknownAndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenRotationAndPivot_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val viewCamera = PinholeCamera()
        OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            pivot,
            camera,
            viewCamera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = PinholeCamera()
        OrientationHelper.fromViewCoordinatesCamera(
            viewRotation,
            pivot,
            viewCamera,
            openGlCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation0New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera =
            OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation90New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera =
            OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation180New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera =
            OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation270New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera =
            OrientationHelper.fromViewCoordinatesCamera(context, CAMERA_ID, viewCamera)

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation0New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation90New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation180New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation270New_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation0New_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation90New_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation180New_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera =
            OrientationHelper.toViewCoordinatesCamera(
                CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
                camera
            )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation270New_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientationUnknownNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenRotationNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            viewRotation,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation0PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera =
            OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation90PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera =
            OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation180PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera =
            OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraIdSensorOrientation270PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val cameraManager = mockk<CameraManager>()
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera =
            OrientationHelper.toViewCoordinatesCamera(context, CAMERA_ID, pivot, camera)

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            CAMERA_ID,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation0PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation90PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation180PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenCameraCharacteristicsOrientation270PivotNew_areInverseCameras() {
        val cameraCharacteristics = mockk<CameraCharacteristics>()
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)

        val display = mockk<Display>()
        every { display.rotation }.returns(Surface.ROTATION_0)

        val context = mockk<Context>()
        every { context.display }.returns(display)

        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            context,
            cameraCharacteristics,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation0PivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation90PivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation180PivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientation270PivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenOrientationUnknownPivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        // convert
        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun toAndFromViewCoordinatesCamera_whenRotationPivotNew_areInverseCameras() {
        // camera with no orientation
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // convert
        val randomizer = UniformRandomizer()
        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        val openGlCamera = OrientationHelper.fromViewCoordinatesCamera(
            viewRotation,
            pivot,
            viewCamera
        )

        openGlCamera.normalize()
        openGlCamera.fixCameraSign()
        openGlCamera.decompose()

        assertTrue(openGlCamera.internalMatrix.equals(camera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun transformCamera_returnsExpectedCamera() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val transformation = OrientationHelper.toViewCoordinatesTransformation(
            viewRotation,
            pivot
        )

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // transform camera
        val result = PinholeCamera()
        OrientationHelper.transformCamera(transformation, camera, result)

        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        result.normalize()
        result.fixCameraSign()
        result.decompose()

        assertTrue(result.internalMatrix.equals(viewCamera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun transformCamera_whenNewInstance_returnsExpectedCamera() {
        val randomizer = UniformRandomizer()
        val angle = Math.toRadians(
            randomizer.nextDouble(
                MIN_ROTATION_ANGLE_DEGREES,
                MAX_ROTATION_ANGLE_DEGREES
            )
        )
        val viewRotation = Rotation2D(angle)

        val pivot = InhomogeneousPoint2D(
            randomizer.nextDouble(MIN_POS, MAX_POS),
            randomizer.nextDouble(MIN_POS, MAX_POS)
        )

        val transformation = OrientationHelper.toViewCoordinatesTransformation(
            viewRotation,
            pivot
        )

        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera = PinholeCamera(intrinsics, rotation, center)

        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()

        // transform camera
        val result = OrientationHelper.transformCamera(transformation, camera)

        val viewCamera = OrientationHelper.toViewCoordinatesCamera(
            viewRotation,
            pivot,
            camera
        )

        viewCamera.normalize()
        viewCamera.fixCameraSign()
        viewCamera.decompose()

        result.normalize()
        result.fixCameraSign()
        result.decompose()

        assertTrue(result.internalMatrix.equals(viewCamera.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun convertOrientationDegreesToEnum() {
        val name = "convertOrientationDegreesToEnum"
        assertEquals(
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES,
            OrientationHelper.callPrivateStaticFunc(name, 0)
        )
        assertEquals(
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES,
            OrientationHelper.callPrivateStaticFunc(name, 90)
        )
        assertEquals(
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES,
            OrientationHelper.callPrivateStaticFunc(name, 180)
        )
        assertEquals(
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES,
            OrientationHelper.callPrivateStaticFunc(name, 270)
        )
        assertEquals(
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN,
            OrientationHelper.callPrivateStaticFunc(name, 45)
        )
    }

    @Test
    fun convertOrientationEnumToRadians() {
        val name = "convertOrientationEnumToRadians"
        assertEquals(
            0.0,
            OrientationHelper.callPrivateStaticFunc(
                name,
                CameraToDisplayOrientation.ORIENTATION_0_DEGREES
            ) as Double,
            0.0
        )
        assertEquals(
            Math.PI / 2.0,
            OrientationHelper.callPrivateStaticFunc(
                name,
                CameraToDisplayOrientation.ORIENTATION_90_DEGREES
            ) as Double,
            0.0

        )
        assertEquals(
            Math.PI,
            OrientationHelper.callPrivateStaticFunc(
                name,
                CameraToDisplayOrientation.ORIENTATION_180_DEGREES
            ) as Double,
            0.0
        )
        assertEquals(
            3.0 * Math.PI / 2.0,
            OrientationHelper.callPrivateStaticFunc(
                name,
                CameraToDisplayOrientation.ORIENTATION_270_DEGREES
            ) as Double,
            0.0
        )
        assertNull(
            OrientationHelper.callPrivateStaticFunc(
                name,
                CameraToDisplayOrientation.ORIENTATION_UNKNOWN
            )
        )
    }

    @Test
    fun convertOrientationEnumToRotation() {
        val name = "convertOrientationEnumToRotation"
        var rotation: Rotation2D? = OrientationHelper.callPrivateStaticFunc(
            name,
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        ) as Rotation2D?

        requireNotNull(rotation)
        assertEquals(0.0, rotation.theta, 0.0)

        rotation = OrientationHelper.callPrivateStaticFunc(
            name,
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES
        ) as Rotation2D?

        requireNotNull(rotation)
        assertEquals(Math.PI / 2.0, rotation.theta, 0.0)

        rotation = OrientationHelper.callPrivateStaticFunc(
            name,
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES
        ) as Rotation2D?

        requireNotNull(rotation)
        assertEquals(Math.PI, rotation.theta, 0.0)

        rotation = OrientationHelper.callPrivateStaticFunc(
            name,
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES
        ) as Rotation2D?

        requireNotNull(rotation)
        assertEquals(3.0 * Math.PI / 2.0, rotation.theta, 0.0)

        rotation = OrientationHelper.callPrivateStaticFunc(
            name,
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        ) as Rotation2D?

        assertNull(rotation)
    }

    private companion object {
        const val CAMERA_ID = "1"

        const val WIDTH = 480
        const val HEIGHT = 640

        const val MIN_ROTATION_ANGLE_DEGREES = -45.0
        const val MAX_ROTATION_ANGLE_DEGREES = 45.0

        const val MIN_POS = -50.0
        const val MAX_POS = 50.0

        const val SMALL_ABSOLUTE_ERROR = 1e-6

        private fun createProjectionMatrix(): FloatArray {
            // Pixel 2 device has the following projection matrix
            // [2.8693345   0.0         -0.004545755        0.0         ]
            // [0.0	        1.5806589   0.009158132         0.0         ]
            // [0.0	        0.0         -1.002002           -0.2002002  ]
            // [0.0         0.0         -1.0                0.0         ]

            // android.opengl.Matrix defines values column-wise
            return floatArrayOf(
                2.8693345f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.5806589f, 0.0f, 0.0f,
                -0.004545755f, 0.009158132f, -1.002002f, -1.0f,
                0.0f, 0.0f, -0.2002002f, 0.0f
            )
        }

        private fun createRotation(): Rotation3D {
            val randomizer = UniformRandomizer()
            val roll = Math.toRadians(
                randomizer.nextDouble(
                    MIN_ROTATION_ANGLE_DEGREES,
                    MAX_ROTATION_ANGLE_DEGREES
                )
            )
            val pitch = Math.toRadians(
                randomizer.nextDouble(
                    MIN_ROTATION_ANGLE_DEGREES,
                    MAX_ROTATION_ANGLE_DEGREES
                )
            )
            val yaw = Math.toRadians(
                randomizer.nextDouble(
                    MIN_ROTATION_ANGLE_DEGREES,
                    MAX_ROTATION_ANGLE_DEGREES
                )
            )

            return Quaternion(roll, pitch, yaw)
        }

        private fun createCenter(): Point3D {
            val randomizer = UniformRandomizer()
            val x = randomizer.nextDouble(
                MIN_POS,
                MAX_POS
            )
            val y = randomizer.nextDouble(
                MIN_POS,
                MAX_POS
            )
            val z = randomizer.nextDouble(
                MIN_POS,
                MAX_POS
            )
            return InhomogeneousPoint3D(x, y, z)
        }
    }
}