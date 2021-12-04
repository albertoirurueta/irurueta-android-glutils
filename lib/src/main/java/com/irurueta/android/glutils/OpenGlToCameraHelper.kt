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
 * Helper class to convert matrices used by OpenGL to draw a scene into its
 * corresponding pinhole camera.
 *
 * Typically, when using OpenGL, a projective matrix defines the projective
 * geometry (i.e. frustum) of the scene, using parameters such as field of view,
 * near and far planes.
 *
 * OpenGL typically also uses a model-view matrix, which defines the amount of
 * translation and rotation between the camera and vertices to be drawn in the
 * scene.
 *
 * Using both a projective matrix and a model-view matrix, the corresponding pinhole
 * camera can be estimated.
 *
 * If a pinhole camera to convert to Android view coordinates (where y coordinates
 * increase downwards), please use one of the methods provided in [OrientationHelper].
 */
object OpenGlToCameraHelper {
    /**
     * Length of arrays used to store values of 4x4 homogeneous matrices.
     */
    const val MATRIX_LENGTH = 16

    /**
     * Computes pinhole camera intrinsic parameters from provided projection matrix
     * and viewport size.
     *
     * @param projectionMatrix array containing values of 4x4 matrix defining projection matrix.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param result instance where computed pinhole camera intrinsic parameters will be stored.
     */
    fun computePinholeCameraIntrinsics(
        projectionMatrix: FloatArray,
        width: Int,
        height: Int,
        result: PinholeCameraIntrinsicParameters,
    ) {
        require(projectionMatrix.size == MATRIX_LENGTH)

        // Check link below for some theory about rendering frustum and camera intrinsics
        // http://ksimek.github.io/2013/08/13/intrinsic/
        // http://ksimek.github.io/2013/06/18/calibrated-cameras-and-gluperspective/
        // http://ksimek.github.io/2013/06/03/calibrated_cameras_in_opengl/

        // According to:
        // http://www.songho.ca/opengl/gl_projectionmatrix.html

        // we know that the projection matrix defining the rendering frustum can be
        // generally expressed as:

        // [2*n/(r - l)     0               (r + l)/(r - l)     0               ]
        // [0               2*n/(t - b)     (t + b)/(t - b)     0               ]
        // [0               0               -(f + n)/(f - n)    -2*f*n/(f - n)  ]
        // [0               0               -1                  0               ]

        // where:
        // n = near plane, which is equal to FRUSTUM_NEAR = 0.1 m
        // f = far plane, which is equal to FRUSTUM_FAR = 100.0 m
        // t = top coordinate.
        // b = bottom coordinate.
        // l = left coordinate.
        // r = right coordinate.

        // this is what android.opengl.Matrix.frustumM(float[] m, int offset,
        // float left, float right, float bottom, float top, float near, float far)
        // generates.

        // knowing projection matrix values, near plane and far plane, we can solve
        // top, bottom, left and right coordinates as follows:

        // projection[0] = 2*n/(r - l) --> r - l = 2*n/projection[0]
        // projection[8] = (r + l)/(r -l) --> r + l = projection[8]*2*n/projection[0]

        // projection[5] = 2*n/(t - b) --> t - b = 2*n/projection[5]
        // projection[9] = (t + b)/(t - b) --> t + b = projection[9]*2*n/projection[5]

        // we have now 2 systems of equations to solve

        // 1st system of equations (solves r and l):
        // r - l = 2*n/projection[0]
        // r + l = projection[8]*2*n/projection[0]

        // r = 2*n/projection[0] + l
        // 2*n/projection[0] + 2*l = projection[8]*2*n/projection[0]

        // l = (projection[8]*2*n/projection[0] - 2*n/projection[0])/2
        // r = 2*n/projection[0] + (projection[8]*2*n/projection[0] - 2*n/projection[0])/2

        // 2nd system of equations (solves t and b):
        // t - b = 2*n/projection[5]
        // t + b = projection[9]*2*n/projection[5]

        // t = 2*n/projection[5] + b
        // 2*n/projection[5] + 2*b = projection[9]*2*n/projection[5]

        // b = (projection[9]*2*n/projection[5] - 2*n/projection[5])/2
        // t = 2*n/projection[5] + (projection[9]*2*n/projection[5] - 2*n/projection[5])/2

        // For a Pixel 2 XL with n = 0.1 and f = 100.0 the projection matrix is:

        // [2.8693345   0.0         -0.004545755        0.0         ]
        // [0.0	        1.5806589   0.009158132         0.0         ]
        // [0.0	        0.0         -1.002002           -0.2002002  ]
        // [0.0         0.0         -1.0                0.0         ]

        // Hence:
        // l = (-0.004545755*2*0.1/2.8693345 - 2*0.1/2.8693345)/2 = -0.03500971236
        // r = 2*0.1/2.8693345 - 0.03500971236 = 0.03469286153
        // b = (0.009158132*2*0.1/1.5806589 - 2*0.1/1.5806589)/2 = -0.06268536925
        // t = 2*0.1/1.5806589 -0.06268536925 = 0,06384414322

        // Notice that l, r, t, b values are expressed on NDC (Normalized Device Coordinates)

        // Also notice that it has been assumed on the projection matrix that the skew factor is zero.

        // On the other hand, using
        // android.opengl.Matrix.perspectiveM(float[] m, int offset,
        // float fovy, float aspect, float zNear, float zFar)
        // the projection matrix can also be defined in terms of field of view
        // (assuming that the principal point is centered).

        // Hence the projection matrix is expressed in this case as:
        // [fv/aspect   0       0                   0               ]
        // [0           fv      0                   0               ]
        // [0           0       -(f + n)/(f - n)    -2*f*n/(f - n)  ]
        // [0           0       -1                  0               ]

        // where:
        // n = near plane, which is equal to FRUSTUM_NEAR = 0.1 m
        // f = far plane, which is equal to FRUSTUM_FAR = 100.0 m
        // fv = 1/tan(fovy * pi / 360)
        // fovy is the vertical field of view expressed on degrees
        // aspect is the width to height aspect ratio of the view port

        // hence:
        // aspect = width / height
        // fovy = atan(1/fv)*360/pi

        // taking into account:
        // https://www.edmundoptics.es/resources/application-notes/imaging/understanding-focal-length-and-field-of-view/

        // we now that the field of view is related to the focal length such that:
        // fovy = 2*atan((height/2)/fy) * 180 / pi

        // where:
        // fovy = the vertical field of view expressed on degrees
        // height = the viewport height expressed in pixels
        // fy = the vertical focal length expressed in pixels.

        // Finally we know that the camera intrinsic parameters is expressed as:
        // [fx  s   px]
        // [0   fy  py]
        // [0   0   1 ]

        // where:
        // fx = horizontal focal length expressed in pixels
        // fy = vertical focal length expressed in pixels
        // px = horizontal principal point coordinate expressed in pixels.
        // py = vertical principal point coordinate expressed in pixels.

        // notice that:
        // fx = fy * pixelAspectRatio

        // Before building the intrinsic parameters we need to finally take into
        // consideration that:
        // - if the sensor is rotated 90 or 270 degrees, horizontal (width) and vertical
        // (height) coordinates are exchanged.
        // - openGL defines vertical coordinates in ascending order from bottom to top,
        // while view coordinates are the opposite (defined in ascending order from top to
        // bottom). For that reason vertical focal length must be negated.
        // However, if sensor is rotated 90 degrees, then horizontal focal length must be negated.

        val fv = projectionMatrix[5].toDouble()

        // Since fovyRadians = 2.0 * atan(1.0 / fv)
        // And fovyRadians = 2.0 * atan((height/2)/fy)
        // Then:
        // 1.0 / fv = (height / 2) / fy
        // Consequently: fy = fv * height / 2

        val fy = fv * height / 2.0

        // Pixel aspect ratio is defined as fy / fx
        // Since:
        // fy = projectionMatrix[5] * height / 2.0 --> projectionMatrix[5] = 2 * fy / height
        // fx = projectionMatrix[0] * width / 2.0 --> projectionMatrix[0] = 2 * fx / width
        // Consequently:
        // aspectRatio = fy / fx = projectionMatrix[5] * height / (projectionMatrix[0] * width)
        val pixelAspectRatio =
            (projectionMatrix[5].toDouble() * height) / (projectionMatrix[0].toDouble() * width)

        val fx = fy / pixelAspectRatio

        // finally principal point should be close to the center of the view port
        // px_ndc = (r + l)/(r - l) = projection[8]
        // py_ndc = (t + b)/(t - b) = projection[9]

        // NDC coordinates maps pixel coordinates to values between -1 and 1
        // Hence:
        // r - l = width
        // l = 0
        // l_ndc = -1
        // r_ndc = 1
        // r_ndc - l_ndc = 2

        // where we can see that:
        // (r - l) / (r_ndc - l_ndc) = (px - l) / (px_ndc - l_ndc)
        // width / 2 = (px - 0) / (px_ndc + 1)
        // So finally the horizontal principal point is:
        // px = (1 + px_ndc)*(width/2)

        // Likewise the vertical principal point is:
        // py = (1 + py_ndc)*(height/2)

        // again, if sensor is rotated, vertical and horizontal coordinates are exchanged
        val px = (1.0 + projectionMatrix[8]) * (width / 2.0)
        val py = (1.0 + projectionMatrix[9]) * (height / 2.0)

        val skewness = projectionMatrix[4] * fx / projectionMatrix[0]

        result.skewness = skewness
        result.horizontalFocalLength = fx
        result.verticalFocalLength = fy
        result.horizontalPrincipalPoint = px
        result.verticalPrincipalPoint = py
    }

