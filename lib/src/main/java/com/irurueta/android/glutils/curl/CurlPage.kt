package com.irurueta.android.glutils.curl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

/**
 * Storage class for page textures and blend colors.
 * This class is based on https://github.com/harism/android-pagecurl
 */
class CurlPage {

    /**
     * Indicates whether textures have changed since last time they were recycled.
     */
    var texturesChanged: Boolean = false
        private set

    /**
     * Indicates whether back side texture exists and if it differs from front facing one.
     */
    val hasBackTexture: Boolean
        get() = (textureFront != textureBack)

    /**
     * Color to be used to draw back page side.
     */
    private var colorBack: Int = 0

    /**
     * Color to be used to draw front page side.
     */
    private var colorFront: Int = 0

    /**
     * Texture to be used to draw front page side.
     */
    private var textureBack: Bitmap? = null

    /**
     * Texture to be used to draw back page side.
     */
    private var textureFront: Bitmap? = null

    init {
        reset()
    }

    /**
     * Gets color for provided page side value.
     *
     * @see [SIDE_FRONT], [SIDE_BACK], [SIDE_BOTH]
     */
    fun getColor(side: Int): Int {
        return when (side) {
            SIDE_FRONT -> colorFront
            else -> colorBack
        }
    }

    /**
     * Getter for textures. Creates Bitmap sized to nearest power of two, copies
     * original Bitmap into it and returns it. RectF given as parameter is filled
     * with actual texture coordinates in this new unscaled texture Bitmap.
     *
     * @param textureRect instance where original texture coordinates will be stored.
     * @return texture for required page side or null if not available.
     */
    fun getTexture(textureRect: RectF, side: Int): Bitmap? {
        return when (side) {
            SIDE_FRONT -> getTexture(textureFront, textureRect)
            else -> getTexture(textureBack, textureRect)
        }
    }

    /**
     * Recycles and frees underlying Bitmaps.
     */
    fun recycle() {
        textureFront?.recycle()
        textureFront = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        textureFront?.eraseColor(colorFront)

        textureBack?.recycle()
        textureBack = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        textureBack?.eraseColor(colorBack)
        texturesChanged = false
    }

    /**
     * Resets this CurlPage into its initial state.
     */
    fun reset() {
        colorBack = Color.WHITE
        colorFront = Color.WHITE
        recycle()
    }

    /**
     * Sets blend color for page sides.
     *
     * @param color blend color to be set.
     * @param side side where blend color is applied.
     * @see [SIDE_FRONT], [SIDE_BACK], [SIDE_BOTH]
     */
    fun setColor(color: Int, side: Int) {
        when (side) {
            SIDE_FRONT -> colorFront = color
            SIDE_BACK -> colorBack = color
            else -> {
                colorFront = color
                colorBack = color
            }
        }
    }

    /**
     * Sets textures for page sides.
     *
     * @param texture texture to be set.
     * @param side side where texture is applied.
     * @see [SIDE_FRONT], [SIDE_BACK], [SIDE_BOTH]
     */
    fun setTexture(texture: Bitmap?, side: Int) {
        var texture2 = texture
        if (texture2 == null) {
            texture2 = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
            if (side == SIDE_BACK) {
                texture2.eraseColor(colorBack)
            } else {
                texture2.eraseColor(colorFront)
            }
        }
        when (side) {
            SIDE_FRONT -> {
                textureFront?.recycle()
                textureFront = texture2
            }
            SIDE_BACK -> {
                textureBack?.recycle()
                textureBack = texture2
            }
            SIDE_BOTH -> {
                textureFront?.recycle()
                textureBack?.recycle()
                textureFront = texture2
                textureBack = texture2
            }
        }
        texturesChanged = true
    }

    /**
     * Calculates the next highest power of two for a given integer.
     */
    private fun getNextHighestPO2(n: Int): Int {
        var n2 = n
        n2 -= 1
        n2 = n2 or (n2 shr 1)
        n2 = n2 or (n2 shr 2)
        n2 = n2 or (n2 shr 4)
        n2 = n2 or (n2 shr 8)
        n2 = n2 or (n2 shr 16)
        n2 = n2 or (n2 shr 32)
        return n2 + 1
    }

    /**
     * Generates nearest power of two sized Bitmap for provided Bitmap.
     * This method return this newly created Bitmap, and original texture coordinates
     * are stored into RectF.
     * Notice that returned texture will be an extended version of the original bitmap,
     * where the new portion of the bitmap will be empty (original bitmap is simply drawn
     * at (0,0)). Hence coordinates stored in textureRect refer to the coordinates where the
     * original bitmap is present but normalized from 0 to 1 as required by OpenGL.
     * NOTE: OpenGL ES requires texture bitmaps to have sizes of a power of two.
     *
     * @param bitmap bitmap to be resized to have proper texture size.
     * @param textureRect instance where original texture coordinates will be stored.
     * @return a new resized texture bitmap or null if no input texture is provided.
     */
    private fun getTexture(bitmap: Bitmap?, textureRect: RectF): Bitmap? {
        if (bitmap == null) {
            return null
        }

        // Bitmap original size
        val w = bitmap.width
        val h = bitmap.height
        // Bitmap size expanded to nex power of two. This is done due to the
        // requirement on many devices by OpenGL ES, where texture width and height
        // must be power of two.
        val newW = getNextHighestPO2(w)
        val newH = getNextHighestPO2(h)

        // draw bitmap with larger size
        val bitmapTex = Bitmap.createBitmap(newW, newH, bitmap.config)
        val c = Canvas(bitmapTex)
        c.drawBitmap(bitmap, 0.0f, 0.0f, null)

        // Calculate final texture coordinates
        val texX = w.toFloat() / newW.toFloat()
        val texY = h.toFloat() / newH.toFloat()
        textureRect.set(0.0f, 0.0f, texX, texY)

        return bitmapTex
    }

    companion object {
        /**
         * Defines front page side.
         */
        const val SIDE_FRONT = 1

        /**
         * Defines back page side
         */
        const val SIDE_BACK = 2

        /**
         * Defines both front and back page sides.
         */
        const val SIDE_BOTH = 3
    }
}