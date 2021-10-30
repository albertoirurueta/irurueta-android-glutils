package com.irurueta.android.glutils.test

import android.app.Activity
import android.os.Bundle
import com.irurueta.android.glutils.curl.CurlTextureView

class CurlTextureViewActivity : Activity() {

    private var view: CurlTextureView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.curl_texture_view_activity)
        view = findViewById(R.id.curl_texture_view_test)
    }

    override fun onPause() {
        super.onPause()
        view?.onPause()
    }

    override fun onResume() {
        super.onResume()
        view?.onResume()
    }
}