    /**
     * Computes pinhole camera intrinsic parameters from provided projection matrix
     * and viewport size.
     *
     * @param projectionMatrix array containing values of 4x4 matrix defining projection matrix.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @return computed pinhole camera intrinsic parameters.
     */
    fun computePinholeCameraIntrinsicsAndReturnNew(
        projectionMatrix: FloatArray,
        width: Int,
        height: Int
    ): PinholeCameraIntrinsicParameters {
        val result = PinholeCameraIntrinsicParameters()
        computePinholeCameraIntrinsics(
            projectionMatrix,
            width,
            height,
            result
        )
        return result
    }

    /**
     * Computes transformation defining pose of a pinhole camera.
     * Camera pose contains both camera rotation and center.
     *
     * @param modelViewMatrix array containing values of 4x4 matrix defining model-view matrix.
     * @param result instance where computed 3D transformation will be stored.
     */
    fun computePoseTransformation(
        modelViewMatrix: FloatArray,
        result: ProjectiveTransformation3D
    ) {
        require(modelViewMatrix.size == MATRIX_LENGTH)

        // copy model view matrix into projective transformation matrix
        val cols = ProjectiveTransformation3D.HOM_COORDS
        val length = cols * cols
        val transformationMatrix = Matrix(cols, cols)
        val buffer = transformationMatrix.buffer
        for (i in 0 until length) {
            buffer[i] = modelViewMatrix[i].toDouble()
        }

        result.t = transformationMatrix
    }

