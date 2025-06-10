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
import android.opengl.GLDebugHelper
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import java.io.Writer
import java.lang.ref.WeakReference
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.withLock

/**
 * View to draw using OpenGL on a [TextureView].
 * This can be used as a replacement of [GLSurfaceView] in situations where advanced view
 * composition is required such as when drawing with view transparency or animating certain
 * view properties (such as alpha transparency channel).
 *
 * @param context Android context.
 * @param attrs XML layout attributes.
 * @param defStyleAttr style to be used.
 */
@Suppress("DEPRECATION")
open class GLTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr) {

    /**
     * Weak reference to this view.
     */
    @Suppress("LeakingThis")
    private val thisWeakRef = WeakReference(this)

    /**
     * The thread that will call the renderer.
     */
    private var glThread: GLThread? = null

    /**
     * Manager of the GL thread.
     * Keeps lock and condition to synchronize access to the GL thread.
     */
    private val glThreadManager = GLThreadManager()

    /**
     * The renderer that will draw the scene.
     * A renderer is responsible for drawing graphics using OpenGL ES.
     * An application typically creates an implementation of this interface, and calls
     * [setRenderer] to register it with the GLTextureView.
     */
    private var renderer: GLSurfaceView.Renderer? = null

    /**
     * Indicates whether the view is detached from a window.
     */
    private var detached = true

    /**
     * The EGL configuration chooser.
     * This is used to choose an EGLConfig configuration from a list of potential configurations,
     * such as the color depth, stencil buffer, and so on.
     */
    private var eglConfigChooser: EGLConfigChooser? = null

    /**
     * The EGL context factory.
     * This is used to customize the eglCreateContext and eglDestroyContext calls, such as setting
     * the EGLContext client version.
     */
    private var eglContextFactory: EGLContextFactory? = null

    /**
     * The EGL window surface factory.
     * This is used to customize the eglCreateWindowSurface and eglDestroySurface calls, such as
     * setting the EGLConfig configuration.
     */
    private var eglWindowSurfaceFactory: EGLWindowSurfaceFactory? = null

    /**
     * The GL wrapper.
     * If the glWrapper is not null, its [GLWrapper.wrap] method is called whenever a surface is
     * created. A GLWrapper can be used to wrap the GL object that's passed to the renderer.
     * Wrapping a GL object enables examining and modifying the behavior of the GL calls made by
     * the renderer.
     */
    private var glWrapper: GLWrapper? = null

    /**
     * The EGL context client version.
     * This is used to set the EGLContext client version to pick.
     */
    private var eglContextClientVersion = 0

    /**
     * Listener to handle surface texture events.
     * This is used to create and start the GL thread when the surface is created.
     */
    private val surfaceTextureListener = object : SurfaceTextureListener {
        /**
         * Called when the surface texture is available.
         * This is used to create and start the GL thread.
         *
         * @param surface the surface texture.
         * @param width the width of the surface texture expressed in pixels.
         * @param height the height of the surface texture expressed in pixels.
         */
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            surfaceCreated(surface)
            surfaceChanged(surface, width, height)
        }

