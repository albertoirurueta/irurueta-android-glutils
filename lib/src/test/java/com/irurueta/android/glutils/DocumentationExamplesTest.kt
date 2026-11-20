/*
 * Copyright (C) 2026 Alberto Irurueta Carro (alberto@irurueta.com)
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

import android.app.Activity
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.irurueta.geometry.InhomogeneousPoint3D
import com.irurueta.geometry.PinholeCamera
import com.irurueta.geometry.PinholeCameraIntrinsicParameters
import com.irurueta.geometry.Quaternion
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Compiled versions of the code examples shown in the documentation pages under
 * `docs/modules/ROOT/pages`. Each snippet is delimited by `tag::name[]` and `end::name[]`
 * comments, and `docs/modules/ROOT/examples/<name>.kt` contains exactly the lines between them.
 * [examples_inDocumentation_matchTaggedRegionsOfThisTest] fails when they differ.
 *
 * Variables used by a snippet but not declared in it (`view`, `context`, `renderer`, ...) are the
 * ones a real application would already have, and are declared before its tag.
 */
@RunWith(RobolectricTestRunner::class)
class DocumentationExamplesTest {

    /**
     * Android context used to create views.
     */
    private lateinit var context: Context

    /**
     * Sets up the context and stubs OpenGL ES calls, which Robolectric cannot execute.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // initialize the class first, so that its static initializer is not recorded as a stub
        Class.forName(GLES20::class.java.name)
        mockkStatic(GLES20::class)
        every { GLES20.glClearColor(any(), any(), any(), any()) } returns Unit
        every { GLES20.glEnable(any()) } returns Unit
        every { GLES20.glViewport(any(), any(), any(), any()) } returns Unit
        every { GLES20.glClear(any()) } returns Unit
    }

    /**
     * Removes OpenGL ES stubs.
     */
    @After
    fun tearDown() {
        unmockkStatic(GLES20::class)
    }

    @Test
    fun sceneTextureView_whenCreated_hasRendererAndRenderMode() {
        val view = SceneTextureView(context)

        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
        assertTrue(!view.isOpaque)
    }

    @Test
    fun programmaticView_whenAddedToContainer_isPartOfViewHierarchy() {
        val container = FrameLayout(context)
        val renderer = SceneRenderer()
        // tag::programmaticView[]
        val view = GLTextureView(context)
        view.setEGLContextClientVersion(2)
        view.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        view.isOpaque = false
        view.setRenderer(renderer)
        view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY
        container.addView(view)

        // GLTextureView is a regular view: it can be animated, faded or transformed
        view.animate().alpha(0.5f).rotation(45.0f).setDuration(300L)
        // end::programmaticView[]

        assertSame(container, view.parent)
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
    }

    @Test
    fun updateScene_whenCameraQueued_requestsRender() {
        val view = SceneTextureView(context)
        val renderer = SceneRenderer()
        val camera = createCamera(WIDTH, HEIGHT)
        // tag::updateScene[]
        // runs on the GL thread, so the renderer state is never touched concurrently
        view.queueEvent { renderer.camera = camera }
        // with RENDER_MODE_WHEN_DIRTY, a new frame is only drawn when requested
        view.requestRender()
        // end::updateScene[]

        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
    }

    @Test
    fun debugOptions_whenSet_keepValues() {
        val view = GLTextureView(context)
        // tag::debugOptions[]
        // check glGetError after every call and log every call made by the renderer
        view.debugFlags = GLTextureView.DEBUG_CHECK_GL_ERROR or GLTextureView.DEBUG_LOG_GL_CALLS

        // try to keep the EGL context (and its textures, buffers and shaders) while paused
        view.preserveEGLContextOnPause = true
        // end::debugOptions[]

        assertEquals(
            GLTextureView.DEBUG_CHECK_GL_ERROR or GLTextureView.DEBUG_LOG_GL_CALLS,
            view.debugFlags
        )
        assertTrue(view.preserveEGLContextOnPause)
    }

    @Test
    fun sceneRenderer_whenCameraSet_drawsFrame() {
        val renderer = SceneRenderer()
        renderer.onSurfaceCreated(null, null)
        renderer.onSurfaceChanged(null, WIDTH, HEIGHT)
        renderer.camera = createCamera(WIDTH, HEIGHT)
        renderer.onDrawFrame(null)

        assertNotNull(renderer.lastModelViewProjectionMatrix)
    }

