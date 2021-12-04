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
package com.irurueta.android.glutils.cube

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.irurueta.algebra.Matrix
import com.irurueta.android.glutils.CameraToDisplayOrientation
import com.irurueta.android.glutils.getPrivateProperty
import com.irurueta.geometry.*
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.microedition.khronos.opengles.GL10

@RunWith(RobolectricTestRunner::class)
class CubeRendererTest {

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        assertNull(renderer.getPrivateProperty("converter"))

        val modelViewProjectionMatrix: FloatArray? =
            renderer.getPrivateProperty("modelViewProjectionMatrix")
        requireNotNull(modelViewProjectionMatrix)
        assertEquals(16, modelViewProjectionMatrix.size)

        val modelViewMatrix: FloatArray? = renderer.getPrivateProperty("modelViewMatrix")
        requireNotNull(modelViewMatrix)
        assertEquals(16, modelViewMatrix.size)

        assertNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))

        val normalMatrix: FloatArray? = renderer.getPrivateProperty("normalMatrix")
        requireNotNull(normalMatrix)
        assertEquals(9, normalMatrix.size)

        val cubeVerticesCoordinatesData: FloatArray? =
            renderer.getPrivateProperty("cubeVerticesCoordinatesData")
        requireNotNull(cubeVerticesCoordinatesData)
        assertEquals(24, cubeVerticesCoordinatesData.size)

        val cubeIndices: IntArray? = renderer.getPrivateProperty("cubeIndices")
        requireNotNull(cubeIndices)
        assertEquals(36, cubeIndices.size)

        val uniforms: IntArray? = renderer.getPrivateProperty("uniforms")
        requireNotNull(uniforms)
        assertEquals(6, uniforms.size)

        val program: Int? = renderer.getPrivateProperty("program")
        requireNotNull(program)
        assertEquals(0, program)

        val vertexPositionAttribute: Int? = renderer.getPrivateProperty("vertexPositionAttribute")
        requireNotNull(vertexPositionAttribute)
        assertEquals(0, vertexPositionAttribute)

        val vertexNormalAttribute: Int? = renderer.getPrivateProperty("vertexNormalAttribute")
        requireNotNull(vertexNormalAttribute)
        assertEquals(0, vertexNormalAttribute)

        val vertexColor3Attribute: Int? = renderer.getPrivateProperty("vertexColor3Attribute")
        requireNotNull(vertexColor3Attribute)
        assertEquals(0, vertexColor3Attribute)

        val vertexColor4Attribute: Int? = renderer.getPrivateProperty("vertexColor4Attribute")
        requireNotNull(vertexColor4Attribute)
        assertEquals(0, vertexColor4Attribute)

        val vertexPositionsBufferId: Int? = renderer.getPrivateProperty("vertexPositionsBufferId")
        requireNotNull(vertexPositionsBufferId)
        assertEquals(0, vertexPositionsBufferId)

        val vertexNormalsBufferId: Int? = renderer.getPrivateProperty("vertexNormalsBufferId")
        requireNotNull(vertexNormalsBufferId)
        assertEquals(0, vertexNormalsBufferId)

        val vertexColorsBufferId: Int? = renderer.getPrivateProperty("vertexColorsBufferId")
        requireNotNull(vertexColorsBufferId)
        assertEquals(0, vertexColorsBufferId)

        val vertexIndicesBufferId: Int? = renderer.getPrivateProperty("vertexIndicesBufferId")
        requireNotNull(vertexIndicesBufferId)
        assertEquals(0, vertexIndicesBufferId)

        val hasNormals: Int? = renderer.getPrivateProperty("hasNormals")
        requireNotNull(hasNormals)
        assertEquals(0, hasNormals)

        val hasColors: Int? = renderer.getPrivateProperty("hasColors")
        requireNotNull(hasColors)
        assertEquals(3, hasColors)

        val rendererDestroyed: Boolean? = renderer.getPrivateProperty("rendererDestroyed")
        requireNotNull(rendererDestroyed)
        assertFalse(rendererDestroyed)

        val cubeRotationMatrix: Matrix? = renderer.getPrivateProperty("cubeRotationMatrix")
        requireNotNull(cubeRotationMatrix)
        assertEquals(cubeRotationMatrix, Matrix.identity(3, 3))

        assertEquals(Color.rgb(127, 127, 127), renderer.diffuseColor)
        assertEquals(Color.rgb(0, 255, 0), renderer.color1)
        assertEquals(Color.rgb(255, 127, 0), renderer.color2)
        assertEquals(Color.rgb(255, 0, 0), renderer.color3)
        assertEquals(Color.rgb(255, 127, 0), renderer.color4)
        assertEquals(Color.rgb(255, 255, 0), renderer.color5)
        assertEquals(Color.rgb(0, 0, 255), renderer.color6)
        assertEquals(Color.rgb(0, 127, 255), renderer.color7)
        assertEquals(Color.rgb(255, 0, 255), renderer.color8)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.width)
        assertNull(renderer.height)
        assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, renderer.clearColor)
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, renderer.cubeSize)
        assertNotNull(renderer.cubePosition)
        assertEquals(
            renderer.cubePosition,
            Point3D.create(
                CoordinatesType.INHOMOGENEOUS_COORDINATES,
                doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
            )
        )
        assertNotNull(renderer.cubeRotation)
        assertEquals(renderer.cubeRotation, Quaternion())
        assertNull(renderer.nearPlane)
        assertNull(renderer.farPlane)
        assertNull(renderer.camera)
        assertNull(renderer.viewCamera)
        assertNull(renderer.cameraIntrinsicParameters)
        assertNull(renderer.cameraCenter)
        assertNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)
    }

    @Test
    fun diffuseColor_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(127, 127, 127), renderer.diffuseColor)

        // set new value
        renderer.diffuseColor = Color.RED

        // check
        assertEquals(Color.RED, renderer.diffuseColor)
    }

    @Test
    fun color1_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(0, 255, 0), renderer.color1)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color1 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color1)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    Color.BLACK,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color2_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(255, 127, 0), renderer.color2)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color2 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color2)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    Color.BLACK,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color3_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(255, 0, 0), renderer.color3)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color3 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color3)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    Color.BLACK,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color4_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(255, 127, 0), renderer.color4)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color4 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color4)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    Color.BLACK,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color5_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(255, 255, 0), renderer.color5)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color5 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color5)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    Color.BLACK,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color6_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(0, 0, 255), renderer.color6)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color6 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color6)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    Color.BLACK,
                    renderer.color7,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color7_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(0, 127, 255), renderer.color7)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color7 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color7)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    Color.BLACK,
                    renderer.color8
                )
            )
        )
    }

    @Test
    fun color8_getOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(Color.rgb(255, 0, 255), renderer.color8)
        val cubeColors: IntArray? = renderer.getPrivateProperty("cubeColors")
        requireNotNull(cubeColors)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    renderer.color8
                )
            )
        )

        // set new value
        renderer.color8 = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.color8)
        assertTrue(
            cubeColors.contentEquals(
                intArrayOf(
                    renderer.color1,
                    renderer.color2,
                    renderer.color3,
                    renderer.color4,
                    renderer.color5,
                    renderer.color6,
                    renderer.color7,
                    Color.BLACK
                )
            )
        )
    }

    @Test(expected = IllegalStateException::class)
    fun orientation_whenNotInitialized_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // set new value
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
    }

    @Test
    fun orientation_whenInitialized_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.width)
        assertNull(renderer.height)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertEquals(WIDTH, renderer.width)
        assertEquals(HEIGHT, renderer.height)

        // set new value
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)
    }

    @Test
    fun width_whenInitialized_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.width)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // check
        assertEquals(WIDTH, renderer.width)
    }

    @Test
    fun height_whenInitialized_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.height)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // check
        assertEquals(HEIGHT, renderer.height)
    }

    @Test
    fun clearColor_getsOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default values
        assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, renderer.clearColor)

        // set new value
        renderer.clearColor = Color.BLACK

        // check
        assertEquals(Color.BLACK, renderer.clearColor)
    }

    @Test
    fun cubeSize_whenPositive_getsOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, renderer.cubeSize)

        // set new value
        val cubeSize = 2.0f * CubeRenderer.DEFAULT_CUBE_SIZE
        renderer.cubeSize = cubeSize

        // check
        assertEquals(cubeSize, renderer.cubeSize)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cubeSize_whenNegative_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, renderer.cubeSize)

        // set new value
        renderer.cubeSize = -1.0f
    }

    @Test
    fun cubePosition_getsOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNotNull(renderer.cubePosition)
        assertEquals(
            renderer.cubePosition,
            Point3D.create(
                CoordinatesType.INHOMOGENEOUS_COORDINATES,
                doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
            )
        )

        // set new value
        val position = Point3D.create()
        renderer.cubePosition = position

        // check
        assertSame(position, renderer.cubePosition)
    }

    @Test
    fun cubeRotation_getsOrSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNotNull(renderer.cubeRotation)
        assertEquals(renderer.cubeRotation, Quaternion())

        // set new value
        val rotation = MatrixRotation3D()
        renderer.cubeRotation = rotation

        // check
        assertSame(rotation, renderer.cubeRotation)
    }

    @Test(expected = IllegalArgumentException::class)
    fun nearPlane_whenNullAndNotInitialized_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.nearPlane)

        // set null value
        renderer.nearPlane = null
    }

    @Test(expected = IllegalStateException::class)
    fun nearPlane_whenNotNullAndNotInitialized_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.nearPlane)

        // set new value
        renderer.nearPlane = NEAR_PLANE
    }

    @Test(expected = IllegalArgumentException::class)
    fun nearPlane_whenNullAndInitialized_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.nearPlane)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set null value
        renderer.nearPlane = null
    }

    @Test
    fun nearPlane_whenNotNullAndInitialized_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.nearPlane)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.nearPlane = NEAR_PLANE

        // check
        assertEquals(NEAR_PLANE, renderer.nearPlane)
    }

    @Test(expected = IllegalArgumentException::class)
    fun farPlane_whenNullAndNotInitialized_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.farPlane)

        // set null value
        renderer.farPlane = null
    }

    @Test(expected = IllegalStateException::class)
    fun farPlane_whenNotNullAndNotInitialized_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.farPlane)

        // set new value
        renderer.farPlane = FAR_PLANE
    }

    @Test(expected = IllegalArgumentException::class)
    fun farPlane_whenNullAndInitialized_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.farPlane)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set null value
        renderer.farPlane = null
    }

    @Test
    fun farPlane_whenNotNullAndInitialized_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.farPlane)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.farPlane = FAR_PLANE

        // check
        assertEquals(FAR_PLANE, renderer.farPlane)
    }

    @Test(expected = IllegalStateException::class)
    fun setNearFarPlanes_whenNotInitialized_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default values
        assertNull(renderer.nearPlane)
        assertNull(renderer.farPlane)

        renderer.setNearFarPlanes(NEAR_PLANE, FAR_PLANE)
    }

    @Test
    fun setNearFarPlanes_whenInitialized_setsExpectedValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.nearPlane)
        assertNull(renderer.farPlane)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.setNearFarPlanes(NEAR_PLANE, FAR_PLANE)

        // check
        assertEquals(NEAR_PLANE, renderer.nearPlane)
        assertEquals(FAR_PLANE, renderer.farPlane)
    }

    @Test(expected = IllegalStateException::class)
    fun camera_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.camera)

        // set new value
        renderer.camera = null
    }

    @Test(expected = IllegalStateException::class)
    fun camera_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.camera)

        // set new value
        val camera = PinholeCamera()
        renderer.camera = camera
    }

    @Test(expected = IllegalArgumentException::class)
    fun camera_whenInitializedAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.camera)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.camera = null
    }

    @Test
    fun camera_whenInitializedAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.camera)
        assertNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.viewCamera)
        assertNull(renderer.cameraIntrinsicParameters)
        assertNull(renderer.cameraCenter)
        assertNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val camera = PinholeCamera()
        renderer.camera = camera

        // check
        assertSame(camera, renderer.camera)
        assertNotNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.viewCamera)
        assertNotNull(renderer.cameraIntrinsicParameters)
        assertNotNull(renderer.cameraCenter)
        assertNotNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCamera_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)

        renderer.viewCamera = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCamera_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)

        // set new value
        val viewCamera = PinholeCamera()
        renderer.viewCamera = viewCamera
    }

    @Test
    fun viewCamera_whenInitializedUnknownOrientationAndNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        renderer.viewCamera = null

        // check
        assertNull(renderer.viewCamera)
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCamera_whenInitializedUnknownOrientationAndNotNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val viewCamera = PinholeCamera()
        renderer.viewCamera = viewCamera
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCamera_whenInitializedKnownOrientationAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        renderer.viewCamera = null
    }

    @Test
    fun viewCamera_whenInitializedKnownOrientationAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        val viewCamera = PinholeCamera()
        renderer.viewCamera = viewCamera

        // check
        assertSame(viewCamera, renderer.viewCamera)
        assertNotNull(renderer.viewCameraIntrinsicParameters)
        assertSame(viewCamera.intrinsicParameters, renderer.viewCameraIntrinsicParameters)
        assertNotNull(renderer.viewCameraCenter)
        assertSame(viewCamera.cameraCenter, renderer.viewCameraCenter)
        assertNotNull(renderer.viewCameraRotation)
        assertSame(viewCamera.cameraRotation, renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun cameraIntrinsicParameters_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraIntrinsicParameters)

        // set new value
        renderer.cameraIntrinsicParameters = null
    }

    @Test(expected = IllegalStateException::class)
    fun cameraIntrinsicParameters_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraIntrinsicParameters)

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        renderer.cameraIntrinsicParameters = intrinsics
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraIntrinsicParameters_whenInitializedAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraIntrinsicParameters)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.cameraIntrinsicParameters = null
    }

    @Test
    fun cameraIntrinsicParameters_whenInitializedAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraIntrinsicParameters)
        assertNull(renderer.viewCamera)
        assertNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.cameraCenter)
        assertNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        renderer.cameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, renderer.cameraIntrinsicParameters)
        assertNotNull(renderer.camera)
        assertSame(intrinsics, renderer.camera?.intrinsicParameters)
        assertNotNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.viewCamera)
        assertNotNull(renderer.cameraCenter)
        assertNotNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun cameraCenter_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraCenter)

        // set new value
        renderer.cameraCenter = null
    }

    @Test(expected = IllegalStateException::class)
    fun cameraCenter_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraCenter)

        // set new value
        val center = Point3D.create()
        renderer.cameraCenter = center
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraCenter_whenInitializedAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraCenter)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.cameraCenter = null
    }

    @Test
    fun cameraCenter_whenInitializedAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraCenter)
        assertNull(renderer.viewCamera)
        assertNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.cameraIntrinsicParameters)
        assertNull(renderer.cameraRotation)

        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val center = Point3D.create()
        renderer.cameraCenter = center

        // check
        assertSame(center, renderer.cameraCenter)
        assertNotNull(renderer.camera)
        assertSame(center, renderer.camera?.cameraCenter)
        assertNotNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.viewCamera)
        assertNotNull(renderer.cameraIntrinsicParameters)
        assertNotNull(renderer.cameraRotation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun cameraRotation_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraRotation)

        // set new value
        renderer.cameraRotation = null
    }

    @Test(expected = IllegalStateException::class)
    fun cameraRotation_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraRotation)

        // set new value
        val rotation = Rotation3D.create()
        renderer.cameraRotation = rotation
    }

    @Test(expected = IllegalArgumentException::class)
    fun cameraRotation_whenInitializedAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        renderer.cameraRotation = null
    }

    @Test
    fun cameraRotation_whenInitializedAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.cameraRotation)
        assertNull(renderer.viewCamera)
        assertNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.cameraIntrinsicParameters)
        assertNull(renderer.cameraCenter)

        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val rotation = Rotation3D.create()
        renderer.cameraRotation = rotation

        // check
        assertSame(rotation, renderer.cameraRotation)
        assertNotNull(renderer.camera)
        assertSame(rotation, renderer.camera?.cameraRotation)
        assertNotNull(renderer.getPrivateProperty("cameraModelViewProjectionMatrix"))
        assertNull(renderer.viewCamera)
        assertNotNull(renderer.cameraIntrinsicParameters)
        assertNotNull(renderer.cameraCenter)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraIntrinsicParameters_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraIntrinsicParameters)

        renderer.viewCameraIntrinsicParameters = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraIntrinsicParameters_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraIntrinsicParameters)

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        renderer.viewCameraIntrinsicParameters = intrinsics
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraIntrinsicParameters_whenInitializedUnknownOrientationAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        renderer.viewCameraIntrinsicParameters = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraIntrinsicParameters_whenInitializedUnknownOrientationAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        val intrinsics = PinholeCameraIntrinsicParameters()
        renderer.viewCameraIntrinsicParameters = intrinsics
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraIntrinsicParameters_whenInitializedKnownOrientationAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        renderer.viewCameraIntrinsicParameters = null
    }

    @Test
    fun viewCameraIntrinsicParameters_whenInitializedKnownOrientationAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCamera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        renderer.viewCameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, renderer.viewCameraIntrinsicParameters)
        assertNotNull(renderer.viewCamera)
        assertSame(intrinsics, renderer.viewCamera?.intrinsicParameters)
        assertNotNull(renderer.viewCameraCenter)
        assertSame(renderer.viewCamera?.cameraCenter, renderer.viewCameraCenter)
        assertNotNull(renderer.viewCameraRotation)
        assertSame(renderer.viewCamera?.cameraRotation, renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraCenter_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)

        renderer.viewCameraCenter = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraCenter_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)

        // set new value
        val center = Point3D.create()
        renderer.viewCameraCenter = center
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraCenter_whenInitializedUnknownOrientationAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        renderer.viewCameraCenter = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraCenter_whenInitializedUnknownOrientationAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        val center = Point3D.create()
        renderer.viewCameraCenter = center
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraCenter_whenInitializedKnownOrientationAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCamera)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraRotation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        renderer.viewCameraCenter = null
    }

    @Test
    fun viewCameraCenter_whenInitializedKnownOrientationAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraRotation)
        assertNull(renderer.viewCamera)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraCenter)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        val center = Point3D.create()
        renderer.viewCameraCenter = center

        // check
        assertSame(center, renderer.viewCameraCenter)
        assertNotNull(renderer.viewCamera)
        assertSame(center, renderer.viewCamera?.cameraCenter)
        assertNotNull(renderer.viewCameraIntrinsicParameters)
        assertSame(renderer.viewCamera?.intrinsicParameters, renderer.viewCameraIntrinsicParameters)
        assertNotNull(renderer.viewCameraRotation)
        assertSame(renderer.viewCamera?.cameraRotation, renderer.viewCameraRotation)
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraRotation_whenNotInitializedAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)

        renderer.viewCameraRotation = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraRotation_whenNotInitializedAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)

        // set new value
        val rotation = Rotation3D.create()
        renderer.viewCameraRotation = rotation
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraRotation_whenInitializedUnknownOrientationAndNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        renderer.viewCameraRotation = null
    }

    @Test(expected = IllegalStateException::class)
    fun viewCameraRotation_whenInitializedUnknownOrientationAndNotNull_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set value
        val rotation = Rotation3D.create()
        renderer.viewCameraRotation = rotation
    }

    @Test(expected = IllegalArgumentException::class)
    fun viewCameraRotation_whenInitializedKnownOrientationAndNull_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCamera)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        renderer.viewCameraRotation = null
    }

    @Test
    fun viewCameraRotation_whenInitializedKnownOrientationAndNotNull_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default value
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, renderer.orientation)
        assertNull(renderer.viewCamera)
        assertNull(renderer.viewCameraIntrinsicParameters)
        assertNull(renderer.viewCameraCenter)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        renderer.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertNull(renderer.viewCameraRotation)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, renderer.orientation)

        // set new value
        val rotation = Rotation3D.create()
        renderer.viewCameraRotation = rotation

        // check
        assertSame(rotation, renderer.viewCameraRotation)
        assertNotNull(renderer.viewCamera)
        assertSame(rotation, renderer.viewCamera?.cameraRotation)
        assertNotNull(renderer.viewCameraIntrinsicParameters)
        assertSame(renderer.viewCamera?.intrinsicParameters, renderer.viewCameraIntrinsicParameters)
        assertNotNull(renderer.viewCameraCenter)
        assertSame(renderer.viewCamera?.cameraCenter, renderer.viewCameraCenter)
    }

    @Test(expected = IllegalStateException::class)
    fun setValues_whenNotInitialized_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default values
        assertNull(renderer.nearPlane)
        assertNull(renderer.farPlane)
        assertNull(renderer.camera)
        assertNull(renderer.width)
        assertNull(renderer.height)

        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        val camera = PinholeCamera()
        renderer.setValues(nearPlane, farPlane, camera)
    }

    @Test
    fun setValues_whenInitialized_setsExpectedValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        // check default values
        assertNull(renderer.nearPlane)
        assertNull(renderer.farPlane)
        assertNull(renderer.camera)
        assertNull(renderer.width)
        assertNull(renderer.height)

        // initialize
        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new values
        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        val camera = PinholeCamera()
        renderer.setValues(nearPlane, farPlane, camera)

        // check
        assertEquals(nearPlane, renderer.nearPlane)
        assertEquals(farPlane, renderer.farPlane)
        assertSame(camera, renderer.camera)
        assertEquals(WIDTH, renderer.width)
        assertEquals(HEIGHT, renderer.height)
    }

    @Test
    fun destroy_setsRendererDestroyed() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val renderer = CubeRenderer(context)

        val rendererDestroyed1: Boolean? = renderer.getPrivateProperty("rendererDestroyed")
        requireNotNull(rendererDestroyed1)
        assertFalse(rendererDestroyed1)

        // destroy
        renderer.destroy()

        // check
        val rendererDestroyed2: Boolean? = renderer.getPrivateProperty("rendererDestroyed")
        requireNotNull(rendererDestroyed2)
        assertTrue(rendererDestroyed2)
    }

    private companion object {
        const val WIDTH = 480
        const val HEIGHT = 640

        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 100.0f
    }
}