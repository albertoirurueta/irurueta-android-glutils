package com.irurueta.android.glutils

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.TextureView
import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import io.mockk.impl.WeakRef
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import javax.microedition.khronos.egl.*

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

    @Test
    fun surfaceTextureListener_whenOnSurfaceTextureAvailableAndNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        val surfaceTextureListener: TextureView.SurfaceTextureListener? =
            view.getPrivateProperty("surfaceTextureListener")
        requireNotNull(surfaceTextureListener)

        val surface = mockk<SurfaceTexture>()
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

        val surface = mockk<SurfaceTexture>()
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

        val surface = mockk<SurfaceTexture>()
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

        val surface = mockk<SurfaceTexture>()
        surfaceTextureListener.onSurfaceTextureUpdated(surface)

        assertNull(view.getPrivateProperty("glThread"))
    }

    @Test
    fun eglContextFactory_whenCreateContext_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val eglConfig = mockk<EGLConfig>()
        val eglContext = mockk<EGLContext>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val eglContext = mockk<EGLContext>()

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglContextFactory: GLTextureView.EGLContextFactory? =
            view.getPrivateProperty("eglContextFactory")
        requireNotNull(eglContextFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val eglContext = mockk<EGLContext>()

        every { egl.eglDestroyContext(display, eglContext) }.returns(true)

        eglContextFactory.destroyContext(egl, display, eglContext)

        verify(exactly = 1) { egl.eglDestroyContext(display, eglContext) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenCreateWindowSurfaceFails_executesExpectedGLCommandsAndLogs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val config = mockk<EGLConfig>()
        val surfaceTexture = mockk<SurfaceTexture>()
        every { egl.eglCreateWindowSurface(display, config, surfaceTexture, null) }.throws(
            IllegalArgumentException()
        )

        assertNull(
            eglWindowSurfaceFactory.createWindowSurface(
                egl,
                display,
                config,
                surfaceTexture
            )
        )

        verify(exactly = 1) { egl.eglCreateWindowSurface(display, config, surfaceTexture, null) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenCreateWindowSurfaceSucceeds_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val config = mockk<EGLConfig>()
        val surfaceTexture = mockk<SurfaceTexture>()
        val eglSurface = mockk<EGLSurface>()
        every { egl.eglCreateWindowSurface(display, config, surfaceTexture, null) }.returns(
            eglSurface
        )

        assertSame(
            eglSurface,
            eglWindowSurfaceFactory.createWindowSurface(egl, display, config, surfaceTexture)
        )

        verify(exactly = 1) { egl.eglCreateWindowSurface(display, config, surfaceTexture, null) }
    }

    @Test
    fun eglWindowSurfaceFactory_whenDestroySurface_executesExpectedGLCommands() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglWindowSurfaceFactory: GLTextureView.EGLWindowSurfaceFactory? =
            view.getPrivateProperty("eglWindowSurfaceFactory")
        requireNotNull(eglWindowSurfaceFactory)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()
        val surface = mockk<EGLSurface>()
        every { egl.eglDestroySurface(display, surface) }.returns(true)

        eglWindowSurfaceFactory.destroySurface(egl, display, surface)

        verify(exactly = 1) { egl.eglDestroySurface(display, surface) }
    }

    @Test
    fun eglConfigChooser_whenChooseConfigAndConfigFails_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = GLTextureView(context)

        // set renderer
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }

        val config = mockk<EGLConfig>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        // check
        val eglConfigChooser: GLTextureView.EGLConfigChooser? =
            view.getPrivateProperty("eglConfigChooser")
        requireNotNull(eglConfigChooser)

        val egl = mockk<EGL10>()
        val display = mockk<EGLDisplay>()

        every { egl.eglChooseConfig(display, any(), null, 0, any()) }.answers { call ->
            val numConfig = call.invocation.args[4] as IntArray
            numConfig[0] = 1
            return@answers true
        }

        val config = mockk<EGLConfig>()
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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val egl = mockk<EGL10>()
        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(EGL10.EGL_NO_DISPLAY)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val eglDisplay = mockk<EGLDisplay>()
        val egl = mockk<EGL10>()
        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(false)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

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

        val eglDisplay = mockk<EGLDisplay>()
        val egl = mockk<EGL10>()
        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(true)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

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
        val renderer = mockk<GLSurfaceView.Renderer>()
        view.setRenderer(renderer)

        val glThread: Thread? = view.getPrivateProperty("glThread")
        requireNotNull(glThread)

        val classes = view.javaClass.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val eglHelperField = glThreadClass.getDeclaredField("eglHelper")
        eglHelperField.isAccessible = true
        val eglHelper = eglHelperField.get(glThread)
        requireNotNull(eglHelper)

        val glSurfaceViewWeakRefField = eglHelper.javaClass.getDeclaredField("glSurfaceViewWeakRef")
        glSurfaceViewWeakRefField.isAccessible = true

        val eglDisplay = mockk<EGLDisplay>()
        val egl = mockk<EGL10>()
        every { egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY) }.returns(eglDisplay)
        every { egl.eglInitialize(eglDisplay, any()) }.returns(true)

        mockkStatic(EGLContext::class)
        every { EGLContext.getEGL() }.returns(egl)

        val eglConfig = mockk<EGLConfig>()
        val eglConfigChooser = mockk<GLTextureView.EGLConfigChooser>()
        every { eglConfigChooser.chooseConfig(egl, eglDisplay) }.returns(eglConfig)
        view.setPrivateProperty("eglConfigChooser", eglConfigChooser)

        val eglContext = mockk<EGLContext>()
        val eglContextFactory = mockk<GLTextureView.EGLContextFactory>()
        every { eglContextFactory.createContext(egl, eglDisplay, eglConfig) }.returns(eglContext)
        view.setPrivateProperty("eglContextFactory", eglContextFactory)

        val eglHelperStartMethod = eglHelper.javaClass.getMethod("start")
        eglHelperStartMethod.invoke(eglHelper)

        val eglHelperConfigField = eglHelper.javaClass.getDeclaredField("eglConfig")
        eglHelperConfigField.isAccessible = true
        assertSame(eglConfig, eglHelperConfigField.get(eglHelper))

        val eglHelperContextField = eglHelper.javaClass.getDeclaredField("eglContext")
        eglHelperContextField.isAccessible = true
        assertSame(eglContext, eglHelperContextField.get(eglHelper))
    }

    private companion object {
        const val SLEEP = 1000L
        const val WIDTH = 1080
        const val HEIGHT = 1920
    }
}