    @Test
    fun cameraConverter_whenCameraSet_computesMatrices() {
        val width = WIDTH
        val height = HEIGHT
        // tag::cameraConverter[]
        val converter = CameraConverter(width, height)
        converter.isModelViewProjectionMatrixComputationEnabled = true
        converter.setNearFarPlanes(0.1f, 100.0f)

        // focal length (in pixels) and principal point (in the center of the viewport)
        val focalLength = 1.5 * width
        val intrinsics = PinholeCameraIntrinsicParameters(
            focalLength, focalLength, width / 2.0, height / 2.0, 0.0
        )
        // camera without rotation, placed 5 units away from the origin along the z axis
        val rotation = Quaternion()
        val center = InhomogeneousPoint3D(0.0, 0.0, 5.0)
        converter.camera = PinholeCamera(intrinsics, rotation, center)

        // 16-length arrays in column-major order, ready for glUniformMatrix4fv
        val projectionMatrix = converter.projectionMatrix
        val modelViewMatrix = converter.modelViewMatrix
        val modelViewProjectionMatrix = converter.modelViewProjectionMatrix
        // end::cameraConverter[]

        assertEquals(16, projectionMatrix?.size)
        assertEquals(16, modelViewMatrix?.size)
        assertEquals(16, modelViewProjectionMatrix?.size)
    }

    @Test
    fun viewCamera_whenOrientationSet_computesViewCamera() {
        val converter = CameraConverter(WIDTH, HEIGHT)
        converter.camera = createCamera(WIDTH, HEIGHT)
        // tag::viewCamera[]
        // in a real app: OrientationHelper.getCameraDisplayOrientation(context, cameraId)
        converter.orientation = CameraToDisplayOrientation.ORIENTATION_90_DEGREES

        // same camera, expressed in Android view coordinates (y axis pointing downwards)
        val viewCamera = converter.viewCamera
        val viewCenter = converter.viewCameraCenter
        // end::viewCamera[]

        assertNotNull(viewCamera)
        assertNotNull(viewCenter)
    }

    @Test
    fun cameraToOpenGl_whenCameraProvided_computesMatrices() {
        val camera = createCamera(WIDTH, HEIGHT)
        val width = WIDTH
        val height = HEIGHT
        // tag::cameraToOpenGl[]
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            camera, width, height, 0.1f, 100.0f
        )
        val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
        val modelViewProjectionMatrix =
            CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
                projectionMatrix, modelViewMatrix
            )
        // end::cameraToOpenGl[]

        assertEquals(CameraToOpenGlHelper.MATRIX_LENGTH, modelViewProjectionMatrix.size)
    }

    @Test
    fun openGlToCamera_whenMatricesProvided_computesCamera() {
        val original = createCamera(WIDTH, HEIGHT)
        val width = WIDTH
        val height = HEIGHT
        // for instance the matrices provided by an AR framework for the current frame
        val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
            original, width, height, 0.1f, 100.0f
        )
        val viewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(original)
        // tag::openGlToCamera[]
        val camera = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
            projectionMatrix, viewMatrix, width, height
        )
        camera.decompose()
        val intrinsics = camera.intrinsicParameters
        val center = camera.cameraCenter
        val rotation = camera.cameraRotation
        // end::openGlToCamera[]

        assertNotNull(intrinsics)
        assertNotNull(rotation)
        assertEquals(5.0, center.inhomZ, ABSOLUTE_ERROR)
    }

    @Test
    fun examples_inDocumentation_matchTaggedRegionsOfThisTest() {
        val lines = File(TEST_SOURCE_PATH).readLines()
        val starts = lines.indices.filter { lines[it].trim().startsWith(TAG_PREFIX) }
        assertTrue(starts.isNotEmpty())

        for (start in starts) {
            val name = lines[start].trim().removePrefix(TAG_PREFIX).removeSuffix(TAG_SUFFIX)
            val end = (start + 1 until lines.size).first {
                lines[it].trim() == "$END_PREFIX$name$TAG_SUFFIX"
            }
            val expected = lines.subList(start + 1, end).joinToString("\n").trimIndent().trim()

            val example = File(EXAMPLES_PATH, "$name.kt")
            assertTrue("missing example ${example.path}", example.isFile)
            assertEquals("example $name is out of date", expected, example.readText().trim())
        }

        // every published Kotlin example comes from a tagged region
        val published = File(EXAMPLES_PATH).listFiles { file -> file.extension == "kt" }
        assertEquals(starts.size, published?.size)
    }

    companion object {
        /**
         * Viewport width used in examples, expressed in pixels.
         */
        private const val WIDTH = 1080

        /**
         * Viewport height used in examples, expressed in pixels.
         */
        private const val HEIGHT = 1920

        /**
         * Tolerance used when comparing values.
         */
        private const val ABSOLUTE_ERROR = 1e-3

        /**
         * Path of this source file, relative to the lib module.
         */
        private const val TEST_SOURCE_PATH =
            "src/test/java/com/irurueta/android/glutils/DocumentationExamplesTest.kt"

        /**
         * Path of the Antora examples directory, relative to the lib module.
         */
        private const val EXAMPLES_PATH = "../docs/modules/ROOT/examples"

        /**
         * Prefix of a line starting a tagged region.
         */
        private const val TAG_PREFIX = "// tag::"

        /**
         * Prefix of a line ending a tagged region.
         */
        private const val END_PREFIX = "// end::"

        /**
         * Suffix of tag delimiter lines.
         */
        private const val TAG_SUFFIX = "[]"

        /**
         * Creates a camera without rotation placed 5 units away from the origin.
         *
         * @param width viewport width expressed in pixels.
         * @param height viewport height expressed in pixels.
         * @return created camera.
         */
        private fun createCamera(width: Int, height: Int): PinholeCamera {
            val focalLength = 1.5 * width
            val intrinsics = PinholeCameraIntrinsicParameters(
                focalLength, focalLength, width / 2.0, height / 2.0, 0.0
            )
            return PinholeCamera(intrinsics, Quaternion(), InhomogeneousPoint3D(0.0, 0.0, 5.0))
        }
    }
}

