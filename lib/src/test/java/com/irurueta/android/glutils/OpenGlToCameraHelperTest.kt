package com.irurueta.android.glutils

import com.irurueta.geometry.*
import com.irurueta.statistics.UniformRandomizer
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenGlToCameraHelperTest {

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun checkConstants() {
        assertEquals(16, OpenGlToCameraHelper.MATRIX_LENGTH)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCameraIntrinsics_whenWrongProjectionMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        val result = PinholeCameraIntrinsicParameters()
        OpenGlToCameraHelper.computePinholeCameraIntrinsics(projectionMatrix, WIDTH, HEIGHT, result)
    }

    @Test
    fun computePinholeCameraIntrinsics_whenValid_computesExpectedValues() {
        val projectionMatrix1 = createProjectionMatrix()
        val intrinsicParameters = PinholeCameraIntrinsicParameters()
        OpenGlToCameraHelper.computePinholeCameraIntrinsics(
            projectionMatrix1,
            WIDTH,
            HEIGHT,
            intrinsicParameters
        )

        val projectionMatrix2 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        assertArrayEquals(projectionMatrix1, projectionMatrix2, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCameraIntrinsicsAndReturnNew_whenWrongProjectionMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            WIDTH,
            HEIGHT
        )
    }

    @Test
    fun computePinholeCameraIntrinsicsAndReturnNew_whenValid_computesExpectedValues() {
        val projectionMatrix1 = createProjectionMatrix()
        val intrinsicParameters = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix1,
            WIDTH,
            HEIGHT
        )

        val projectionMatrix2 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        assertArrayEquals(projectionMatrix1, projectionMatrix2, 0.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePoseTransformation_whenWrongModelViewMatrixLength_throwsIllegalArgumentException() {
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        val result = ProjectiveTransformation3D()
        OpenGlToCameraHelper.computePoseTransformation(modelViewMatrix, result)
    }

    @Test
    fun computePoseTransformation_whenValid_computesExpectedValue() {
        val rotation = createRotation()
        val center = createCenter()
        val transformation = createTransformation(rotation, center)
        val modelViewMatrix = toModelViewMatrix(transformation)
        val result = ProjectiveTransformation3D()
        OpenGlToCameraHelper.computePoseTransformation(modelViewMatrix, result)

        val matrix1 = transformation.asMatrix()
        val matrix2 = result.asMatrix()
        assertTrue(matrix1.equals(matrix2, ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePoseTransformationAndReturnNew_whenWrongModelViewMatrixLength_throwsIllegalArgumentException() {
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        OpenGlToCameraHelper.computePoseTransformationAndReturnNew(modelViewMatrix)
    }

    @Test
    fun computePoseTransformationAndReturnNew_whenValid_computesExpectedValue() {
        val rotation = createRotation()
        val center = createCenter()
        val transformation = createTransformation(rotation, center)
        val modelViewMatrix = toModelViewMatrix(transformation)
        val result = OpenGlToCameraHelper.computePoseTransformationAndReturnNew(modelViewMatrix)

        val matrix1 = transformation.asMatrix()
        val matrix2 = result.asMatrix()
        assertTrue(matrix1.equals(matrix2, ABSOLUTE_ERROR))
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCamera_whenWrongProjectionMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH)
        val result = PinholeCamera()
        OpenGlToCameraHelper.computePinholeCamera(
            projectionMatrix,
            modelViewMatrix,
            WIDTH,
            HEIGHT,
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCamera_whenWrongModelViewMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH)
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        val result = PinholeCamera()
        OpenGlToCameraHelper.computePinholeCamera(
            projectionMatrix,
            modelViewMatrix,
            WIDTH,
            HEIGHT,
            result
        )
    }

    @Test
    fun computePinholeCamera_whenValid_computesExpectedCamera() {
        val projectionMatrix1 = createProjectionMatrix()
        val intrinsics1 = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix1,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera1 = PinholeCamera(intrinsics1, rotation, center)

        camera1.normalize()
        camera1.fixCameraSign()
        camera1.decompose()

        val transformation = createTransformation(rotation, center)
        val modelViewMatrix1 = toModelViewMatrix(transformation)

        val result = PinholeCamera()
        OpenGlToCameraHelper.computePinholeCamera(
            projectionMatrix1,
            modelViewMatrix1,
            WIDTH,
            HEIGHT,
            result
        )

        result.normalize()
        result.fixCameraSign()
        result.decompose()

        assertTrue(camera1.internalMatrix.equals(result.internalMatrix, LARGE_ABSOLUTE_ERROR))

        val projectionMatrix2 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            result,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix2 = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(result)

        assertArrayEquals(projectionMatrix1, projectionMatrix2, SMALL_ABSOLUTE_ERROR.toFloat())
        assertArrayEquals(modelViewMatrix1, modelViewMatrix2, SMALL_ABSOLUTE_ERROR.toFloat())
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCameraAndReturnNew_whenWrongProjectionMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH)
        OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            modelViewMatrix,
            WIDTH,
            HEIGHT
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun computePinholeCameraAndReturnNew_whenWrongModelViewMatrixLength_throwsIllegalArgumentException() {
        val projectionMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH)
        val modelViewMatrix = FloatArray(OpenGlToCameraHelper.MATRIX_LENGTH + 1)
        OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix,
            modelViewMatrix,
            WIDTH,
            HEIGHT
        )
    }

    @Test
    fun computePinholeCameraAndReturnNew_whenValid_computesExpectedCamera() {
        val projectionMatrix1 = createProjectionMatrix()
        val intrinsics1 = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix1,
            WIDTH,
            HEIGHT
        )
        val rotation = createRotation()
        val center = createCenter()
        val camera1 = PinholeCamera(intrinsics1, rotation, center)

        camera1.normalize()
        camera1.fixCameraSign()
        camera1.decompose()

        val transformation = createTransformation(rotation, center)
        val modelViewMatrix1 = toModelViewMatrix(transformation)

        val result = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix1,
            modelViewMatrix1,
            WIDTH,
            HEIGHT
        )

        result.normalize()
        result.fixCameraSign()
        result.decompose()

        assertTrue(camera1.internalMatrix.equals(result.internalMatrix, LARGE_ABSOLUTE_ERROR))

        val projectionMatrix2 = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            result,
            WIDTH,
            HEIGHT,
            NEAR_PLANE,
            FAR_PLANE
        )

        val modelViewMatrix2 = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(result)

        assertArrayEquals(projectionMatrix1, projectionMatrix2, SMALL_ABSOLUTE_ERROR.toFloat())
        assertArrayEquals(modelViewMatrix1, modelViewMatrix2, SMALL_ABSOLUTE_ERROR.toFloat())
    }

    private companion object {
        const val WIDTH = 480
        const val HEIGHT = 640

        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 100.0f

        const val MIN_ROTATION_ANGLE_DEGREES = -45.0
        const val MAX_ROTATION_ANGLE_DEGREES = 45.0

        const val MIN_POS = -50.0
        const val MAX_POS = 50.0

        const val ABSOLUTE_ERROR = 1e-4

        const val SMALL_ABSOLUTE_ERROR = 5e-6

        const val LARGE_ABSOLUTE_ERROR = 3e-3

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