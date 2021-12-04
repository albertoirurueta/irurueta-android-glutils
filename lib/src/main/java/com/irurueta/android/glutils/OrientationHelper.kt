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
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import com.irurueta.algebra.Matrix
import com.irurueta.geometry.*

/**
 * Utility class to determine the orientation of the camera sensor respect to current device
 * orientation.
 * Determining such orientation is useful for Augmented Reality applications, where a
 * pinhole camera needs to be estimated taking into account such orientation.
 *
 * @see OpenGlToCameraHelper
 */
object OrientationHelper {
    /**
     * Orientation corresponding to 0 degrees.
     */
    const val ORIENTATION_0_DEGREES = 0

    /**
     * Orientation corresponding to 90 degrees.
     */
    const val ORIENTATION_90_DEGREES = 90

    /**
     * Orientation corresponding to 180 degrees.
     */
    const val ORIENTATION_180_DEGREES = 180

    /**
     * Orientation corresponding to 270 degrees.
     */
    const val ORIENTATION_270_DEGREES = 270

    /**
     * Gets orientation of camera sensor. Camera sensor orientation is computed in terms of device
     * orientation.
     *
     * @param context  Android context.
     * @param cameraId A camera id.
     * @return Orientation of camera sensor.
     * @throws CameraAccessException If application does not have permission to access the camera.
     * @throws NullPointerException  If camera service is not available.
     */
    @Throws(CameraAccessException::class)
    fun getCameraDisplayOrientation(
        context: Context,
        cameraId: String
    ): CameraToDisplayOrientation {
        return convertOrientationDegreesToEnum(
            getCameraDisplayOrientationDegrees(
                context,
                cameraId
            )
        )
    }

    /**
     * Gets orientation of camera sensor. Camera sensor orientation is computed in terms of device
     * orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @return Orientation of camera sensor.
     */
    fun getCameraDisplayOrientation(
        context: Context,
        characteristics: CameraCharacteristics
    ): CameraToDisplayOrientation {
        return convertOrientationDegreesToEnum(
            getCameraDisplayOrientationDegrees(
                context,
                characteristics
            )
        )
    }

    /**
     * * Gets clockwise orientation of camera sensor respect to display orientation.
     *
     * @param context  Android context.
     * @param cameraId A camera id.
     * @return Clockwise orientation of camera sensor expressed in degrees.
     * @throws CameraAccessException If application does not have permission to access the camera.
     * @throws NullPointerException  If camera service is not available.
     */
    @Throws(CameraAccessException::class)
    fun getCameraDisplayOrientationDegrees(context: Context, cameraId: String): Int {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(cameraId)
        return getCameraDisplayOrientationDegrees(context, characteristics)
    }

    /**
     * Gets clockwise orientation of camera sensor respect to display orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @return Clockwise orientation of camera sensor expressed in degrees.
     */
    @Suppress("DEPRECATION")
    fun getCameraDisplayOrientationDegrees(
        context: Context,
        characteristics: CameraCharacteristics
    ): Int {
        // Camera sensor orientation respect the device
        val cameraSensorOrientation =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: return 0

        // Device orientation (portrait, landscape, etc)
        val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.rotation
        } else {
            val windowManager = context.getSystemService(
                Context.WINDOW_SERVICE
            ) as WindowManager

            windowManager.defaultDisplay.rotation
        }

