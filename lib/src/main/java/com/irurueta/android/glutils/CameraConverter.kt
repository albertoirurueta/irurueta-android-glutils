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

/**
 * Converts camera into OpenGL matrices used to render a 3D scene.
 */
class CameraConverter(width: Int, height: Int) {

    init {
        setViewportSize(width, height)
    }

    /**
     * Internal camera defining point of view of a scene.
     */
    private var _camera: PinholeCamera? = null

    /**
     * Internal camera defining point of view of a scene and expressed in Android view coordinates
     * by taking into account current orientation.
     * If orientation is unknown, view camera is not computed.
     */
    private var _viewCamera: PinholeCamera? = null

    /**
     * Internal width of the viewport expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual viewport width expressed in pixels.
     */
    private var _width = 0

    /**
     * Internal height of the viewport expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual viewport height expressed in pixels.
     */
    private var _height = 0

    /**
     * Internal near plane value.
     * Any vertex nearer to the camera than this value is ignored and not drawn.
     */
    private var _nearPlane = DEFAULT_NEAR_PLANE

    /**
     * Internal far plane value.
     * Any vertex further from the camera than this value is ignored and not drawn.
     */
    private var _farPlane = DEFAULT_FAR_PLANE

    /**
     * Contains matrix defining frustum of projection camera, which defines parameters
     * such as focal length, principal point or camera skewness.
     */
    private var _projectionMatrix: FloatArray? = null

    /**
     * Contains matrix transforming the camera to be at current camera center and
     * rotation.
     */
    private var _modelViewMatrix: FloatArray? = null

    /**
     * Indicates orientation to compute a pinhole camera expressed in view coordinates.
     * If orientation is unknown, view camera is not computed.
     */
    private var _orientation: CameraToDisplayOrientation =
        CameraToDisplayOrientation.ORIENTATION_UNKNOWN

    /**
     * Contains a 3x3 rotation matrix to be reused for efficiency reasons.
     */
    private val rotationMatrix = Matrix.identity(Rotation3D.INHOM_COORDS, Rotation3D.INHOM_COORDS)

    /**
     * Transformation to be reused.
     */
    private var transformation = ProjectiveTransformation2D()

    /**
     * Inverse transformation to be reused.
     */
    private var inverseTransformation = ProjectiveTransformation2D()

    /**
     * Gets or sets orientation to compute a pinhole camera expressed in view coordinates.
     * If orientation is unknown, view camera is not computed.
     */
    var orientation: CameraToDisplayOrientation
        get() = _orientation
        set(value) {
            _orientation = value
            computeTransformations()
            computeCameras()
            computeMatrices()
        }

    /**
     * Gets or sets OpenGL projection matrix using current values.
     */
    var projectionMatrix: FloatArray?
        get() = _projectionMatrix
        set(value) {
            _projectionMatrix = value
            computeCameras()
        }

    /**
     * Gets or sets OpenGL model view matrix using current values.
     */
    var modelViewMatrix: FloatArray?
        get() = _modelViewMatrix
        set(value) {
            _modelViewMatrix = value
            computeCameras()
        }

    /**
     * Contains matrix defining full camera matrix to draw the scene as a 4x4 matrix
     * following OpenGL format (which also encodes near and far planes.
     */
    var modelViewProjectionMatrix: FloatArray? = null
        private set

    /**
     * Gets or sets flag indicating whether model view projection matrices must be
     * computed when any value changes.
     */
    var isModelViewProjectionMatrixComputationEnabled = false
        set(value) {
            field = value
            computeMatrices()
        }

    /**
     * Near plane value.
     * Any vertex nearer to the camera than this value is ignored and not drawn.
     */
    var nearPlane
        get() = _nearPlane
        set(value) {
            _nearPlane = value
            computeMatrices()
            computeCameras()
        }

    /**
     * Far plane value.
     * Any vertex further from the camera than this value is ignored and not drawn.
     */
    var farPlane
        get() = _farPlane
        set(value) {
            _farPlane = value
            computeMatrices()
            computeCameras()
        }

    /**
     * Sets near and far plane values.
     * Any vertex nearer to the camera than the near plane, or any vertex further
     * from the camera than the far plane is ignored and not drawn.
     *
     * @param nearPlane near plane.
     * @param farPlane far plane.
     */
    fun setNearFarPlanes(nearPlane: Float, farPlane: Float) {
        _nearPlane = nearPlane
        _farPlane = farPlane
        computeMatrices()
        computeCameras()
    }

