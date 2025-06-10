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

/**
 * Indicates the amount of rotation between a camera sensor and the current display orientation.
 * This can be used for Augmented Reality applications conversion to and from pinhole
 * cameras is meant to convert to view coordinates.
 *
 * It must be noticed that OpenGL is right handed in object space and world space,
 * which means that y coordinate values increase upwards.
 *
 * When converting 3D vertices to display coordinates, projected points are converted to normalized
 * coordinates between -1 and 1. However, y coordinate values still increase upwards.
 *
 * However android views have a different system for screen coordinates, so that
 * x coordinate values increase towards right direction (just like OpenGL), but
 * oppositely to OpenGL, y coordinate values increase downwards.
 */
enum class CameraToDisplayOrientation {
    /**
     * Orientation corresponding to 0 degrees.
     * This is the default if no orientation is provided.
     */
    ORIENTATION_0_DEGREES,

    /**
     * Orientation corresponding to 90 degrees.
     */
    ORIENTATION_90_DEGREES,

    /**
     * Orientation corresponding to 180 degrees.
     */
    ORIENTATION_180_DEGREES,

    /**
     * Orientation corresponding to 270 degrees.
     */
    ORIENTATION_270_DEGREES,

    /**
     * This is used when camera-to-display orientation is unknown and must not be
     * taken into account when computing pinhole cameras or projection matrices
     * using [OpenGlToCameraHelper] or [CameraToOpenGlHelper].
     * When using this orientation, computed pinhole cameras remain right-handed
     * so that y coordinate values increase upwards.
     */
    ORIENTATION_UNKNOWN
}