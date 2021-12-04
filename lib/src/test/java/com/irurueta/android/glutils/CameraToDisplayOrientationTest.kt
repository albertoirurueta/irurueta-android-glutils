package com.irurueta.android.glutils

import org.junit.Assert.*
import org.junit.Test

class CameraToDisplayOrientationTest {

    @Test
    fun enum_hasExpectedValues() {
        assertEquals(5, CameraToDisplayOrientation.values().size)
    }
}