        /**
         * Called when the size of the surface texture changes.
         * This is used to update the size of the surface.
         *
         * @param surface the surface texture.
         * @param width the new width of the surface texture expressed in pixels.
         * @param height the new height of the surface texture expressed in pixels.
         */
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            surfaceChanged(surface, width, height)
        }

        /**
         * Called when the surface texture is destroyed.
         * This is used to destroy the surface.
         *
         * @param surface the surface texture.
         * @return true if the surface texture is destroyed, false otherwise.
         */
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            surfaceDestroyed(surface)
            return true
        }

        /**
         * Called when the surface texture is updated.
         * This is used to update the surface.
         *
         * @param surface the surface texture.
         */
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            // no action needed
        }
    }

    /**
     * Listener to handle layout changes.
     * This is used to update the size of the surface when the layout changes.
     */
    private val layoutChangeListener =
        OnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            surfaceChanged(
                surfaceTexture,
                right - left,
                bottom - top
            )
        }

    /**
     * Initializes this view.
     */
    init {
        super.setSurfaceTextureListener(surfaceTextureListener)
        super.addOnLayoutChangeListener(layoutChangeListener)
    }

    /**
     * Sets the glWrapper. If the glWrapper is not null, its [GLWrapper.wrap] method
     * is called whenever a surface is created. A GLWrapper can be used to wrap the GL object that's
     * passed to the renderer. Wrapping a GL object enables examining and modifying the behavior of
     * the GL calls made by the renderer.
     *
     * Wrapping is typically used for debugging purposes.
     *
     * The default value is null.
     *
     * @param glWrapper the new GLWrapper.
     */
    fun setGLWrapper(glWrapper: GLWrapper) {
        this.glWrapper = glWrapper
    }

    /**
     * Gets or sets current debug flags.
     * The value is constructed by OR-together zero or more of the DEBUG_CHECK_* constants. The
     * debug flags take effect whenever a surface is created.
     * The default value is zero.
     *
     * @see DEBUG_CHECK_GL_ERROR
     * @see DEBUG_LOG_GL_CALLS
     */
    var debugFlags = 0

    /**
     * Controls whether the EGL context is preserved when the GLTextureView is paused and resumed.
     *
     * If set to true, then the EGL context may be preserved when the GLTextureView is paused.
     * Whether the EGL context is actually preserved or not depends upon whether the Android device
     * that the program is running on can support an arbitrary number of EGL contexts or not.
     * Devices that can only support a limited number of EGL contexts must release the EGL context
     * in order to allow multiple applications to share the GPU.
     *
     * If set to false, the EGL context will be released when the GLTextureView is paused, and
     * recreated when the GLTextureView is resumed.
     *
     * The default is false.
     */
    var preserveEGLContextOnPause = false

    /**
     * Sets the renderer associated with this view. Also starts the thread that will call the
     * renderer, which in turn causes the rendering to start.
     *
     * This method should be called once and only once in the life-cycle of a GLTextureView.
     *
     * The following GLTextureView methods can only be called before setRenderer is called:
     * - [renderMode]
     * - [onPause]
     * - [onResume]
     * - [queueEvent]
     * - [requestRender]
     */
    fun setRenderer(renderer: GLSurfaceView.Renderer) {
        checkRenderThreadState()
        if (eglConfigChooser == null) {
            eglConfigChooser = SimpleEGLConfigChooser(true)
        }
        if (eglContextFactory == null) {
            eglContextFactory = DefaultContextFactory()
        }
        if (eglWindowSurfaceFactory == null) {
            eglWindowSurfaceFactory = DefaultWindowSurfaceFactory()
        }
        this.renderer = renderer
        initializeGlThread()
    }

    /**
     * Installs a custom EGLContextFactory.
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * If this method is not called, then by default a context will be created with no shared
     * context and with a null attribute list.
     */
    fun setEGLContextFactory(factory: EGLContextFactory) {
        checkRenderThreadState()
        eglContextFactory = factory
    }

    /**
     * Installs a custom EGLWindowSurfaceFactory.
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * If this method is not called, then by a default a window surface will be created with a null
     * attribute list.
     */
    fun setEGLWindowSurfaceFactory(factory: EGLWindowSurfaceFactory) {
        checkRenderThreadState()
        eglWindowSurfaceFactory = factory
    }

    /**
     * Installs a custom EGLConfigChooser.
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * If no [setEGLConfigChooser] method is called, then by default the view will choose an
     * EGLConfig that is compatible with the current android.view.Surface, with a depth buffer of
     * at least 16 bits.
     */
    fun setEGLConfigChooser(configChooser: EGLConfigChooser) {
        checkRenderThreadState()
        eglConfigChooser = configChooser
    }

    /**
     * Installs a config chooser which will choose a config as close to 16-bit RGB as possible, with
     * or without an optional depth buffer as close to 16-bits as possible.
     *
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * If no [setEGLConfigChooser] method is called, then by default the view will choose an RGB_888
     * surface with a depth buffer of at least 16 bits.
     */
    fun setEGLConfigChooser(needDepth: Boolean) {
        setEGLConfigChooser(SimpleEGLConfigChooser(needDepth))
    }

    /**
     * Installs a config chooser which will choose a config with at least the specified depthSize
     * and stencilSize, and exactly the specified redSize, greenSize, blueSize and alphaSize.
     *
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * If no [setEGLConfigChooser] method is called, then by default the view will choose an RGB_888
     * surface with a depth buffer of at least 16 bits.
     *
     * @param redSize the size of the red component in bits.
     * @param greenSize the size of the green component in bits.
     * @param blueSize the size of the blue component in bits.
     * @param alphaSize the size of the alpha component in bits.
     * @param depthSize the size of the depth buffer in bits.
     * @param stencilSize the size of the stencil buffer in bits.
     */
    fun setEGLConfigChooser(
        redSize: Int,
        greenSize: Int,
        blueSize: Int,
        alphaSize: Int,
        depthSize: Int,
        stencilSize: Int
    ) {
        setEGLConfigChooser(
            ComponentSizeChooser(
                redSize,
                greenSize,
                blueSize,
                alphaSize,
                depthSize,
                stencilSize
            )
        )
    }

    /**
     * Informs the default EGLContextFactory and default EGLConfigChooser which EGLContext client
     * version to pick.
     *
     * Use this method to create an OpenGL ES 2.0-compatible context.
     * Example:
     * ```
     * class MyView(context: Context) : GLTextureView(context) {
     *      ...
     *      init {
     *          setEGLContextClientVersion(2)
     *          setRenderer(MyRenderer())
     *      }
     * }
     * ```
     *
     * Note: Activities which require OpenGL ES 2.0 should indicate this by setting
     * `uses-feature android:glEsVersion="0x00020000"`in the activity's AndroidManifest.xml file.
     *
     * If this method is called, it must be called before [setRenderer] is called.
     *
     * This method only affects the behavior of the default EGLContextFactory and the default
     * EGLConfigChooser.
     * If [setEGLContextFactory] has been called, then the supplied EGLConfigChooser is responsible
     * for choosing an OpenGL ES 2.0-compatible config.
     *
     * @param version The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
     *
     */
    fun setEGLContextClientVersion(version: Int) {
        checkRenderThreadState()
        eglContextClientVersion = version
    }

    /**
     * Gets or sets the rendering mode. When renderMode is [RENDER_MODE_CONTINUOUSLY], the renderer
     * is called repeatedly to re-render the scene. When renderMode is [RENDER_MODE_WHEN_DIRTY], the
     * renderer only renders when the surface is created, or when [requestRender] is called.
     * Defaults to [RENDER_MODE_CONTINUOUSLY].
     *
     * Using [RENDER_MODE_WHEN_DIRTY] can improve battery life and overall system performance by
     * allowing the GPU and CPU to become idle when the view does not need to be updated.
     *
     * Setter can only be called after [setRenderer]
     * Getter can be called from any thread.
     *
     * @see [RENDER_MODE_CONTINUOUSLY]
     * @see [RENDER_MODE_WHEN_DIRTY]
     */
    var renderMode: Int
        get() = glThread?.renderMode ?: RENDER_MODE_CONTINUOUSLY
        set(value) {
            glThread?.renderMode = value
        }

    /**
     * Requests tha the renderer renders a frame.
     * This method is typically used when the render mode has ben set to [RENDER_MODE_WHEN_DIRTY], so
     * that frames are only rendered on demand.
     * May be called from any thread.
     * Calling it before a renderer has been set has no effect.
     */
    fun requestRender() {
        glThread?.requestRender()
    }

    /**
     * Inform the view that the activity has paused. The owner of this view must call this method
     * when the activity is paused. Calling this method will pause the rendering thread.
     * Calling this method before a renderer is set has no effect.
     */
    fun onPause() {
        glThread?.onPause()
    }

    /**
     * Informs the view that the activity has resumed. The owner of this view must call this method
     * when the activity is resumed. Calling this method will recreate the OpenGL scene and resume
     * the rendering thread.
     * Calling this method before a renderer is set has no effect.
     */
    fun onResume() {
        glThread?.onResume()
    }

    /**
     * Queues a runnable to be run on the GL rendering thread. This can be used to communicate with
     * the renderer on the rendering thread.
     * Calling this method before a renderer is set has no effect.
     *
     * @param r the runnable to be run on the GL rendering thread.
     */
    fun queueEvent(r: Runnable) {
        glThread?.queueEvent(r)
    }

    /**
     * This method is used by the internal [TextureView.SurfaceTextureListener], and is not normally
     * called or subclassed by clients of GLTextureView.
     */
    protected fun finalize() {
        // GLThread may still be running if this view was never
        // attached to a window
        glThread?.requestExitAndWait()
    }

    /**
     * This method is used by the internal [TextureView.SurfaceTextureListener], and is not normally
     * called or subclassed by clients of GLTextureView.
     */
    protected open fun surfaceCreated(texture: SurfaceTexture) {
        glThread?.surfaceCreated()
    }

    /**
     * This method is used by the internal [TextureView.SurfaceTextureListener], and is not normally
     * called or subclassed by clients of GLTextureView.
     */
    protected open fun surfaceDestroyed(texture: SurfaceTexture) {
        // Surface will be destroyed when we return
        glThread?.surfaceDestroyed()
    }

    /**
     * This method is used by the internal [TextureView.SurfaceTextureListener], and is not normally
     * called or subclassed by clients of GLTextureView.
     */
    protected open fun surfaceChanged(texture: SurfaceTexture?, w: Int, h: Int) {
        glThread?.onWindowResize(w, h)
    }

    /**
     * Creates and starts GL thread if not already initialized.
     */
    protected open fun initializeGlThread() {
        val renderMode = glThread?.renderMode ?: RENDER_MODE_CONTINUOUSLY
        glThreadManager.lock.withLock {
            val threadAlive = glThread?.isAlive == true
            if (!threadAlive) {
                glThread = GLThread(thisWeakRef, glThreadManager)
                if (renderMode != RENDER_MODE_CONTINUOUSLY) {
                    glThread?.renderMode = renderMode
                }
                glThread?.start()
            }
        }
    }

    /**
     * This method is used as part of the View class and is not normally called or subclassed by
     * clients of GLTextureView.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (LOG_ATTACH_DETACH) {
            Log.d(TAG, "onAttachedToWindow reattach =$detached")
        }
        if (detached && renderer != null) {
            initializeGlThread()
        }
        detached = false
    }

    /**
     * This method is used as part of the View class and is not normally called or subclassed by
     * clients of GLTextureView.
     * Must not be called before a renderer has been set.
     */
    override fun onDetachedFromWindow() {
        if (LOG_ATTACH_DETACH) {
            Log.d(TAG, "onDetachedFromWindow")
        }
        glThread?.requestExitAndWait()
        detached = true
        super.onDetachedFromWindow()
    }

    private fun checkRenderThreadState() {
        if (glThread != null) {
            throw IllegalStateException("renderer has already been set for this instance.")
        }
    }

    companion object {
        /**
         * The tag for logging.
         */
        private const val TAG = "GLTextureView"

        /**
         * Set to true to enable verbose logging.
         */
        private const val LOG_ATTACH_DETACH = true

        /**
         * Set to true to log thread creation and destruction.
         */
        private const val LOG_THREADS = true

        /**
         * Set to true to log thread pause and resume.
         */
        private const val LOG_PAUSE_RESUME = true

        /**
         * Set to true to log surface creation and destruction.
         */
        private const val LOG_SURFACE = true

        /**
         * Set to true to log surface size changes.
         */
        private const val LOG_RENDERER = true

        /**
         * Set to true to log draw frame completion.
         */
        private const val LOG_RENDERER_DRAW_FRAME = false

        /**
         * Set to true to log EGL configuration changes.
         */
        private const val LOG_EGL = true

        /**
         * The main thread name.
         */
        const val MAIN_THREAD = "Main thread"

        /**
         * The renderer only renders when the surface is created, or when [requestRender] is called.
         *
         * @see renderMode
         * @see requestRender
         */
        const val RENDER_MODE_WHEN_DIRTY = 0

        /**
         * The renderer is called continuously to re-render the scene.
         *
         * @see renderMode
         */
        const val RENDER_MODE_CONTINUOUSLY = 1

        /**
         * Check glError() after every GL call and throw an exception if glError indicates that an
         * error has occurred. This can be used to help track down which OpenGL ES call is causing
         * an error.
         *
         * @see debugFlags
         */
        const val DEBUG_CHECK_GL_ERROR = 1

        /**
         * Logs GL calls to the system log at "verbose" level with tag "GLTextureView".
         *
         * @see debugFlags
         */
        const val DEBUG_LOG_GL_CALLS = 2

        /**
         * Flag to set client version to GL ES2.0.
         */
        const val EGL_CONTEXT_CLIENT_VERSION = 0x3098

        /**
         * Version of GL ES2.0.
         */
        private const val EGL_OPENGL_ES2_BIT = 4
    }

    /**
     * An interface used to wrap a GL interface.
     *
     * Typically used for implementing debugging and tracing on top of the default GL interface.
     * You would typically use this by creating your own class that implemented all the GL methods
     * by delegating to another GL instance.
     * Then you could add your own behavior before or after calling the delegate.
     * All the GLWrapper would do was instantiate and return the wrapper GL instance:
     * ```
     * class MyGLWrapper : GLWrapper {
     *      fun wrap(gl: GL): GL {
     *          return MyGLImplementation(gl)
     *      }
     *
     *      class MyGLImplementation : GL, GL10, GL11, ... {
     *          ...
     *      }
     * }
     * ```
     *
     * @see setGLWrapper
     */
    interface GLWrapper {
        /**
         * Wraps a gl interface in another gl interface.
         *
         * @param gl a GL interface that is meant to be wrapped.
         * @return either the input argument or another GL object that wraps the input argument.
         */
        fun wrap(gl: GL?): GL?
    }

    /**
     * An interface for customizing the eglCreateContext and eglDestroyContext calls.
     *
     * This interface must be implemented by clients wishing to call [setEGLContextFactory]
     */
    interface EGLContextFactory {
        /**
         * Create a GL context.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param eglConfig the current EGLConfig.
         * @return the EGL context.
         */
        fun createContext(egl: EGL10?, display: EGLDisplay?, eglConfig: EGLConfig?): EGLContext?

        /**
         * Destroy a GL context.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param context the context to destroy.
         */
        fun destroyContext(egl: EGL10?, display: EGLDisplay?, context: EGLContext?)
    }

    /**
     * An interface to customize the eglCreateWindowSurface and eglDestroySurface calls.
     *
     * This interface must be implemented by clients wishing to call [setEGLWindowSurfaceFactory]
     */
    interface EGLWindowSurfaceFactory {
        /**
         * Creates a window surface to draw into.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param config the current EGLConfig.
         * @param nativeWindow the native window to draw into.
         * @return null if the surface cannot be constructed
         */
        fun createWindowSurface(
            egl: EGL10?,
            display: EGLDisplay?,
            config: EGLConfig?,
            nativeWindow: Any?
        ): EGLSurface?

        /**
         * Destroys a window surface.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param surface the surface to destroy.
         */
        fun destroySurface(egl: EGL10?, display: EGLDisplay?, surface: EGLSurface?)
    }

    /**
     * An interface for choosing an EGLConfig configuration from a list of potential configurations.
     *
     * This interface must be implemented by clients wishing to call [setEGLConfigChooser].
     */
    interface EGLConfigChooser {
        /**
         * Choose a configuration from the list. Implementors typically implement this method by
         * calling [EGL10.eglChooseConfig] and iterating through the results. Please check the EGL
         * specification available from the Khronos Group to learn how to call eglChooseConfig.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @return the chosen configuration.
         */
        fun chooseConfig(egl: EGL10?, display: EGLDisplay?): EGLConfig
    }

    /**
     * A simple [EGLContextFactory] for use with [GLTextureView] classes.
     * This class is used to create a new OpenGL ES context when the view is created
     * and to destroy the OpenGL ES context when the view is destroyed.
     */
    private inner class DefaultContextFactory : EGLContextFactory {
        /**
         * Create a new OpenGL ES context.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param eglConfig the current EGLConfig.
         */
        override fun createContext(
            egl: EGL10?,
            display: EGLDisplay?,
            eglConfig: EGLConfig?
        ): EGLContext? {
            val attribList = intArrayOf(
                EGL_CONTEXT_CLIENT_VERSION, eglContextClientVersion,
                EGL10.EGL_NONE
            )
            return egl?.eglCreateContext(
                display, eglConfig, EGL10.EGL_NO_CONTEXT,
                if (eglContextClientVersion != 0) attribList else null
            )
        }

        /**
         * Destroy an OpenGL ES context.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param context the context to destroy.
         */
        override fun destroyContext(egl: EGL10?, display: EGLDisplay?, context: EGLContext?) {
            if (egl?.eglDestroyContext(display, context) != true) {
                Log.e("DefaultContextFactory", "display: $display context: $context")
                if (LOG_THREADS) {
                    Log.i("DefaultContextFactory", "tid=" + Thread.currentThread().id)
                }
                EglHelper.throwEglException("eglDestroyContext", egl?.eglGetError())
            }
        }
    }

    /**
     * A default [EGLWindowSurfaceFactory] for use with [GLTextureView] classes.
     * This class is used to create a new window surface when the view is created
     * and to destroy the window surface when the view is destroyed.
     */
    private class DefaultWindowSurfaceFactory : EGLWindowSurfaceFactory {
        /**
         * Creates a window surface to draw into.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param config the current EGLConfig.
         * @param nativeWindow the native window to draw into.
         * @return null if the surface cannot be constructed
         */
        override fun createWindowSurface(
            egl: EGL10?,
            display: EGLDisplay?,
            config: EGLConfig?,
            nativeWindow: Any?
        ): EGLSurface? {
            var result: EGLSurface? = null
            try {
                result = egl?.eglCreateWindowSurface(display, config, nativeWindow, null)
            } catch (e: IllegalArgumentException) {
                // This exception indicates that the surface flinger surface is not valid. This can
                // happen if the surface flinger surface has been torn down, but the application has
                // not yet been notified via SurfaceHolder.Callback.surfaceDestroyed.
                // In theory the application should be notified first, but in practice sometimes it
                // is not. See b/4588890
                Log.e(TAG, "eglCreateWindowSurface", e)
            }
            return result
        }

        /**
         * Destroys a window surface.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param surface the surface to destroy.
         */
        override fun destroySurface(egl: EGL10?, display: EGLDisplay?, surface: EGLSurface?) {
            egl?.eglDestroySurface(display, surface)
        }
    }

    /**
     * An abstract base class for choosing an EGLConfig configuration.
     */
    private abstract inner class BaseConfigChooser(configSpec: IntArray) : EGLConfigChooser {
        /**
         * The configuration spec.
         */
        private var configSpec: IntArray? = null

        /**
         * Initializes the base config chooser.
         */
        init {
            this.configSpec = filterConfigSpec(configSpec)
        }

        /**
         * Choose a configuration from the list.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @return the chosen configuration.
         */
        override fun chooseConfig(egl: EGL10?, display: EGLDisplay?): EGLConfig {
            val numConfig = IntArray(1)
            if (egl?.eglChooseConfig(display, configSpec, null, 0, numConfig) != true) {
                throw IllegalArgumentException("eglChooseConfig failed")
            }

            val numConfigs = numConfig[0]

            if (numConfigs <= 0) {
                throw IllegalArgumentException("No configs match configSpec")
            }

            val configs = Array<EGLConfig?>(numConfigs) { null }
            if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, numConfig)) {
                throw IllegalArgumentException("eglChooseConfig#2 failed")
            }
            return chooseConfig(egl, display, configs)
                ?: throw IllegalArgumentException("No config chosen")
        }

        /**
         * Chooses a configuration from the list of configurations.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param configs the list of configurations.
         * @return the chosen configuration.
         */
        abstract fun chooseConfig(
            egl: EGL10?,
            display: EGLDisplay?,
            configs: Array<EGLConfig?>
        ): EGLConfig?

        /**
         * Filters the configuration spec.
         *
         * @param configSpec the configuration spec.
         * @return the filtered configuration spec.
         */
        private fun filterConfigSpec(configSpec: IntArray): IntArray {
            if (eglContextClientVersion != 2) {
                return configSpec
            }
            // We know none of the subclasses define EGL_RENDERABLE_TYPE.
            // And we know the configSpec is well formed.
            val len = configSpec.size
            val newConfigSpec = IntArray(len + 2)
            configSpec.copyInto(newConfigSpec, 0, 0, len - 1)
            newConfigSpec[len - 1] = EGL10.EGL_RENDERABLE_TYPE
            newConfigSpec[len] = EGL_OPENGL_ES2_BIT
            newConfigSpec[len + 1] = EGL10.EGL_NONE
            return newConfigSpec
        }
    }

    /**
     * Choose a configuration with exactly the specified r,g,b,a sizes, and at least the specified
     * depth and stencil sizes.
     *
     * @param redSize the size of the red component in bits.
     * @param greenSize the size of the green component in bits.
     * @param blueSize the size of the blue component in bits.
     * @param alphaSize the size of the alpha component in bits.
     * @param depthSize the size of the depth buffer in bits.
     * @param stencilSize the size of the stencil buffer in bits.
     */
    private open inner class ComponentSizeChooser(
        private val redSize: Int,
        private val greenSize: Int,
        private val blueSize: Int,
        private val alphaSize: Int,
        private val depthSize: Int,
        private val stencilSize: Int
    ) : BaseConfigChooser(
        intArrayOf(
            EGL10.EGL_RED_SIZE,
            redSize,
            EGL10.EGL_GREEN_SIZE,
            greenSize,
            EGL10.EGL_BLUE_SIZE,
            blueSize,
            EGL10.EGL_ALPHA_SIZE,
            alphaSize,
            EGL10.EGL_DEPTH_SIZE,
            depthSize,
            EGL10.EGL_STENCIL_SIZE,
            stencilSize,
            EGL10.EGL_NONE
        )
    ) {
        /**
         * A value.
         */
        private val value = IntArray(1)

        /**
         * Chooses a configuration from the list of configurations.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param configs the list of configurations.
         * @return the chosen configuration.
         */
        override fun chooseConfig(
            egl: EGL10?,
            display: EGLDisplay?,
            configs: Array<EGLConfig?>
        ): EGLConfig? {
            for (config in configs) {
                val d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE)
                val s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE)
                if (d >= depthSize && s >= stencilSize) {
                    val r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE)
                    val g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE)
                    val b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE)
                    val a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE)
                    if (r == redSize && g == greenSize && b == blueSize && a == alphaSize) {
                        return config
                    }
                }
            }
            return null
        }

        /**
         * Finds a configuration attribute.
         *
         * @param egl the EGL10 for the current display.
         * @param display the current display.
         * @param config the current configuration.
         * @param attribute the attribute to find.
         */
        private fun findConfigAttrib(
            egl: EGL10?,
            display: EGLDisplay?,
            config: EGLConfig?,
            attribute: Int,
        ): Int {
            return if (egl?.eglGetConfigAttrib(display, config, attribute, value) == true) {
                value[0]
            } else {
                0
            }
        }
    }

    /**
     * This class will chose an RGB_888 surface with or without a depth buffer.
     */
    private inner class SimpleEGLConfigChooser(withDepthBuffer: Boolean) :
        ComponentSizeChooser(8, 8, 8, 0, if (withDepthBuffer) 16 else 0, 0)

    /**
     * An EGL helper class.
     *
     * @param glSurfaceViewWeakRef the weak reference to the GLTextureView.
     */
    private class EglHelper(private val glSurfaceViewWeakRef: WeakReference<GLTextureView>) {

        /**
         * The EGL10.
         */
        private var egl: EGL10? = null

        /**
         * The EGL display.
         */
        private var eglDisplay: EGLDisplay? = null

        /**
         * The EGL surface.
         */
        private var eglSurface: EGLSurface? = null

        /**
         * The EGL context.
         */
        private var eglContext: EGLContext? = null

        /**
         * The EGL configuration.
         */
        var eglConfig: EGLConfig? = null
            private set

        /**
         * Initialize EGL for a given configuration spec.
         */
        fun start() {
            if (LOG_EGL) {
                Log.w("EglHelper", "start() tid=" + Thread.currentThread().id)
            }

            // Get an EGL instance
            egl = EGLContext.getEGL() as EGL10

            // Get tto the default display
            eglDisplay = egl?.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

            if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw RuntimeException("eglGetDisplay failed")
            }

            // We can now initialize EGL for that display
            val version = IntArray(2)
            if (egl?.eglInitialize(eglDisplay, version) != true) {
                throw RuntimeException("eglInitialize failed")
            }
            val view = glSurfaceViewWeakRef.get()
            if (view == null) {
                eglConfig = null
                eglContext = null
            } else {
                eglConfig = view.eglConfigChooser?.chooseConfig(egl, eglDisplay)

                // Create an EGL context. We want to do this as rarely as we can, because an
                // EGL context is a somewhat heavy object.
                eglContext = view.eglContextFactory?.createContext(egl, eglDisplay, eglConfig)
            }
            if (eglContext == null || eglContext == EGL10.EGL_NO_CONTEXT) {
                eglContext = null
                throwEglException("createContext", egl?.eglGetError())
            }
            if (LOG_EGL) {
                Log.w(
                    "EglHelper",
                    "createContext " + eglContext + " tid=" + Thread.currentThread().id
                )
            }

            eglSurface = null
        }

        /**
         * Create an egl surface for the current SurfaceHolder surface. If a surface already exists,
         * destroy it before creating the new surface.
         *
         * @return true if the surface was created successfully.
         */
        fun createSurface(): Boolean {
            if (LOG_EGL) {
                Log.w("EglHelper", "createSurface() tid=" + Thread.currentThread().id)
            }

            // check preconditions
            if (egl == null) {
                throw RuntimeException("egl not initialized")
            }
            if (eglDisplay == null) {
                throw RuntimeException("eglDisplay not initialized")
            }
            if (eglConfig == null) {
                throw RuntimeException("eglConfig not initialized")
            }

            // The window size has changed, so we need to create a new surface
            destroySurfaceImp()

            // Create an EGL surface we can render into
            val view = glSurfaceViewWeakRef.get()
            eglSurface = view?.eglWindowSurfaceFactory?.createWindowSurface(
                egl,
                eglDisplay,
                eglConfig,
                view.surfaceTexture
            )

            if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
                val error = egl?.eglGetError()
                if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.")
                }
                return false
            }

            // Before we can issue GL commands, we need to make sure the context is current and
            // bound to a surface.
            if (egl?.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) != true) {
                // Could not make the context current, probably because the underlying SurfaceView
                // surface has been destroyed.
                logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", egl?.eglGetError())
                return false
            }

            return true
        }

        /**
         * Creates a GL object for the current EGL context.
         */
        fun createGL(): GL? {
            var gl = eglContext?.gl
            val view = glSurfaceViewWeakRef.get()
            if (view != null) {
                if (view.glWrapper != null) {
                    gl = view.glWrapper?.wrap(gl)
                }

                if ((view.debugFlags and (DEBUG_CHECK_GL_ERROR or DEBUG_LOG_GL_CALLS)) != 0) {
                    var configFlags = 0
                    var log: Writer? = null
                    if ((view.debugFlags and DEBUG_CHECK_GL_ERROR) != 0) {
                        configFlags = configFlags or GLDebugHelper.CONFIG_CHECK_GL_ERROR
                    }
                    if ((view.debugFlags and DEBUG_LOG_GL_CALLS) != 0) {
                        log = LogWriter()
                    }
                    gl = GLDebugHelper.wrap(gl, configFlags, log)
                }
            }
            return gl
        }

        /**
         * Display the current render surface.
         *
         * @return the EGL error code from eglSwapBuffers.
         */
        fun swap(): Int? {
            if (egl?.eglSwapBuffers(eglDisplay, eglSurface) != true) {
                return egl?.eglGetError()
            }
            return EGL10.EGL_SUCCESS
        }

        /**
         * Destroy the current surface.
         */
        fun destroySurface() {
            if (LOG_EGL) {
                Log.w("EglHelper", "destroySurface() tid=" + Thread.currentThread().id)
            }
            destroySurfaceImp()
        }

        /**
         * Finish the egl context.
         */
        fun finish() {
            if (LOG_EGL) {
                Log.w("EglHelper", "finish() tid=" + Thread.currentThread().id)
            }
            if (eglContext != null) {
                val view = glSurfaceViewWeakRef.get()
                view?.eglContextFactory?.destroyContext(egl, eglDisplay, eglContext)
                eglContext = null
            }
            if (eglDisplay != null) {
                egl?.eglTerminate(eglDisplay)
                eglDisplay = null
            }
        }

        /**
         * Terminate the egl context.
         */
        private fun destroySurfaceImp() {
            if (eglSurface != null && eglSurface != EGL10.EGL_NO_SURFACE) {
                egl?.eglMakeCurrent(
                    eglDisplay,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT
                )
                val view = glSurfaceViewWeakRef.get()
                view?.eglWindowSurfaceFactory?.destroySurface(egl, eglDisplay, eglSurface)
                eglSurface = null
            }
        }

        companion object {
            /**
             * Throws an exception with the given function and error.
             *
             * @param function the function.
             * @param error the error.
             */
            fun throwEglException(function: String, error: Int?) {
                val message = formatEglError(function, error)
                if (LOG_THREADS) {
                    Log.e(
                        "EglHelper",
                        "throwEglException tid=" + Thread.currentThread().id + " " + message
                    )
                }
                throw RuntimeException(message)
            }

            /**
             * Logs an EGL error as a warning.
             *
             * @param tag the tag.
             * @param function the function.
             * @param error the error.
             */
            fun logEglErrorAsWarning(tag: String, function: String, error: Int?) {
                Log.w(tag, formatEglError(function, error))
            }

            /**
             * Formats an EGL error.
             *
             * @param function the function.
             * @param error the error.
             * @return the formatted error.
             */
            fun formatEglError(function: String, error: Int?): String {
                return function + " failed" + if (error != null) ": $error" else ""
            }
        }
    }

    /**
     * Thread in charge of running all OpenGL operations.
     *
     * @property glSurfaceViewWeakRef set once at thread construction time. Nulled out when the
     * parent view is garbage called. This weak reference allows the GLTextureView to be garbage
     * collected while the GLThread is still alive.
     * @property glThreadManager the manager for the GLThread. This is used to communicate with the
     * the parent GLTextureView.
     */
    private class GLThread(
        private val glSurfaceViewWeakRef: WeakReference<GLTextureView>,
        private val glThreadManager: GLThreadManager
    ) : Thread() {

        // Once the thread is started, all accesses to the following member variables are protected
        // by the glThreadManager monitor

        /**
         * Indicates if the thread has exited.
         */
        var exited = false

        /**
         * Indicates if the thread should exit.
         */
        private var shouldExit = false

        /**
         * Indicates if there is a thread pending to be paused.
         */
        private var requestPaused = false

        /**
         * Indicates if the thread is paused.
         */
        private var paused = false

        /**
         * Indicates if the surface is ready.
         */
        private var hasSurface = false

        /**
         * Indicates if the surface is bad.
         */
        private var surfaceIsBad = false

        /**
         * Indicates if the thread is waiting for the surface.
         */
        private var waitingForSurface = false

        /**
         * Indicates if the thread has an EGL context.
         */
        private var haveEglContext = false

        /**
         * Indicates if the thread has an EGL surface.
         */
        private var haveEglSurface = false

        /**
         * Indicates if the thread should release the EGL context.
         */
        private var shouldReleaseEglContext = false

        /**
         * Width of the surface in pixels.
         */
        private var width = 0

        /**
         * Height of the surface in pixels.
         */
        private var height = 0

        /**
         * The render mode.
         */
        private var _renderMode = RENDER_MODE_CONTINUOUSLY

        /**
         * Indicates if a render is requested.
         */
        private var requestRender = true

        /**
         * Indicates if the render is complete.
         */
        private var renderComplete = false

        /**
         * The event queue. This is used to queue events to be run on the GL rendering thread.
         */
        private var eventQueue = ArrayList<Runnable>()

        /**
         * Indicates if the size has changed.
         */
        private var sizeChanged = true

        // End of member variables protected by the glThreadManager monitor

        /**
         * The EGL helper.
         */
        private var eglHelper: EglHelper? = null

        /**
         * Executes the guarded run.
         */
        override fun run() {
            name = "GLThread $id"
            if (LOG_THREADS) {
                Log.i("GLThread", "starting tid=$id")
            }

            try {
                guardedRun()
            } catch (_: InterruptedException) {
                // fall through and exit normally
            } finally {
                glThreadManager.threadExiting(this)
            }
        }

        /**
         * Indicates if the thread is able to draw.
         *
         * @return true if the thread is able to draw, false otherwise.
         */
        fun ableToDraw(): Boolean {
            return haveEglContext && haveEglSurface && readyToDraw()
        }

        /**
         * Indicates if the thread is ready to draw.
         *
         * @return true if the thread is ready to draw, false otherwise.
         */
        fun readyToDraw(): Boolean {
            return !paused && hasSurface && !surfaceIsBad && width > 0 && height > 0 &&
                    (requestRender || _renderMode == RENDER_MODE_CONTINUOUSLY)
        }

        /**
         * Gets the render mode.
         *
         * @return the render mode.
         */
        var renderMode: Int
            get() {
                glThreadManager.lock.withLock {
                    return _renderMode
                }
            }
            set(value) {
                if (value !in RENDER_MODE_WHEN_DIRTY..RENDER_MODE_CONTINUOUSLY) {
                    throw IllegalArgumentException("renderMode")
                }
                glThreadManager.lock.withLock {
                    _renderMode = value
                    glThreadManager.condition.signalAll()
                }
            }

        /**
         * Requests a render.
         */
        fun requestRender() {
            glThreadManager.lock.withLock {
                requestRender = true
                glThreadManager.condition.signalAll()
            }
        }

        /**
         * Called when the surface is created.
         */
        fun surfaceCreated() {
            glThreadManager.lock.withLock {
                if (LOG_THREADS) {
                    Log.i("GLThread", "surfaceCreated tid=$id")
                }
                hasSurface = true
                glThreadManager.condition.signalAll()
                while (waitingForSurface && !exited) {
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Called when the surface is destroyed.
         */
        fun surfaceDestroyed() {
            glThreadManager.lock.withLock {
                if (LOG_THREADS) {
                    Log.i("GLThread", "surfaceDestroyed tid=$id")
                }
                hasSurface = false
                glThreadManager.condition.signalAll()
                while (!waitingForSurface && !exited) {
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Called when the thread is paused.
         */
        fun onPause() {
            glThreadManager.lock.withLock {
                if (LOG_PAUSE_RESUME) {
                    Log.i("GLThread", "onPause tid=$id")
                }
                requestPaused = true
                glThreadManager.condition.signalAll()
                while (!exited && !paused) {
                    if (LOG_PAUSE_RESUME) {
                        Log.i(MAIN_THREAD, "onPause waiting for paused.")
                    }
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Called when the thread is resumed.
         */
        fun onResume() {
            glThreadManager.lock.withLock {
                if (LOG_PAUSE_RESUME) {
                    Log.i("GLThread", "onResume tid=$id")
                }
                requestPaused = false
                requestRender = true
                renderComplete = false
                glThreadManager.condition.signalAll()
                while (!exited && paused && !renderComplete) {
                    if (LOG_PAUSE_RESUME) {
                        Log.i(MAIN_THREAD, "onResume waiting for !paused")
                    }
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Called when the window is resized.
         *
         * @param w the new width of the window.
         * @param h the new height of the window.
         */
        fun onWindowResize(w: Int, h: Int) {
            glThreadManager.lock.withLock {
                width = w
                height = h
                sizeChanged = true
                requestRender = true
                renderComplete = false
                glThreadManager.condition.signalAll()

                // Wait for thread to react to resize and render a frame
                while (!exited && !paused && !renderComplete && ableToDraw()) {
                    if (LOG_SURFACE) {
                        Log.i(
                            MAIN_THREAD,
                            "onWindowResize waiting for render complete from tid=$id"
                        )
                    }
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Requests the thread to exit and waits for it to finish.
         */
        fun requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed
            // deadlock!
            glThreadManager.lock.withLock {
                shouldExit = true
                glThreadManager.condition.signalAll()
                while (!exited) {
                    try {
                        glThreadManager.condition.await()
                    } catch (_: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Requests the thread to release the EGL context.
         */
        fun requestReleaseEglContextLocked() {
            shouldReleaseEglContext = true
            glThreadManager.condition.signalAll()
        }

        /**
         * Queues an "event" to be run on the GL rendering thread.
         *
         * @param r the runnable to be run on the GL rendering thread.
         */
        fun queueEvent(r: Runnable) {
            glThreadManager.lock.withLock {
                eventQueue.add(r)
                glThreadManager.condition.signalAll()
            }
        }

        /**
         * Stops the EGL surface.
         * This private method should only be called inside a glThreadManager.lock.withLock{ }
         */
        private fun stopEglSurfaceLocked() {
            if (haveEglSurface) {
                haveEglSurface = false
                eglHelper?.destroySurface()
            }
        }

        /**
         * Stops the EGL context.
         * This private method should only be called inside a glThreadManager.lock.withLock{ }
         */
        private fun stopEglContextLocked() {
            if (haveEglContext) {
                eglHelper?.finish()
                haveEglContext = false
                glThreadManager.releaseEglContextLocked(this)
            }
        }

        /**
         * Executes the guarded run.
         */
        private fun guardedRun() {
            eglHelper = EglHelper(glSurfaceViewWeakRef)
            haveEglContext = false
            haveEglSurface = false
            try {
                var gl: GL10? = null
                var createEglContext = false
                var createEglSurface = false
                var createGlInterface = false
                var lostEglContext = false
                var sizeChanged = false
                var wantRenderNotification = false
                var doRenderNotification = false
                var askedToReleaseEglContext = false
                var w = 0
                var h = 0
                var event: Runnable? = null

                while (true) {
                    glThreadManager.lock.withLock {
                        while (true) {
                            if (shouldExit) {
                                return
                            }

                            if (eventQueue.isNotEmpty()) {
                                event = eventQueue.removeAt(0)
                                break
                            }

                            // Update the pause state
                            var pausing = false
                            if (paused != requestPaused) {
                                pausing = requestPaused
                                paused = requestPaused
                                glThreadManager.condition.signalAll()
                                if (LOG_PAUSE_RESUME) {
                                    Log.i("GLThread", "paused is now $paused tid=$id")
                                }
                            }

                            // Do we need to give up the EGL context?
                            if (shouldReleaseEglContext) {
                                if (LOG_SURFACE) {
                                    Log.i(
                                        "GLThread",
                                        "releasing EGL context because asked to tid=$id"
                                    )
                                }
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                shouldReleaseEglContext = false
                                askedToReleaseEglContext = true
                            }

                            // Have we lost the EGL context?
                            if (lostEglContext) {
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                lostEglContext = false
                            }

                            // When pausing, release the EGL surface:
                            if (pausing && haveEglSurface) {
                                if (LOG_SURFACE) {
                                    Log.i(
                                        "GLThread",
                                        "releasing EGL surface because paused tid=$id"
                                    )
                                }
                                stopEglSurfaceLocked()
                            }

                            // When pausing, optionally release the EGL Context:
                            if (pausing && haveEglContext) {
                                val view = glSurfaceViewWeakRef.get()
                                val preserveEglContextOnPause =
                                    view?.preserveEGLContextOnPause == true
                                if (!preserveEglContextOnPause ||
                                    glThreadManager.shouldReleaseEGLContextWhenPausing()
                                ) {
                                    stopEglContextLocked()
                                    if (LOG_SURFACE) {
                                        Log.i(
                                            "GLThread",
                                            "releasing EGL context because paused tid=$id"
                                        )
                                    }
                                }
                            }

                            // When pausing, optionally terminate EGL:
                            if (pausing && glThreadManager.shouldTerminateEGLWhenPausing()) {
                                eglHelper?.finish()
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "terminating EGL because paused tid=$id")
                                }
                            }

                            // Have we lost the SurfaceView surface?
                            if (!hasSurface && !waitingForSurface) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "noticed surfaceView surface lost tid=$id")
                                }
                                if (haveEglSurface) {
                                    stopEglSurfaceLocked()
                                }
                                waitingForSurface = true
                                surfaceIsBad = false
                                glThreadManager.condition.signalAll()
                            }

                            // Have we acquired the surface view surface?
                            if (hasSurface && waitingForSurface) {
                                if (LOG_SURFACE) {
                                    Log.i(
                                        "GLThread",
                                        "noticed surfaceView surface acquired tid=$id"
                                    )
                                }
                                waitingForSurface = false
                                glThreadManager.condition.signalAll()
                            }

                            if (doRenderNotification) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "sending render notification tid=$id")
                                }
                                wantRenderNotification = false
                                doRenderNotification = false
                                renderComplete = true
                                glThreadManager.condition.signalAll()
                            }

                            // Ready to draw?
                            if (readyToDraw()) {

                                // If we don't have an EGL context, try to acquire one.
                                if (!haveEglContext) {
                                    if (askedToReleaseEglContext) {
                                        askedToReleaseEglContext = false
                                    } else if (glThreadManager.tryAcquireEglContextLocked(this)) {
                                        try {
                                            eglHelper?.start()
                                        } catch (e: RuntimeException) {
                                            glThreadManager.releaseEglContextLocked(this)
                                            throw e
                                        }
                                        haveEglContext = true
                                        createEglContext = true

                                        glThreadManager.condition.signalAll()
                                    }
                                }

                                if (haveEglContext && !haveEglSurface) {
                                    haveEglSurface = true
                                    createEglSurface = true
                                    createGlInterface = true
                                    sizeChanged = true
                                }

                                if (haveEglSurface) {
                                    if (this.sizeChanged) {
                                        sizeChanged = true
                                        w = width
                                        h = height
                                        wantRenderNotification = true
                                        if (LOG_SURFACE) {
                                            Log.i(
                                                "GLThread",
                                                "noticing that we want render notification tid=$id"
                                            )
                                        }

                                        // Destroy and recreate the EGL surface.
                                        createEglSurface = true

                                        this.sizeChanged = false
                                    }
                                    requestRender = false
                                    glThreadManager.condition.signalAll()
                                    break
                                }
                            }

                            // By design, this is the only place in GLThread thread where we wait().
                            if (LOG_THREADS) {
                                Log.i(
                                    "GLThread",
                                    "waiting tid=$id " +
                                            "haveEglContext: $haveEglContext " +
                                            "haveEglSurface: $haveEglSurface " +
                                            "paused: $paused" +
                                            "hasSurface: $hasSurface" +
                                            "surfaceIsBad: $surfaceIsBad" +
                                            "waitingForSurface: $waitingForSurface" +
                                            "width: $width" +
                                            "height: $height" +
                                            "requestRender: $requestRender" +
                                            "renderMode: $_renderMode"
                                )
                            }
                            glThreadManager.condition.await()
                        }
                    }

                    if (event != null) {
                        event.run()
                        event = null
                        continue
                    }

                    if (createEglSurface) {
                        if (LOG_SURFACE) {
                            Log.w("GLThread", "egl createSurface")
                        }
                        if (eglHelper?.createSurface() != true) {
                            glThreadManager.lock.withLock {
                                surfaceIsBad = true
                                glThreadManager.condition.signalAll()
                            }
                            continue
                        }
                        createEglSurface = false
                    }

                    if (createGlInterface) {
                        gl = eglHelper?.createGL() as GL10?

                        glThreadManager.checkGLDriver(gl)
                        createGlInterface = false
                    }

                    if (createEglContext) {
                        if (LOG_RENDERER) {
                            Log.w("GLThread", "onSurfaceCreated")
                        }
                        val view = glSurfaceViewWeakRef.get()
                        view?.renderer?.onSurfaceCreated(gl, eglHelper?.eglConfig)
                        createEglContext = false
                    }

                    if (sizeChanged) {
                        if (LOG_RENDERER) {
                            Log.w("GLThread", "onSurfaceChanged($w, $h)")
                        }
                        val view = glSurfaceViewWeakRef.get()
                        view?.renderer?.onSurfaceChanged(gl, w, h)
                        sizeChanged = false
                    }

                    if (LOG_RENDERER_DRAW_FRAME) {
                        Log.w("GLThread", "onDrawFrame tid=$id")
                    }
                    val view = glSurfaceViewWeakRef.get()
                    view?.renderer?.onDrawFrame(gl)
                    when (val swapError = eglHelper?.swap()) {
                        EGL10.EGL_SUCCESS -> Unit // make no action
                        EGL11.EGL_CONTEXT_LOST -> {
                            if (LOG_SURFACE) {
                                Log.i("GLThread", "egl context lost tid=$id")
                            }
                            lostEglContext = true
                        }
                        else -> {
                            // Other errors typically mean that the current surface is bad,
                            // probably because the SurfaceView surface has been destroyed,
                            // but we haven't been notified yet.
                            // Log the error to help developers understand why rendering stopped.
                            EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError)

                            glThreadManager.lock.withLock {
                                surfaceIsBad = true
                                glThreadManager.condition.signalAll()
                            }
                        }
                    }

                    if (wantRenderNotification) {
                        doRenderNotification = true
                    }
                }
            } finally {
                // clean-up everything...
                glThreadManager.lock.withLock {
                    stopEglSurfaceLocked()
                    stopEglContextLocked()
                }
            }
        }
    }

    /**
     * Writer to log messages.
     */
    private class LogWriter : Writer() {

        /**
         * The builder to store the log message.
         */
        private val builder = StringBuilder()

        /**
         * Closes the writer.
         */
        override fun close() {
            flushBuilder()
        }

        /**
         * Flushes the writer.
         */
        override fun flush() {
            flushBuilder()
        }

        /**
         * Writes a buffer to the writer.
         *
         * @param buf the buffer to write.
         * @param offset the offset in the buffer.
         * @param count the number of characters to write.
         */
        override fun write(buf: CharArray?, offset: Int, count: Int) {
            if (buf == null) {
                return
            }

            for (i in 0 until count) {
                val c = buf[offset + i]
                if (c == '\n') {
                    flushBuilder()
                } else {
                    builder.append(c)
                }
            }
        }

        /**
         * Flushes the builder.
         */
        private fun flushBuilder() {
            if (builder.isNotEmpty()) {
                Log.v("GLTextureView", builder.toString())
                builder.delete(0, builder.length)
            }
        }
    }

    /**
     * Manager for the GLThread.
     */
    private class GLThreadManager {
        /**
         * The lock. This is used to synchronize access to the GLThreadManager.
         */
        val lock = ReentrantLock()

        /**
         * The condition. This is used to communicate with the GLThread.
         */
        val condition: Condition = lock.newCondition()

        /**
         * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides
         * support for hardware-accelerated views, therefore multiple EGL contexts are supported
         * on all Android 3.0+ EGL drivers.
         */
        private var glESDriverCheckComplete = false

        /**
         * Indicates if multiple GLES contexts are allowed.
         */
        private var multipleGLESContextsAllowed = false

        /**
         * Indicates if limited GLES contexts are allowed.
         */
        private var limitedGLESContexts = false

        /**
         * The EGL owner.
         */
        private var eglOwner: GLThread? = null

        /**
         * Exits the thread.
         *
         * @param thread the thread.
         */
        fun threadExiting(thread: GLThread) {
            lock.withLock {
                if (LOG_THREADS) {
                    Log.i("GLThread", "exiting tid=" + thread.id)
                }
                thread.exited = true
                if (eglOwner == thread) {
                    eglOwner = null
                }
                condition.signalAll()
            }
        }

        /**
         * Tries once to acquire the right to use an EGL context.
         * Does not block.
         * Requires that we are already in the glThreadManager monitor when this is called.
         *
         * @param thread the GL thread.
         * @return true if the right to use an EGL context was acquired.
         */
        fun tryAcquireEglContextLocked(thread: GLThread): Boolean {
            if (eglOwner == thread || eglOwner == null) {
                eglOwner = thread
                condition.signalAll()
                return true
            }
            if (multipleGLESContextsAllowed) {
                return true
            }
            // Notify the owning thread that it should release the context.
            eglOwner?.requestReleaseEglContextLocked()
            return false
        }

        /**
         * Releases the EGL context. Requires that we are already in the glThreadManager monitor
         * when this is called.
         *
         * @param thread the GL thread.
         */
        fun releaseEglContextLocked(thread: GLThread) {
            if (eglOwner == thread) {
                eglOwner = null
            }
            condition.signalAll()
        }

        /**
         * Releases the EGL context when pausing even if the hardware supports multiple EGL
         * contexts. Otherwise the device could run out of EGL contexts.
         *
         * @return true if the EGL context should be released when pausing, false otherwise.
         */
        fun shouldReleaseEGLContextWhenPausing(): Boolean {
            lock.withLock {
                return limitedGLESContexts
            }
        }

        /**
         * Indicates if the EGL should be terminated when pausing.
         *
         * @return true if the EGL should be terminated when pausing, false otherwise.
         */
        fun shouldTerminateEGLWhenPausing(): Boolean {
            lock.withLock {
                return !multipleGLESContextsAllowed
            }
        }

        /**
         * Checks the GL driver.
         *
         * @param gl the GL.
         */
        fun checkGLDriver(gl: GL10?) {
            lock.withLock {
                if (!glESDriverCheckComplete) {
                    val renderer = gl?.glGetString(GL10.GL_RENDERER)
                    multipleGLESContextsAllowed =
                        renderer?.startsWith(RENDERER_PREFIX) != true
                    condition.signalAll()
                    limitedGLESContexts = !multipleGLESContextsAllowed
                    if (LOG_SURFACE) {
                        Log.w(
                            TAG,
                            "checkDriver renderer = \"$renderer\" " +
                                    "multipleContextsAllowed = $multipleGLESContextsAllowed " +
                                    "limitedGLESContexts = $limitedGLESContexts"
                        )
                    }
                    glESDriverCheckComplete = true
                }
            }
        }

        companion object {
            /**
             * Tag for the log.
             */
            const val TAG = "GLThreadManager"

            /**
             * Indicates if the threads are logged.
             */
            const val RENDERER_PREFIX = "Q3Dimension MSM7500 "
        }
    }
}