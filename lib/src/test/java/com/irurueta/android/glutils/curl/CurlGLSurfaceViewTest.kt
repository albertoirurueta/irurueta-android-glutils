package com.irurueta.android.glutils.curl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.irurueta.android.glutils.getPrivateProperty
import com.irurueta.android.glutils.setPrivateProperty
import io.mockk.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import javax.microedition.khronos.opengles.GL10

@RunWith(RobolectricTestRunner::class)
class CurlGLSurfaceViewTest {

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
        assertEquals(CurlGLSurfaceView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)
        assertEquals(
            CurlGLSurfaceView.PAGE_JUMP_ANIMATION_DURATION_MILLIS,
            view.pageJumpDurationTime
        )
        assertEquals(CurlGLSurfaceView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
        assertNull(view.pageProvider)
        assertEquals(CurlGLSurfaceView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)
        assertTrue(view.renderLeftPage)
        assertNull(view.sizeChangedObserver)
        assertNull(view.currentIndexChangedListener)
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)
        assertNull(view.pageClickListener)
        assertEquals(CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH, view.maxCurlSplitsInMesh)
        assertEquals(CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
        assertEquals(
            CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
        assertEquals(CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
        assertEquals(CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertFalse(animate)
        assertNotNull(view.getPrivateProperty("animationSource"))
        val animationStartTime: Long? = view.getPrivateProperty("animationStartTime")
        assertEquals(0L, animationStartTime)
        assertNotNull(view.getPrivateProperty("animationTarget"))
        val animationTargetEvent: Int? = view.getPrivateProperty("animationTargetEvent")
        assertEquals(0, animationTargetEvent)
        assertNotNull(view.getPrivateProperty("curlDir"))
        assertNotNull(view.getPrivateProperty("curlPos"))
        assertNull(view.getPrivateProperty("targetIndex"))
        assertNotNull(view.getPrivateProperty("dragStartPos"))
        val pageBitmapWidth: Int? = view.getPrivateProperty("pageBitmapWidth")
        assertEquals(-1, pageBitmapWidth)
        val pageBitmapHeight: Int? = view.getPrivateProperty("pageBitmapHeight")
        assertEquals(-1, pageBitmapHeight)
        assertNotNull(view.getPrivateProperty("pageCurl"))
        assertNotNull(view.getPrivateProperty("pageLeft"))
        assertNotNull(view.getPrivateProperty("pageRight"))
        assertNull(view.getPrivateProperty("pointerPost"))
        assertNotNull(view.getPrivateProperty("curlRenderer"))
        assertNotNull(view.getPrivateProperty("gestureDetector"))
        assertNull(view.getPrivateProperty("curlAnimator"))
        val scrollX: Float? = view.getPrivateProperty("scrollX")
        assertEquals(0.0f, scrollX)
        val scrollY: Float? = view.getPrivateProperty("scrollY")
        assertEquals(0.0f, scrollY)
        val scrollP: Float? = view.getPrivateProperty("scrollP")
        assertEquals(0.0f, scrollP)
        assertNotNull(view.getPrivateProperty("observer"))
    }

    @Test
    fun observer_onDrawFrameWhenNoCurlRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer1: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer1)

        // set null curl renderer
        view.setPrivateProperty("curlRenderer", null)

        val curlRenderer2: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        assertNull(curlRenderer2)

        val notifySmoothChange1: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange1)
        assertTrue(notifySmoothChange1)

        observer.onDrawFrame()

        // check
        val notifySmoothChange2: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange2)
        assertTrue(notifySmoothChange2)
    }

    @Test
    fun observer_onDrawFrameWhenNotAnimateAndNotifySmoothChange() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertFalse(animate)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertTrue(notifySmoothChange)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }
    }

    @LooperMode(LooperMode.Mode.LEGACY)
    @Test
    fun observer_onDrawFrameWhenNotAnimateAndNotNotifySmoothChange() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener =
            mockk<CurlGLSurfaceView.CurrentIndexChangedListener>(relaxUnitFun = true)
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertFalse(animate)

        val notifySmoothChange1: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange1)
        assertTrue(notifySmoothChange1)

        view.setPrivateProperty("notifySmoothChange", false)

        val notifySmoothChange2: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange2)
        assertFalse(notifySmoothChange2)

        observer.onDrawFrame()

        verify { currentIndexChangedListener.onCurrentIndexChanged(view, 0) }

        val notifySmoothChange3: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange3)
        assertTrue(notifySmoothChange3)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedButNoCurl() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlRightAndNoPreviousCurlState() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_RIGHT)

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlRightAndNoPageCurl() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_RIGHT)

        // set page curl to null
        assertNotNull(view.getPrivateProperty("pageCurl"))
        view.setPrivateProperty("pageCurl", null)
        assertNull(view.getPrivateProperty("pageCurl"))

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        // set curl state and current index, and check they are not modified
        view.setPrivateProperty("curlState", CurlTextureView.CURL_RIGHT)
        view.setPrivateProperty("currentIndex", 1)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_RIGHT, view.curlState)
        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlRightAndNoPageRight() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_RIGHT)

        // set page right to null
        assertNotNull(view.getPrivateProperty("pageRight"))
        view.setPrivateProperty("pageRight", null)
        assertNull(view.getPrivateProperty("pageRight"))

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        // set curl state and current index, and check they are not modified
        view.setPrivateProperty("curlState", CurlTextureView.CURL_RIGHT)
        view.setPrivateProperty("currentIndex", 1)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_RIGHT, view.curlState)
        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlRightPreviousLeftCurlStateAndNoTargetIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_RIGHT)

        // set previous curl state
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        view.setPrivateProperty("curlState", CurlTextureView.CURL_LEFT)
        assertEquals(0, view.currentIndex)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(-1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlRightPreviousLeftCurlStateAndTargetIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_RIGHT)

        // set previous curl state
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        view.setPrivateProperty("curlState", CurlTextureView.CURL_LEFT)
        assertEquals(0, view.currentIndex)

        // set targetIndex
        assertNull(view.getPrivateProperty("targetIndex"))

        view.setPrivateProperty("targetIndex", 1)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftAndNoPreviousCurlState() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftAndNoPageCurl() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        // set page curl to null
        assertNotNull(view.getPrivateProperty("pageCurl"))
        view.setPrivateProperty("pageCurl", null)
        assertNull(view.getPrivateProperty("pageCurl"))

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        // set curl state and current index, and check they are not modified
        view.setPrivateProperty("curlState", CurlTextureView.CURL_LEFT)
        view.setPrivateProperty("currentIndex", 1)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_LEFT, view.curlState)
        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftAndNoPageLeft() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        // set page left to null
        assertNotNull(view.getPrivateProperty("pageLeft"))
        view.setPrivateProperty("pageLeft", null)
        assertNull(view.getPrivateProperty("pageLeft"))

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        // set curl state and current index, and check they are not modified
        view.setPrivateProperty("curlState", CurlTextureView.CURL_LEFT)
        view.setPrivateProperty("currentIndex", 1)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_LEFT, view.curlState)
        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftNoPreviousCurlStateAndNotRenderLeftPage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)

        // set not renderLeftPage
        val renderLeftPage1: Boolean? = view.getPrivateProperty("renderLeftPage")
        requireNotNull(renderLeftPage1)
        assertTrue(renderLeftPage1)

        view.setPrivateProperty("renderLeftPage", false)

        val renderLeftPage2: Boolean? = view.getPrivateProperty("renderLeftPage")
        requireNotNull(renderLeftPage2)
        assertFalse(renderLeftPage2)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftPreviousLeftCurlStateAndNoTargetIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        // set previous curl state
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        view.setPrivateProperty("curlState", CurlTextureView.CURL_RIGHT)
        assertEquals(0, view.currentIndex)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(1, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAnimationFinishedCurlLeftPreviousLeftCurlStateAndTargetIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        val animationTargetEvent1: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent1)
        assertEquals(0, animationTargetEvent1)

        // set animation target event
        view.setPrivateProperty("animationTargetEvent", CurlTextureView.SET_CURL_TO_LEFT)

        // set previous curl state
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        view.setPrivateProperty("curlState", CurlTextureView.CURL_RIGHT)
        assertEquals(0, view.currentIndex)

        // set targetIndex
        assertNull(view.getPrivateProperty("targetIndex"))

        view.setPrivateProperty("targetIndex", 2)

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }

        assertEquals(CurlTextureView.CURL_NONE, view.curlState)

        val animate3: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate3)
        assertFalse(animate3)

        val notifySmoothChange: Boolean? = view.getPrivateProperty("notifySmoothChange")
        requireNotNull(notifySmoothChange)
        assertFalse(notifySmoothChange)

        assertEquals(2, view.currentIndex)
    }

    @Test
    fun observer_onDrawFrameWhenAnimatedAndAnimationFinished() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val currentIndexChangedListener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = currentIndexChangedListener

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val animate1: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate1)
        assertFalse(animate1)

        // set animate
        view.setPrivateProperty("animate", true)

        val animate2: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate2)
        assertTrue(animate2)

        // set animation start time
        view.setPrivateProperty("animationStartTime", System.nanoTime())
        view.animationDurationTime = ANIMATION_DURATION_MILLIS

        observer.onDrawFrame()

        verify { currentIndexChangedListener wasNot Called }
    }

    @Test
    fun observer_onPageSizeChanged() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        // check default values
        val pageBitmapWidth1: Int? = view.getPrivateProperty("pageBitmapWidth")
        requireNotNull(pageBitmapWidth1)
        assertEquals(-1, pageBitmapWidth1)

        val pageBitmapHeight1: Int? = view.getPrivateProperty("pageBitmapHeight")
        requireNotNull(pageBitmapHeight1)
        assertEquals(-1, pageBitmapHeight1)

        observer.onPageSizeChanged(WIDTH, HEIGHT)

        // check
        val pageBitmapWidth2: Int? = view.getPrivateProperty("pageBitmapWidth")
        requireNotNull(pageBitmapWidth2)
        assertEquals(WIDTH, pageBitmapWidth2)

        val pageBitmapHeight2: Int? = view.getPrivateProperty("pageBitmapHeight")
        requireNotNull(pageBitmapHeight2)
        assertEquals(HEIGHT, pageBitmapHeight2)
    }

    @Test
    fun observer_onSurfaceCreated() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        // set spies
        val pageLeft: CurlMesh? = view.getPrivateProperty("pageLeft")
        requireNotNull(pageLeft)
        val pageRight: CurlMesh? = view.getPrivateProperty("pageRight")
        requireNotNull(pageRight)
        val pageCurl: CurlMesh? = view.getPrivateProperty("pageCurl")
        requireNotNull(pageCurl)

        val pageLeftSpy = spyk(pageLeft)
        view.setPrivateProperty("pageLeft", pageLeftSpy)
        val pageRightSpy = spyk(pageRight)
        view.setPrivateProperty("pageRight", pageRightSpy)
        val pageCurlSpy = spyk(pageCurl)
        view.setPrivateProperty("pageCurl", pageCurlSpy)

        observer.onSurfaceCreated()

        // check
        verify(exactly = 1) { pageLeftSpy.resetTexture() }
        verify(exactly = 1) { pageRightSpy.resetTexture() }
        verify(exactly = 1) { pageCurlSpy.resetTexture() }
    }

    @Test
    fun observer_onSurfaceCreatedWhenNoPages() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val observer: CurlRenderer.Observer? = view.getPrivateProperty("observer")
        requireNotNull(observer)

        // set null pages
        view.setPrivateProperty("pageLeft", null)
        view.setPrivateProperty("pageRight", null)
        view.setPrivateProperty("pageCurl", null)

        observer.onSurfaceCreated()

        // check
        assertNull(view.getPrivateProperty("pageLeft"))
        assertNull(view.getPrivateProperty("pageRight"))
        assertNull(view.getPrivateProperty("pageCurl"))
    }

    @Test
    fun allowLastPageCurl_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)

        // set new value
        view.allowLastPageCurl = !CurlGLSurfaceView.ALLOW_LAST_PAGE_CURL

        // check
        assertEquals(!CurlGLSurfaceView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
    }

    @Test
    fun animationDurationTime_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)

        // set new value
        view.animationDurationTime = 500

        // check
        assertEquals(500, view.animationDurationTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun animationDurationTie_whenNegative_throwsException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.animationDurationTime = -1
    }

    @Test
    fun pageJumpDurationTime_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.PAGE_JUMP_ANIMATION_DURATION_MILLIS,
            view.pageJumpDurationTime
        )

        // set new value
        view.pageJumpDurationTime = 300L

        // check
        assertEquals(300L, view.pageJumpDurationTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun pageJumpDurationTime_whenNegative_throwsException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.pageJumpDurationTime = -1
    }

    @Test
    fun enableTouchPressure_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)

        // set new value
        view.enableTouchPressure = !CurlGLSurfaceView.TOUCH_PRESSURE_ENABLED

        // check
        assertEquals(!CurlGLSurfaceView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
    }

    @Test
    fun pageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertNull(view.pageProvider)

        // set new value
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // check
        assertSame(pageProvider, view.pageProvider)
        assertEquals(0, view.currentIndex)
    }

    @Test
    fun maxCurlSplitsInMesh_whenNoPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH, view.maxCurlSplitsInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.maxCurlSplitsInMesh = 20

        // check
        assertEquals(20, view.maxCurlSplitsInMesh)
    }

    @Test
    fun maxCurlSplitsInMesh_whenNoPageProviderAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH, view.maxCurlSplitsInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.maxCurlSplitsInMesh = CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH

        // check
        assertEquals(CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH, view.maxCurlSplitsInMesh)
    }

    @Test
    fun maxCurlSplitsInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.MAX_CURL_SPLITS_IN_MESH, view.maxCurlSplitsInMesh)
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.maxCurlSplitsInMesh = 20 }
    }

    @Test
    fun drawCurlPositionInMesh_whenNoPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawCurlPositionInMesh = !CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH

        // check
        assertEquals(!CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
    }

    @Test
    fun drawCurlPositionInMesh_whenNoPageProviderAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawCurlPositionInMesh = CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH

        // check
        assertEquals(CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
    }

    @Test
    fun drawCurlPositionInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_CURL_POSITION_IN_MESH, view.drawCurlPositionInMesh)
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.drawCurlPositionInMesh = true }
    }

    @Test
    fun drawPolygonOutlinesInMesh_whenNoPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
        assertNull(view.pageProvider)

        // set new value
        view.drawPolygonOutlinesInMesh = !CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH

        // check
        assertEquals(
            !CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
    }

    @Test
    fun drawPolygonOutlinesInMesh_whenNoPageProviderAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
        assertNull(view.pageProvider)

        // set new value
        view.drawPolygonOutlinesInMesh = CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH

        // check
        assertEquals(
            CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
    }

    @Test
    fun drawPolygonOutlinesInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DRAW_POLYGON_OUTLINES_IN_MESH,
            view.drawPolygonOutlinesInMesh
        )
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.drawPolygonOutlinesInMesh = true }
    }

    @Test
    fun drawShadowInMesh_whenNoPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawShadowInMesh = !CurlGLSurfaceView.DRAW_SHADOW_IN_MESH

        // check
        assertEquals(!CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
    }

    @Test
    fun drawShadowInMesh_whenNoPageProviderAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawShadowInMesh = CurlGLSurfaceView.DRAW_SHADOW_IN_MESH

        // check
        assertEquals(CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
    }

    @Test
    fun drawShadowInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_SHADOW_IN_MESH, view.drawShadowInMesh)
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.drawShadowInMesh = false }
    }

    @Test
    fun drawTextureInMesh_whenNoPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawTextureInMesh = !CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH

        // check
        assertEquals(!CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
    }

    @Test
    fun drawTextureInMesh_whenNoPageProviderAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
        assertNull(view.pageProvider)

        // set new value
        view.drawTextureInMesh = CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH

        // check
        assertEquals(CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
    }

    @Test
    fun drawTextureInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.DRAW_TEXTURE_IN_MESH, view.drawTextureInMesh)
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.drawTextureInMesh = false }
    }

    @Test
    fun shadowInnerColorInMesh_whenValid_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(1.0f, 0.0f, 0.0f, 0.5f)
        view.shadowInnerColorInMesh = color

        // check
        assertSame(color, view.shadowInnerColorInMesh)
    }

    @Test
    fun shadowInnerColorInMesh_whenValidAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        view.shadowInnerColorInMesh = CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH

        // check
        assertSame(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH, view.shadowInnerColorInMesh)
    }

    @Test
    fun shadowInnerColorInMesh_whenInvalidLength_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(1.0f, 0.5f, 0.25f)
        assertThrows(IllegalArgumentException::class.java) {
            view.shadowInnerColorInMesh = color
        }
    }

    @Test
    fun shadowInnerColorInMesh_whenInvalidValue_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(2.0f, 0.0f, 0.0f, 0.5f)
        assertThrows(IllegalArgumentException::class.java) {
            view.shadowInnerColorInMesh = color
        }
    }

    @Test
    fun shadowInnerColorInMesh_whenPageProvider_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_INNER_COLOR_IN_MESH.contentEquals(view.shadowInnerColorInMesh))
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        val color = floatArrayOf(1.0f, 0.0f, 0.0f, 0.5f)
        assertThrows(IllegalArgumentException::class.java) { view.shadowInnerColorInMesh = color }
    }

    @Test
    fun shadowOuterColorInMesh_whenValid_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f)
        view.shadowOuterColorInMesh = color

        // check
        assertSame(color, view.shadowOuterColorInMesh)
    }

    @Test
    fun shadowOuterColorInMesh_whenValidAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        view.shadowOuterColorInMesh = CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH

        // check
        assertSame(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH, view.shadowOuterColorInMesh)
    }

    @Test
    fun shadowOuterColorInMesh_whenInvalidLength_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(1.0f, 0.5f, 0.25f)
        assertThrows(IllegalArgumentException::class.java) {
            view.shadowOuterColorInMesh = color
        }
    }

    @Test
    fun shadowOuterColorInMesh_whenInvalidValue_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertNull(view.pageProvider)

        // set new value
        val color = floatArrayOf(2.0f, 0.0f, 0.0f, 0.5f)
        assertThrows(IllegalArgumentException::class.java) {
            view.shadowOuterColorInMesh = color
        }
    }

    @Test
    fun shadowOuterColorInMesh_whenPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertTrue(CurlGLSurfaceView.SHADOW_OUTER_COLOR_IN_MESH.contentEquals(view.shadowOuterColorInMesh))
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        val color = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f)
        assertThrows(IllegalArgumentException::class.java) { view.shadowOuterColorInMesh = color }
    }

    @Test
    fun colorFactorOffsetInMesh_whenValid_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )
        assertNull(view.pageProvider)

        // set new value
        view.colorFactorOffsetInMesh = 0.5f

        assertEquals(0.5f, view.colorFactorOffsetInMesh)
    }

    @Test
    fun colorFactorOffsetInMesh_whenValidAndNoValueChange_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )
        assertNull(view.pageProvider)

        // set new value
        view.colorFactorOffsetInMesh = CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH

        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )
    }

    @Test
    fun colorFactorOffsetInMesh_whenInvalid_throwsIllegalArgumentException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )

        // set new value
        assertThrows(IllegalArgumentException::class.java) {
            view.colorFactorOffsetInMesh = -1.0f
        }
    }

    @Test
    fun colorFactorOffsetInMesh_whenPageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(
            CurlGLSurfaceView.DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH,
            view.colorFactorOffsetInMesh
        )
        assertNull(view.pageProvider)

        // set page provider
        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        view.pageProvider = pageProvider

        // set new value
        assertThrows(IllegalArgumentException::class.java) { view.colorFactorOffsetInMesh = 0.5f }
    }

    @Test
    fun renderLeftPage_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // check
        assertTrue(view.renderLeftPage)

        // set new value
        view.renderLeftPage = false

        // check
        assertFalse(view.renderLeftPage)
    }

    @Test
    fun sizeChangeObserver_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // check
        assertNull(view.sizeChangedObserver)

        // set new value
        val observer = mockk<CurlGLSurfaceView.SizeChangedObserver>()
        view.sizeChangedObserver = observer

        // check
        assertSame(observer, view.sizeChangedObserver)
    }

    @Test
    fun currentIndexChangedListener_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // check
        assertNull(view.currentIndexChangedListener)

        // set new value
        val listener = mockk<CurlGLSurfaceView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = listener

        // check
        assertSame(listener, view.currentIndexChangedListener)
    }

    @Test
    fun viewMode_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // check default value
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)

        // set two pages
        view.viewMode = CurlGLSurfaceView.SHOW_TWO_PAGES

        // check
        assertEquals(CurlGLSurfaceView.SHOW_TWO_PAGES, view.viewMode)

        // set one page
        view.viewMode = CurlGLSurfaceView.SHOW_ONE_PAGE

        // check
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)
    }

    @Test
    fun viewMode_setsExpectedValueWhenNoPageOrRenderer() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.setPrivateProperty("pageLeft", null)
        view.setPrivateProperty("curlRenderer", null)

        // check default value
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)

        // set two pages
        view.viewMode = CurlGLSurfaceView.SHOW_TWO_PAGES

        // check
        assertEquals(CurlGLSurfaceView.SHOW_TWO_PAGES, view.viewMode)

        // set one page
        view.viewMode = CurlGLSurfaceView.SHOW_ONE_PAGE

        // check
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)
    }

    @Test
    fun viewMode_whenNotSupportedValue_setsExpectedValueButMakesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // setup spies
        val pageLeft : CurlMesh? = view.getPrivateProperty("pageLeft")
        requireNotNull(pageLeft)

        val curlRenderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(curlRenderer)

        val pageLeftSpy = spyk(pageLeft)
        view.setPrivateProperty("pageLeft", pageLeftSpy)
        val curlRendererSpy = spyk(curlRenderer)
        view.setPrivateProperty("curlRenderer", curlRendererSpy)

        // check default value
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)

        // set not supported value
        view.viewMode = 0

        // check
        assertEquals(CurlGLSurfaceView.SHOW_ONE_PAGE, view.viewMode)

        verify { pageLeftSpy wasNot Called }
        verify { curlRendererSpy wasNot Called }
    }

    @Test
    fun pageClickListener_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // check default value
        assertNull(view.pageClickListener)

        // set new value
        val listener = mockk<CurlGLSurfaceView.PageClickListener>()
        view.pageClickListener = listener

        // check
        assertSame(listener, view.pageClickListener)
    }

    @Test(expected = NullPointerException::class)
    fun onTouchEvent_whenNoEvent_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.onTouchEvent(null)
    }

    @Test
    fun onTouchEvent_whenActionCancel_handlesTouchUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // set mock for gesture detector
        val gestureDetector = mockk<GestureDetector>()
        every { gestureDetector.onTouchEvent(any()) }.returns(true)
        view.setPrivateProperty("gestureDetector", gestureDetector)

        val event = mockk<MotionEvent>()
        every { event.action }.returns(MotionEvent.ACTION_CANCEL)
        every { event.x }.returns(100.0f)
        every { event.y }.returns(200.0f)
        every { event.pressure }.returns(1.0f)

        assertTrue(view.onTouchEvent(event))

        verify(exactly = 1) { gestureDetector.onTouchEvent(event) }
    }

    @Test
    fun onTouchEvent_whenActionUp_handlesTouchUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // set mock for gesture detector
        val gestureDetector = mockk<GestureDetector>()
        every { gestureDetector.onTouchEvent(any()) }.returns(true)
        view.setPrivateProperty("gestureDetector", gestureDetector)

        val event = mockk<MotionEvent>()
        every { event.action }.returns(MotionEvent.ACTION_UP)
        every { event.x }.returns(100.0f)
        every { event.y }.returns(200.0f)
        every { event.pressure }.returns(1.0f)

        assertTrue(view.onTouchEvent(event))

        verify(exactly = 1) { gestureDetector.onTouchEvent(event) }
    }

    @Test
    fun onTouchEvent_whenOtherAction_callsGestureDetector() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // set mock for gesture detector
        val gestureDetector = mockk<GestureDetector>()
        every { gestureDetector.onTouchEvent(any()) }.returns(true)
        view.setPrivateProperty("gestureDetector", gestureDetector)

        val event = mockk<MotionEvent>()
        every { event.action }.returns(MotionEvent.ACTION_MOVE)

        assertTrue(view.onTouchEvent(event))

        verify(exactly = 1) { gestureDetector.onTouchEvent(event) }
    }

    @Test
    fun onTouchEvent_whenNoGestureDetector_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        // set null gesture detector
        view.setPrivateProperty("gestureDetector", null)

        val event = mockk<MotionEvent>()
        every { event.action }.returns(MotionEvent.ACTION_MOVE)

        assertTrue(view.onTouchEvent(event))
    }

    @Test
    fun setCurrentIndex_whenNoPageProvider_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(0, view.currentIndex)

        view.setCurrentIndex(1)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun setCurrentIndex_whenPageProviderAndNegativeValue_setsZeroCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        view.pageProvider = pageProvider

        view.setCurrentIndex(-1)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun setCurrentIndex_whenPageProviderAndLastPageCurlAllowed_setsExpectedCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        view.pageProvider = pageProvider

        view.setCurrentIndex(2)

        assertEquals(2, view.currentIndex)
    }

    @Test
    fun setCurrentIndex_whenPageProviderAndLastPageCurlNotAllowed_setsExpectedCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.allowLastPageCurl = false

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        view.pageProvider = pageProvider

        view.setCurrentIndex(2)

        assertEquals(1, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenNoPageProvider_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(0, view.currentIndex)

        view.setSmoothCurrentIndex(1)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenPageProviderAndNegativeValue_setsZeroCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        every { pageProvider.updatePage(any(), any(), any(), any(), any()) }
            .answers { call ->
                val page = call.invocation.args[0] as CurlPage
                val width = call.invocation.args[1] as Int
                val height = call.invocation.args[2] as Int

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.setTexture(bitmap, CurlPage.SIDE_BOTH)
            }
        view.pageProvider = pageProvider

        view.setSmoothCurrentIndex(-1)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenPageProviderButNoPageSizeChangeNotified_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        every { pageProvider.updatePage(any(), any(), any(), any(), any()) }
            .answers { call ->
                val page = call.invocation.args[0] as CurlPage
                val width = call.invocation.args[1] as Int
                val height = call.invocation.args[2] as Int

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.setTexture(bitmap, CurlPage.SIDE_BOTH)
            }
        view.pageProvider = pageProvider

        view.setSmoothCurrentIndex(2)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenPageProviderNoPageSizeChangeNotifiedAndGoingToPrevious_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        every { pageProvider.updatePage(any(), any(), any(), any(), any()) }
            .answers { call ->
                val page = call.invocation.args[0] as CurlPage
                val width = call.invocation.args[1] as Int
                val height = call.invocation.args[2] as Int

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.setTexture(bitmap, CurlPage.SIDE_BOTH)
            }
        view.pageProvider = pageProvider

        view.setCurrentIndex(2)

        assertEquals(2, view.currentIndex)

        view.setSmoothCurrentIndex(1)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(2, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenPageProviderButNoPageSizeChangeNotifiedAndLastPageCurlNotAllowed_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = false

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlGLSurfaceView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        every { pageProvider.updatePage(any(), any(), any(), any(), any()) }
            .answers { call ->
                val page = call.invocation.args[0] as CurlPage
                val width = call.invocation.args[1] as Int
                val height = call.invocation.args[2] as Int

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.setTexture(bitmap, CurlPage.SIDE_BOTH)
            }
        view.pageProvider = pageProvider

        view.setSmoothCurrentIndex(2)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun setMargins_setsExpectedMargins() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // check
        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        val margins: RectF? = renderer.getPrivateProperty("margins")
        requireNotNull(margins)
        assertEquals(0.0f, margins.left, 0.0f)
        assertEquals(0.0f, margins.right, 0.0f)
        assertEquals(0.0f, margins.top, 0.0f)
        assertEquals(0.0f, margins.bottom, 0.0f)

        // set margins
        view.setMargins(100, 200, 300, 400)

        // check
        assertEquals(100.0f / WIDTH, margins.left, 0.0f)
        assertEquals(200.0f / HEIGHT, margins.top, 0.0f)
        assertEquals(300.0f / WIDTH, margins.right, 0.0f)
        assertEquals(400.0f / HEIGHT, margins.bottom, 0.0f)
    }

    @Test
    fun setMargins_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.setPrivateProperty("curlRenderer", null)
        assertNull(view.getPrivateProperty("curlRenderer"))

        // set margins
        view.setMargins(100, 200, 300, 400)
    }

    @Test
    fun setProportionalMargins_setsExpectedMargins() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // check
        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        val margins: RectF? = renderer.getPrivateProperty("margins")
        requireNotNull(margins)
        assertEquals(0.0f, margins.left, 0.0f)
        assertEquals(0.0f, margins.right, 0.0f)
        assertEquals(0.0f, margins.top, 0.0f)
        assertEquals(0.0f, margins.bottom, 0.0f)

        // set margins
        view.setProportionalMargins(100.0f, 200.0f, 300.0f, 400.0f)

        // check
        assertEquals(100.0f, margins.left, 0.0f)
        assertEquals(200.0f, margins.top, 0.0f)
        assertEquals(300.0f, margins.right, 0.0f)
        assertEquals(400.0f, margins.bottom, 0.0f)
    }

    @Test
    fun setProportionalMargins_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.setPrivateProperty("curlRenderer", null)
        assertNull(view.getPrivateProperty("curlRenderer"))

        // set margins
        view.setProportionalMargins(100.0f, 200.0f, 300.0f, 400.0f)
    }

    @Test
    fun setBackgroundColor_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val renderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        // check default value
        assertEquals(Color.TRANSPARENT, renderer.backgroundColor)

        // set new background color
        view.setBackgroundColor(Color.RED)

        // check
        assertEquals(Color.RED, renderer.backgroundColor)
    }

    @Test
    fun setBackgroundColor_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.setPrivateProperty("curlRenderer", null)
        assertNull(view.getPrivateProperty("curlRenderer"))

        // set new background color
        view.setBackgroundColor(Color.RED)
    }

    @Test
    fun onSizeChange_whenHasSizeChangeObserver() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val sizeChangedObserver = mockk<CurlGLSurfaceView.SizeChangedObserver>(relaxUnitFun = true)
        view.sizeChangedObserver = sizeChangedObserver

        val onSizeChangedMethod = view::class.java.getDeclaredMethod(
            "onSizeChanged",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        onSizeChangedMethod.isAccessible = true
        onSizeChangedMethod.invoke(view, WIDTH, HEIGHT, WIDTH, HEIGHT)

        verify(exactly = 1) { sizeChangedObserver.onSizeChanged(WIDTH, HEIGHT) }
    }

    @Test
    fun onSizeChange_whenNoSizeChangeObserver() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.setPrivateProperty("sizeChangedObserver", null)

        val onSizeChangedMethod = view::class.java.getDeclaredMethod(
            "onSizeChanged",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        onSizeChangedMethod.isAccessible = true
        onSizeChangedMethod.invoke(view, WIDTH, HEIGHT, WIDTH, HEIGHT)
    }

    @Test
    fun updateLastCurlPos_whenNoRenderer_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.setPrivateProperty("curlRenderer", null)
        assertNull(view.getPrivateProperty("curlRenderer"))

        assertNull(view.getPrivateProperty("targetIndex"))

        // check default value
        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        requireNotNull(posField)
        posField.isAccessible = true
        val pos1: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 0.0f, 1)

        // check
        assertNull(view.getPrivateProperty("targetIndex"))

        val pos2: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(0.0f, pos2.x)
        assertEquals(0.0f, pos2.y)
    }

    @Test
    fun updateLastCurlPos_whenNoRightPage_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        renderer.setPrivateProperty("pageRectRight", null)
        assertNull(renderer.getPrivateProperty("pageRectRight"))

        assertNull(view.getPrivateProperty("targetIndex"))

        // check default value
        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        requireNotNull(posField)
        posField.isAccessible = true
        val pos1: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 0.0f, 1)

        // check
        assertNull(view.getPrivateProperty("targetIndex"))

        val pos2: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(0.0f, pos2.x)
        assertEquals(0.0f, pos2.y)
    }

    @Test
    fun updateLastCurlPos_whenNoLeftPage_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        renderer.setPrivateProperty("pageRectLeft", null)
        assertNull(renderer.getPrivateProperty("pageRectLeft"))

        assertNull(view.getPrivateProperty("targetIndex"))

        // check default value
        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        requireNotNull(posField)
        posField.isAccessible = true
        val pos1: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 0.0f, 1)

        // check
        assertNull(view.getPrivateProperty("targetIndex"))

        val pos2: PointF? = posField.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(0.0f, pos2.x)
        assertEquals(0.0f, pos2.y)
    }

    @Test
    fun updateLastCurlPost_whenRendererWithPagesAndNoCurlState_updatesPointerPos() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        assertNotNull(renderer.getPrivateProperty("pageRectLeft"))
        assertNotNull(renderer.getPrivateProperty("pageRectRight"))

        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        posField?.isAccessible = true
        val pos1: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val pressureField = pointerPositionClass?.getDeclaredField("pressure")
        pressureField?.isAccessible = true
        val pressure1: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.0f, pressure1)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 3.0f, 1)

        // check
        assertNull(view.getPrivateProperty("targetIndex"))

        val pos2: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(-0.56145835f, pos2.x)
        assertEquals(0.99791664f, pos2.y)

        val pressure2: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.8f, pressure2)
    }

    @Test
    fun updateLastCurlPost_whenRendererWithPagesEnabledTouchPressureAndNoCurlState_updatesPointerPos() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)
        view.enableTouchPressure = true

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        assertNotNull(renderer.getPrivateProperty("pageRectLeft"))
        assertNotNull(renderer.getPrivateProperty("pageRectRight"))

        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        posField?.isAccessible = true
        val pos1: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val pressureField = pointerPositionClass?.getDeclaredField("pressure")
        pressureField?.isAccessible = true
        val pressure1: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.0f, pressure1)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 3.0f, 1)

        // check
        assertNull(view.getPrivateProperty("targetIndex"))

        val pos2: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(-0.56145835f, pos2.x)
        assertEquals(0.99791664f, pos2.y)

        val pressure2: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(3.0f, pressure2)
    }

    @Test
    fun updateLastCurlPos_whenRendererWithPagesLeftCurlStateAndOnePageViewMode_setsAnimationTarget() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        assertNotNull(renderer.getPrivateProperty("pageRectLeft"))
        assertNotNull(renderer.getPrivateProperty("pageRectRight"))

        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        posField?.isAccessible = true
        val pos1: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val pressureField = pointerPositionClass?.getDeclaredField("pressure")
        pressureField?.isAccessible = true
        val pressure1: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.0f, pressure1)

        // set curlState
        view.setPrivateProperty("curlState", CurlGLSurfaceView.CURL_LEFT)

        val curlState: Int? = view.getPrivateProperty("curlState")
        requireNotNull(curlState)
        assertEquals(CurlGLSurfaceView.CURL_LEFT, curlState)

        // initially no animation start time is set
        val animationStartTime1: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime1)
        assertEquals(0L, animationStartTime1)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 3.0f, 1)

        // check
        val targetIndex: Int? = view.getPrivateProperty("targetIndex")
        requireNotNull(targetIndex)
        assertEquals(1, targetIndex)

        val pos2: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(-0.56145835f, pos2.x)
        assertEquals(0.99791664f, pos2.y)

        val pressure2: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.8f, pressure2)

        val animationStartTime2: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime2)
        assertTrue(animationStartTime2 > 0)

        val animationTargetEvent: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent)
        assertEquals(CurlGLSurfaceView.SET_CURL_TO_LEFT, animationTargetEvent)

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertTrue(animate)
    }

    @Test
    fun updateLastCurlPos_whenRendererWithPagesRightCurlStateAndOnePageViewMode_setsAnimationTarget() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        assertNotNull(renderer.getPrivateProperty("pageRectLeft"))
        assertNotNull(renderer.getPrivateProperty("pageRectRight"))

        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        posField?.isAccessible = true
        val pos1: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val pressureField = pointerPositionClass?.getDeclaredField("pressure")
        pressureField?.isAccessible = true
        val pressure1: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.0f, pressure1)

        // set curlState
        view.setPrivateProperty("curlState", CurlGLSurfaceView.CURL_RIGHT)

        val curlState: Int? = view.getPrivateProperty("curlState")
        requireNotNull(curlState)
        assertEquals(CurlGLSurfaceView.CURL_RIGHT, curlState)

        // initially no animation start time is set
        val animationStartTime1: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime1)
        assertEquals(0L, animationStartTime1)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 1.0f, 2.0f, 3.0f, 1)

        // check
        val targetIndex: Int? = view.getPrivateProperty("targetIndex")
        requireNotNull(targetIndex)
        assertEquals(1, targetIndex)

        val pos2: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(-0.56145835f, pos2.x)
        assertEquals(0.99791664f, pos2.y)

        val pressure2: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.8f, pressure2)

        val animationStartTime2: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime2)
        assertTrue(animationStartTime2 > 0)

        val animationTargetEvent: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent)
        assertEquals(CurlGLSurfaceView.SET_CURL_TO_LEFT, animationTargetEvent)

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertTrue(animate)
    }

    @Test
    fun updateLastCurlPos_whenRendererWithPagesAndTwoPageViewMode_setsAnimationTarget() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)
        view.viewMode = CurlGLSurfaceView.SHOW_TWO_PAGES

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        val renderer : CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>(relaxUnitFun = true)
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        val viewportWidth: Int? = renderer.getPrivateProperty("viewportWidth")
        assertEquals(WIDTH, viewportWidth)
        val viewportHeight: Int? = renderer.getPrivateProperty("viewportHeight")
        assertEquals(HEIGHT, viewportHeight)

        assertNotNull(renderer.getPrivateProperty("pageRectLeft"))
        assertNotNull(renderer.getPrivateProperty("pageRectRight"))

        val pointerPos: Any? = view.getPrivateProperty("pointerPos")
        requireNotNull(pointerPos)

        val classes = view.javaClass.declaredClasses
        val pointerPositionClass: Class<*>? =
            classes.firstOrNull { it.name.endsWith("PointerPosition") }

        val posField = pointerPositionClass?.getDeclaredField("pos")
        posField?.isAccessible = true
        val pos1: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos1)

        assertEquals(0.0f, pos1.x)
        assertEquals(0.0f, pos1.y)

        val pressureField = pointerPositionClass?.getDeclaredField("pressure")
        pressureField?.isAccessible = true
        val pressure1: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.0f, pressure1)

        // set curlState
        view.setPrivateProperty("curlState", CurlGLSurfaceView.CURL_RIGHT)

        val curlState: Int? = view.getPrivateProperty("curlState")
        requireNotNull(curlState)
        assertEquals(CurlGLSurfaceView.CURL_RIGHT, curlState)

        // initially no animation start time is set
        val animationStartTime1: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime1)
        assertEquals(0L, animationStartTime1)

        val updateLastCurlPosMethod = CurlGLSurfaceView::class.java.getDeclaredMethod(
            "updateLastCurlPos",
            Float::class.java,
            Float::class.java,
            Float::class.java,
            Integer::class.java
        )
        updateLastCurlPosMethod.isAccessible = true

        updateLastCurlPosMethod.invoke(view, 2.0f * WIDTH.toFloat(), 2.0f, 3.0f, 1)

        // check
        val targetIndex: Int? = view.getPrivateProperty("targetIndex")
        requireNotNull(targetIndex)
        assertEquals(1, targetIndex)

        val pos2: PointF? = posField?.get(pointerPos) as PointF?
        requireNotNull(pos2)

        assertEquals(1.6875f, pos2.x)
        assertEquals(0.99791664f, pos2.y)

        val pressure2: Float? = pressureField?.getFloat(pointerPos)
        assertEquals(0.8f, pressure2)

        val animationStartTime2: Long? = view.getPrivateProperty("animationStartTime")
        requireNotNull(animationStartTime2)
        assertTrue(animationStartTime2 > 0)

        val animationTargetEvent: Int? = view.getPrivateProperty("animationTargetEvent")
        requireNotNull(animationTargetEvent)
        assertEquals(CurlGLSurfaceView.SET_CURL_TO_RIGHT, animationTargetEvent)

        val animate: Boolean? = view.getPrivateProperty("animate")
        requireNotNull(animate)
        assertTrue(animate)
    }

    // TODO: handleFirstScrollEvent
    // TODO: updateFirstCurlPos
    // TODO: handleScrollEvent
    // TODO: gestureDetector_onSingleTapUp
    // TODO: gestureDetector_onScroll
    // TODO: gestureDetector_onDown
    // TODO: setCurlPos
    // TODO: startCurl
    // TODO: updateCurlPos
    // TODO: updatePage
    // TODO: updatePages

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1920

        const val ANIMATION_DURATION_MILLIS = 10000
    }
}