// tag::sceneTextureView[]
/**
 * Translucent view drawing an OpenGL ES 2.0 scene. It can be declared in an XML layout.
 */
class SceneTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GLTextureView(context, attrs, defStyleAttr) {

    val renderer = SceneRenderer()

    init {
        // must be called before setRenderer
        setEGLContextClientVersion(2)
        // RGBA_8888 with a 16-bit depth buffer, so that the background can be transparent
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        isOpaque = false

        setRenderer(renderer)
        // only draw when requestRender() is called, to save battery
        renderMode = RENDER_MODE_WHEN_DIRTY
    }
}
// end::sceneTextureView[]

// tag::sceneRenderer[]
/**
 * Any GLSurfaceView.Renderer works with GLTextureView.
 */
class SceneRenderer : GLSurfaceView.Renderer {

    // only modified on the GL thread, through GLTextureView.queueEvent
    var camera: PinholeCamera? = null

    var lastModelViewProjectionMatrix: FloatArray? = null
        private set

    private var converter: CameraConverter? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // fully transparent background, so that views behind this one remain visible
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        // compile shaders, create buffers and textures here
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val converter = CameraConverter(width, height)
        converter.isModelViewProjectionMatrixComputationEnabled = true
        this.converter = converter
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val converter = converter ?: return
        val camera = camera ?: return
        if (converter.camera !== camera) {
            converter.camera = camera
        }
        val modelViewProjectionMatrix = converter.modelViewProjectionMatrix ?: return
        drawScene(modelViewProjectionMatrix)
    }

    private fun drawScene(modelViewProjectionMatrix: FloatArray) {
        lastModelViewProjectionMatrix = modelViewProjectionMatrix
        // use your shader program, upload the matrix with
        // GLES20.glUniformMatrix4fv(mvpHandle, 1, false, modelViewProjectionMatrix, 0)
        // and draw your geometry
    }
}
// end::sceneRenderer[]

// tag::sceneActivity[]
/**
 * Forwards activity lifecycle events to the view, so that its rendering thread is paused and
 * resumed.
 */
class SceneActivity : Activity() {

    private lateinit var sceneView: SceneTextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sceneView = SceneTextureView(this)
        setContentView(sceneView)
    }

    override fun onResume() {
        super.onResume()
        sceneView.onResume()
    }

    override fun onPause() {
        sceneView.onPause()
        super.onPause()
    }
}
// end::sceneActivity[]
