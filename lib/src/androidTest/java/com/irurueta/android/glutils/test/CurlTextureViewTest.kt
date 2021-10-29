package com.irurueta.android.glutils.test

import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.RequiresDevice
import androidx.test.rule.ActivityTestRule
import com.irurueta.android.glutils.GLTextureView
import com.irurueta.android.glutils.curl.CurlPage
import com.irurueta.android.glutils.curl.CurlTextureView
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@RequiresDevice
@RunWith(AndroidJUnit4::class)
class CurlTextureViewTest {

    @get:Rule
    val activityRule = ActivityTestRule(CurlTextureViewActivity::class.java, true)

    private var activity: CurlTextureViewActivity? = null
    private var view: CurlTextureView? = null

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private var currentIndex = 0
    private var pageClicked = 0
    private var sizeChanged = 0

    private val pageProvider = object : CurlTextureView.PageProvider {
        override val pageCount: Int
            get() = PAGE_COUNT

        override fun updatePage(
            page: CurlPage,
            width: Int,
            height: Int,
            index: Int,
            backIndex: Int?
        ) {
            // load new bitmap for curl page view
            val bitmap = loadBitmap(width, height, index)
            if (backIndex == null) {
                page.setTexture(bitmap, CurlPage.SIDE_BOTH)
            } else {
                // load bitmap for back side
                val backBitmap = loadBitmap(width, height, backIndex)
                page.setTexture(bitmap, CurlPage.SIDE_FRONT)
                page.setTexture(backBitmap, CurlPage.SIDE_BACK)
            }

            // set semi transparent white for background to get a bit of transparency
            // on back images when being flipped
            val context = view?.context ?: return
            page.setColor(
                ContextCompat.getColor(context, R.color.translucid_white),
                CurlPage.SIDE_BACK
            )
        }
    }

    private val sizeChangeObserver = object : CurlTextureView.SizeChangedObserver {
        override fun onSizeChanged(width: Int, height: Int) {
            lock.withLock {
                this@CurlTextureViewTest.sizeChanged++
                condition.signalAll()
            }
        }
    }

    private val currentIndexChangeListener = object : CurlTextureView.CurrentIndexChangedListener {
        override fun onCurrentIndexChanged(view: CurlTextureView, currentIndex: Int) {
            lock.withLock {
                this@CurlTextureViewTest.currentIndex++
                condition.signalAll()
            }
        }
    }

    private val pageClickListener = object : CurlTextureView.PageClickListener {
        override fun onPageClick(view: CurlTextureView, currentIndex: Int): Boolean {
            lock.withLock {
                this@CurlTextureViewTest.pageClicked++
                condition.signalAll()
            }
            return true
        }
    }

    @Before
    fun setUp() {
        activity = activityRule.activity
        view = activity?.findViewById(R.id.curl_texture_view_test)
        reset()
    }

    @After
    fun tearDown() {
        view = null
        activity = null
        reset()
    }

