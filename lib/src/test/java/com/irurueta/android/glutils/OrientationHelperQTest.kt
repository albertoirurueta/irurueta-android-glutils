/*
 * Copyright (C) 2021 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irurueta.android.glutils

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class OrientationHelperQTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var cameraCharacteristics: CameraCharacteristics

    @MockK
    private lateinit var cameraManager: CameraManager

    @MockK
    private lateinit var display: Display

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var windowManager: WindowManager

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraIdSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, CAMERA_ID)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientation_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientation(context, cameraCharacteristics)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraIdSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation0DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation90DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(90)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation180DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(180)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation0AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_0)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(270, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation90AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_90)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(180, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation180AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_180)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(90, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenCameraCharacteristicsSensorOrientation270DisplayRotation270AndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(270)
        every { display.rotation }.returns(Surface.ROTATION_270)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result =
            OrientationHelper.getCameraDisplayOrientationDegrees(context, cameraCharacteristics)
        assertEquals(0, result)
    }

    @Config(sdk = [Build.VERSION_CODES.Q])
    @Test
    fun getCameraDisplayOrientationDegrees_whenDisplayRotationUnknownAndSdk29_returnsExpectedValue() {
        every { cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) }.returns(0)
        every { cameraManager.getCameraCharacteristics(any()) }.returns(cameraCharacteristics)
        every { context.getSystemService(Context.CAMERA_SERVICE) }.returns(cameraManager)
        every { display.rotation }.returns(-1)
        every { windowManager.defaultDisplay }.returns(display)
        every { context.getSystemService(Context.WINDOW_SERVICE) }.returns(windowManager)

        val result = OrientationHelper.getCameraDisplayOrientationDegrees(context, CAMERA_ID)
        assertEquals(0, result)
    }

    private companion object {
        const val CAMERA_ID = "1"
    }
}