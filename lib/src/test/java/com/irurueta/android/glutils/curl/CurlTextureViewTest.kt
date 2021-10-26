package com.irurueta.android.glutils.curl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.irurueta.android.glutils.getPrivateProperty
import com.irurueta.android.glutils.setPrivateProperty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import javax.microedition.khronos.opengles.GL10

@RunWith(RobolectricTestRunner::class)
class CurlTextureViewTest {

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(CurlTextureView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
        assertEquals(CurlTextureView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)
        assertEquals(CurlTextureView.PAGE_JUMP_ANIMATION_DURATION_MILLIS, view.pageJumpDurationTime)
        assertEquals(CurlTextureView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
        assertNull(view.pageProvider)
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)
        assertTrue(view.renderLeftPage)
        assertNull(view.sizeChangedObserver)
        assertNull(view.currentIndexChangedListener)
        assertEquals(CurlTextureView.SHOW_ONE_PAGE, view.viewMode)
        assertNull(view.pageClickListener)

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
    fun allowLastPageCurl_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(CurlTextureView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)

        // set new value
        view.allowLastPageCurl = !CurlTextureView.ALLOW_LAST_PAGE_CURL

        // check
        assertEquals(!CurlTextureView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
    }

    @Test
    fun animationDurationTime_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(CurlTextureView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)

        // set new value
        view.animationDurationTime = 500

        // check
        assertEquals(500, view.animationDurationTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun animationDurationTie_whenNegative_throwsException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.animationDurationTime = -1
    }

    @Test
    fun pageJumpDurationTime_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(CurlTextureView.PAGE_JUMP_ANIMATION_DURATION_MILLIS, view.pageJumpDurationTime)

        // set new value
        view.pageJumpDurationTime = 300L

        // check
        assertEquals(300L, view.pageJumpDurationTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun pageJumpDurationTime_whenNegative_throwsException() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.pageJumpDurationTime = -1
    }

    @Test
    fun enableTouchPressure_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(CurlTextureView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)

        // set new value
        view.enableTouchPressure = !CurlTextureView.TOUCH_PRESSURE_ENABLED

        // check
        assertEquals(!CurlTextureView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
    }

    @Test
    fun pageProvider_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertNull(view.pageProvider)

        // set new value
        val pageProvider = mockk<CurlTextureView.PageProvider>()
        view.pageProvider = pageProvider

        // check
        assertSame(pageProvider, view.pageProvider)
        assertEquals(0, view.currentIndex)
    }

    @Test
    fun renderLeftPage_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

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
        val view = CurlTextureView(context)

        // check
        assertNull(view.sizeChangedObserver)

        // set new value
        val observer = mockk<CurlTextureView.SizeChangedObserver>()
        view.sizeChangedObserver = observer

        // check
        assertSame(observer, view.sizeChangedObserver)
    }

    @Test
    fun currentIndexChangedListener_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        // check
        assertNull(view.currentIndexChangedListener)

        // set new value
        val listener = mockk<CurlTextureView.CurrentIndexChangedListener>()
        view.currentIndexChangedListener = listener

        // check
        assertSame(listener, view.currentIndexChangedListener)
    }

    @Test
    fun viewMode_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        // check default value
        assertEquals(CurlTextureView.SHOW_ONE_PAGE, view.viewMode)

        // set two pages
        view.viewMode = CurlTextureView.SHOW_TWO_PAGES

        // check
        assertEquals(CurlTextureView.SHOW_TWO_PAGES, view.viewMode)

        // set one page
        view.viewMode = CurlTextureView.SHOW_ONE_PAGE

        // check
        assertEquals(CurlTextureView.SHOW_ONE_PAGE, view.viewMode)
    }

    @Test
    fun pageClickListener_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        // check default value
        assertNull(view.pageClickListener)

        // set new value
        val listener = mockk<CurlTextureView.PageClickListener>()
        view.pageClickListener = listener

        // check
        assertSame(listener, view.pageClickListener)
    }

    @Test(expected = NullPointerException::class)
    fun onTouchEvent_whenNoEvent_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.onTouchEvent(null)
    }

    @Test
    fun onTouchEvent_whenActionCancel_handlesTouchUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

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
        val view = CurlTextureView(context)

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
        val view = CurlTextureView(context)

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
    fun setCurrentIndex_whenNoPageProvider_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(0, view.currentIndex)

        view.setCurrentIndex(1)

        assertEquals(0, view.currentIndex)
    }

    @Test
    fun setCurrentIndex_whenPageProviderAndLastPageCurlAllowed_setsExpectedCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlTextureView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        view.pageProvider = pageProvider

        view.setCurrentIndex(2)

        assertEquals(2, view.currentIndex)
    }

    @Test
    fun setCurrentIndex_whenPageProviderAndLastPageCurlNotAllowed_setsExpectedCurrentIndex() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.allowLastPageCurl = false

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlTextureView.PageProvider>()
        every { pageProvider.pageCount }.returns(2)
        view.pageProvider = pageProvider

        view.setCurrentIndex(2)

        assertEquals(1, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenNoPageProvider_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        assertEquals(0, view.currentIndex)

        view.setSmoothCurrentIndex(1)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, view.currentIndex)
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setSmoothCurrentIndex_whenPageProviderButNoPageSizeChangeNotified_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlTextureView.PageProvider>()
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
        val view = CurlTextureView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = true

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlTextureView.PageProvider>()
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
        val view = CurlTextureView(context)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, WIDTH, HEIGHT)

        view.allowLastPageCurl = false

        assertEquals(0, view.currentIndex)

        val pageProvider = mockk<CurlTextureView.PageProvider>()
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
        val view = CurlTextureView(context)

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
    fun setProportionalMargins_setsExpectedMargins() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

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
    fun setBackgroundColor_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlTextureView(context)

        val renderer: CurlRenderer? = view.getPrivateProperty("curlRenderer")
        requireNotNull(renderer)

        // check default value
        assertEquals(Color.TRANSPARENT, renderer.backgroundColor)

        // set new background color
        view.setBackgroundColor(Color.RED)

        // check
        assertEquals(Color.RED, renderer.backgroundColor)
    }

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1920
    }
}