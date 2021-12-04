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
import android.util.AttributeSet
import com.irurueta.android.glutils.GLTextureView
import com.irurueta.geometry.PinholeCamera

class CubeTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GLTextureView(context, attrs, defStyleAttr) {

    /**
     * Renderer in charge of drawing the scene.
     */
    private var cubeRenderer: CubeRenderer? = null

    /**
     * Gets or sets cube size.
     */
    var cubeSize: Float
        get() = cubeRenderer?.cubeSize ?: CubeRenderer.DEFAULT_CUBE_SIZE
        set(value) {
            cubeRenderer?.cubeSize = value
            requestRender()
        }

    /**
     * Gets or sets near plane value.
     * Any vertex nearer to the camera than this value is ignored and not drawn.
     */
    var nearPlane: Float
        get() = cubeRenderer?.nearPlane ?: CubeRenderer.DEFAULT_NEAR_PLANE
        set(value) {
            cubeRenderer?.nearPlane = value
            requestRender()
        }

    /**
     * Gets or sets far plane value.
     * Any vertex further from the camera than this value is ignored and not drawn.
     */
    var farPlane: Float
        get() = cubeRenderer?.farPlane ?: CubeRenderer.DEFAULT_FAR_PLANE
        set(value) {
            cubeRenderer?.farPlane = value
            requestRender()
        }

    /**
     * Sets camera defining perspective and point of view and requests a new frame to be rendered.
     *
     * @param camera new camera to be set.
     */
    fun setCamera(camera: PinholeCamera) {
        cubeRenderer?.camera = camera
        requestRender()
    }

    /**
     * Called when window is detached.
     * Destroys OpenGL program.
     */
    override fun onDetachedFromWindow() {
        cubeRenderer?.destroy()
        super.onDetachedFromWindow()
    }

    init {
        cubeRenderer = CubeRenderer(context)

        // create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // enforce transparent background
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        // render only when new camera position is set.
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}