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

import com.irurueta.algebra.Matrix
import com.irurueta.geometry.*
import com.irurueta.statistics.UniformRandomizer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraToOpenGlHelperTest {

    @Test
    fun checkConstants() {
        assertEquals(16, CameraToOpenGlHelper.MATRIX_LENGTH)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenCameraAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenCameraAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenCameraAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenCameraAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenCameraAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE,
            result
        )
    }

    @Test
    fun computeProjectionMatrix_whenCameraAndValid_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            camera1,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera1)

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            result,
            modelViewMatrix,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenIntrinsicsAndZeroWidth_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenIntrinsicsAndZeroHeight_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenIntrinsicsAndWrongResultLength_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenIntrinsicsAndZeroNearPlane_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrix_whenIntrinsicsAndSmallFarPlane_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE,
            result
        )
    }

    @Test
    fun computeProjectionMatrix_whenIntrinsicsValid_computesExpectedValue() {
        val intrinsicParameters1 = createIntrinsicParameters()
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeProjectionMatrix(
            intrinsicParameters1,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            projectionMatrix
        )

        val intrinsicParameters2 = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        assertEquals(
            intrinsicParameters1.horizontalFocalLength,
            intrinsicParameters2.horizontalFocalLength,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.verticalFocalLength,
            intrinsicParameters2.verticalFocalLength,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.horizontalPrincipalPoint,
            intrinsicParameters2.horizontalPrincipalPoint,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.verticalPrincipalPoint,
            intrinsicParameters2.verticalPrincipalPoint,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.skewness,
            intrinsicParameters2.skewness,
            ABSOLUTE_ERROR
        )
        assertTrue(
            intrinsicParameters1.internalMatrix.equals(
                intrinsicParameters2.internalMatrix,
                ABSOLUTE_ERROR
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroWidth_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenCameraAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenCameraAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenCameraAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenCameraAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE
        )
    }

    @Test
    fun computeProjectionMatrixAndReturnNew_whenCamera_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val result = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera1)

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            result,
            modelViewMatrix,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroHeight_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroNearPlane_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeProjectionMatrixAndReturnNew_whenIntrinsicsAndSmallFarPlane_throwsIllegalArgumentException() {
        val intrinsicParameters = createIntrinsicParameters()
        CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE
        )
    }

    @Test
    fun computeProjectionMatrixAndReturnNew_whenIntrinsicsValid_computesExpectedValue() {
        val intrinsicParameters1 = createIntrinsicParameters()
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters1,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val intrinsicParameters2 = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )

        assertEquals(
            intrinsicParameters1.horizontalFocalLength,
            intrinsicParameters2.horizontalFocalLength,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.verticalFocalLength,
            intrinsicParameters2.verticalFocalLength,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.horizontalPrincipalPoint,
            intrinsicParameters2.horizontalPrincipalPoint,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.verticalPrincipalPoint,
            intrinsicParameters2.verticalPrincipalPoint,
            ABSOLUTE_ERROR
        )
        assertEquals(
            intrinsicParameters1.skewness,
            intrinsicParameters2.skewness,
            ABSOLUTE_ERROR
        )
        assertTrue(
            intrinsicParameters1.internalMatrix.equals(
                intrinsicParameters2.internalMatrix,
                ABSOLUTE_ERROR
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrix_whenCameraAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewMatrix(camera, result)
    }

    @Test
    fun computeModelViewMatrix_whenCamera_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewMatrix(camera1, result)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test
    fun computeModelViewMatrix_whenRotationCenter_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val rotation1 = camera1.cameraRotation
        val center1 = camera1.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewMatrix(rotation1, center1, result)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrix_whenWrongRotationMatrixAndCenter1_throwsIllegalArgumentException() {
        val rotationMatrix = Matrix(1, 3)
        val center = Point3D.create()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewMatrix(rotationMatrix, center, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrix_whenWrongRotationMatrixAndCenter2_throwsIllegalArgumentException() {
        val rotationMatrix = Matrix(3, 1)
        val center = Point3D.create()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewMatrix(rotationMatrix, center, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrix_whenRotationMatrixCenterAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()

        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewMatrix(rotationMatrix, center, result)
    }

    @Test
    fun computeModelViewMatrix_whenRotationMatrixAndCenter_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val rotation1 = camera1.cameraRotation
        val rotationMatrix1 = rotation1.asInhomogeneousMatrix()
        val center = camera1.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewMatrix(rotationMatrix1, center, result)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test
    fun computeModelViewMatrixAndReturnNew_whenCameraAndValidResult_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val result = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera1)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test
    fun computeModelViewMatrixAndReturnNew_whenRotationAndCenter_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val rotation1 = camera1.cameraRotation
        val center1 = camera1.cameraCenter

        val result = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(rotation1, center1)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrixAndReturnNew_whenWrongRotationMatrixAndCenter1_throwsIllegalArgumentException() {
        val rotationMatrix = Matrix(1, 3)
        val center = Point3D.create()
        CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(rotationMatrix, center)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewMatrixAndReturnNew_whenWrongRotationMatrixAndCenter2_throwsIllegalArgumentException() {
        val rotationMatrix = Matrix(3, 1)
        val center = Point3D.create()
        CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(rotationMatrix, center)
    }

    @Test
    fun computeModelViewMatrixAndReturnNew_whenRotationMatrixAndCenter_computesExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()

        val rotation1 = camera1.cameraRotation
        val rotationMatrix1 = rotation1.asInhomogeneousMatrix()
        val center = camera1.cameraCenter

        val result =
            CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(rotationMatrix1, center)

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera1.intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
        )

        val camera2 = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            result,
            WIDTH,
            HEIGHT
        )
        camera2.fixCameraSign()
        camera2.normalize()
        camera2.decompose()

        assertTrue(camera1.internalMatrix.equals(camera2.internalMatrix, LARGE_ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenProjectionMatrixWrongLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        val modelViewMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenModelViewMatrixWrongLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        val modelViewMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenResultWrongLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        val modelViewMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            result
        )
    }

    @Test
    fun computeModelViewProjectionMatrix_whenValid_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            result
        )

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        android.opengl.Matrix.multiplyMM(expected, 0, projectionMatrix, 0, modelViewMatrix, 0)

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenCameraAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenCameraAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenCameraAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenCameraAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenCameraAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test
    fun computeModelViewProjectionMatrix_whenCamera_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            expected
        )

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenIntrinsicsAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenIntrinsicsAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenIntrinsicsAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenIntrinsicsAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenIntrinsicsAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test
    fun computeModelViewProjectionMatrix_whenIntrinsics_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            expected
        )

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenWrongRotationMatrix1_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotationMatrix = Matrix(1, 3)
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenWrongRotationMatrix2_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotationMatrix = Matrix(3, 1)
        val center = camera.cameraCenter
        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenRotationMatrixAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenRotationMatrixAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenRotationMatrixAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenRotationMatrixAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrix_whenRotationMatrixAndWrongResultLength_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )
    }

    @Test
    fun computeModelViewProjectionMatrix_whenRotationMatrix_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            expected
        )

        val result = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE,
            result
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenProjectionMatrixWrongLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        val modelViewMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            projectionMatrix,
            modelViewMatrix
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenModelViewMatrixWrongLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        val modelViewMatrix = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH + 1)
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            projectionMatrix,
            modelViewMatrix
        )
    }

    @Test
    fun computeModelViewProjectionMatrixAndReturnNew_whenValid_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val result = CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            projectionMatrix,
            modelViewMatrix
        )

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        android.opengl.Matrix.multiplyMM(expected, 0, projectionMatrix, 0, modelViewMatrix, 0)

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenCameraAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            camera,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenCameraAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenCameraAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenCameraAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE
        )
    }

    @Test
    fun computeModelViewProjectionMatrixAndReturnNew_whenCamera_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            expected
        )

        val result = CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotation,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotation,
            center,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenIntrinsicsAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenIntrinsicsAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE
        )
    }

    @Test
    fun computeModelViewProjectionMatrixAndReturnNew_whenIntrinsics_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val center = camera.cameraCenter

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = FloatArray(CameraToOpenGlHelper.MATRIX_LENGTH)
        CameraToOpenGlHelper.computeModelViewProjectionMatrix(
            projectionMatrix,
            modelViewMatrix,
            expected
        )

        val result = CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotation,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenWrongRotationMatrix1_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotationMatrix = Matrix(1, 3)
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenWrongRotationMatrix2_throwsIllegalArgumentException() {
        val camera = createCamera()
        val intrinsics = camera.intrinsicParameters
        val rotationMatrix = Matrix(3, 1)
        val center = camera.cameraCenter
        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenRotationMatrixAndZeroWidth_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            0,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenRotationMatrixAndZeroHeight_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            0,
            NEAR_PLANE,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenRotationMatrixAndZeroNearPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            0.0f,
            FAR_PLANE
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computeModelViewProjectionMatrixAndReturnNew_whenRotationMatrixAndSmallFarPlane_throwsIllegalArgumentException() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            NEAR_PLANE
        )
    }

    @Test
    fun computeModelViewProjectionMatrixAndReturnNew_whenRotationMatrix_computesExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val intrinsics = camera.intrinsicParameters
        val rotation = camera.cameraRotation
        val rotationMatrix = rotation.asInhomogeneousMatrix()
        val center = camera.cameraCenter

        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)

        val expected = CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            projectionMatrix,
            modelViewMatrix
        )

        val result = CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
            intrinsics,
            rotationMatrix,
            center,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        assertArrayEquals(expected, result, 0.0f)
    }

    @Test
    fun intrinsicParametersFromCamera_whenIntrinsicsAvailable_returnsExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val expected = camera.intrinsicParameters
        val result = CameraToOpenGlHelper.intrinsicParametersFromCamera(camera)

        assertEquals(expected, result)
    }

    @Test
    fun intrinsicParametersFromCamera_whenIntrinsicsNotAvailable_returnsExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()
        camera1.decompose()
        val cameraMatrix = camera1.internalMatrix

        val camera2 = PinholeCamera(cameraMatrix)
        camera2.fixCameraSign()
        camera2.normalize()

        val expected = camera1.intrinsicParameters
        val result = CameraToOpenGlHelper.intrinsicParametersFromCamera(camera2)

        assertTrue(expected.internalMatrix.equals(result.internalMatrix, SMALL_ABSOLUTE_ERROR))
    }

    @Test
    fun rotationFromCamera_whenRotationAvailable_returnsExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val expected = camera.cameraRotation
        val result = CameraToOpenGlHelper.rotationFromCamera(camera)

        assertEquals(expected, result)
    }

    @Test
    fun rotationFromCamera_whenRotationNotAvailable_returnsExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()
        camera1.decompose()
        val cameraMatrix = camera1.internalMatrix

        val camera2 = PinholeCamera(cameraMatrix)
        camera2.fixCameraSign()
        camera2.normalize()

        val expected = camera1.cameraRotation
        val result = CameraToOpenGlHelper.rotationFromCamera(camera2)

        assertTrue(
            expected.asInhomogeneousMatrix()
                .equals(result.asInhomogeneousMatrix(), SMALL_ABSOLUTE_ERROR)
        )
    }

    @Test
    fun centerFromCamera_whenCenterAvailable_returnsExpectedValue() {
        val camera = createCamera()
        camera.fixCameraSign()
        camera.normalize()
        camera.decompose()

        val expected = camera.cameraCenter
        val result = CameraToOpenGlHelper.centerFromCamera(camera)

        assertEquals(expected, result)
    }

    @Test
    fun centerFromCamera_whenCenterNotAvailable_returnsExpectedValue() {
        val camera1 = createCamera()
        camera1.fixCameraSign()
        camera1.normalize()
        camera1.decompose()
        val cameraMatrix = camera1.internalMatrix

        val camera2 = PinholeCamera(cameraMatrix)
        camera2.fixCameraSign()
        camera2.normalize()

        val expected = camera1.cameraCenter
        val result = CameraToOpenGlHelper.centerFromCamera(camera2)

        assertTrue(expected.equals(result, SMALL_ABSOLUTE_ERROR))
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

        const val ABSOLUTE_ERROR = 1e-4

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
    }
}