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

import com.irurueta.geometry.*
import com.irurueta.statistics.UniformRandomizer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraConverterTest {

    @Test(expected = IllegalArgumentException::class)
    fun constructor_whenInvalidWidth_throwsIllegalArgumentException() {
        CameraConverter(0, HEIGHT)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructor_whenInvalidHeight_throwsIllegalArgumentException() {
        CameraConverter(WIDTH, 0)
    }

    @Test
    fun constructor_setsDefaultValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertFalse(converter.isModelViewProjectionMatrixComputationEnabled)
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)
    }

    @Test
    fun orientation_whenNoCamera_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check initial value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)

        // set new value
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)
    }

    @Test
    fun orientation_whenCamera_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check initial value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        // set new value
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNotNull(converter.viewCamera)
        assertNotNull(converter.cameraIntrinsicParameters)
        assertNotNull(converter.cameraCenter)
        assertNotNull(converter.cameraRotation)
        assertNotNull(converter.viewCameraIntrinsicParameters)
        assertNotNull(converter.viewCameraCenter)
        assertNotNull(converter.viewCameraRotation)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)

        val viewCamera1 = converter.viewCamera
        requireNotNull(viewCamera1)
        viewCamera1.normalize()
        viewCamera1.fixCameraSign()

        val viewCamera2 = OrientationHelper.toViewCoordinatesCamera(converter.orientation, camera)
        viewCamera2.normalize()
        viewCamera2.fixCameraSign()

        assertTrue(
            viewCamera1.internalMatrix.equals(
                viewCamera2.internalMatrix,
                LARGE_ABSOLUTE_ERROR
            )
        )

        assertSame(camera.intrinsicParameters, converter.cameraIntrinsicParameters)
        assertSame(camera.cameraCenter, converter.cameraCenter)
        assertSame(camera.cameraRotation, converter.cameraRotation)
        assertSame(
            converter.viewCamera?.intrinsicParameters,
            converter.viewCameraIntrinsicParameters
        )
        assertSame(converter.viewCamera?.cameraCenter, converter.viewCameraCenter)
        assertSame(converter.viewCamera?.cameraRotation, converter.viewCameraRotation)
    }

    @Test
    fun projectionMatrix_whenNoCamera_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check initial values
        assertNull(converter.projectionMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)

        // set new value
        val projectionMatrix = createProjectionMatrix()
        converter.projectionMatrix = projectionMatrix

        // check
        assertSame(projectionMatrix, converter.projectionMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
    }

    @Test
    fun projectionMatrix_whenCameraAndUnknownOrientation_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check initial values
        assertNull(converter.projectionMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        // set new value
        val projectionMatrix = createProjectionMatrix()
        converter.projectionMatrix = projectionMatrix

        // check
        assertSame(projectionMatrix, converter.projectionMatrix)
        assertNotNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNotNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNotNull(converter.cameraIntrinsicParameters)
        assertNotNull(converter.cameraCenter)
        assertNotNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(
            modelViewMatrix,
            converter.modelViewMatrix,
            5.0f * SMALL_ABSOLUTE_ERROR.toFloat()
        )

        assertSame(camera.intrinsicParameters, converter.cameraIntrinsicParameters)
        assertSame(camera.cameraCenter, converter.cameraCenter)
        assertSame(camera.cameraRotation, converter.cameraRotation)
    }

    @Test
    fun modelViewMatrix_whenNoCamera_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check initial values
        assertNull(converter.modelViewMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)

        // set new value
        val camera = createCamera()
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val transformation = createTransformation(rotation, center)
        val modelViewMatrix = toModelViewMatrix(transformation)
        converter.modelViewMatrix = modelViewMatrix

        // check
        assertSame(modelViewMatrix, converter.modelViewMatrix)
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
    }

    @Test
    fun modelViewProjectionMatrix_whenEnabled_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)
        converter.isModelViewProjectionMatrixComputationEnabled = true

        // check default values
        assertTrue(converter.isModelViewProjectionMatrixComputationEnabled)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)
        assertNotNull(converter.modelViewProjectionMatrix)

        val projectionMatrix = converter.projectionMatrix
        requireNotNull(projectionMatrix)
        val modelViewMatrix = converter.modelViewMatrix
        requireNotNull(modelViewMatrix)

        val modelViewProjectionMatrix =
            CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
                projectionMatrix,
                modelViewMatrix
            )
        assertArrayEquals(modelViewProjectionMatrix, converter.modelViewProjectionMatrix, 0.0f)
    }

    @Test
    fun modelViewProjectionMatrix_whenDisabledAfterBeingEnabled_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)
        converter.isModelViewProjectionMatrixComputationEnabled = true

        // check default values
        assertTrue(converter.isModelViewProjectionMatrixComputationEnabled)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)
        assertNotNull(converter.modelViewProjectionMatrix)

        // disable
        converter.isModelViewProjectionMatrixComputationEnabled = false

        // check
        assertFalse(converter.isModelViewProjectionMatrixComputationEnabled)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
    }

    @Test
    fun nearPlane_whenNoCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set new value
        val nearPlane = 2.0f * CameraConverter.DEFAULT_NEAR_PLANE
        converter.nearPlane = nearPlane

        // check
        assertEquals(nearPlane, converter.nearPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)
    }

    @Test
    fun nearPlane_whenCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new value
        val nearPlane = 2.0f * CameraConverter.DEFAULT_NEAR_PLANE
        converter.nearPlane = nearPlane

        // check
        assertEquals(nearPlane, converter.nearPlane)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test
    fun farPlane_whenNoCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set new value
        val farPlane = 2.0f * CameraConverter.DEFAULT_FAR_PLANE
        converter.farPlane = farPlane

        // check
        assertEquals(farPlane, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)
    }

    @Test
    fun farPlane_whenCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new value
        val farPlane = 2.0f * CameraConverter.DEFAULT_FAR_PLANE
        converter.farPlane = farPlane

        // check
        assertEquals(farPlane, converter.farPlane)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test
    fun setNearFarPlanes_whenNoCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set new values
        val nearPlane = 2.0f * CameraConverter.DEFAULT_NEAR_PLANE
        val farPlane = 2.0f * CameraConverter.DEFAULT_FAR_PLANE
        converter.setNearFarPlanes(nearPlane, farPlane)

        // check
        assertEquals(nearPlane, converter.nearPlane)
        assertEquals(farPlane, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)
    }

    @Test
    fun setNearFarPlanes_whenCamera_returnsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)
        assertNull(converter.camera)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new values
        val nearPlane = 2.0f * CameraConverter.DEFAULT_NEAR_PLANE
        val farPlane = 2.0f * CameraConverter.DEFAULT_FAR_PLANE
        converter.setNearFarPlanes(nearPlane, farPlane)

        // check
        assertEquals(nearPlane, converter.nearPlane)
        assertEquals(farPlane, converter.farPlane)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun width_whenInvalidValue_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(WIDTH, converter.width)

        // set new value
        converter.width = 0
    }

    @Test
    fun width_whenNoCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(WIDTH, converter.width)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set new value
        converter.width = 2 * WIDTH

        // check
        assertEquals(2 * WIDTH, converter.width)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
    }

    @Test
    fun width_whenCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(WIDTH, converter.width)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new value
        val width = 2 * WIDTH
        converter.width = width

        // check
        assertEquals(width, converter.width)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun height_whenInvalidValue_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(HEIGHT, converter.height)

        // set new value
        converter.height = 0
    }

    @Test
    fun height_whenNoCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set new value
        converter.height = 2 * HEIGHT

        // check
        assertEquals(2 * HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
    }

    @Test
    fun height_whenCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new value
        val height = 2 * HEIGHT
        converter.height = height

        assertEquals(height, converter.height)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setViewportSize_whenInvalidWidth_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)

        // set new values
        converter.setViewportSize(0, HEIGHT)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setViewportSize_whenInvalidHeight_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)

        // set new values
        converter.setViewportSize(WIDTH, 0)
    }

    @Test
    fun setViewportSize_whenNoCamera_returnsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set new value
        val width = 2 * WIDTH
        val height = 2 * HEIGHT
        converter.setViewportSize(width, height)

        // check
        assertEquals(width, converter.width)
        assertEquals(height, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
    }

    @Test
    fun setViewportSize_whenCamera_returnsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)

        val projectionMatrix1 = converter.projectionMatrix
        assertNotNull(projectionMatrix1)

        val modelViewMatrix1 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix1)

        // set new value
        val width = 2 * WIDTH
        val height = 2 * HEIGHT
        converter.setViewportSize(width, height)

        // check
        assertEquals(width, converter.width)
        assertEquals(height, converter.height)
        val projectionMatrix2 = converter.projectionMatrix
        assertNotNull(projectionMatrix2)
        val modelViewMatrix2 = converter.modelViewMatrix
        assertNotNull(modelViewMatrix2)

        val projectionMatrix3 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertSame(projectionMatrix1, projectionMatrix2)
        assertNotSame(projectionMatrix2, projectionMatrix3)
        assertArrayEquals(projectionMatrix2, projectionMatrix3, SMALL_ABSOLUTE_ERROR.toFloat())
        assertSame(modelViewMatrix1, modelViewMatrix2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun camera_whenNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)

        // set null value
        converter.camera = null
    }

    @Test
    fun camera_whenNotNullAndUnknownOrientation_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        // set new value
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        assertNull(converter.viewCamera)
        assertNotNull(converter.cameraIntrinsicParameters)
        assertNotNull(converter.cameraCenter)
        assertNotNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)

        assertSame(camera.intrinsicParameters, converter.cameraIntrinsicParameters)
        assertSame(camera.cameraCenter, converter.cameraCenter)
        assertSame(camera.cameraRotation, converter.cameraRotation)
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCamera_whenNullAndKnownOrientation_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_90_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, converter.orientation)

        // set null value
        converter.viewCamera = null
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCamera_whenNotNullAndUnknownOrientation_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)

        // set not null value
        val viewCamera = createCamera()
        converter.viewCamera = viewCamera
    }

    @Test
    fun viewCamera_whenValid_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertNull(converter.camera)
        assertNull(converter.viewCamera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)

        val viewCamera1 = createCamera()
        converter.viewCamera = viewCamera1

        // check
        assertSame(viewCamera1, converter.viewCamera)

        viewCamera1.normalize()
        viewCamera1.fixCameraSign()
        viewCamera1.decompose()

        val camera1 = converter.camera
        requireNotNull(camera1)
        val viewCamera2 = OrientationHelper.toViewCoordinatesCamera(converter.orientation, camera1)
        val camera2 =
            OrientationHelper.fromViewCoordinatesCamera(converter.orientation, viewCamera1)

        viewCamera2.normalize()
        viewCamera2.fixCameraSign()
        viewCamera2.decompose()

        camera2.normalize()
        camera2.fixCameraSign()
        camera2.decompose()

        assertTrue(
            viewCamera2.internalMatrix.equals(
                viewCamera1.internalMatrix,
                SMALL_ABSOLUTE_ERROR
            )
        )
        assertTrue(
            camera2.internalMatrix.equals(
                camera1.internalMatrix,
                SMALL_ABSOLUTE_ERROR
            )
        )

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera1)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)

        assertSame(
            converter.viewCamera?.intrinsicParameters,
            converter.viewCameraIntrinsicParameters
        )
        assertSame(converter.viewCamera?.cameraCenter, converter.viewCameraCenter)
        assertSame(converter.viewCamera?.cameraRotation, converter.viewCameraRotation)
    }

    @Test
    fun cameraIntrinsicParameters_whenNoCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraIntrinsicParameters)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraIntrinsicParameters_whenNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        converter.cameraIntrinsicParameters = null
    }

    @Test
    fun cameraIntrinsicParameters_whenNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraIntrinsicParameters)

        // set new value
        val intrinsics = createIntrinsicParameters()
        converter.cameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, converter.cameraIntrinsicParameters)
        assertNotNull(converter.camera)
        assertSame(intrinsics, converter.camera?.intrinsicParameters)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun cameraIntrinsicParameters_whenCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraIntrinsicParameters)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        val intrinsics1 = converter.cameraIntrinsicParameters
        assertNotNull(intrinsics1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val intrinsics2 = createIntrinsicParameters()
        converter.cameraIntrinsicParameters = intrinsics2

        // check
        assertSame(intrinsics2, converter.cameraIntrinsicParameters)
        assertSame(camera, converter.camera)
        assertSame(intrinsics2, camera.intrinsicParameters)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun cameraCenter_whenNoCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraCenter)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraCenter_whenNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        converter.cameraCenter = null
    }

    @Test
    fun cameraCenter_whenNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraCenter)

        // set new value
        val center = createCenter()
        converter.cameraCenter = center

        // check
        assertSame(center, converter.cameraCenter)
        assertNotNull(converter.camera)
        assertSame(center, converter.camera?.cameraCenter)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun cameraCenter_whenCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraCenter)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        val center1 = converter.cameraCenter
        assertNotNull(center1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val center2 = createCenter()
        converter.cameraCenter = center2

        // check
        assertSame(center2, converter.cameraCenter)
        assertSame(camera, converter.camera)
        assertSame(center2, camera.cameraCenter)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun cameraRotation_whenNoCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraRotation)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraRotation_whenNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        converter.cameraRotation = null
    }

    @Test
    fun cameraRotation_whenNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraRotation)

        // set new value
        val rotation = createRotation()
        converter.cameraRotation = rotation

        // check
        assertSame(rotation, converter.cameraRotation)
        assertNotNull(converter.camera)
        assertSame(rotation, converter.camera?.cameraRotation)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun cameraRotation_whenCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.camera)
        assertNull(converter.cameraCenter)

        // set camera
        val camera = createCamera()
        converter.camera = camera

        // check
        assertSame(camera, converter.camera)
        val rotation1 = converter.cameraRotation
        assertNotNull(rotation1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val rotation2 = createRotation()
        converter.cameraRotation = rotation2

        // check
        assertSame(rotation2, converter.cameraRotation)
        assertSame(camera, converter.camera)
        assertSame(rotation2, camera.cameraRotation)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraIntrinsicParameters_whenNoViewCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.viewCamera)
        assertNull(converter.viewCameraIntrinsicParameters)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraIntrinsicParameters_whenUnknownOrientation_throwsIllegalStateException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set new value
        val intrinsics = createIntrinsicParameters()
        converter.viewCameraIntrinsicParameters = intrinsics
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraIntrinsicParameters_whenKnownOrientationAndNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set null value
        converter.viewCameraIntrinsicParameters = null
    }

    @Test
    fun viewCameraIntrinsicParameters_whenKnownOrientationAndNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set new value
        val intrinsics = createIntrinsicParameters()
        converter.viewCameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, converter.viewCameraIntrinsicParameters)
        assertNotNull(converter.viewCamera)
        assertSame(intrinsics, converter.viewCamera?.intrinsicParameters)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraIntrinsicParameters_whenKnownOrientationAndCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCamera)

        // set view camera
        val viewCamera = createCamera()
        converter.viewCamera = viewCamera

        // check
        assertSame(viewCamera, converter.viewCamera)
        val intrinsics1 = converter.viewCameraIntrinsicParameters
        assertNotNull(intrinsics1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val intrinsics2 = createIntrinsicParameters()
        converter.viewCameraIntrinsicParameters = intrinsics2

        // check
        assertSame(intrinsics2, converter.viewCameraIntrinsicParameters)
        assertNotNull(converter.viewCamera)
        assertSame(intrinsics2, viewCamera.intrinsicParameters)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraCenter_whenNoViewCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.viewCamera)
        assertNull(converter.viewCameraCenter)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraCenter_whenUnknownOrientation_throwsIllegalStateException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set new value
        val center = createCenter()
        converter.viewCameraCenter = center
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraCenter_whenKnownOrientationAndNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set null value
        converter.viewCameraCenter = null
    }

    @Test
    fun viewCameraCenter_whenKnownOrientationAndNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set new value
        val center = createCenter()
        converter.viewCameraCenter = center

        // check
        assertSame(center, converter.viewCameraCenter)
        assertNotNull(converter.viewCamera)
        assertSame(center, converter.viewCamera?.cameraCenter)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraCenter_whenKnownOrientationAndCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set view camera
        val viewCamera = createCamera()
        converter.viewCamera = viewCamera

        // check
        assertSame(viewCamera, converter.viewCamera)
        val center1 = converter.viewCameraCenter
        assertNotNull(center1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val center2 = createCenter()
        converter.viewCameraCenter = center2

        // check
        assertSame(center2, converter.viewCameraCenter)
        assertNotNull(converter.viewCamera)
        assertSame(center2, viewCamera.cameraCenter)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraRotation_whenNoViewCamera_returnsNull() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        assertNull(converter.viewCamera)
        assertNull(converter.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraRotation_whenUnknownOrientation_throwsIllegalStateException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraRotation)
        assertNull(converter.viewCamera)

        // set new value
        val rotation = createRotation()
        converter.viewCameraRotation = rotation
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraRotation_whenKnownOrientationAndNull_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraRotation)
        assertNull(converter.viewCameraCenter)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set null value
        converter.viewCameraRotation = null
    }

    @Test
    fun viewCameraRotation_whenKnownOrientationAndNoCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCamera)

        // set new value
        val rotation = createRotation()
        converter.viewCameraRotation = rotation

        // check
        assertSame(rotation, converter.viewCameraRotation)
        assertNotNull(converter.viewCamera)
        assertSame(rotation, converter.viewCamera?.cameraRotation)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test
    fun viewCameraRotation_whenKnownOrientationAndCamera_setsExpectedValue() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertNull(converter.viewCameraRotation)
        assertNull(converter.viewCamera)

        // set known orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertNull(converter.viewCameraRotation)
        assertNull(converter.viewCamera)

        // set view camera
        val viewCamera = createCamera()
        converter.viewCamera = viewCamera

        // check
        assertSame(viewCamera, converter.viewCamera)
        val rotation1 = converter.viewCameraRotation
        assertNotNull(rotation1)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        // set new value
        val rotation2 = createRotation()
        converter.viewCameraRotation = rotation2

        // check
        assertSame(rotation2, converter.viewCameraRotation)
        assertNotNull(converter.viewCamera)
        assertSame(rotation2, viewCamera.cameraRotation)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        val camera = converter.camera
        requireNotNull(camera)
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValues_whenInvalidWidth_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        val camera = createCamera()
        converter.setValues(NEAR_PLANE, FAR_PLANE, 0, HEIGHT, camera)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValues_whenInvalidHeight_throwsIllegalArgumentException() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        val camera = createCamera()
        converter.setValues(NEAR_PLANE, FAR_PLANE, WIDTH, 0, camera)
    }

    @Test
    fun setValues_whenValidAndUnknownOrientation_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        val width = 2 * WIDTH
        val height = 2 * HEIGHT
        val camera = createCamera()
        converter.setValues(nearPlane, farPlane, width, height, camera)

        // check
        assertEquals(nearPlane, converter.nearPlane)
        assertEquals(farPlane, converter.farPlane)
        assertEquals(width, converter.width)
        assertEquals(height, converter.height)
        assertSame(camera, converter.camera)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        assertNull(converter.viewCamera)
        assertNotNull(converter.cameraIntrinsicParameters)
        assertNotNull(converter.cameraCenter)
        assertNotNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)

        assertSame(camera.intrinsicParameters, converter.cameraIntrinsicParameters)
        assertSame(camera.cameraCenter, converter.cameraCenter)
        assertSame(camera.cameraRotation, converter.cameraRotation)
    }

    @Test
    fun setValues_whenValidAndKnownOrientation_setsExpectedValues() {
        val converter = CameraConverter(WIDTH, HEIGHT)

        // check default values
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, converter.orientation)
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        // set orientation
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, converter.orientation)
        assertEquals(CameraConverter.DEFAULT_NEAR_PLANE, converter.nearPlane)
        assertEquals(CameraConverter.DEFAULT_FAR_PLANE, converter.farPlane)
        assertEquals(WIDTH, converter.width)
        assertEquals(HEIGHT, converter.height)
        assertNull(converter.camera)
        assertNull(converter.projectionMatrix)
        assertNull(converter.modelViewMatrix)
        assertNull(converter.modelViewProjectionMatrix)

        assertNull(converter.viewCamera)
        assertNull(converter.cameraIntrinsicParameters)
        assertNull(converter.cameraCenter)
        assertNull(converter.cameraRotation)
        assertNull(converter.viewCameraIntrinsicParameters)
        assertNull(converter.viewCameraCenter)
        assertNull(converter.viewCameraRotation)

        // set values
        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        val width = 2 * WIDTH
        val height = 2 * HEIGHT
        val camera1 = createCamera()
        converter.setValues(nearPlane, farPlane, width, height, camera1)

        // check
        assertEquals(nearPlane, converter.nearPlane)
        assertEquals(farPlane, converter.farPlane)
        assertEquals(width, converter.width)
        assertEquals(height, converter.height)
        assertSame(camera1, converter.camera)
        assertNotNull(converter.projectionMatrix)
        assertNotNull(converter.modelViewMatrix)

        assertNotNull(converter.viewCamera)
        assertNotNull(converter.cameraIntrinsicParameters)
        assertNotNull(converter.cameraCenter)
        assertNotNull(converter.cameraRotation)
        assertNotNull(converter.viewCameraIntrinsicParameters)
        assertNotNull(converter.viewCameraCenter)
        assertNotNull(converter.viewCameraRotation)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1,
            converter.width,
            converter.height,
            converter.nearPlane,
            converter.farPlane
        )
        assertArrayEquals(projectionMatrix, converter.projectionMatrix, 0.0f)

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera1)
        assertArrayEquals(modelViewMatrix, converter.modelViewMatrix, 0.0f)

        assertSame(camera1.intrinsicParameters, converter.cameraIntrinsicParameters)
        assertSame(camera1.cameraCenter, converter.cameraCenter)
        assertSame(camera1.cameraRotation, converter.cameraRotation)

        val viewCamera1 = converter.viewCamera
        requireNotNull(viewCamera1)

        viewCamera1.normalize()
        viewCamera1.fixCameraSign()
        viewCamera1.decompose()

        val viewCamera2 = OrientationHelper.toViewCoordinatesCamera(converter.orientation, camera1)
        val camera2 =
            OrientationHelper.fromViewCoordinatesCamera(converter.orientation, viewCamera1)

        viewCamera2.normalize()
        viewCamera2.fixCameraSign()
        viewCamera2.decompose()

        camera2.normalize()
        camera2.fixCameraSign()
        camera2.decompose()

        assertTrue(
            viewCamera2.internalMatrix.equals(
                viewCamera1.internalMatrix,
                SMALL_ABSOLUTE_ERROR
            )
        )
        assertTrue(
            camera2.internalMatrix.equals(
                camera1.internalMatrix,
                SMALL_ABSOLUTE_ERROR
            )
        )

        assertSame(
            converter.viewCamera?.intrinsicParameters,
            converter.viewCameraIntrinsicParameters
        )
        assertSame(converter.viewCamera?.cameraCenter, converter.viewCameraCenter)
        assertSame(converter.viewCamera?.cameraRotation, converter.viewCameraRotation)
    }

    private companion object {
        const val WIDTH = 480
        const val HEIGHT = 640

        const val MIN_FOCAL_LENGTH = 500.0
        const val MAX_FOCAL_LENGTH = 510.0

        const val MIN_SKEWNESS = -1e-3
        const val MAX_SKEWNESS = 1e-3

        const val HORIZONTAL_PRINCIPAL_POINT = 238.86550903320312
        const val VERTICAL_PRINCIPAL_POINT = 322.5972595214844

        const val MIN_ROTATION_ANGLE_DEGREES = -45.0
        const val MAX_ROTATION_ANGLE_DEGREES = 45.0

        const val MIN_POS = -50.0
        const val MAX_POS = 50.0

        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 100.0f

        const val LARGE_ABSOLUTE_ERROR = 5e-3

        const val SMALL_ABSOLUTE_ERROR = 1e-6

        private fun createIntrinsicParameters(): PinholeCameraIntrinsicParameters {
            val randomizer = UniformRandomizer()
            val horizontalFocalLength = randomizer.nextDouble(MIN_FOCAL_LENGTH, MAX_FOCAL_LENGTH)
            val verticalFocalLength = -randomizer.nextDouble(MIN_FOCAL_LENGTH, MAX_FOCAL_LENGTH)
            val skewness = randomizer.nextDouble(MIN_SKEWNESS, MAX_SKEWNESS)
            return PinholeCameraIntrinsicParameters(
                horizontalFocalLength,
                verticalFocalLength,
                HORIZONTAL_PRINCIPAL_POINT,
                VERTICAL_PRINCIPAL_POINT,
                skewness
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
            val x = randomizer.nextDouble(MIN_POS, MAX_POS)
            val y = randomizer.nextDouble(MIN_POS, MAX_POS)
            val z = randomizer.nextDouble(MIN_POS, MAX_POS)
            return InhomogeneousPoint3D(x, y, z)
        }

        private fun createCamera(): PinholeCamera {
            val intrinsicParameters = createIntrinsicParameters()
            val rotation = createRotation()
            val center = createCenter()
            return PinholeCamera(intrinsicParameters, rotation, center)
        }

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

        private fun createTransformation(rotation: Rotation3D, center: Point3D): Transformation3D {
            val invRotation = rotation.inverseRotationAndReturnNew()
            val result = EuclideanTransformation3D(invRotation)
            result.setTranslation(center)
            return result
        }

        private fun toModelViewMatrix(transformation: Transformation3D): FloatArray {
            val result = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH)
            val matrix = transformation.asMatrix()
            val buffer = matrix.buffer
            for (i in buffer.indices) {
                result[i] = buffer[i].toFloat()
            }

            return result
        }
    }
}