    /**
     * Width of the viewport expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual viewport width expressed in pixels.
     *
     * @throws IllegalArgumentException if provided value is negative.
     */
    var width
        get() = _width
        @Throws(IllegalArgumentException::class)
        set(value) {
            require(value > 0)

            _width = value
            computeMatrices()
        }

    /**
     * Height of the viewport expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual viewport height expressed in pixels.
     *
     * @throws IllegalArgumentException if provided value is negative.
     */
    var height
        get() = _height
        @Throws(IllegalArgumentException::class)
        set(value) {
            require(value > 0)

            _height = value
            computeMatrices()
        }

    /**
     * Sets size of viewport expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f.
     *
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @throws IllegalArgumentException if either provided width or height is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun setViewportSize(width: Int, height: Int) {
        require(width > 0)
        require(height > 0)

        _width = width
        _height = height

        computeCameras()
        computeMatrices()
    }

    /**
     * Camera being converted.
     *
     * @throws IllegalArgumentException if provided value is null.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var camera
        get() = _camera
        @Throws(IllegalArgumentException::class, CameraException::class)
        set(value) {
            require(value != null)

            val previousCamera = _camera
            value.normalize()
            value.fixCameraSign()
            value.decompose()
            _camera = value

            if (previousCamera == null) {
                computeTransformations()
            }
            computeMatrices()
            computeViewCameraFromCamera()
        }

    /**
     * Camera being converted expressed in Android view coordinates.
     * This is only available or can only be set if orientation is defined
     * (not unknown).
     * @throws IllegalArgumentException if value to be set is not null when orientation is unknown,
     * or if value to be set is null when orientation is known.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var viewCamera
        get() = _viewCamera
        @Throws(IllegalArgumentException::class, CameraException::class)
        set(value) {
            if (_orientation != CameraToDisplayOrientation.ORIENTATION_UNKNOWN) {
                require(value != null)
            } else {
                require(value == null)
            }

            val previousViewCamera = _viewCamera
            value?.normalize()
            value?.fixCameraSign()
            value?.decompose()
            _viewCamera = value

            if (previousViewCamera == null && value != null) {
                computeTransformations()
            }
            if (value != null) {
                computeCameraFromViewCamera()
            }
            computeMatrices()
        }

    /**
     * Gets or sets camera intrinsic parameters.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraIntrinsicParameters: PinholeCameraIntrinsicParameters?
        get() {
            val camera = _camera ?: return null
            return CameraToOpenGlHelper.intrinsicParametersFromCamera(camera)
        }
        @Throws(IllegalArgumentException::class)
        set(value) {
            require(value != null)

            val camera = _camera
            if (camera != null) {
                camera.intrinsicParameters = value
            } else {
                _camera = PinholeCamera(value, Rotation3D.create(), Point3D.create())
            }
            computeMatrices()
            computeViewCameraFromCamera()
        }

    /**
     * Gets or sets camera center position.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraCenter: Point3D?
        get() {
            val camera = _camera ?: return null
            return CameraToOpenGlHelper.centerFromCamera(camera)
        }
        @Throws(IllegalArgumentException::class)
        set(value) {
            require(value != null)

            val camera = _camera
            if (camera != null) {
                camera.cameraCenter = value
            } else {
                _camera = PinholeCamera(
                    PinholeCameraIntrinsicParameters(), Rotation3D.create(), value
                )
            }
            computeMatrices()
            computeViewCameraFromCamera()
        }

    /**
     * Gets or sets camera rotation.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraRotation: Rotation3D?
        get() {
            val camera = _camera ?: return null
            return CameraToOpenGlHelper.rotationFromCamera(camera)
        }
        @Throws(IllegalArgumentException::class)
        set(value) {
            require(value != null)

            val camera = _camera
            if (camera != null) {
                camera.cameraRotation = value
            } else {
                _camera = PinholeCamera(PinholeCameraIntrinsicParameters(), value, Point3D.create())
            }
            computeMatrices()
            computeViewCameraFromCamera()
        }

    /**
     * Gets or sets view camera intrinsic parameters
     *
     * @throws IllegalStateException if attempting to set any value when orientation is unknown
     * @throws IllegalArgumentException if provided value is null
     */
    var viewCameraIntrinsicParameters: PinholeCameraIntrinsicParameters?
        get() {
            val viewCamera = _viewCamera ?: return null
            return CameraToOpenGlHelper.intrinsicParametersFromCamera(viewCamera)
        }
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            check(_orientation != CameraToDisplayOrientation.ORIENTATION_UNKNOWN)
            requireNotNull(value)

