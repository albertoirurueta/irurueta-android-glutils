package com.irurueta.android.glutils

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GLTextureViewTest {

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
        val glWrapper = mockk<GLTextureView.GLWrapper>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
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
        assertEquals(GLTextureView.RENDERMODE_CONTINUOUSLY, view.renderMode)

        // set new value
        view.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDERMODE_CONTINUOUSLY, view.renderMode)
    }

    @Test
    fun renderMode_whenRendererIsProvider_getAndSetsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check default value
        assertEquals(GLTextureView.RENDERMODE_CONTINUOUSLY, view.renderMode)

        // set new value
        view.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)
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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val texture = mockk<SurfaceTexture>()
        view.callPrivateFunc("surfaceCreated", texture)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceCreate_whenRenderer_notifiesSurfaceToRenderThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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
        val texture = mockk<SurfaceTexture>()
        view.callPrivateFunc("surfaceCreated", texture)

        // check
        val hasSurface2 = field.getBoolean(glThread)
        assertTrue(hasSurface2)
    }

    @Test
    fun surfaceDestroyed_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val texture = mockk<SurfaceTexture>()
        view.callPrivateFunc("surfaceDestroyed", texture)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceDestroyed_whenRenderer_notifiesSurfaceToRenderThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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
        val texture = mockk<SurfaceTexture>()
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

        val texture = mockk<SurfaceTexture>()
        view.callPrivateFunc("surfaceChanged", texture, WIDTH, HEIGHT)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun surfaceChanged_whenRenderer_notifiesSizeChange() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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
        val texture = mockk<SurfaceTexture>()
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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)
        view.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)
        assertTrue(glThread.isAlive)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("_renderMode")
        field.isAccessible = true
        val renderMode = field.getInt(glThread)
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, renderMode)

        val detached1: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached1)
        assertTrue(detached1)

        view.callPrivateFunc("onAttachedToWindow")

        val detached2: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached2)
        assertFalse(detached2)

        assertTrue(glThread.isAlive)

        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)
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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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
        view.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY

        // check
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)

        // detach
        view.callPrivateFunc("onDetachedFromWindow")

        // check
        val detached3: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached3)
        assertTrue(detached3)

        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)

        // attach again
        view.callPrivateFunc("onAttachedToWindow")

        val detached4: Boolean? = view.getPrivateProperty("detached")
        requireNotNull(detached4)
        assertFalse(detached4)

        // ensure render mode is preserved
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)
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

        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

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

    private companion object {
        const val SLEEP = 1000L
        const val WIDTH = 1080
        const val HEIGHT = 1920
    }
}