    @Test
    fun constructor_whenNotAttached_setsDefaultValues() {
        val activity = this.activity ?: return fail()
        activityRule.runOnUiThread {
            val view = CurlTextureView(activity)

            assertEquals(CurlTextureView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
            assertEquals(CurlTextureView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)
            assertEquals(
                CurlTextureView.PAGE_JUMP_ANIMATION_DURATION_MILLIS,
                view.pageJumpDurationTime
            )
            assertEquals(CurlTextureView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
            assertNull(view.pageProvider)
            assertEquals(CurlTextureView.CURL_NONE, view.curlState)
            assertEquals(0, view.currentIndex)
            assertTrue(view.renderLeftPage)
            assertNull(view.sizeChangedObserver)
            assertNull(view.currentIndexChangedListener)
            assertEquals(CurlTextureView.SHOW_ONE_PAGE, view.viewMode)
            assertNull(view.pageClickListener)
            assertEquals(0, view.debugFlags)
            assertFalse(view.preserveEGLContextOnPause)
            assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)
        }
    }

    @Test
    fun constructor_whenAttached_setsDefaultValues() {
        val view = this.view ?: return fail()
        assertEquals(CurlTextureView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
        assertEquals(CurlTextureView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)
        assertEquals(
            CurlTextureView.PAGE_JUMP_ANIMATION_DURATION_MILLIS,
            view.pageJumpDurationTime
        )
        assertEquals(CurlTextureView.TOUCH_PRESSURE_ENABLED, view.enableTouchPressure)
        assertNull(view.pageProvider)
        assertEquals(CurlTextureView.CURL_NONE, view.curlState)
        assertEquals(0, view.currentIndex)
        assertTrue(view.renderLeftPage)
        assertNull(view.sizeChangedObserver)
        assertNull(view.currentIndexChangedListener)
        assertEquals(CurlTextureView.SHOW_ONE_PAGE, view.viewMode)
        assertNull(view.pageClickListener)
        assertEquals(0, view.debugFlags)
        assertFalse(view.preserveEGLContextOnPause)
        assertEquals(GLTextureView.RENDERMODE_WHEN_DIRTY, view.renderMode)
    }

    @Test
    fun pageProvider_setsExpectedValue() {
        val view = this.view ?: return fail()
        view.pageProvider = pageProvider

        assertSame(pageProvider, view.pageProvider)
    }

    @Test
    fun sizeChangeObserver_setsExpectedValue() {
        val view = this.view ?: return fail()
        view.pageProvider = pageProvider
        view.sizeChangedObserver = sizeChangeObserver

        assertSame(sizeChangeObserver, view.sizeChangedObserver)

        // change size
        val width = view.width
        val height = view.height

        assertTrue(width > 0)
        assertTrue(height > 0)

        val newWidth = width / 2
        val newHeight = height / 2

        view.measure(
            View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, newWidth, newHeight)

        waitOnCondition({ sizeChanged == 0 })

        assertEquals(1, sizeChanged)
    }

    @Test
    fun currentIndexChangedListener_whenNotAnimated_setsExpectedValue() {
        val view = this.view ?: return fail()
        view.pageProvider = pageProvider
        view.currentIndexChangedListener = currentIndexChangeListener

        assertSame(currentIndexChangeListener, view.currentIndexChangedListener)

        assertEquals(0, view.currentIndex)

        Thread.sleep(SLEEP)

        // change current index
        activityRule.runOnUiThread {
            view.setCurrentIndex(1)
        }

        waitOnCondition({ currentIndex == 0 })

        assertEquals(1, view.currentIndex)

        Thread.sleep(SLEEP)

        // change current index again
        activityRule.runOnUiThread {
            view.setCurrentIndex(2)
        }

        waitOnCondition({ currentIndex == 1 })

        assertEquals(2, view.currentIndex)

        Thread.sleep(SLEEP)

        // change current index again
        activityRule.runOnUiThread {
            view.setCurrentIndex(0)
        }

        waitOnCondition({ currentIndex == 0 })

        assertEquals(0, view.currentIndex)

        Thread.sleep(SLEEP)
    }

    @Test
    fun currentIndexChangedListener_whenAnimated_setsExpectedValue() {
        val view = this.view ?: return fail()
        view.pageProvider = pageProvider
        view.currentIndexChangedListener = currentIndexChangeListener

        assertSame(currentIndexChangeListener, view.currentIndexChangedListener)

        assertEquals(0, view.currentIndex)

        reset()

        // change current index
        activityRule.runOnUiThread {
            view.setSmoothCurrentIndex(1)
        }

        waitOnCondition({ currentIndex == 0 })

        assertEquals(1, view.currentIndex)

        // change current index again
        activityRule.runOnUiThread {
            view.setSmoothCurrentIndex(2)
        }

        waitOnCondition({ currentIndex == 1 })

        assertEquals(2, view.currentIndex)

        // change current index again
        activityRule.runOnUiThread {
            view.setSmoothCurrentIndex(0)
        }

        waitOnCondition({ currentIndex == 2 })

        assertEquals(0, view.currentIndex)
    }

    // TODO: renderLeftPage when TWO page view mode

    private fun loadBitmap(width: Int, height: Int, index: Int): Bitmap {
        val drawables = listOf(R.drawable.image1, R.drawable.image2, R.drawable.image3)
        val bitmap = BitmapFactory.decodeResource(activity?.resources, drawables[index])

        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height

        val rect = Rect(0, 0, width, height)

        val imageHeight = rect.height()
        val scale = imageHeight.toFloat() / bitmapHeight.toFloat()
        val imageWidth = (scale * bitmapWidth.toFloat()).toInt()

        /*if (imageWidth > rect.width()) {
            imageWidth
        }

        var imageWidth = rect.width()
        var scale = imageWidth.toFloat() / bitmapWidth.toFloat()
        var imageHeight = (scale * bitmapHeight.toFloat()).toInt()

        if (imageHeight > rect.height()) {
            imageHeight = rect.height()
            scale = imageHeight.toFloat() / bitmapHeight.toFloat()
            imageWidth = (scale * bitmapWidth.toFloat()).toInt()
        }*/

        // center image on page
        rect.left += ((rect.width() - imageWidth) / 2)
        rect.right = rect.left + imageWidth
        rect.top += ((rect.height() - imageHeight) / 2)
        rect.bottom = rect.top + imageHeight

        // draw resized bitmap using canvas
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        b.eraseColor(Color.WHITE)
        val canvas = Canvas(b)
        canvas.drawBitmap(bitmap, null, rect, null)

        return b
    }

    private fun reset() {
        currentIndex = 0
        pageClicked = 0
        sizeChanged = 0
    }

    private fun waitOnCondition(
        condition: () -> Boolean,
        maxRetries: Int = MAX_RETRIES,
        timeout: Long = TIMEOUT_MILLIS
    ) {
        lock.withLock {
            var count = 0
            while (condition() && count < maxRetries) {
                this.condition.await(timeout, TimeUnit.MILLISECONDS)
                count++
            }
        }
    }

    private companion object {
        const val SLEEP = 2000L
        const val PAGE_COUNT = 3
        const val MAX_RETRIES = 2
        const val TIMEOUT_MILLIS = 10000L
    }
}