    /**
     * Computes transformation defining pose of a pinhole camera.
     * Camera pose contains both camera rotation and center.
     *
     * @param modelViewMatrix array containing values of 4x4 matrix defining model-view matrix.
     * @return compute pinhole camera pose as a transformation to be applied to a canonical pinhole
     * camera (without rotation and located at origin of coordinates).
     */
    fun computePoseTransformationAndReturnNew(
        modelViewMatrix: FloatArray
    ): ProjectiveTransformation3D {
        val result = ProjectiveTransformation3D()
        computePoseTransformation(modelViewMatrix, result)
        return result
    }

    /**
     * Computes pinhole camera from provided projection and model-view matrices.
     *
     * @param projectionMatrix array containing values of 4x4 matrix defining projection matrix.
     * @param modelViewMatrix array containing values of 4x4 matrix defining model-view matrix.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @param result instance where computed pinhole camera will be stored.
     */
    fun computePinholeCamera(
        projectionMatrix: FloatArray,
        modelViewMatrix: FloatArray,
        width: Int,
        height: Int,
        result: PinholeCamera
    ) {
        require(projectionMatrix.size == MATRIX_LENGTH)
        require(modelViewMatrix.size == MATRIX_LENGTH)

        val rotation = MatrixRotation3D()
        val center = InhomogeneousPoint3D()
        val intrinsics = computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            width,
            height
        )
        result.setIntrinsicAndExtrinsicParameters(intrinsics, rotation, center)

        // Finally transform camera using transformation
        val transformation = computePoseTransformationAndReturnNew(modelViewMatrix)
        transformation.transform(result)
    }

    /**
     * Computes pinhole camera from provided projection and model-view matrices.
     *
     * @param projectionMatrix array containing values of 4x4 matrix defining projection matrix.
     * @param modelViewMatrix array containing values of 4x4 matrix defining model-view matrix.
     * @param width width of viewport expressed in pixels.
     * @param height height of viewport expressed in pixels.
     * @return computed pinhole camera.
     */
    fun computePinholeCameraAndReturnNew(
        projectionMatrix: FloatArray,
        modelViewMatrix: FloatArray,
        width: Int,
        height: Int
    ): PinholeCamera {
        val result = PinholeCamera()
        computePinholeCamera(
            projectionMatrix,
            modelViewMatrix,
            width,
            height,
            result
        )
        return result
    }
}