        val deviceOrientation = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        return (cameraSensorOrientation - deviceOrientation + 360) % 360
    }

    /**
     * Creates the rotation required to convert from OpenGL coordinates to Android
     * view coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param result instance where counter-clockwise rotation indicating the
     * amount of display rotation respect the camera sensor will be stored.
     */
    fun toViewCoordinatesRotation(context: Context, cameraId: String, result: Rotation2D) {
        val angle = getCameraDisplayOrientationDegrees(context, cameraId)
        result.theta = Math.toRadians(angle.toDouble())
    }

    /**
     * Creates the rotation required to convert from OpenGL coordinates to Android
     * view coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param result instance where counter-clockwise rotation indicating the
     * amount of display rotation respect the camera sensor will be stored.
     */
    fun toViewCoordinatesRotation(
        context: Context,
        characteristics: CameraCharacteristics,
        result: Rotation2D
    ) {
        val angle = getCameraDisplayOrientationDegrees(context, characteristics)
        result.theta = Math.toRadians(angle.toDouble())
    }

    /**
     * Creates the rotation required to convert from OpenGL coordinates to Android
     * view coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * * @param cameraId A camera id.
     * @return counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     */
    fun toViewCoordinatesRotation(context: Context, cameraId: String): Rotation2D {
        val angle = getCameraDisplayOrientationDegrees(context, cameraId)
        return Rotation2D(Math.toRadians(angle.toDouble()))
    }

    /**
     * Creates the rotation required to convert from OpenGL coordinates to Android
     * view coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @return counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     */
    fun toViewCoordinatesRotation(
        context: Context,
        characteristics: CameraCharacteristics
    ): Rotation2D {
        val angle = getCameraDisplayOrientationDegrees(context, characteristics)
        return Rotation2D(Math.toRadians(angle.toDouble()))
    }

    /**
     * Creates the rotation required to convert from Android view coordinates to
     * OpenGL coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param result instance where counter-clockwise rotation indicating the
     * inverse of display rotation respect the camera sensor will be stored.
     */
    fun fromViewCoordinatesRotation(context: Context, cameraId: String, result: Rotation2D) {
        val angle = getCameraDisplayOrientationDegrees(context, cameraId)
        result.theta = Math.toRadians(-angle.toDouble())
    }

    /**
     * Creates the rotation required to convert from Android view coordinates to
     * OpenGL coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param result instance where counter-clockwise rotation indicating the
     * inverse of display rotation respect the camera sensor will be stored.
     */
    fun fromViewCoordinatesRotation(
        context: Context,
        characteristics: CameraCharacteristics,
        result: Rotation2D
    ) {
        val angle = getCameraDisplayOrientationDegrees(context, characteristics)
        result.theta = Math.toRadians(-angle.toDouble())
    }

    /**
     * Creates the rotation required to convert from Android view coordinates to
     * OpenGL coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @return counter-clockwise rotation indicating the inverse of display
     * rotation respect the camera sensor.
     */
    fun fromViewCoordinatesRotation(context: Context, cameraId: String): Rotation2D {
        val angle = getCameraDisplayOrientationDegrees(context, cameraId)
        return Rotation2D(Math.toRadians(-angle.toDouble()))
    }

    /**
     * Creates the rotation required to convert from Android view coordinates to
     * OpenGL coordinates.
     *
     * This rotation does not take into account the reversal of y coordinates
     * performed when estimating a transformation, and only takes into account
     * the current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @return counter-clockwise rotation indicating the inverse of display
     * rotation respect the camera sensor.
     */
    fun fromViewCoordinatesRotation(
        context: Context,
        characteristics: CameraCharacteristics
    ): Rotation2D {
        val angle = getCameraDisplayOrientationDegrees(context, characteristics)
        return Rotation2D(Math.toRadians(-angle.toDouble()))
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        val rotation = toViewCoordinatesRotation(context, cameraId)
        toViewCoordinatesTransformation(rotation, intrinsicParameters, result)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        val rotation = toViewCoordinatesRotation(context, characteristics)
        toViewCoordinatesTransformation(rotation, intrinsicParameters, result)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        if (orientation == CameraToDisplayOrientation.ORIENTATION_UNKNOWN) {
            result.t = Matrix.identity(
                ProjectiveTransformation2D.HOM_COORDS,
                ProjectiveTransformation2D.HOM_COORDS
            )
        } else {
            val rotation = convertOrientationEnumToRotation(orientation)
            toViewCoordinatesTransformation(rotation, intrinsicParameters, result)
        }
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        rotation: Rotation2D?,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        val px = intrinsicParameters.horizontalPrincipalPoint
        val py = intrinsicParameters.verticalPrincipalPoint
        toViewCoordinatesTransformation(rotation, InhomogeneousPoint2D(px, py), result)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        val rotation = toViewCoordinatesRotation(context, cameraId)
        toViewCoordinatesTransformation(rotation, pivot, result)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        val rotation = toViewCoordinatesRotation(context, characteristics)
        toViewCoordinatesTransformation(rotation, pivot, result)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        if (orientation == CameraToDisplayOrientation.ORIENTATION_UNKNOWN) {
            result.t = Matrix.identity(
                ProjectiveTransformation2D.HOM_COORDS,
                ProjectiveTransformation2D.HOM_COORDS
            )
        } else {
            val rotation = convertOrientationEnumToRotation(orientation)
            toViewCoordinatesTransformation(rotation, pivot, result)
        }
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation
     * @param result instance where computed transformation will be stored.
     */
    fun toViewCoordinatesTransformation(
        rotation: Rotation2D?,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        val px = pivot.inhomX
        val py = pivot.inhomY

        // First bring pivot point to origin of coordinates
        // Then rotate
        // Then apply y coordinate reversal
        // And finally undo the first step

        // T = Tt^-1 * Tr * R * Tt
        val tt = ProjectiveTransformation2D()
        tt.setTranslation(-px, -py)
        tt.normalize()

        val invTt = tt.inverseAndReturnNew()
        val r = ProjectiveTransformation2D(rotation)
        val tr = ProjectiveTransformation2D(reverseYCoordinatesMatrix())

        // Tt^-1 * Tr
        result.t = invTt.asMatrix()
        result.combine(tr)

        // Tt^-1 * Tr * R
        result.combine(r)

        // Tt^-1 * Tr * R * Tt
        result.combine(tt)
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(context, cameraId, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(
            context,
            characteristics,
            intrinsicParameters,
            result
        )
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(orientation, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation or the identity if rotation is null
     */
    fun toViewCoordinatesTransformation(
        rotation: Rotation2D?,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(rotation, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(context, cameraId, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(context, characteristics, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(orientation, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from OpenGL
     * coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @return 2D transformation.
     */
    fun toViewCoordinatesTransformation(
        rotation: Rotation2D?,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        toViewCoordinatesTransformation(rotation, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(context, cameraId, intrinsicParameters, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(
            context,
            characteristics,
            intrinsicParameters,
            result
        )
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(orientation, intrinsicParameters, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param intrinsicParameters camera intrinsic parameters.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        rotation: Rotation2D?,
        intrinsicParameters: PinholeCameraIntrinsicParameters,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(rotation, intrinsicParameters, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(context, cameraId, pivot, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(context, characteristics, pivot, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(orientation, pivot, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @param result instance where computed transformation will be stored.
     */
    fun fromViewCoordinatesTransformation(
        rotation: Rotation2D?,
        pivot: Point2D,
        result: ProjectiveTransformation2D
    ) {
        toViewCoordinatesTransformation(rotation, pivot, result)
        result.inverse()
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(context, cameraId, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(
            context,
            characteristics,
            intrinsicParameters,
            result
        )
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(orientation, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param intrinsicParameters camera intrinsic parameters.
     * @return 2D transformation or the identity if rotation is null
     */
    fun fromViewCoordinatesTransformation(
        rotation: Rotation2D?,
        intrinsicParameters: PinholeCameraIntrinsicParameters
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(rotation, intrinsicParameters, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        cameraId: String,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(context, cameraId, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(context, characteristics, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation
     * @return 2D transformation.
     */
    fun fromViewCoordinatesTransformation(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(orientation, pivot, result)
        return result
    }

    /**
     * Creates 2D transformation to convert 2D point coordinates from Android
     * view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation
     * @return 2D transformation or the identity if rotation is null.
     */
    fun fromViewCoordinatesTransformation(
        rotation: Rotation2D?,
        pivot: Point2D
    ): ProjectiveTransformation2D {
        val result = ProjectiveTransformation2D()
        fromViewCoordinatesTransformation(rotation, pivot, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = toViewCoordinatesTransformation(context, cameraId, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation =
            toViewCoordinatesTransformation(context, characteristics, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = toViewCoordinatesTransformation(orientation, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        rotation: Rotation2D?,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = toViewCoordinatesTransformation(rotation, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = toViewCoordinatesTransformation(context, cameraId, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = toViewCoordinatesTransformation(context, characteristics, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = toViewCoordinatesTransformation(orientation, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun toViewCoordinatesCamera(
        rotation: Rotation2D?,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = toViewCoordinatesTransformation(rotation, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(context, cameraId, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(context, characteristics, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(orientation, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        rotation: Rotation2D?,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(rotation, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(context, cameraId, pivot, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(context, characteristics, pivot, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(orientation, pivot, input, result)
        return result
    }

    /**
     * Converts camera from OpenGL coordinates to Android view coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun toViewCoordinatesCamera(
        rotation: Rotation2D?,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        toViewCoordinatesCamera(rotation, pivot, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = fromViewCoordinatesTransformation(context, cameraId, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation =
            fromViewCoordinatesTransformation(context, characteristics, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = fromViewCoordinatesTransformation(orientation, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        rotation: Rotation2D?,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val intrinsic = CameraToOpenGlHelper.intrinsicParametersFromCamera(input)
        val transformation = fromViewCoordinatesTransformation(rotation, intrinsic)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = fromViewCoordinatesTransformation(context, cameraId, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = fromViewCoordinatesTransformation(context, characteristics, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = fromViewCoordinatesTransformation(orientation, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun fromViewCoordinatesCamera(
        rotation: Rotation2D?,
        pivot: Point2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        val transformation = fromViewCoordinatesTransformation(rotation, pivot)
        transformCamera(transformation, input, result)
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(context, cameraId, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(context, characteristics, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(orientation, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        rotation: Rotation2D?,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(rotation, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param cameraId A camera id.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        cameraId: String,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(context, cameraId, pivot, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * This method takes into account current display and camera orientation.
     *
     * @param context Android context.
     * @param characteristics Information about the camera.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        context: Context,
        characteristics: CameraCharacteristics,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(context, characteristics, pivot, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        orientation: CameraToDisplayOrientation,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(orientation, pivot, input, result)
        return result
    }

    /**
     * Converts camera from Android view coordinates to OpenGL coordinates.
     *
     * It must be noticed that OpenGL uses a right handed coordinate system where
     * y coordinates increase upwards, whereas Android uses y coordinates that
     * increase downwards and takes into account screen and camera sensor orientation.
     *
     * @param rotation counter-clockwise rotation indicating the amount of display
     * rotation respect the camera sensor.
     * @param pivot point to use as pivot for rotation.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun fromViewCoordinatesCamera(
        rotation: Rotation2D?,
        pivot: Point2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        fromViewCoordinatesCamera(rotation, pivot, input, result)
        return result
    }

    /**
     * Transforms camera using provided 2D transformation.
     *
     * @param transformation 2D transformation to apply to the camera.
     * @param input camera to be transformed.
     * @param result instance where transformed camera will be stored.
     */
    fun transformCamera(
        transformation: ProjectiveTransformation2D,
        input: PinholeCamera,
        result: PinholeCamera
    ) {
        transformation.normalize()
        val t = transformation.t

        input.normalize()
        input.fixCameraSign()
        val p = input.internalMatrix

        result.internalMatrix = t.multiplyAndReturnNew(p)
    }

    /**
     * Transforms camera using provided 2D transformation.
     *
     * @param transformation 2D transformation to apply to the camera.
     * @param input camera to be transformed.
     * @return transformed camera.
     */
    fun transformCamera(
        transformation: ProjectiveTransformation2D,
        input: PinholeCamera
    ): PinholeCamera {
        val result = PinholeCamera()
        transformCamera(transformation, input, result)
        return result
    }

    /**
     * Converts provided clockwise orientation expressed in degrees to the
     * corresponding [CameraToDisplayOrientation]
     *
     * @param degrees clockwise orientation expressed in degrees.
     * @return converted result
     */
    private fun convertOrientationDegreesToEnum(degrees: Int): CameraToDisplayOrientation {
        return when (degrees) {
            ORIENTATION_0_DEGREES -> CameraToDisplayOrientation.ORIENTATION_0_DEGREES
            ORIENTATION_90_DEGREES -> CameraToDisplayOrientation.ORIENTATION_90_DEGREES
            ORIENTATION_180_DEGREES -> CameraToDisplayOrientation.ORIENTATION_180_DEGREES
            ORIENTATION_270_DEGREES -> CameraToDisplayOrientation.ORIENTATION_270_DEGREES
            else -> CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        }
    }

    /**
     * Converts provided orientation to amount of clockwise rotation expressed in radians.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored.
     * @return amount of clockwise rotation expressed in radians or null if orientation is unknown.
     */
    private fun convertOrientationEnumToRadians(orientation: CameraToDisplayOrientation): Double? {
        return when (orientation) {
            CameraToDisplayOrientation.ORIENTATION_0_DEGREES -> 0.0
            CameraToDisplayOrientation.ORIENTATION_90_DEGREES -> 0.5 * Math.PI
            CameraToDisplayOrientation.ORIENTATION_180_DEGREES -> Math.PI
            CameraToDisplayOrientation.ORIENTATION_270_DEGREES -> 1.5 * Math.PI
            CameraToDisplayOrientation.ORIENTATION_UNKNOWN -> null
        }
    }

    /**
     * Converts provided orientation into a 2D rotation representing required
     * rotation to go from OpenGL coordinates to Android view coordinates.
     *
     * @param orientation amount of rotation between display and camera sensor.
     * [CameraToDisplayOrientation.ORIENTATION_UNKNOWN] indicates that orientation is unknown and
     * must be ignored (Y coordinates are not reversed either).
     */
    private fun convertOrientationEnumToRotation(
        orientation: CameraToDisplayOrientation
    ): Rotation2D? {
        val angle = convertOrientationEnumToRadians(orientation)
        return if (angle != null) {
            Rotation2D(angle)
        } else {
            null
        }
    }

    /**
     * Creates a 2D transformation matrix to reverse Y coordinates.
     * This is done to convert from the right handed coordinate system provided by
     * OpenGL, where y coordinates increase upwards, to the view coordinates
     * system provided by Android where y coordinates increase downwards.
     *
     * @return matrix containing 2D transformation to reverse Y coordinates.
     */
    private fun reverseYCoordinatesMatrix(): Matrix {
        // The reverse transformation has the following expression:
        // Tr = [1  0   0]
        //      [0  -1  0]
        //      [0  0   1]

        val result = Matrix.identity(
            ProjectiveTransformation2D.HOM_COORDS,
            ProjectiveTransformation2D.HOM_COORDS
        )
        result.setElementAt(1, 1, -1.0)
        return result
    }
}