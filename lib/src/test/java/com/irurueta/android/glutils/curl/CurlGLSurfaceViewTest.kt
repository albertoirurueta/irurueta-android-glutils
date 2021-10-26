package com.irurueta.android.glutils.curl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CurlGLSurfaceViewTest {

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CurlGLSurfaceView(context)

        assertEquals(CurlGLSurfaceView.ALLOW_LAST_PAGE_CURL, view.allowLastPageCurl)
        assertEquals(CurlGLSurfaceView.ANIMATION_DURATION_MILLIS, view.animationDurationTime)
    }

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1920
    }
}