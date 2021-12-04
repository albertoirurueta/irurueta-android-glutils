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
import java.util.*

/**
 * Helper class to convert a pinhole camera into arrays used as matrices when
 * drawing projective 3D scenes with OpenGL.
 *
 * Typically, when using OpenGL, a projective matrix defines the projective
 * geometry (i.e. frustum) of the scene, using parameters such as field of view,
 * near and far planes. This kind of information is partially encoded in pinhole
 * camera intrinsic parameters.
 *
 * OpenGL typically also uses a model-view matrix, which defines the amount of
 * translation and rotation between the camera and vertices to be drawn in the
 * scene. This information is contained in a pinhole camera as its extrinsic
 * parameters (i.e. rotation and camera center).
 *
 * Finally, OpenGL uses the model-view projection matrix, which is the combination
 * of both the projection and the model-view matrices to draw the scene.
 */
object CameraToOpenGlHelper {
    /**
     * Length of arrays used to store values of 4x4 homogeneous matrices.
     */
    const val MATRIX_LENGTH = 16

    /**
     * Computes projection matrix using provided pinhole camera, viewport size
     * expressed in pixels, near and far planes and field of view.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param camera pinhole camera.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeProjectionMatrix(
        camera: PinholeCamera,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float,
        result: FloatArray
    ) {
        computeProjectionMatrix(
            intrinsicParametersFromCamera(camera),
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
    }

    /**
     * Computes projection matrix using provided intrinsic pinhole camera parameters,
     * viewport size expressed in pixels, near and far planes and field of view.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeProjectionMatrix(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float,
        result: FloatArray
    ) {
        require(width != 0)
        require(height != 0)
        require(result.size == MATRIX_LENGTH)
        require(nearPlane > 0.0f)
        require(farPlane > nearPlane)

        val fx = intrinsicParameters.horizontalFocalLength
        val fy = intrinsicParameters.verticalFocalLength

        val px = intrinsicParameters.horizontalPrincipalPoint
        val py = intrinsicParameters.verticalPrincipalPoint

        val skewness = intrinsicParameters.skewness

        Arrays.fill(result, 0.0f)
        result[0] = (2.0 * fx / width).toFloat()
        result[4] = (result[0] * skewness / fx).toFloat()
        result[5] = (2.0 * fy / height).toFloat()
        result[8] = (2.0 * (px / width) - 1.0).toFloat()
        result[9] = (2.0 * (py / height) - 1.0).toFloat()
        result[10] = -(farPlane + nearPlane) / (farPlane - nearPlane)
        result[11] = -1.0f
        result[14] = -2.0f * farPlane * nearPlane / (farPlane - nearPlane)
    }

    /**
     * Computes projection matrix using provided pinhole camera, viewport size
     * expressed in pixels, near and far planes and field of view.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param camera pinhole camera.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @return 16-length array containing projection matrix values.
     */
    fun computeProjectionMatrixAndReturnNew(
        camera: PinholeCamera,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeProjectionMatrix(
            camera,
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
        return result
    }

    /**
     * Computes projection matrix using provided intrinsic pinhole camera parameters,
     * viewport size expressed in pixels, near and far planes and field of view.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @return 16-length array containing projection matrix values.
     */
    fun computeProjectionMatrixAndReturnNew(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeProjectionMatrix(
            intrinsicParameters,
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
        return result
    }

    /**
     * Computes model-view matrix using provided pinhole camera.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param camera pinhole camera.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewMatrix(camera: PinholeCamera, result: FloatArray) {
        val cameraRotation = rotationFromCamera(camera)
        val cameraCenter = centerFromCamera(camera)
        computeModelViewMatrix(cameraRotation, cameraCenter, result)
    }

    /**
     * Computes model-view matrix using provided camera rotation and center.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param cameraRotation camera rotation.
     * @param cameraCenter camera center.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewMatrix(
        cameraRotation: Rotation3D, cameraCenter: Point3D,
        result: FloatArray
    ) {
        val rotationMatrix = Matrix(Rotation3D.INHOM_COORDS, Rotation3D.INHOM_COORDS)
        cameraRotation.asInhomogeneousMatrix(rotationMatrix)
        computeModelViewMatrix(rotationMatrix, cameraCenter, result)
    }

    /**
     * Computes model-view matrix using provided camera rotation and center.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param rotationMatrix 3x3 matrix defining camera rotation. This matrix must be orthonormal.
     * @param cameraCenter camera center.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewMatrix(
        rotationMatrix: Matrix, cameraCenter: Point3D,
        result: FloatArray
    ) {
        require(
            rotationMatrix.rows == Rotation3D.INHOM_COORDS
                    && rotationMatrix.columns == Rotation3D.INHOM_COORDS
        )
        require(result.size == MATRIX_LENGTH)

        android.opengl.Matrix.setIdentityM(result, 0)

        // set camera center
        val centerX = cameraCenter.inhomX.toFloat()
        val centerY = cameraCenter.inhomY.toFloat()
        val centerZ = cameraCenter.inhomZ.toFloat()
        android.opengl.Matrix.translateM(result, 0, centerX, centerY, centerZ)

        // A pinhole camera having its center at C = [cx cy cz]' has the following expression:
        // P = [K*R -K*R*C],
        // so that the point [C 1] is the null-space of the matrix, which is equal to the camera
        // center.

        // Having a camera located at origin, with no rotation, has the following expression:
        // P0 = [K 0]

        // The model view matrix is an euclidean 3D transformation so that:
        // T = [R^-1   C]
        //     [0      1]

        // T^-1 = [R -R*C]
        //        [0    1]

        // Consequently, transforming camera P0 with transformation T, results in the following:
        // P0 * T^-1 = [K 0] * [R -R*C] = [K*R   -K*R*C] = P
        //                     [0    1]
        // Which is equal to a camera with intrinsic parameters K, located at center C and
        // having rotation R.

        // It must be noticed that since rotation matrices are orthonormal, then the inverse of a
        // rotation matrix R is its transpose:
        // R^-1 = R'

        // set inverse camera rotation (the inverse of a rotation is its transpose)
        // Rotation matrix buffer contains elements stored column-wise, consequently:
        // R = [r[0] r[3] r[6]] --> R^-1 =  [r[0] r[1] r[2]]
        //     [r[1] r[4] r[7]]             [r[3] r[4] r[5]]
        //     [r[2] r[5] r[8]]             [r[6] r[7] r[8]]

        val rotationBuffer = rotationMatrix.buffer
        result[0] = rotationBuffer[0].toFloat()
        result[1] = rotationBuffer[3].toFloat()
        result[2] = rotationBuffer[6].toFloat()

        result[4] = rotationBuffer[1].toFloat()
        result[5] = rotationBuffer[4].toFloat()
        result[6] = rotationBuffer[7].toFloat()

        result[8] = rotationBuffer[2].toFloat()
        result[9] = rotationBuffer[5].toFloat()
        result[10] = rotationBuffer[8].toFloat()
    }

    /**
     * Computes model-view matrix using provided pinhole camera.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param camera pinhole camera.
     * @return 16-length array containing model-view matrix values.
     */
    fun computeModelViewMatrixAndReturnNew(camera: PinholeCamera): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewMatrix(camera, result)
        return result
    }

    /**
     * Computes model-view matrix using provided camera rotation and center.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param cameraRotation camera rotation.
     * @param cameraCenter camera center.
     * @return 16-length array containing model-view matrix values.
     */
    fun computeModelViewMatrixAndReturnNew(
        cameraRotation: Rotation3D,
        cameraCenter: Point3D
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewMatrix(cameraRotation, cameraCenter, result)
        return result
    }

    /**
     * Computes model-view matrix using provided camera rotation and center.
     *
     * The model-view matrix defines the amount of translation and rotation between
     * the camera and vertices to be drawn in the scene.
     *
     * @param rotationMatrix 3x3 matrix defining camera rotation. This matrix must be orthonormal.
     * @param cameraCenter camera center.
     * @return 16-length array containing model-view matrix values.
     */
    fun computeModelViewMatrixAndReturnNew(
        rotationMatrix: Matrix,
        cameraCenter: Point3D
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewMatrix(rotationMatrix, cameraCenter, result)
        return result
    }

    /**
     * Computes model-view projection matrix using provided projection and
     * model-view matrices.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * Provided arrays must have 16 length to contain the values of 4x4 matrices.
     *
     * @param projectionMatrix projection matrix. Must have length 16.
     * @param modelViewMatrix model-view matrix. Must have length 16.
     * @return 16-length array that will store the resulting matrix.
     */
    fun computeModelViewProjectionMatrix(
        projectionMatrix: FloatArray,
        modelViewMatrix: FloatArray,
        result: FloatArray
    ) {
        require(projectionMatrix.size == MATRIX_LENGTH)
        require(modelViewMatrix.size == MATRIX_LENGTH)
        require(result.size == MATRIX_LENGTH)
        android.opengl.Matrix.multiplyMM(
            result,
            0,
            projectionMatrix,
            0,
            modelViewMatrix,
            0
        )
    }

    /**
     * Computes model-view projection matrix using provided pinhole camera, viewport
     * size expressed in pixels, near and far planes and field of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param camera pinhole camera.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewProjectionMatrix(
        camera: PinholeCamera,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float,
        result: FloatArray
    ) {
        require(result.size == MATRIX_LENGTH)
        val projectionMatrix = computeProjectionMatrixAndReturnNew(
            camera,
            width,
            height,
            nearPlane,
            farPlane
        )
        val modelViewMatrix = computeModelViewMatrixAndReturnNew(camera)
        computeModelViewProjectionMatrix(projectionMatrix, modelViewMatrix, result)
    }

    /**
     * Computes model-view projection matrix using provided intrinsic pinhole camera
     * parameters, viewport size expressed in pixels, near and far planes and field
     * of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param cameraRotation camera rotation.
     * @param cameraCenter camera center.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewProjectionMatrix(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        cameraRotation: Rotation3D,
        cameraCenter: Point3D,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float,
        result: FloatArray
    ) {
        require(result.size == MATRIX_LENGTH)
        val projectionMatrix = computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            width,
            height,
            nearPlane,
            farPlane
        )
        val modelViewMatrix = computeModelViewMatrixAndReturnNew(cameraRotation, cameraCenter)
        computeModelViewProjectionMatrix(projectionMatrix, modelViewMatrix, result)
    }

    /**
     * Computes model-view projection matrix using provided intrinsic pinhole camera
     * parameters, viewport size expressed in pixels, near and far planes and field
     * of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param rotationMatrix 3x3 matrix defining camera rotation. This matrix must be orthonormal.
     * @param cameraCenter camera center.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @param result 16-length array that will store the resulting matrix.
     */
    fun computeModelViewProjectionMatrix(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        rotationMatrix: Matrix,
        cameraCenter: Point3D,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float,
        result: FloatArray
    ) {
        require(result.size == MATRIX_LENGTH)
        val projectionMatrix = computeProjectionMatrixAndReturnNew(
            intrinsicParameters,
            width,
            height,
            nearPlane,
            farPlane
        )
        val modelViewMatrix = computeModelViewMatrixAndReturnNew(rotationMatrix, cameraCenter)
        computeModelViewProjectionMatrix(projectionMatrix, modelViewMatrix, result)
    }

    /**
     * Computes model-view projection matrix using provided projection and
     * model-view matrices.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * Provided arrays must have 16 length to contain the values of 4x4 matrices.
     *
     * @param projectionMatrix projection matrix. Must have length 16.
     * @param modelViewMatrix model-view matrix. Must have length 16.
     * @return 16-length array containing the model-view projection matrix values.
     */
    fun computeModelViewProjectionMatrixAndReturnNew(
        projectionMatrix: FloatArray,
        modelViewMatrix: FloatArray
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewProjectionMatrix(projectionMatrix, modelViewMatrix, result)
        return result
    }

    /**
     * Computes model-view projection matrix using provided pinhole camera, viewport
     * size expressed in pixels, near and far planes and field of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param camera pinhole camera.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @return 16-length array containing the model-view projection matrix values.
     */
    fun computeModelViewProjectionMatrixAndReturnNew(
        camera: PinholeCamera,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewProjectionMatrix(
            camera,
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
        return result
    }

    /**
     * Computes model-view projection matrix using provided intrinsic pinhole camera
     * parameters, viewport size expressed in pixels, near and far planes and field
     * of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param cameraRotation camera rotation.
     * @param cameraCenter camera center.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @return 16-length array containing the model-view projection matrix values.
     */
    fun computeModelViewProjectionMatrixAndReturnNew(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        cameraRotation: Rotation3D,
        cameraCenter: Point3D,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewProjectionMatrix(
            intrinsicParameters,
            cameraRotation,
            cameraCenter,
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
        return result
    }

    /**
     * Computes model-view projection matrix using provided intrinsic pinhole camera
     * parameters, viewport size expressed in pixels, near and far planes and field
     * of view.
     *
     * The model-view projection matrix is the combination of both the projection
     * and the model-view matrices that OpenGL uses to draw the scene.
     *
     * The viewport is the surface where the scene is rendered. OpenGL internally
     * normalizes viewport coordinates from -1.0 to 1.0, but the hardware needs to
     * know the actual size of the surface expressed in pixels.
     *
     * Near and far planes define the region of the 3D space that is actually drawn.
     * Any vertex being at a depth closer to the camera than the near plane, or
     * any vertex being at a depth further from the camera than the far plane, is
     * ignored and not drawn.
     *
     * The field of view defines how wide is the region of the visible space. It is
     * closely related to the focal length of the pinhole camera intrinsic
     * parameters, and related to the amount of zoom of the scene.
     *
     * @param intrinsicParameters intrinsic pinhole camera parameters.
     * @param rotationMatrix 3x3 matrix defining camera rotation. This matrix must be orthonormal.
     * @param cameraCenter camera center.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param nearPlane near plane defining the closest visible vertices.
     * @param farPlane far plane defining the furthest visible vertices.
     * @return 16-length array containing the model-view projection matrix values.
     */
    fun computeModelViewProjectionMatrixAndReturnNew(
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        rotationMatrix: Matrix,
        cameraCenter: Point3D,
        width: Int,
        height: Int,
        nearPlane: Float,
        farPlane: Float
    ): FloatArray {
        val result = FloatArray(MATRIX_LENGTH)
        computeModelViewProjectionMatrix(
            intrinsicParameters,
            rotationMatrix,
            cameraCenter,
            width,
            height,
            nearPlane,
            farPlane,
            result
        )
        return result
    }

    /**
     * Obtains the intrinsic parameters contained within provided pinhole camera.
     *
     * @param camera a pinhole camera.
     * @throws CameraException if intrinsic parameters cannot be retrieved due to numerical
     * instabilities.
     */
    @Throws(CameraException::class)
    fun intrinsicParametersFromCamera(camera: PinholeCamera): PinholeCameraIntrinsicParameters {
        return if (camera.areIntrinsicParametersAvailable()) {
            camera.intrinsicParameters
        } else {
            camera.normalize()
            camera.fixCameraSign()
            camera.decompose()
            camera.intrinsicParameters
        }
    }

    /**
     * Obtains the camera rotation for provided pinhole camera.
     *
     * @param camera a pinhole camera.
     * @throws CameraException if camera rotation cannot be retrieved due to numerical
     * instabilities.
     */
    @Throws(CameraException::class)
    fun rotationFromCamera(camera: PinholeCamera): Rotation3D {
        return if (camera.isCameraRotationAvailable) {
            camera.cameraRotation
        } else {
            camera.normalize()
            camera.fixCameraSign()
            camera.decompose()
            camera.cameraRotation
        }
    }

    /**
     * Obtains the camera center for provided pinhole camera.
     *
     * @param camera a pinhole camera.
     * @throws CameraException if camera center cannot be retrieved due to numerical
     * instabilities.
     */
    @Throws(CameraException::class)
    fun centerFromCamera(camera: PinholeCamera): Point3D {
        return if (camera.isCameraCenterAvailable) {
            camera.cameraCenter
        } else {
            camera.normalize()
            camera.fixCameraSign()
            camera.decompose(false, true)
            camera.cameraCenter
        }
    }
}