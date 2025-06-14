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
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.TextureView
import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.Writer
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.withLock

@RunWith(RobolectricTestRunner::class)
class GLTextureViewTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var renderer: GLSurfaceView.Renderer

    @MockK
    private lateinit var glWrapper: GLTextureView.GLWrapper

    @MockK
    private lateinit var texture: SurfaceTexture

    @MockK
    private lateinit var surface: SurfaceTexture

    @MockK
    private lateinit var egl: EGL10

    @MockK
    private lateinit var display: EGLDisplay

    @MockK
    private lateinit var eglConfig: EGLConfig

    @MockK
    private lateinit var eglContext: EGLContext

    @MockK
    private lateinit var config: EGLConfig

    @MockK
    private lateinit var eglSurface: EGLSurface

    @MockK
    private lateinit var eglDisplay: EGLDisplay

    private var threadFailures = 0

    @Before
    fun setUp() {
        threadFailures = 0
    }

    @After
    fun afterTest() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        assertNotNull(view.getPrivateProperty("thisWeakRef"))
        assertNull(view.getPrivateProperty("glThread"))
        assertNull(view.getPrivateProperty("renderer"))
        val detached: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached)
        assertTrue(detached)
        assertNull(view.getPrivateProperty("eglConfigChooser"))
        assertNull(view.getPrivateProperty("eglContextFactory"))
        assertNull(view.getPrivateProperty("eglWindowSurfaceFactory"))
        assertNull(view.getPrivateProperty("glWrapper"))
        assertEquals(0, view.getPrivateProperty("eglContextClientVersion"))
        assertNotNull(view.getPrivateProperty("surfaceTextureListener"))
        assertSame(view.surfaceTextureListener, view.getPrivateProperty("surfaceTextureListener"))
        assertNotNull(view.getPrivateProperty("layoutChangeListener"))
    }

    @Test
    fun setGLWrapper_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("glWrapper"))

        // set new value
        view.setGLWrapper(glWrapper)

        // check
        assertSame(glWrapper, view.getPrivateProperty("glWrapper"))
    }

    @Test
    fun debugFlags_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        assertEquals(0, view.debugFlags)

        // set new value
        view.debugFlags = GLTextureView.DEBUG_CHECK_GL_ERROR

        // check
        assertEquals(GLTextureView.DEBUG_CHECK_GL_ERROR, view.debugFlags)
    }

    @Test
    fun preserveEGLContextOnPause_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        assertFalse(view.preserveEGLContextOnPause)

        // set new value
        view.preserveEGLContextOnPause = true

        // check
        @Suppress("KotlinConstantConditions")
        assertTrue(view.preserveEGLContextOnPause)
    }

    @Test
    fun setRenderer_whenNonExisting_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("renderer"))
        assertNull(view.getPrivateProperty("glThread"))

        // set new value
        view.setRenderer(renderer)

        // check
        assertNotNull(view.getPrivateProperty("eglConfigChooser"))
        assertNotNull(view.getPrivateProperty("eglContextFactory"))
        assertNotNull(view.getPrivateProperty("eglWindowSurfaceFactory"))
        assertSame(renderer, view.getPrivateProperty("renderer"))

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)
        assertTrue(glThread.isAlive)
    }

    @Test
    fun setRenderer_whenAlreadySet_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("renderer"))

        // set new value
        view.setRenderer(renderer)

        // check
        assertNotNull(view.getPrivateProperty("eglConfigChooser"))
        assertNotNull(view.getPrivateProperty("eglContextFactory"))
        assertNotNull(view.getPrivateProperty("eglWindowSurfaceFactory"))
        assertSame(renderer, view.getPrivateProperty("renderer"))
        assertNotNull(view.getPrivateProperty("glThread"))

        // set renderer again
        assertThrows(IllegalStateException::class.java) { view.setRenderer(renderer) }
    }

    @Test
    fun setEGLContextFactory_whenNoRenderer_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglContextFactory"))

        // set new value
        val factory = mockk<GLTextureView.EGLContextFactory>()
        view.setEGLContextFactory(factory)

        // check
        assertSame(factory, view.getPrivateProperty("eglContextFactory"))
    }

    @Test
    fun setEGLContextFactory_whenRendererAlreadySet_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglContextFactory"))

        // set renderer
        view.setRenderer(renderer)

        assertNotNull(view.getPrivateProperty("eglContextFactory"))

        // set new value
        val factory = mockk<GLTextureView.EGLContextFactory>()
        assertThrows(IllegalStateException::class.java) { view.setEGLContextFactory(factory) }
    }

    @Test
    fun setEGLWindowSurfaceFactory_whenNoRenderer_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglWindowSurfaceFactory"))

        // set new value
        val factory = mockk<GLTextureView.EGLWindowSurfaceFactory>()
        view.setEGLWindowSurfaceFactory(factory)

        // check
        assertSame(factory, view.getPrivateProperty("eglWindowSurfaceFactory"))
    }

    @Test
    fun setEGLWindowSurfaceFactory_whenRendererAlreadySet_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglWindowSurfaceFactory"))

        // set renderer
        view.setRenderer(renderer)

        assertNotNull(view.getPrivateProperty("eglWindowSurfaceFactory"))

        // set new value
        val factory = mockk<GLTextureView.EGLWindowSurfaceFactory>()
        assertThrows(IllegalStateException::class.java) { view.setEGLWindowSurfaceFactory(factory) }
    }

    @Test
    fun setEGLConfigChooser_whenRendererAlreadySet_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglConfigChooser"))

        // set renderer
        view.setRenderer(renderer)

        assertNotNull(view.getPrivateProperty("eglConfigChooser"))

        // set new value
        val chooser = mockk<GLTextureView.EGLConfigChooser>()
        assertThrows(IllegalStateException::class.java) { view.setEGLConfigChooser(chooser) }
    }

    @Test
    fun setEGLConfigChooser_whenDepthNeeded_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglConfigChooser"))

        // set
        view.setEGLConfigChooser(true)

        // check
        assertNotNull(view.getPrivateProperty("eglConfigChooser"))
    }

    @Test
    fun setEGLConfigChooser_whenDepthNotNeeded_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglConfigChooser"))

        // set
        view.setEGLConfigChooser(false)

        // check
        assertNotNull(view.getPrivateProperty("eglConfigChooser"))
    }

    @Test
    fun setEGLConfigChooser_whenSizesAreUsed_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertNull(view.getPrivateProperty("eglConfigChooser"))

        // set
        view.setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        // check
        assertNotNull(view.getPrivateProperty("eglConfigChooser"))
    }

    @Test
    fun setEGLContextClientVersion_whenNoRenderer_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertEquals(0, view.getPrivateProperty("eglContextClientVersion"))

        // set
        view.setEGLContextClientVersion(1)

        // check
        assertEquals(1, view.getPrivateProperty("eglContextClientVersion"))
    }

    @Test
    fun setEGLContextClientVersion_whenRendererAlreadySet_throwsIllegalStateException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertEquals(0, view.getPrivateProperty("eglContextClientVersion"))

        // set renderer
        view.setRenderer(renderer)

        assertEquals(0, view.getPrivateProperty("eglContextClientVersion"))

        // set
        assertThrows(IllegalStateException::class.java) { view.setEGLContextClientVersion(1) }
    }

    @Test
    fun renderMode_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // check default value
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, view.renderMode)

        // set new value
        view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, view.renderMode)
    }

    @Test
    fun renderMode_whenRendererIsProvider_getAndSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check default value
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, view.renderMode)

        // set new value
        view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
    }

    @Test(expected = IllegalArgumentException::class)
    fun renderMode_whenInvalidValue_throwsException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check default value
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, view.renderMode)

        // set new value
        view.renderMode = 2
    }

    @Test
    fun requestRender_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.requestRender()

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun requestRender_whenRendererIsProvided_requestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        // set new value
        field.set(glThread, false)

        // check
        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // request render
        view.requestRender()

        // check
        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun onPause_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.onPause()

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun onPause_whenRendererIsProvided_requestsPause() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestPaused")
        field.isAccessible = true
        val requestPaused1 = field.getBoolean(glThread)
        assertFalse(requestPaused1)

        // pause
        view.onPause()

        // check
        val requestPaused2 = field.getBoolean(glThread)
        assertTrue(requestPaused2)
    }

    @Test
    fun onResume_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.onResume()

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun onResume_whenRendererIsProvided_resetsRequestPaused() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestPaused")
        field.isAccessible = true

        // pause first
        view.onPause()

        // check
        val requestPaused1 = field.getBoolean(glThread)
        assertTrue(requestPaused1)

        // resume
        view.onResume()

        // check
        val requestPaused2 = field.getBoolean(glThread)
        assertFalse(requestPaused2)
    }

    @Test
    fun queueEvent_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.queueEvent {}

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun queueEvent_whenRenderer_addsRunnableToQueueAndExecutesIt() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val eventQueueField = glThreadClass.getDeclaredField("eventQueue")
        eventQueueField.isAccessible = true
        val queue = eventQueueField.get(glThread) as ArrayList<*>

        // initially queue is empty
        assertTrue(queue.isEmpty())

        // queue
        val runnable = mockk<Runnable>(relaxUnitFun = true)
        view.queueEvent(runnable)

        Thread.sleep(SLEEP)

        // check that queue is executed an emptied
        assertEquals(0, queue.size)
        verify(exactly = 1) { runnable.run() }
    }

    @Test
    fun finalize_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.callPrivateFunc("finalize")

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun finalize_whenRenderer_exitsThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val field = glThreadClass.getDeclaredField("shouldExit")
        field.isAccessible = true
        val shouldExit1 = field.getBoolean(glThread)

        assertFalse(shouldExit1)

        assertTrue(glThread.isAlive)

        view.callPrivateFunc("finalize")

        // check
        val shouldExit2 = field.getBoolean(glThread)

        assertTrue(shouldExit2)

        Thread.sleep(SLEEP)

        // check that thread has finished
        assertFalse(glThread.isAlive)
    }

    @Test
    fun surfaceCreated_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.callPrivateFunc("surfaceCreated", texture)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceCreate_whenRenderer_notifiesSurfaceToRenderThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val field = glThreadClass.getDeclaredField("hasSurface")
        field.isAccessible = true
        val hasSurface1 = field.getBoolean(glThread)

        // check initial value
        assertFalse(hasSurface1)

        // notify surface creation
        view.callPrivateFunc("surfaceCreated", texture)

        // check
        val hasSurface2 = field.getBoolean(glThread)
        assertTrue(hasSurface2)
    }

    @Test
    fun surfaceDestroyed_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.callPrivateFunc("surfaceDestroyed", texture)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceDestroyed_whenRenderer_notifiesSurfaceToRenderThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val field = glThreadClass.getDeclaredField("hasSurface")
        field.isAccessible = true
        val hasSurface1 = field.getBoolean(glThread)

        // check initial value
        assertFalse(hasSurface1)

        // notify surface creation
        view.callPrivateFunc("surfaceCreated", texture)

        // check
        val hasSurface2 = field.getBoolean(glThread)
        assertTrue(hasSurface2)

        // now notify surface destruction
        view.callPrivateFunc("surfaceDestroyed", texture)

        // check
        val hasSurface3 = field.getBoolean(glThread)
        assertFalse(hasSurface3)
    }

    @Test
    fun surfaceChanged_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.callPrivateFunc("surfaceChanged", texture, WIDTH, HEIGHT)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceChanged_whenRenderer_notifiesSizeChange() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val fieldWidth = glThreadClass.getDeclaredField("width")
        fieldWidth.isAccessible = true
        val width1 = fieldWidth.getInt(glThread)

        val fieldHeight = glThreadClass.getDeclaredField("height")
        fieldHeight.isAccessible = true
        val height1 = fieldHeight.getInt(glThread)

        // check initial value
        assertEquals(0, width1)
        assertEquals(0, height1)

        // notify surface creation
        view.callPrivateFunc("surfaceChanged", texture, WIDTH, HEIGHT)

        // check
        val width2 = fieldWidth.getInt(glThread)
        val height2 = fieldHeight.getInt(glThread)

        assertEquals(WIDTH, width2)
        assertEquals(HEIGHT, height2)
    }

    @Test
    fun onAttachedToWindow_whenDetachedAndNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)
    }

    @Test
    fun onAttachedToWindow_whenNotDetached_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        // attach again
        view.callPrivateFunc("onAttachedToWindow")

        val detached3: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached3)
        assertFalse(detached3)
    }

    @Test
    fun onAttachedToWindow_whenDetachedRendererAvailable_ensuresThreadIsAlive() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)
        assertTrue(glThread.isAlive)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        assertTrue(glThread.isAlive)
    }

    @Test
    fun onAttachedToWindow_whenDirtyRenderMode_ensuresRenderModeIsPreserved() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)
        view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)
        assertTrue(glThread.isAlive)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("_renderMode")
        field.isAccessible = true
        val renderMode = field.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, renderMode)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        assertTrue(glThread.isAlive)

        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
    }

    @Test
    fun onDetachedFromWindow_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        // detach
        view.callPrivateFunc("onDetachedFromWindow")

        // check
        val detached3: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached3)
        assertTrue(detached3)
    }

    @Test
    fun onDetachedFromWindow_whenAlreadyAttached_stopsGlThreadAndDetaches() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        // attach
        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        // detach
        view.callPrivateFunc("onDetachedFromWindow")

        // check
        val detached3: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached3)
        assertTrue(detached3)
    }

    @Test
    fun onDetachedFromWindow_whenAlreadyAttachedAndDirtyRenderMode_preservesRenderMode() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)
        assertTrue(glThread.isAlive)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        // attach
        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        // set render mode
        view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)

        // detach
        view.callPrivateFunc("onDetachedFromWindow")

        // check
        val detached3: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached3)
        assertTrue(detached3)

        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)

        // attach again
        view.callPrivateFunc("onAttachedToWindow")

        val detached4: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached4)
        assertFalse(detached4)

        // ensure render mode is preserved
        assertEquals(GLTextureView.RENDER_MODE_WHEN_DIRTY, view.renderMode)
    }

    @Test
    fun layoutChangeListener_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        assertNull(glThread)
    }

    @Test
    fun layoutChangeListener_whenRendererAvailable_setsExpectedSize() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val fieldWidth = glThreadClass.getDeclaredField("width")
        fieldWidth.isAccessible = true
        val width1 = fieldWidth.getInt(glThread)

        val fieldHeight = glThreadClass.getDeclaredField("height")
        fieldHeight.isAccessible = true
        val height1 = fieldHeight.getInt(glThread)

        // check initial value
        assertEquals(0, width1)
        assertEquals(0, height1)

        // set new layout
        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        // check
        val width2 = fieldWidth.getInt(glThread)
        val height2 = fieldHeight.getInt(glThread)

        assertEquals(WIDTH, width2)
        assertEquals(HEIGHT, height2)
    }

    @Test
    fun surfaceTextureListener_whenOnSurfaceTextureAvailableAndNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val surfaceTextureListener: TextureView.SurfaceTextureListener? =
            view.getPrivateProperty("surfaceTextureListener")
        requireNotNull(surfaceTextureListener)

        surfaceTextureListener.onSurfaceTextureAvailable(surface, WIDTH, HEIGHT)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceTextureListener_whenOnSurfaceTextureSizeChangedAndNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val surfaceTextureListener: TextureView.SurfaceTextureListener? =
            view.getPrivateProperty("surfaceTextureListener")
        requireNotNull(surfaceTextureListener)

        surfaceTextureListener.onSurfaceTextureSizeChanged(surface, WIDTH, HEIGHT)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceTextureListener_whenOnSurfaceTextureDestroyedAndNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val surfaceTextureListener: TextureView.SurfaceTextureListener? =
            view.getPrivateProperty("surfaceTextureListener")
        requireNotNull(surfaceTextureListener)

        assertTrue(surfaceTextureListener.onSurfaceTextureDestroyed(surface))

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceTextureListener_whenOnSurfaceTextureUpdated_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val surfaceTextureListener: TextureView.SurfaceTextureListener? =
            view.getPrivateProperty("surfaceTextureListener")
        requireNotNull(surfaceTextureListener)

        surfaceTextureListener.onSurfaceTextureUpdated(surface)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun eglContextFactory_whenCreateContext_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        every { egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, null) }.returns(
            eglContext
        )

        assertSame(eglContext, eglContextFactory.createContext(egl, display, eglConfig))

        verify(exactly = 1) { egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, null) }
    }

    @Test
    fun eglContextFactory_whenDestroyContextFails_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        every { egl.eglDestroyContext(display, eglContext) }.returns(false)

        assertThrows(RuntimeException::class.java) {
            eglContextFactory.destroyContext(
                egl,
                display,
                eglContext
            )
        }
        verify(exactly = 1) { egl.eglDestroyContext(display, eglContext) }
    }

    @Test
    fun eglContextFactory_whenDestroyContextSucceeds_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        every { egl.eglDestroyContext(display, eglContext) }.returns(true)

        eglContextFactory.destroyContext(egl, display, eglContext)

        verify(exactly = 1) { egl.eglDestroyContext(display, eglContext) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenCreateWindowSurfaceFails_executesExpectedGLCommandsAndLogs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        every { egl.eglCreateWindowSurface(display, config, surface, null) }.throws(
            IllegalArgumentException()
        )

        assertNull(
            eglWindowSurfaceFactory.createWindowSurface(
                egl,
                display,
                config,
                surface
            )
        )

        verify(exactly = 1) { egl.eglCreateWindowSurface(display, config, surface, null) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenCreateWindowSurfaceSucceeds_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        every { egl.eglCreateWindowSurface(display, config, surface, null) }.returns(
            eglSurface
        )

        assertSame(
            eglSurface,
            eglWindowSurfaceFactory.createWindowSurface(egl, display, config, surface)
        )

        verify(exactly = 1) { egl.eglCreateWindowSurface(display, config, surface, null) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenDestroySurface_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        every { egl.eglDestroySurface(display, eglSurface) }.returns(true)

        eglWindowSurfaceFactory.destroySurface(egl, display, eglSurface)

        verify(exactly = 1) { egl.eglDestroySurface(display, eglSurface) }
    }

    @Test
    fun eglConfigChooser_whenChooseConfigAndConfigFails_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.returns(
            false
        )

        assertThrows(IllegalArgumentException::class.java) {
            eglConfigChooser.chooseConfig(
                egl,
                display
            )
        }

        verify(exactly = 1) { egl.eglChooseConfig(display, any(), null, 0, any()) }
    }

    @Test
    fun eglConfigChooser_whenChooseConfigAndNoConfigMatch_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 0
            return@answers true
        }

        assertThrows(IllegalArgumentException::class.java) {
            eglConfigChooser.chooseConfig(
                egl,
                display
            )
        }

        val slot = slot<IntArray>()
        verify(exactly = 1) { egl.eglChooseConfig(display, any(), null, 0, capture(slot)) }
        val numConfigs = slot.captured
        assertEquals(1, numConfigs.size)
        assertEquals(0, numConfigs[0])
    }

    @Test
    fun eglConfigChooser_whenChooseConfigAndHasConfigMatchButFails_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }
        every { egl.eglChooseConfig(display, any(), any<Array<EGLConfig?>>(), 1, any()) }.returns(
            false
        )

        assertThrows(IllegalArgumentException::class.java) {
            eglConfigChooser.chooseConfig(
                egl,
                display
            )
        }

        val list = mutableListOf<IntArray>()
        verify(exactly = 1) { egl.eglChooseConfig(display, any(), null, 0, capture(list)) }
        val numConfigs = list[0]
        assertEquals(1, numConfigs.size)
        assertEquals(1, numConfigs[0])

        verify(exactly = 1) {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }
    }

    @Test
    fun eglConfigChooser_whenChooseConfigAndNoConfigChosen_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }
        every { egl.eglChooseConfig(display, any(), any<Array<EGLConfig?>>(), 1, any()) }.returns(
            true
        )
        every { egl.eglGetConfigAttrib(display, any(), any(), any()) }.returns(false)

        assertThrows(IllegalArgumentException::class.java) {
            eglConfigChooser.chooseConfig(
                egl,
                display
            )
        }

        val list = mutableListOf<IntArray>()
        verify(exactly = 1) { egl.eglChooseConfig(display, any(), null, 0, capture(list)) }
        val numConfigs = list[0]
        assertEquals(1, numConfigs.size)
        assertEquals(1, numConfigs[0])

        verify(exactly = 1) {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }
    }

    @Test
    fun eglConfigChooser_whenChooseConfigSucceeds_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }

        every {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }.answers { call ->
            @Suppress("UNCHECKED_CAST")
            val configs = call.invocation.args[2] as Array<EGLConfig?>
            configs[0] = config
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_DEPTH_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 16
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_STENCIL_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 0
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_RED_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_GREEN_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_BLUE_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_ALPHA_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 0
            return@answers true
        }

        assertSame(config, eglConfigChooser.chooseConfig(egl, display))

        val list = mutableListOf<IntArray>()
        verify(exactly = 1) { egl.eglChooseConfig(display, any(), null, 0, capture(list)) }
        val numConfigs = list[0]
        assertEquals(1, numConfigs.size)
        assertEquals(1, numConfigs[0])

        verify(exactly = 1) {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }

        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, any()) }
        verify(exactly = 1) {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_STENCIL_SIZE,
                any()
            )
        }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_RED_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, any()) }
    }

    @Test
    fun eglConfigChooser_whenEglContextClientVersion2_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)
        view.setEGLContextClientVersion(2)

        // set renderer
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }

        every {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }.answers { call ->
            @Suppress("UNCHECKED_CAST")
            val configs = call.invocation.args[2] as Array<EGLConfig?>
            configs[0] = config
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_DEPTH_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 16
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_STENCIL_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 0
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_RED_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_GREEN_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_BLUE_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 8
            return@answers true
        }

        every {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_ALPHA_SIZE,
                any()
            )
        }.answers { call ->
            val value = call.invocation.args[3] as IntArray
            value[0] = 0
            return@answers true
        }

        assertSame(config, eglConfigChooser.chooseConfig(egl, display))

        val listNumConfig = mutableListOf<IntArray>()
        val listConfigSpec = mutableListOf<IntArray>()
        verify(exactly = 1) {
            egl.eglChooseConfig(
                display,
                capture(listConfigSpec),
                null,
                0,
                capture(listNumConfig)
            )
        }
        val numConfigs = listNumConfig[0]
        assertEquals(1, numConfigs.size)
        assertEquals(1, numConfigs[0])
        val configSpec = listConfigSpec[0]
        assertTrue(configSpec.contains(EGL10.EGL_RENDERABLE_TYPE))
        assertEquals(4, configSpec[configSpec.indexOf(EGL10.EGL_RENDERABLE_TYPE) + 1])
        assertTrue(configSpec.contains(EGL10.EGL_NONE))

        verify(exactly = 1) {
            egl.eglChooseConfig(
                display,
                any(),
                any<Array<EGLConfig?>>(),
                1,
                any()
            )
        }

        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, any()) }
        verify(exactly = 1) {
            egl.eglGetConfigAttrib(
                display,
                config,
                EGL10.EGL_STENCIL_SIZE,
                any()
            )
        }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_RED_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, any()) }
        verify(exactly = 1) { egl.eglGetConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, any()) }
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenStartAndNoDisplay_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(EGL10.EGL_NO_DISPLAY)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

        // invoke start method
        val eglHelperStartMethod = eglHelper.javaClass.getMethod("start")
        try {
            eglHelperStartMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenStartAndInitializationFails_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(false)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

        // invoke start method
        val eglHelperStartMethod = eglHelper.javaClass.getMethod("start")
        try {
            eglHelperStartMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenStartAndNoView_resetsConfig() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        glSurfaceViewWeakRefField.set(eglHelper, WeakReference(null))

        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(true)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

        // invoke start method
        val eglHelperStartMethod = eglHelper.javaClass.getMethod("start")
        try {
            eglHelperStartMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test
    fun eglHelper_whenStartAndViewAvailable_setsExpectedConfigAndContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true

        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(true)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

        val eglConfigChooser = mockk<GLTextureView.EGLConfigChooser>()
        every { eglConfigChooser.chooseConfig(egl, eglDisplay) }.returns(eglConfig)
        view.setPrivateProperty("eglConfigChooser", eglConfigChooser)

        val eglContextFactory = mockk<GLTextureView.EGLContextFactory>()
        every { eglContextFactory.createContext(egl, eglDisplay, eglConfig) }.returns(eglContext)
        view.setPrivateProperty("eglContextFactory", eglContextFactory)

        // invoke start method
        val eglHelperStartMethod = eglHelper.javaClass.getMethod("start")
        eglHelperStartMethod.invoke(eglHelper)

        val eglHelperConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglHelperConfigField.isAccessible = true
        assertSame(eglConfig, eglHelperConfigField.get(eglHelper))

        val eglHelperContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglHelperContextField.isAccessible = true
        assertSame(eglContext, eglHelperContextField.get(eglHelper))
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenCreateSurfaceAndNoEgl_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        // missing egl
        assertNull(eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        try {
            eglHelperCreateSurfaceMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenCreateSurfaceAndNoEglDisplay_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        // missing eglDisplay
        assertNull(eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        try {
            eglHelperCreateSurfaceMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenCreateSurfaceAndNoEglConfig_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        try {
            eglHelperCreateSurfaceMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test
    fun eglHelper_whenCreateSurfaceAndNoViewAndBadNativeWindowError_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        glSurfaceViewWeakRefField.set(eglHelper, WeakReference(null))

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        val result = eglHelperCreateSurfaceMethod.invoke(eglHelper) as Boolean
        assertFalse(result)
    }

    @Test
    fun eglHelper_whenCreateSurfaceNoViewAndOtherError_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_DISPLAY)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        // set no view
        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        glSurfaceViewWeakRefField.set(eglHelper, WeakReference(null))

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        val result = eglHelperCreateSurfaceMethod.invoke(eglHelper) as Boolean
        assertFalse(result)
    }

    @Test
    fun eglHelper_whenCreateSurfaceAndNoSurface_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        assertNotNull(eglWindowSurfaceFactory)

        every {
            egl.eglCreateWindowSurface(
                eglDisplay,
                eglConfig,
                any(),
                null
            )
        }.returns(EGL10.EGL_NO_SURFACE)

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        val result = eglHelperCreateSurfaceMethod.invoke(eglHelper) as Boolean
        assertFalse(result)
    }

    @Test
    fun eglHelper_whenCreateSurfaceAndMakeCurrentFails_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        // set eglContext
        val eglContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglContextField.isAccessible = true

        assertNull(eglContextField.get(eglHelper))

        // set eglContext
        eglContextField.set(eglHelper, eglContext)


        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        assertNotNull(eglWindowSurfaceFactory)

        every {
            egl.eglCreateWindowSurface(
                eglDisplay,
                eglConfig,
                any(),
                null
            )
        }.returns(eglSurface)

        every { egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) }.returns(false)

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        val result = eglHelperCreateSurfaceMethod.invoke(eglHelper) as Boolean
        assertFalse(result)
    }

    @Test
    fun eglHelper_whenCreateSurfaceSucceeds_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        // set eglContext
        val eglContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglContextField.isAccessible = true

        assertNull(eglContextField.get(eglHelper))

        eglContextField.set(eglHelper, eglContext)

        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        assertNotNull(eglWindowSurfaceFactory)

        every {
            egl.eglCreateWindowSurface(
                eglDisplay,
                eglConfig,
                any(),
                null
            )
        }.returns(eglSurface)

        every { egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) }.returns(true)

        // invoke createSurface
        val eglHelperCreateSurfaceMethod = eglHelper.javaClass.getMethod("createSurface")
        val result = eglHelperCreateSurfaceMethod.invoke(eglHelper) as Boolean
        assertTrue(result)
    }

    @Test
    fun eglHelper_whenCreateGLAndNoView_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        // set no view
        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        glSurfaceViewWeakRefField.set(eglHelper, WeakReference(null))

        // invoke createGL
        val eglHelperCreateGLMethod = eglHelper.javaClass.getMethod("createGL")
        val result = eglHelperCreateGLMethod.invoke(eglHelper) as GL?
        assertNull(result)
    }

    @Test
    fun eglHelper_whenCreateGLAndExistingViewWithoutGlWrapper_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        // invoke createGL
        val eglHelperCreateGLMethod = eglHelper.javaClass.getMethod("createGL")
        val result = eglHelperCreateGLMethod.invoke(eglHelper) as GL?
        assertNull(result)
    }

    @Test
    fun eglHelper_whenCreateGLAndExistingViewWithGlWrapper_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        every { glWrapper.wrap(any()) }.returns(null)
        view.setGLWrapper(glWrapper)

        // invoke createGL
        val eglHelperCreateGLMethod = eglHelper.javaClass.getMethod("createGL")
        val result = eglHelperCreateGLMethod.invoke(eglHelper) as GL?
        assertNull(result)
    }

    @Test
    fun eglHelper_whenCreateGLAndDebugGLError_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        view.debugFlags = GLTextureView.DEBUG_CHECK_GL_ERROR

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        every { glWrapper.wrap(any()) }.returns(null)
        view.setGLWrapper(glWrapper)

        // invoke createGL
        val eglHelperCreateGLMethod = eglHelper.javaClass.getMethod("createGL")
        val result = eglHelperCreateGLMethod.invoke(eglHelper) as GL?
        assertNotNull(result)
    }

    @Test
    fun eglHelper_whenCreateGLAndDebugGLCalls_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        view.debugFlags = GLTextureView.DEBUG_LOG_GL_CALLS

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        every { glWrapper.wrap(any()) }.returns(null)
        view.setGLWrapper(glWrapper)

        // invoke createGL
        val eglHelperCreateGLMethod = eglHelper.javaClass.getMethod("createGL")
        val result = eglHelperCreateGLMethod.invoke(eglHelper) as GL?
        assertNotNull(result)
    }

    @Test
    fun eglHelper_whenSwapFails_returnsExpectedError() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        view.debugFlags = GLTextureView.DEBUG_LOG_GL_CALLS

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglSwapBuffers(any(), any()) }.returns(false)
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        // invoke swap
        val eglHelperSwapMethod = eglHelper.javaClass.getMethod("swap")
        eglHelperSwapMethod.isAccessible = true
        val result = eglHelperSwapMethod.invoke(eglHelper) as Int?
        assertEquals(EGL10.EGL_BAD_NATIVE_WINDOW, result)
    }

    @Test
    fun eglHelper_whenSwapSucceeds_returnsExpectedSuccess() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        view.debugFlags = GLTextureView.DEBUG_LOG_GL_CALLS

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglSwapBuffers(any(), any()) }.returns(true)
        eglField.set(eglHelper, egl)

        // invoke swap
        val eglHelperSwapMethod = eglHelper.javaClass.getMethod("swap")
        val result = eglHelperSwapMethod.invoke(eglHelper) as Int?
        assertEquals(EGL10.EGL_SUCCESS, result)
    }

    @Test
    fun eglHelper_whenDestroySurfaceAndNoExistingSurface_logsAndMakesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        // invoke destroySurface
        val eglHelperDestroySurfaceMethod = eglHelper.javaClass.getMethod("destroySurface")
        eglHelperDestroySurfaceMethod.invoke(eglHelper)

        val eglSurfaceField = eglHelper.javaClass.getDeclaredField("eglSurface")
        eglSurfaceField.isAccessible = true

        assertNull(eglSurfaceField.get(eglHelper))
    }

    @Test
    fun eglHelper_whenDestroySurfaceAndExistingSurface_destroysSurface() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        // set eglSurface
        val eglSurfaceField = eglHelper.javaClass.getDeclaredField("eglSurface")
        eglSurfaceField.isAccessible = true

        assertNull(eglSurfaceField.get(eglHelper))

        eglSurfaceField.set(eglHelper, eglSurface)

        assertSame(eglSurface, eglSurfaceField.get(eglHelper))

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        every {
            egl.eglMakeCurrent(
                any(),
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)
        every { egl.eglDestroySurface(any(), eglSurface) }.returns(true)

        // invoke destroySurface
        val eglHelperDestroySurfaceMethod = eglHelper.javaClass.getMethod("destroySurface")
        eglHelperDestroySurfaceMethod.invoke(eglHelper)

        // check
        assertNull(eglSurfaceField.get(eglHelper))
    }

    @Test(expected = RuntimeException::class)
    fun eglHelper_whenFinishAndExistingEglContextDisplayAndNoEgl_throwsRuntimeException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        // set eglContext
        val eglContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglContextField.isAccessible = true

        assertNull(eglContextField.get(eglHelper))

        eglContextField.set(eglHelper, eglContext)

        assertSame(eglContext, eglContextField.get(eglHelper))

        // set eglDisplay
        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        eglDisplayField.set(eglHelper, eglDisplay)

        // invoke finish
        val eglHelperFinishMethod = eglHelper.javaClass.getMethod("finish")
        try {
            eglHelperFinishMethod.invoke(eglHelper)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            requireNotNull(cause)
            throw cause
        }
    }

    @Test
    fun eglHelper_whenFinishAndExistingEglContextAndDisplay_resetsEglContextAndDisplay() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        // set egl
        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        every { egl.eglDestroyContext(any(), any()) }.returns(true)
        every { egl.eglTerminate(any()) }.returns(true)
        eglField.set(eglHelper, egl)

        // set eglContext
        val eglContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglContextField.isAccessible = true

        assertNull(eglContextField.get(eglHelper))

        eglContextField.set(eglHelper, eglContext)

        assertSame(eglContext, eglContextField.get(eglHelper))

        // set eglDisplay
        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        eglDisplayField.set(eglHelper, eglDisplay)

        // invoke finish
        val eglHelperFinishMethod = eglHelper.javaClass.getMethod("finish")
        eglHelperFinishMethod.invoke(eglHelper)

        assertNull(eglContextField.get(eglHelper))
        assertNull(eglDisplayField.get(eglHelper))
    }

    @Test
    fun glThread_whenAbleToDrawAndNoContextOrSurfaceAndNotReadyToDraw_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        val haveEglContext = haveEglContextField.getBoolean(glThread)
        assertFalse(haveEglContext)

        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        val haveEglSurface = haveEglSurfaceField.getBoolean(glThread)
        assertFalse(haveEglSurface)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width = widthField.getInt(glThread)
        assertEquals(0, width)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height = heightField.getInt(glThread)
        assertEquals(0, height)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke ableToDraw
        val ableToDrawMethod = glThread.javaClass.getMethod("ableToDraw")
        val result = ableToDrawMethod.invoke(glThread) as Boolean
        assertFalse(result)
    }

    @Test
    fun glThread_whenAbleToDrawAndHasContextButNoSurfaceOrReadyToDraw_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        val haveEglContext1 = haveEglContextField.getBoolean(glThread)
        assertFalse(haveEglContext1)

        // set haveEglContext
        haveEglContextField.set(glThread, true)

        val haveEglContext2 = haveEglContextField.getBoolean(glThread)
        assertTrue(haveEglContext2)

        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        val haveEglSurface = haveEglSurfaceField.getBoolean(glThread)
        assertFalse(haveEglSurface)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width = widthField.getInt(glThread)
        assertEquals(0, width)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height = heightField.getInt(glThread)
        assertEquals(0, height)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke ableToDraw
        val ableToDrawMethod = glThread.javaClass.getMethod("ableToDraw")
        val result = ableToDrawMethod.invoke(glThread) as Boolean
        assertFalse(result)
    }

    @Test
    fun glThread_whenAbleToDrawHasContextAndSurfaceButNotReadyToDraw_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        val haveEglContext1 = haveEglContextField.getBoolean(glThread)
        assertFalse(haveEglContext1)

        // set haveEglContext
        haveEglContextField.set(glThread, true)

        val haveEglContext2 = haveEglContextField.getBoolean(glThread)
        assertTrue(haveEglContext2)

        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        val haveEglSurface1 = haveEglSurfaceField.getBoolean(glThread)
        assertFalse(haveEglSurface1)

        // set haveEglSurface
        haveEglSurfaceField.set(glThread, true)

        val haveEglSurface2 = haveEglSurfaceField.getBoolean(glThread)
        assertTrue(haveEglSurface2)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width = widthField.getInt(glThread)
        assertEquals(0, width)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height = heightField.getInt(glThread)
        assertEquals(0, height)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke ableToDraw
        val ableToDrawMethod = glThread.javaClass.getMethod("ableToDraw")
        val result = ableToDrawMethod.invoke(glThread) as Boolean
        assertFalse(result)
    }

    @Test
    fun glThread_whenAbleToDrawHasContextAndSurfaceAndNotReadyToDraw_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        val haveEglContext1 = haveEglContextField.getBoolean(glThread)
        assertFalse(haveEglContext1)

        // set haveEglContext
        haveEglContextField.set(glThread, true)

        val haveEglContext2 = haveEglContextField.getBoolean(glThread)
        assertTrue(haveEglContext2)

        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        val haveEglSurface1 = haveEglSurfaceField.getBoolean(glThread)
        assertFalse(haveEglSurface1)

        // set haveEglSurface
        haveEglSurfaceField.set(glThread, true)

        val haveEglSurface2 = haveEglSurfaceField.getBoolean(glThread)
        assertTrue(haveEglSurface2)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface1 = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface1)

        // set hasSurface
        hasSurfaceField.set(glThread, true)

        val hasSurface2 = hasSurfaceField.getBoolean(glThread)
        assertTrue(hasSurface2)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width1 = widthField.getInt(glThread)
        assertEquals(0, width1)

        // set width
        widthField.set(glThread, WIDTH)

        val width2 = widthField.getInt(glThread)
        assertEquals(WIDTH, width2)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height1 = heightField.getInt(glThread)
        assertEquals(0, height1)

        // set height
        heightField.set(glThread, HEIGHT)

        val height2 = heightField.getInt(glThread)
        assertEquals(HEIGHT, height2)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke ableToDraw
        val ableToDrawMethod = glThread.javaClass.getMethod("ableToDraw")
        val result = ableToDrawMethod.invoke(glThread) as Boolean
        assertTrue(result)
    }

    @Test
    fun glThread_whenReadyToDraw_returnsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width = widthField.getInt(glThread)
        assertEquals(0, width)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height = heightField.getInt(glThread)
        assertEquals(0, height)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke readyToDraw
        val readyToDrawMethod = glThread.javaClass.getMethod("readyToDraw")
        val result = readyToDrawMethod.invoke(glThread) as Boolean
        assertFalse(result)
    }

    @Test
    fun glThread_whenReadyToDraw_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        val hasSurface1 = hasSurfaceField.getBoolean(glThread)
        assertFalse(hasSurface1)

        // set hasSurface
        hasSurfaceField.set(glThread, true)

        val hasSurface2 = hasSurfaceField.getBoolean(glThread)
        assertTrue(hasSurface2)

        val surfaceIsBadField = glThreadClass.getDeclaredField("surfaceIsBad")
        surfaceIsBadField.isAccessible = true
        val surfaceIsBad = surfaceIsBadField.getBoolean(glThread)
        assertFalse(surfaceIsBad)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width1 = widthField.getInt(glThread)
        assertEquals(0, width1)

        // set width
        widthField.set(glThread, WIDTH)

        val width2 = widthField.getInt(glThread)
        assertEquals(WIDTH, width2)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height1 = heightField.getInt(glThread)
        assertEquals(0, height1)

        // set height
        heightField.set(glThread, HEIGHT)

        val height2 = heightField.getInt(glThread)
        assertEquals(HEIGHT, height2)

        val requestRenderField = glThreadClass.getDeclaredField("requestRender")
        requestRenderField.isAccessible = true
        val requestRender = requestRenderField.getBoolean(glThread)
        assertTrue(requestRender)

        val renderModeField = glThreadClass.getDeclaredField("_renderMode")
        renderModeField.isAccessible = true
        val renderMode = renderModeField.getInt(glThread)
        assertEquals(GLTextureView.RENDER_MODE_CONTINUOUSLY, renderMode)

        // invoke readyToDraw
        val readyToDrawMethod = glThread.javaClass.getMethod("readyToDraw")
        val result = readyToDrawMethod.invoke(glThread) as Boolean
        assertTrue(result)
    }

    @Test
    fun glThread_whenOnWindowResizeAndNotAbleToDraw_marksSizeAsChanged() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width1 = widthField.getInt(glThread)
        assertEquals(0, width1)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height1 = heightField.getInt(glThread)
        assertEquals(0, height1)

        val sizeChangedField = glThreadClass.getDeclaredField("sizeChanged")
        sizeChangedField.isAccessible = true
        val sizeChanged1 = sizeChangedField.getBoolean(glThread)
        assertTrue(sizeChanged1)

        val exitedField = glThreadClass.getDeclaredField("exited")
        exitedField.isAccessible = true
        val exitedField1 = exitedField.getBoolean(glThread)
        assertFalse(exitedField1)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused1 = pausedField.getBoolean(glThread)
        assertFalse(paused1)

        val renderCompleteField = glThreadClass.getDeclaredField("renderComplete")
        renderCompleteField.isAccessible = true
        val renderComplete1 = renderCompleteField.getBoolean(glThread)
        assertFalse(renderComplete1)

        // execute onWindowResize
        val onWindowResizeMethod =
            glThreadClass.getDeclaredMethod("onWindowResize", Int::class.java, Int::class.java)
        onWindowResizeMethod.invoke(glThread, WIDTH, HEIGHT)

        // check
        val width2 = widthField.getInt(glThread)
        assertEquals(WIDTH, width2)

        val height2 = heightField.getInt(glThread)
        assertEquals(HEIGHT, height2)

        val sizeChanged2 = sizeChangedField.getBoolean(glThread)
        assertTrue(sizeChanged2)
    }

    @Test
    fun glThread_whenOnWindowResizeAndAbleToDraw_marksSizeAsChanged() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        Thread.sleep(SLEEP)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val widthField = glThreadClass.getDeclaredField("width")
        widthField.isAccessible = true
        val width1 = widthField.getInt(glThread)
        assertEquals(0, width1)

        val heightField = glThreadClass.getDeclaredField("height")
        heightField.isAccessible = true
        val height1 = heightField.getInt(glThread)
        assertEquals(0, height1)

        val sizeChangedField = glThreadClass.getDeclaredField("sizeChanged")
        sizeChangedField.isAccessible = true
        val sizeChanged1 = sizeChangedField.getBoolean(glThread)
        assertTrue(sizeChanged1)

        val exitedField = glThreadClass.getDeclaredField("exited")
        exitedField.isAccessible = true
        val exitedField1 = exitedField.getBoolean(glThread)
        assertFalse(exitedField1)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused1 = pausedField.getBoolean(glThread)
        assertFalse(paused1)

        val renderCompleteField = glThreadClass.getDeclaredField("renderComplete")
        renderCompleteField.isAccessible = true
        val renderComplete1 = renderCompleteField.getBoolean(glThread)
        assertFalse(renderComplete1)

        val ableToDrawMethod = glThread.javaClass.getMethod("ableToDraw")
        val ableToDraw1 = ableToDrawMethod.invoke(glThread) as Boolean
        assertFalse(ableToDraw1)

        // set haveEglContext
        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        haveEglContextField.set(glThread, true)

        // set haveEglSurface
        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        haveEglSurfaceField.set(glThread, true)

        // set hasSurface
        val hasSurfaceField = glThreadClass.getDeclaredField("hasSurface")
        hasSurfaceField.isAccessible = true
        hasSurfaceField.set(glThread, true)

        // set width
        widthField.set(glThread, 1)

        // set height
        heightField.set(glThread, 1)

        // check able to draw
        val ableToDraw2 = ableToDrawMethod.invoke(glThread) as Boolean
        assertTrue(ableToDraw2)

        // setup eglHelper
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglField = eglHelper.javaClass.getDeclaredField("egl")
        eglField.isAccessible = true

        assertNull(eglField.get(eglHelper))

        // set egl
        every { egl.eglGetError() }.returns(EGL10.EGL_BAD_NATIVE_WINDOW)
        eglField.set(eglHelper, egl)

        assertSame(egl, eglField.get(eglHelper))

        val eglDisplayField = eglHelper.javaClass.getDeclaredField("eglDisplay")
        eglDisplayField.isAccessible = true

        assertNull(eglDisplayField.get(eglHelper))

        // set eglDisplay
        eglDisplayField.set(eglHelper, eglDisplay)

        assertSame(eglDisplay, eglDisplayField.get(eglHelper))

        val eglConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglConfigField.isAccessible = true

        assertNull(eglConfigField.get(eglHelper))

        // set eglConfig
        eglConfigField.set(eglHelper, eglConfig)

        assertSame(eglConfig, eglConfigField.get(eglHelper))

        // set eglContext
        val eglContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglContextField.isAccessible = true

        assertNull(eglContextField.get(eglHelper))

        eglContextField.set(eglHelper, eglContext)

        every {
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        }.returns(true)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true
        assertNotNull(glSurfaceViewWeakRefField.get(eglHelper))

        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        assertNotNull(eglWindowSurfaceFactory)

        every {
            egl.eglCreateWindowSurface(
                eglDisplay,
                eglConfig,
                any(),
                null
            )
        }.returns(eglSurface)

        every { egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) }.returns(true)
        every { egl.eglDestroySurface(any(), eglSurface) }.returns(true)
        every { egl.eglDestroyContext(any(), any()) }.returns(true)
        every { egl.eglTerminate(any()) }.returns(true)
        every { egl.eglSwapBuffers(eglDisplay, eglSurface) }.returns(true)

        // execute onWindowResize
        val onWindowResizeMethod =
            glThreadClass.getDeclaredMethod("onWindowResize", Int::class.java, Int::class.java)
        onWindowResizeMethod.invoke(glThread, WIDTH, HEIGHT)

        // check
        val width2 = widthField.getInt(glThread)
        assertEquals(WIDTH, width2)

        val height2 = heightField.getInt(glThread)
        assertEquals(HEIGHT, height2)

        val sizeChanged2 = sizeChangedField.getBoolean(glThread)
        assertFalse(sizeChanged2)

        verify(exactly = 1) { renderer.onSurfaceChanged(any(), WIDTH, HEIGHT) }
    }

    @Test
    fun glThread_whenRequestReleaseEglContextLocked_requestsEglContextRelease() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val lockField = glThreadManagerClass.getDeclaredField("lock")
        lockField.isAccessible = true
        val lock = lockField.get(glThreadManager) as ReentrantLock

        val shouldReleaseEglContextField = glThreadClass.getDeclaredField("shouldReleaseEglContext")
        shouldReleaseEglContextField.isAccessible = true
        val shouldReleaseEglContext1 = shouldReleaseEglContextField.getBoolean(glThread)
        assertFalse(shouldReleaseEglContext1)

        // invoke requestReleaseEglContextLocked
        val requestReleaseEglContextMethod =
            glThread.javaClass.getMethod("requestReleaseEglContextLocked")
        lock.withLock {
            // invocation must occur within the same reentrant locked used for condition signaling
            requestReleaseEglContextMethod.invoke(glThread)
        }

        // check
        val shouldReleaseEglContext2 = shouldReleaseEglContextField.getBoolean(glThread)
        assertNotNull(shouldReleaseEglContext2)
    }

    @Test
    fun glThread_whenGuardedRunShouldReleaseEglContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val shouldReleaseEglContextField = glThreadClass.getDeclaredField("shouldReleaseEglContext")
        shouldReleaseEglContextField.isAccessible = true
        shouldReleaseEglContextField.set(glThread, true)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val lockField = glThreadManagerClass.getDeclaredField("lock")
        lockField.isAccessible = true
        val lock = lockField.get(glThreadManager) as ReentrantLock

        val conditionField = glThreadManagerClass.getDeclaredField("condition")
        conditionField.isAccessible = true
        val condition = conditionField.get(glThreadManager) as Condition

        val requestExitMethod = glThread.javaClass.getDeclaredMethod("requestExitAndWait")
        requestExitMethod.isAccessible = true

        // since gl thread will lock waiting for next render, we need to signal to awake
        // it from another thread, and then we need to request thread to exit
        val thread = Thread {
            try {
                @Suppress("CallToThreadRun")
                glThread.run()
            } catch (_: Throwable) {
                threadFailures++
                fail()
            }
        }

        thread.start()

        lock.withLock {
            condition.signalAll()
        }

        // request exit
        requestExitMethod.invoke(glThread)

        thread.join()

        assertEquals(0, threadFailures)
    }

    @Test
    fun glThread_whenGuardedRunPausingAndHaveEglSurface() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val haveEglSurfaceField = glThreadClass.getDeclaredField("haveEglSurface")
        haveEglSurfaceField.isAccessible = true
        val haveEglSurface1 = haveEglSurfaceField.getBoolean(glThread)
        assertFalse(haveEglSurface1)

        // set haveEglSurface
        haveEglSurfaceField.set(glThread, true)

        val haveEglSurface2 = haveEglSurfaceField.getBoolean(glThread)
        assertTrue(haveEglSurface2)

        val requestPausedField = glThreadClass.getDeclaredField("requestPaused")
        requestPausedField.isAccessible = true
        val requestPaused1 = requestPausedField.getBoolean(glThread)
        assertFalse(requestPaused1)

        view.onPause()

        Thread.sleep(SLEEP)

        val requestPaused2 = requestPausedField.getBoolean(glThread)
        assertTrue(requestPaused2)
    }

    @Test
    fun glThread_whenGuardedRunPausingAndHaveEglContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val pausedField = glThreadClass.getDeclaredField("paused")
        pausedField.isAccessible = true
        val paused = pausedField.getBoolean(glThread)
        assertFalse(paused)

        val haveEglContextField = glThreadClass.getDeclaredField("haveEglContext")
        haveEglContextField.isAccessible = true
        val haveEglContext1 = haveEglContextField.getBoolean(glThread)
        assertFalse(haveEglContext1)

        // set haveEglContext
        haveEglContextField.set(glThread, true)

        val haveEglContext2 = haveEglContextField.getBoolean(glThread)
        assertTrue(haveEglContext2)

        val requestPausedField = glThreadClass.getDeclaredField("requestPaused")
        requestPausedField.isAccessible = true
        val requestPaused1 = requestPausedField.getBoolean(glThread)
        assertFalse(requestPaused1)

        view.onPause()

        Thread.sleep(SLEEP)

        val requestPaused2 = requestPausedField.getBoolean(glThread)
        assertTrue(requestPaused2)
    }

    @Test
    fun logWriter_close() {
        val classes = GLTextureView::class.java.declaredClasses

        @Suppress("UNCHECKED_CAST")
        val logWriterClass: Class<Writer> =
            classes.firstOrNull { it.name.endsWith("LogWriter") } as Class<Writer>
        val logWriter = logWriterClass.getDeclaredConstructor().newInstance()
        requireNotNull(logWriter)

        logWriter.close()
    }

    @Test
    fun logWriter_flush() {
        val classes = GLTextureView::class.java.declaredClasses

        @Suppress("UNCHECKED_CAST")
        val logWriterClass: Class<Writer> =
            classes.firstOrNull { it.name.endsWith("LogWriter") } as Class<Writer>
        val logWriter = logWriterClass.getDeclaredConstructor().newInstance()
        requireNotNull(logWriter)

        logWriter.use {
            logWriter.flush()
        }
    }

    @Test
    fun logWriter_write() {
        val classes = GLTextureView::class.java.declaredClasses

        @Suppress("UNCHECKED_CAST")
        val logWriterClass: Class<Writer> =
            classes.firstOrNull { it.name.endsWith("LogWriter") } as Class<Writer>
        val logWriter = logWriterClass.getDeclaredConstructor().newInstance()
        requireNotNull(logWriter)

        logWriter.use {
            logWriter.write("message", 0, 1)
        }
    }

    @Test
    fun logWriter_writeWhenLineBreak() {
        val classes = GLTextureView::class.java.declaredClasses

        @Suppress("UNCHECKED_CAST")
        val logWriterClass: Class<Writer> =
            classes.firstOrNull { it.name.endsWith("LogWriter") } as Class<Writer>
        val logWriter = logWriterClass.getDeclaredConstructor().newInstance()
        requireNotNull(logWriter)

        val msg = "message with \nline break"
        logWriter.use {
            logWriter.write(msg, 0, msg.length)
        }
    }

    @Test
    fun logWriter_writeWhenNullMessage() {
        val classes = GLTextureView::class.java.declaredClasses

        @Suppress("UNCHECKED_CAST")
        val logWriterClass: Class<Writer> =
            classes.firstOrNull { it.name.endsWith("LogWriter") } as Class<Writer>
        val logWriter = logWriterClass.getDeclaredConstructor().newInstance()
        requireNotNull(logWriter)

        logWriter.use {
            logWriter.write(null as CharArray?, 0, 1)
        }
    }

    @Test
    fun glThreadManager_whenThreadExiting() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val eglOwnerField = glThreadManagerClass.getDeclaredField("eglOwner")
        eglOwnerField.isAccessible = true
        val eglOwner1 = eglOwnerField.get(glThreadManager)
        assertNull(eglOwner1)

        // set eglOwner
        eglOwnerField.set(glThreadManager, glThread)

        val eglOwner2 = eglOwnerField.get(glThreadManager)
        assertSame(glThread, eglOwner2)

        val threadExitingMethod =
            glThreadManagerClass.getDeclaredMethod("threadExiting", glThreadClass)
        threadExitingMethod.invoke(glThreadManager, glThread)

        val eglOwner3 = eglOwnerField.get(glThreadManager)
        assertNull(eglOwner3)
    }

    @Test
    fun glThreadManager_whenTryAcquireEglContextLockedAndExistingEglOwner() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val eglOwnerField = glThreadManagerClass.getDeclaredField("eglOwner")
        eglOwnerField.isAccessible = true
        val eglOwner1 = eglOwnerField.get(glThreadManager)
        assertNull(eglOwner1)

        // set eglOwner
        eglOwnerField.set(glThreadManager, glThread)

        val eglOwner2 = eglOwnerField.get(glThreadManager)
        assertSame(glThread, eglOwner2)

        val lockField = glThreadManagerClass.getDeclaredField("lock")
        lockField.isAccessible = true
        val lock = lockField.get(glThreadManager) as ReentrantLock

        // invoke tryAcquireEglContextLocked
        val tryAcquireEglContextLockedMethod =
            glThreadManagerClass.getDeclaredMethod("tryAcquireEglContextLocked", glThreadClass)
        lock.withLock {
            val result =
                tryAcquireEglContextLockedMethod.invoke(glThreadManager, glThread) as Boolean
            assertTrue(result)
        }
    }

    @Test
    fun glThreadManager_whenTryAcquireEglContextLockedAndNonExistingEglOwner() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val eglOwnerField = glThreadManagerClass.getDeclaredField("eglOwner")
        eglOwnerField.isAccessible = true
        val eglOwner1 = eglOwnerField.get(glThreadManager)
        assertNull(eglOwner1)

        val lockField = glThreadManagerClass.getDeclaredField("lock")
        lockField.isAccessible = true
        val lock = lockField.get(glThreadManager) as ReentrantLock

        // invoke tryAcquireEglContextLocked
        val tryAcquireEglContextLockedMethod =
            glThreadManagerClass.getDeclaredMethod("tryAcquireEglContextLocked", glThreadClass)
        lock.withLock {
            val result =
                tryAcquireEglContextLockedMethod.invoke(glThreadManager, glThread) as Boolean
            assertTrue(result)
        }
    }

    @Test
    fun glThreadManager_whenTryAcquireEglContextLockedAndMultipleGLESContextsAllowed() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)
        val view2 = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)
        view2.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val glThread2: Thread? = view2.getPrivateProperty("glThread")
        requireNotNull(glThread2)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val eglOwnerField = glThreadManagerClass.getDeclaredField("eglOwner")
        eglOwnerField.isAccessible = true
        val eglOwner1 = eglOwnerField.get(glThreadManager)
        assertNull(eglOwner1)

        // set eglOwner
        eglOwnerField.set(glThreadManager, glThread2)

        val multipleGLESContextsAllowedField =
            glThreadManagerClass.getDeclaredField("multipleGLESContextsAllowed")
        multipleGLESContextsAllowedField.isAccessible = true
        val multipleGLESContextsAllowed1 =
            multipleGLESContextsAllowedField.getBoolean(glThreadManager)
        assertFalse(multipleGLESContextsAllowed1)

        // set multipleGLESContextsAllowed
        multipleGLESContextsAllowedField.setBoolean(glThreadManager, true)

        val multipleGLESContextsAllowed2 =
            multipleGLESContextsAllowedField.getBoolean(glThreadManager)
        assertTrue(multipleGLESContextsAllowed2)

        // invoke tryAcquireEglContextLocked
        val tryAcquireEglContextLockedMethod =
            glThreadManagerClass.getDeclaredMethod("tryAcquireEglContextLocked", glThreadClass)
        val result = tryAcquireEglContextLockedMethod.invoke(glThreadManager, glThread) as Boolean
        assertTrue(result)
    }

    @Test
    fun glThreadManager_whenTryAcquireEglContextLockedAndSingleGLESContextsAllowed() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)
        val view2 = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)
        view2.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val glThread2: Thread? = view2.getPrivateProperty("glThread")
        requireNotNull(glThread2)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager1 = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager1)
        val glThreadManager2 = glThreadManagerField.get(glThread2)
        requireNotNull(glThreadManager2)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val eglOwnerField = glThreadManagerClass.getDeclaredField("eglOwner")
        eglOwnerField.isAccessible = true
        val eglOwner1 = eglOwnerField.get(glThreadManager1)
        assertNull(eglOwner1)

        // set eglOwner
        eglOwnerField.set(glThreadManager1, glThread2)

        val multipleGLESContextsAllowedField =
            glThreadManagerClass.getDeclaredField("multipleGLESContextsAllowed")
        multipleGLESContextsAllowedField.isAccessible = true
        val multipleGLESContextsAllowed =
            multipleGLESContextsAllowedField.getBoolean(glThreadManager1)
        assertFalse(multipleGLESContextsAllowed)

        val lockField = glThreadManagerClass.getDeclaredField("lock")
        lockField.isAccessible = true
        val lock = lockField.get(glThreadManager2) as ReentrantLock

        // invoke tryAcquireEglContextLocked
        val tryAcquireEglContextLockedMethod =
            glThreadManagerClass.getDeclaredMethod("tryAcquireEglContextLocked", glThreadClass)
        lock.withLock {
            val result =
                tryAcquireEglContextLockedMethod.invoke(glThreadManager1, glThread) as Boolean
            assertFalse(result)
        }
    }

    @Test
    fun glThreadManager_shouldReleaseEGLContextWhenPausing() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val limitedGLESContextsField = glThreadManagerClass.getDeclaredField("limitedGLESContexts")
        limitedGLESContextsField.isAccessible = true
        val limitedGLESContexts = limitedGLESContextsField.getBoolean(glThreadManager)
        assertFalse(limitedGLESContexts)

        // set limitedGLESContexts
        limitedGLESContextsField.setBoolean(glThreadManager, true)

        val shouldReleaseEGLContextWhenPausingMethod =
            glThreadManagerClass.getDeclaredMethod("shouldReleaseEGLContextWhenPausing")
        val result = shouldReleaseEGLContextWhenPausingMethod.invoke(glThreadManager) as Boolean
        assertTrue(result)
    }

    @Test
    fun glThreadManager_whenCheckGLDriver() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        justRun { renderer.onSurfaceChanged(any(), any(), any()) }
        justRun { renderer.onDrawFrame(any()) }
        view.setRenderer(renderer)

        Thread.sleep(SLEEP)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)

        val glThreadManagerField = glThreadClass.getDeclaredField("glThreadManager")
        glThreadManagerField.isAccessible = true
        val glThreadManager = glThreadManagerField.get(glThread)
        requireNotNull(glThreadManager)

        val glThreadManagerClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("GLThreadManager") }
        requireNotNull(glThreadManagerClass)

        val checkGLDriverMethod =
            glThreadManagerClass.getDeclaredMethod("checkGLDriver", GL10::class.java)
        val gl = mockk<GL10>()
        every { gl.glGetString(GL10.GL_RENDERER) }.returns("renderer")
        checkGLDriverMethod.invoke(glThreadManager, gl)

        // check
        val multipleGLESContextsAllowedField =
            glThreadManagerClass.getDeclaredField("multipleGLESContextsAllowed")
        multipleGLESContextsAllowedField.isAccessible = true
        val multipleGLESContextsAllowed =
            multipleGLESContextsAllowedField.getBoolean(glThreadManager)
        assertTrue(multipleGLESContextsAllowed)

        val limitedGLESContextsField =
            glThreadManagerClass.getDeclaredField("limitedGLESContexts")
        limitedGLESContextsField.isAccessible = true
        val limitedGLESContexts = limitedGLESContextsField.getBoolean(glThreadManager)
        assertFalse(limitedGLESContexts)

        val glESDriverCheckCompleteField =
            glThreadManagerClass.getDeclaredField("glESDriverCheckComplete")
        glESDriverCheckCompleteField.isAccessible = true
        val glESDriverCheckComplete = glESDriverCheckCompleteField.getBoolean(glThreadManager)
        assertTrue(glESDriverCheckComplete)
    }

    private companion object {
        const val SLEEP = 1000L
        const val WIDTH = 1080
        const val HEIGHT = 1920
    }
}