            val viewCamera = _viewCamera
            if (viewCamera != null) {
                viewCamera.intrinsicParameters = value
            } else {
                _viewCamera = PinholeCamera(value, Rotation3D.create(), Point3D.create())
            }
            computeCameraFromViewCamera()
            computeMatrices()
        }

    /**
     * Gets or sets view camera center position.
     *
     * @throws IllegalStateException if attempting to set any value when orientation is unknown
     * @throws IllegalArgumentException if provided value is null
     */
    var viewCameraCenter: Point3D?
        get() {
            val viewCamera = _viewCamera ?: return null
            return CameraToOpenGlHelper.centerFromCamera(viewCamera)
        }
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            check(_orientation != CameraToDisplayOrientation.ORIENTATION_UNKNOWN)
            requireNotNull(value)

            val viewCamera = _viewCamera
            if (viewCamera != null) {
                viewCamera.cameraCenter = value
            } else {
                _viewCamera =
                    PinholeCamera(PinholeCameraIntrinsicParameters(), Rotation3D.create(), value)
            }
            computeCameraFromViewCamera()
            computeMatrices()
        }

    /**
     * Gets or sets view camera rotation.
     *
     * @throws IllegalStateException if attempting to set any value when orientation is unknown
     * @throws IllegalArgumentException if provided value is null
     */
    var viewCameraRotation: Rotation3D?
        get() {
            val viewCamera = _viewCamera ?: return null
            return CameraToOpenGlHelper.rotationFromCamera(viewCamera)
        }
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            check(_orientation != CameraToDisplayOrientation.ORIENTATION_UNKNOWN)
            requireNotNull(value)

            val viewCamera = _viewCamera
            if (viewCamera != null) {
                viewCamera.cameraRotation = value
            } else {
                _viewCamera =
                    PinholeCamera(PinholeCameraIntrinsicParameters(), value, Point3D.create())
            }
            computeCameraFromViewCamera()
            computeMatrices()
        }

    /**
     * Sets all required values to compute the scene camera matrix.
     * @param nearPlane near plane. Any vertex nearer to the camera than this value will be ignored
     * and not drawn.
     * @param farPlane far plane. Any vertex further from the camera than this value will be ignored
     * and not drawn.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param camera camera to be set.
     * @throws IllegalArgumentException if either provided width or height is negative.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    @Throws(IllegalArgumentException::class, CameraException::class)
    fun setValues(
        nearPlane: Float,
        farPlane: Float,
        width: Int,
        height: Int,
        camera: PinholeCamera
    ) {
        require(width >= 0)
        require(height >= 0)

        _nearPlane = nearPlane
        _farPlane = farPlane
        _width = width
        _height = height

        val previousCamera = _camera
        camera.normalize()
        camera.fixCameraSign()
        camera.decompose()
        _camera = camera

        if (previousCamera == null) {
            computeTransformations()
        }
        computeMatrices()
        computeViewCameraFromCamera()
    }

    /**
     * Computes OpenGL projection matrix using current values.
     */
    private fun computeProjectionMatrix() {
        val camera = _camera
        if (camera != null) {
            val projectionMatrix = createProjectionMatrixIfNeeded()
            CameraToOpenGlHelper.computeProjectionMatrix(
                camera,
                _width,
                _height,
                _nearPlane,
                _farPlane,
                projectionMatrix
            )
        } else {
            _projectionMatrix = null
        }
    }

    /**
     * Computes OpenGL model view matrix using current values.
     */
    private fun computeModelViewMatrix() {
        val camera = _camera
        if (camera != null) {
            val rotation = cameraRotation
            require(rotation != null)
            rotation.asInhomogeneousMatrix(rotationMatrix)

            val center = cameraCenter
            require(center != null)

            val modelViewMatrix = createModelViewMatrixIfNeeded()
            CameraToOpenGlHelper.computeModelViewMatrix(rotationMatrix, center, modelViewMatrix)
        } else {
            _modelViewMatrix = null
        }
    }

    /**
     * Computes OpenGL model view projection matrix using current values.
     */
    private fun computeModelViewProjectionMatrix() {
        val camera = _camera
        if (camera != null) {
            val projectionMatrix = createProjectionMatrixIfNeeded()
            val modelViewMatrix = createModelViewMatrixIfNeeded()
            val modelViewProjectionMatrix = createModelViewProjectionMatrixIfNeeded()

            CameraToOpenGlHelper.computeModelViewProjectionMatrix(
                projectionMatrix,
                modelViewMatrix,
                modelViewProjectionMatrix
            )
        } else {
            modelViewProjectionMatrix = null
        }
    }

    /**
     * Computes all OpenGL matrices using current values.
     */
    private fun computeMatrices() {
        computeProjectionMatrix()
        computeModelViewMatrix()
        if (isModelViewProjectionMatrixComputationEnabled) {
            computeModelViewProjectionMatrix()
        } else {
            modelViewProjectionMatrix = null
        }
    }

    /**
     * Creates an instance of an OpenGL projection matrix if none already exists.
     *
     * @return existing projection matrix or a new one if none exists.
     */
    private fun createProjectionMatrixIfNeeded(): FloatArray {
        var matrix = _projectionMatrix
        if (matrix == null) {
            matrix = FloatArray(HOM_MATRIX_LENGTH)
            _projectionMatrix = matrix
        }
        return matrix
    }

    /**
     * Creates an instance of an OpenGL model view matrix if none already exists.
     *
     * @return existing model view matrix or a new one if none exists.
     */
    private fun createModelViewMatrixIfNeeded(): FloatArray {
        var matrix = _modelViewMatrix
        if (matrix == null) {
            matrix = FloatArray(HOM_MATRIX_LENGTH)
            _modelViewMatrix = matrix
        }
        return matrix
    }

    /**
     * Creates an instance of an OpenGL model view projection matrix if none already
     * exists.
     *
     * @return existing model view projection matrix or a new one if none exists.
     */
    private fun createModelViewProjectionMatrixIfNeeded(): FloatArray {
        var matrix = modelViewProjectionMatrix
        if (matrix == null) {
            matrix = FloatArray(HOM_MATRIX_LENGTH)
            modelViewProjectionMatrix = matrix
        }
        return matrix
    }

    /**
     * Computes a pinhole camera using current values.
     */
    private fun computeCameraFromMatrices() {
        val projectionMatrix = _projectionMatrix
        val modelViewMatrix = _modelViewMatrix
        _camera = if (projectionMatrix != null && modelViewMatrix != null) {

            val camera = _camera ?: PinholeCamera()
            OpenGlToCameraHelper.computePinholeCamera(
                projectionMatrix,
                modelViewMatrix,
                _width,
                _height,
                camera
            )
            camera
        } else {
            null
        }
    }

    /**
     * Computes camera expressed in Android view coordinates using current camera values.
     * Computation will be made only if an orientation is defined.
     */
    private fun computeViewCameraFromCamera() {
        _viewCamera = if (_orientation == CameraToDisplayOrientation.ORIENTATION_UNKNOWN) {
            null
        } else {
            val camera = _camera
            if (camera != null) {
                val viewCamera = _viewCamera ?: PinholeCamera()
                OrientationHelper.transformCamera(transformation, camera, viewCamera)
                viewCamera
            } else {
                null
            }
        }
    }

    /**
     * Computes camera in using current view camera values.
     * Computation will be made only if an orientation is defined.
     */
    private fun computeCameraFromViewCamera() {
        val viewCamera = _viewCamera
        require(viewCamera != null)

        val camera = _camera ?: PinholeCamera()
        OrientationHelper.transformCamera(inverseTransformation, viewCamera, camera)
        _camera = camera
    }

    /**
     * Computes cameras using current values.
     * Camera expressed in Android view coordinates is only computed if an
     * orientation is defined.
     */
    private fun computeCameras() {
        computeCameraFromMatrices()
        computeViewCameraFromCamera()
    }

    /**
     * Computes transformations to convert cameras to and from Android view coordinates.
     */
    private fun computeTransformations() {
        if (_orientation != CameraToDisplayOrientation.ORIENTATION_UNKNOWN) {
            val intrinsicParameters = cameraIntrinsicParameters ?: viewCameraIntrinsicParameters
            if (intrinsicParameters != null) {
                OrientationHelper.toViewCoordinatesTransformation(
                    _orientation,
                    intrinsicParameters,
                    transformation
                )
                transformation.normalize()

                OrientationHelper.fromViewCoordinatesTransformation(
                    _orientation,
                    intrinsicParameters,
                    inverseTransformation
                )
                inverseTransformation.normalize()
            }
        }
    }

    companion object {
        /**
         * Default near plane value defining visible frustum.
         */
        const val DEFAULT_NEAR_PLANE = 0.1f

        /**
         * Default far plane value defining visible frustum.
         */
        const val DEFAULT_FAR_PLANE = 1000.0f

        /**
         * Length of arrays containing 4x4 homogeneous matrices to be used by OpenGL.
         */
        private const val HOM_MATRIX_LENGTH = 16
    }
}