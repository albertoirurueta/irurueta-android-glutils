package com.irurueta.android.glutils.curl

import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Shows a paper curl like pagination using an OpenGL ES surface view.
 * This class is based on https://github.com/harism/android-pagecurl
 */
class CurlGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    /**
     * Indicates whether a curl animation needs to be performed or is being done when
     * finger action up touch event is received.
     */
    private var animate: Boolean = false

    /**
     * Point where animation starts.
     */
    private var animationSource: PointF = PointF()

    /**
     * Timestamp when animation starts expressed in milliseconds.
     */
    private var animationStartTime: Long = 0

    /**
     * Point where animation ends.
     */
    private var animationTarget: PointF = PointF()

    /**
     * Event that produced the animation. Indicates whether animation needs to be done on left page.
     */
    private var animationTargetEvent: Int = 0

    /**
     * Direction of curl.
     */
    private val curlDir: PointF = PointF()

    /**
     * Position of curl.
     */
    private val curlPos: PointF = PointF()

    /**
     * Start position for dragging.
     */
    private val dragStartPos: PointF = PointF()

    /**
     * Bitmap width. This is updated from renderer once it is initialized.
     */
    private var pageBitmapWidth: Int = -1

    /**
     * Bitmap height. This is updated from renderer once it is initialized.
     */
    private var pageBitmapHeight: Int = -1

    // Page meshes. Left and right meshes are 'static' while curl is used to
    // show page flipping

    /**
     * Page mesh to show page flipping.
     */
    private var pageCurl: CurlMesh? = null

    /**
     * Left page mesh.
     */
    private var pageLeft: CurlMesh? = null

    private var pageRight: CurlMesh? = null

    /**
     * Position where user has touched in the view.
     */
    private val pointerPos: PointerPosition = PointerPosition()

    /**
     * Gets or sets the renderer associated with this view. Also starts the thread that
     * will call the renderer, which in turn causes the rendering to start.
     * <p>This method should be called once and only once in the life-cycle of
     * a GLSurfaceView.
     * <p>The following GLSurfaceView methods can only be called <em>before</em>
     * setRenderer is called:
     * <ul>
     * <li>{@link #setEGLConfigChooser(boolean)}
     * <li>{@link #setEGLConfigChooser(EGLConfigChooser)}
     * <li>{@link #setEGLConfigChooser(int, int, int, int, int, int)}
     * </ul>
     * <p>
     * The following GLSurfaceView methods can only be called <em>after</em>
     * setRenderer is called:
     * <ul>
     * <li>{@link #getRenderMode()}
     * <li>{@link #onPause()}
     * <li>{@link #onResume()}
     * <li>{@link #queueEvent(Runnable)}
     * <li>{@link #requestRender()}
     * <li>{@link #setRenderMode(int)}
     * </ul>
     */
    private var curlRenderer: CurlRenderer? = null

    /**
     * Allows curl on last page.
     */
    var allowLastPageCurl: Boolean = ALLOW_LAST_PAGE_CURL

    /**
     * Curl animation duration expressed in milliseconds.
     */
    var animationDurationTime: Int = ANIMATION_DURATION_MILLIS
        set(value) {
            require(value >= 0)
            field = value
        }

    /**
     * If set to true, touch event pressure information is used to adjust curl
     * radius. The more you press, the flatter the curl becomes. This is
     * somewhat experimental and results may vary significantly between devices.
     * On emulator pressure information seems to be flat 1.0f which is maximum
     * value and therefore not very much of use.
     */
    var enableTouchPressure: Boolean = TOUCH_PRESSURE_ENABLED

    /**
     * Provides page bitmap data.
     */
    var pageProvider: PageProvider? = null
        set(value) {
            field = value
            currentIndex = 0
            updatePages()
            requestRender()
        }

    /**
     * Current curl state. Can be either on left page, right page or none.
     */
    var curlState: Int = CURL_NONE
        private set

    /**
     * Current bitmap index.
     * This is always displayed as front of right page.
     */
    var currentIndex: Int = 0
        private set

    /**
     * Whether left side page is rendered. This is useful mostly for situations
     * where right (main) page is aligned to left side of screen and left page is
     * not visible anyway.
     */
    var renderLeftPage: Boolean = true

    /**
     * Size changed observer for this view. Call back method is called from this
     * View's onSizeChanged method.
     */
    var sizeChangedObserver: SizeChangedObserver? = null

    /**
     * Handles events when currently visible page changes.
     */
    var currentIndexChangedListener: CurrentIndexChangedListener? = null

    /**
     * Gets or sets view mode.
     * Value can be either SHOW_ONE_PAGE or SHOW_TWO_PAGES. In
     * former case right page is made size of display, and in latter case two
     * pages are laid on visible area.
     * One page is the default.
     */
    var viewMode: Int = SHOW_ONE_PAGE
        set(value) {
            if (value == SHOW_ONE_PAGE) {
                field = value
                pageLeft?.setFlipTexture(true)
                curlRenderer?.setViewMode(CurlRenderer.SHOW_ONE_PAGE)
            } else if (value == SHOW_TWO_PAGES) {
                field = value
                pageLeft?.setFlipTexture(false)
                curlRenderer?.setViewMode(CurlRenderer.SHOW_TWO_PAGES)
            }
        }

    private val touchListener = OnTouchListener { _, event ->
        // No dragging during animation at the moment.
        // TODO: Stop animation on touch event and return to drag mode.
        if (animate || pageProvider == null) {
            return@OnTouchListener false
        }

        // We need page rects quite extensively so get them for later use.
        val rightRect = curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)
            ?: return@OnTouchListener false
        val leftRect = curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT)
            ?: return@OnTouchListener false
        val pageProvider = pageProvider ?: return@OnTouchListener false

        // Store pointer position.
        pointerPos.pos.set(event.x, event.y)
        curlRenderer?.translate(pointerPos.pos)
        pointerPos.pressure = if (enableTouchPressure) event.pressure else 0.8f

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Once we receive pointer down event its position is mapped to
                // right or left edge of page and that'll be the position from where
                // user is holding the paper to make curl happen.
                dragStartPos.set(pointerPos.pos)

                // First we make sure it's not over or below page. Pages are
                // supposed to be same height so it really doesn't matter do we use
                // left or right one.
                if (dragStartPos.y > rightRect.top) {
                    dragStartPos.y = rightRect.top
                } else if (dragStartPos.y < rightRect.bottom) {
                    dragStartPos.y = rightRect.bottom
                }

                // Then we have to make decisions for the user whether curl is going
                // to happen from left or right, and on which page.
                if (viewMode == SHOW_TWO_PAGES) {
                    // If we have an open book and pointer is on the left from right
                    // page we'll mark drag position to left edge of left page.
                    // Additionally checking mCurrentIndex is higher than zero tells
                    // us there is a visible page at all.
                    if (dragStartPos.x < rightRect.left && currentIndex > 0) {
                        dragStartPos.x = leftRect.left
                        startCurl(CURL_LEFT)
                    }
                    // Otherwise check pointer is on right page's side.
                    else if (dragStartPos.x >= rightRect.left
                        && currentIndex < pageProvider.pageCount
                    ) {
                        dragStartPos.x = rightRect.right
                        if (!allowLastPageCurl
                            && currentIndex >= pageProvider.pageCount - 1
                        ) {
                            return@OnTouchListener false
                        }
                        startCurl(CURL_RIGHT)
                    }
                } else if (viewMode == SHOW_ONE_PAGE) {
                    val halfX = (rightRect.right + rightRect.left) / 2.0f
                    if (dragStartPos.x < halfX && currentIndex > 0) {
                        dragStartPos.x = rightRect.left
                        startCurl(CURL_LEFT)
                    } else if (dragStartPos.x >= halfX
                        && currentIndex < pageProvider.pageCount
                    ) {
                        dragStartPos.x = rightRect.right
                        if (!allowLastPageCurl
                            && currentIndex >= pageProvider.pageCount - 1
                        ) {
                            return@OnTouchListener false
                        }
                        startCurl(CURL_RIGHT)
                    }
                }
                // If we have are in curl state, let this case clause flow through
                // to next one. We have pointer position and drag position defined
                // and this will create first render request given these points.
                if (curlState == CURL_NONE) {
                    return@OnTouchListener false
                }

                updateCurlPos(pointerPos)
            }
            MotionEvent.ACTION_MOVE -> {
                updateCurlPos(pointerPos)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (curlState == CURL_LEFT || curlState == CURL_RIGHT) {
                    // Animation source is the point from where animation starts.
                    // Also it's handled in a way we actually simulate touch events
                    // meaning the output is exactly the same as if user drags the
                    // page to other side. While not producing the best looking
                    // result (which is easier done by altering curl position and/or
                    // direction directly), this is done in a hope it made code a
                    // bit more readable and easier to maintain.
                    animationSource.set(pointerPos.pos)
                    animationStartTime = System.currentTimeMillis()

                    // Given the explanation, here we decide whether to simulate
                    // drag to left or right end.
                    if ((viewMode == SHOW_ONE_PAGE && pointerPos.pos.x > (rightRect.left + rightRect.right) / 2.0f)
                        || viewMode == SHOW_TWO_PAGES
                        && pointerPos.pos.x > rightRect.left
                    ) {
                        // On right side target is always right page's right border.
                        animationTarget.set(dragStartPos)
                        animationTarget.x =
                            curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)?.right ?: 0.0f
                        animationTargetEvent = SET_CURL_TO_RIGHT
                    } else {
                        // On left side target depends on visible pages.
                        animationTarget.set(dragStartPos)
                        if (curlState == CURL_RIGHT || viewMode == SHOW_TWO_PAGES) {
                            animationTarget.x = leftRect.left
                        } else {
                            animationTarget.x = rightRect.left
                        }
                        animationTargetEvent = SET_CURL_TO_LEFT
                    }
                    animate = true
                    requestRender()
                }
            }
        }

        return@OnTouchListener true
    }

    private val observer = object : CurlRenderer.Observer {
        override fun onDrawFrame() {
            // We are not animating
            if (!animate) return
            val renderer = curlRenderer ?: return

            val currentTime = System.currentTimeMillis()
            if (currentTime >= animationStartTime + animationDurationTime) {
                // If animation is done
                if (animationTargetEvent == SET_CURL_TO_RIGHT) {
                    // Switch curled page to right.
                    val right = pageCurl ?: return
                    val curl = pageRight ?: return
                    right.setRect(renderer.getPageRect(CurlRenderer.PAGE_RIGHT))
                    right.setFlipTexture(false)
                    right.reset()
                    renderer.removeCurlMesh(curl)
                    pageCurl = curl
                    pageRight = right
                    // If we were curling left page, update current index.
                    if (curlState == CURL_LEFT) {
                        --currentIndex
                    }
                } else if (animationTargetEvent == SET_CURL_TO_LEFT) {
                    // Switch curled page to left.
                    val left = pageCurl ?: return
                    val curl = pageLeft ?: return
                    left.setRect(renderer.getPageRect(CurlRenderer.PAGE_LEFT))
                    left.setFlipTexture(true)
                    left.reset()
                    renderer.removeCurlMesh(curl)
                    if (!renderLeftPage) {
                        renderer.removeCurlMesh(left)
                    }
                    pageCurl = curl
                    pageLeft = left
                    // If we were curling right page, update current index.
                    if (curlState == CURL_RIGHT) {
                        ++currentIndex
                    }
                }
                curlState = CURL_NONE
                animate = false
                requestRender()

                // notify page position changed
                currentIndexChangedListener?.onCurrentIndexChanged(
                    this@CurlGLSurfaceView,
                    currentIndex
                )
            } else {
                pointerPos.pos.set(animationSource)
                var t =
                    1.0f - (currentTime - animationStartTime).toFloat() / animationDurationTime.toFloat()
                t = 1.0f - (t * t * t * (3.0f - 2.0f * t))
                pointerPos.pos.x += (animationTarget.x - animationSource.x) * t
                pointerPos.pos.y += (animationTarget.y - animationSource.y) * t
                updateCurlPos(pointerPos)
            }
        }

        override fun onPageSizeChanged(width: Int, height: Int) {
            pageBitmapWidth = width
            pageBitmapHeight = height
            updatePages()
            requestRender()
        }

        override fun onSurfaceCreated() {
            // In case surface is recreated, let page meshes drop allocated texture
            // ids and ask for new ones. There's no need to set textures here as
            // onPageSizeChanged should be called later on.
            pageLeft?.resetTexture()
            pageRight?.resetTexture()
            pageCurl?.resetTexture()
        }
    }

    /**
     * Initializes the view.
     */
    init {
        curlRenderer = CurlRenderer(observer)
        setRenderer(curlRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        setOnTouchListener(touchListener)

        // Even though left and right pages are static we have to allocate room
        // for curl on them too as we are switching meshes. Another way would be
        // to swap texture ids only.
        // TODO: additional properties can be set (draw polygons, change shadow colors, etc)
        pageLeft = CurlMesh(
            MAX_CURL_SPLITS_IN_MESH,
            DRAW_CURL_POSITION_IN_MESH,
            DRAW_POLYGON_OUTLINES_IN_MESH,
            DRAW_SHADOW_IN_MESH,
            DRAW_TEXTURE_IN_MESH,
            SHADOW_INNER_COLOR_IN_MESH,
            SHADOW_OUTER_COLOR_IN_MESH,
            DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH
        )
        pageRight = CurlMesh(
            MAX_CURL_SPLITS_IN_MESH,
            DRAW_CURL_POSITION_IN_MESH,
            DRAW_POLYGON_OUTLINES_IN_MESH,
            DRAW_SHADOW_IN_MESH,
            DRAW_TEXTURE_IN_MESH,
            SHADOW_INNER_COLOR_IN_MESH,
            SHADOW_OUTER_COLOR_IN_MESH,
            DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH
        )
        pageCurl = CurlMesh(
            MAX_CURL_SPLITS_IN_MESH,
            DRAW_CURL_POSITION_IN_MESH,
            DRAW_POLYGON_OUTLINES_IN_MESH,
            DRAW_SHADOW_IN_MESH,
            DRAW_TEXTURE_IN_MESH,
            SHADOW_INNER_COLOR_IN_MESH,
            SHADOW_OUTER_COLOR_IN_MESH,
            DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH
        )
        pageLeft?.setFlipTexture(true)
        pageRight?.setFlipTexture(false)
    }

    /**
     * Set current page index. Page indices are zero based values presenting
     * page being shown on right side of the book. E.g if you set value to 4;
     * right side front facing bitmap will be with index 4, back facing 5 and
     * for left side page index 3 is front facing, and index 2 back facing (once
     * page is on left side it's flipped over).
     *
     * Current index is rounded to closest value divisible by 2.
     *
     * @param index index of page to be displayed as current page.
     */
    fun setCurrentIndex(index: Int) {
        val pageProvider = this.pageProvider
        currentIndex = if (pageProvider == null || index < 0) {
            0
        } else {
            if (allowLastPageCurl) {
                min(index, pageProvider.pageCount)
            } else {
                min(
                    index,
                    pageProvider.pageCount - 1
                )
            }
        }
        updatePages()
        requestRender()
    }

    /**
     * Sets actual screen pixel margins.
     * @param left left margin expressed in pixels.
     * @param top top margin expressed in pixels.
     * @param right right margin expressed in pixels.
     * @param bottom bottom margin expressed in pixels.
     */
    fun setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        curlRenderer?.setMargins(left, top, right, bottom)
    }

    /**
     * Set margins (or padding). Note: margins are proportional. Meaning a value
     * of .1f will produce a 10% margin.
     *
     * @param left percentage of left margin.
     * @param top percentage of top margin.
     * @param right percentage of right margin.
     * @param bottom percentage of bottom margin.
     */
    fun setProportionalMargins(left: Float, top: Float, right: Float, bottom: Float) {
        curlRenderer?.setProportionalMargins(left, top, right, bottom)
    }

    /**
     * Called when view changes its size.
     * @param w current width in pixels.
     * @param h current height in pixels.
     * @param oldw previous width in pixels.
     * @param oldh previous height in pixels.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        requestRender()
        sizeChangedObserver?.onSizeChanged(w, h)
    }

    /**
     * Sets background color - or OpenGL clear color to be more precise. Color
     * is a 32bit value consisting of 0xAARRGGBB and is extracted using
     * android.graphics.Color eventually.
     */
    override fun setBackgroundColor(color: Int) {
        curlRenderer?.backgroundColor = color
        requestRender()
    }

    /**
     * Sets pageCurl curl position.
     */
    private fun setCurlPos(curlPos: PointF, curlDir: PointF, radius: Double) {
        // First reposition curl so that page doesn't 'rip off' from book.
        if (curlState == CURL_RIGHT
            || (curlState == CURL_LEFT && viewMode == SHOW_ONE_PAGE)
        ) {
            val pageRect = curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT) ?: return
            if (curlPos.x >= pageRect.right) {
                pageCurl?.reset()
                requestRender()
                return
            }
            if (curlPos.x < pageRect.left) {
                curlPos.x = pageRect.left
            }
            if (curlDir.y != 0.0f) {
                val diffX = curlPos.x - pageRect.left
                val leftY = curlPos.y + (diffX * curlDir.x / curlDir.y)
                if (curlDir.y < 0 && leftY < pageRect.top) {
                    curlDir.x = curlPos.y - pageRect.top
                    curlDir.y = pageRect.left - curlPos.x
                } else if (curlDir.y > 0 && leftY > pageRect.bottom) {
                    curlDir.x = pageRect.bottom - curlPos.y
                    curlDir.y = curlPos.x - pageRect.left
                }
            }
        } else if (curlState == CURL_LEFT) {
            val pageRect = curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT) ?: return
            if (curlPos.x <= pageRect.left) {
                pageCurl?.reset()
                requestRender()
                return
            }
            if (curlPos.x > pageRect.right) {
                curlPos.x = pageRect.right
            }
            if (curlDir.y != 0.0f) {
                val diffX = curlPos.x - pageRect.right
                val rightY = curlPos.y + (diffX * curlDir.x / curlDir.y)
                if (curlDir.y < 0 && rightY < pageRect.top) {
                    curlDir.x = pageRect.top - curlPos.y
                    curlDir.y = curlPos.x - pageRect.right
                } else if (curlDir.y > 0 && rightY > pageRect.bottom) {
                    curlDir.x = curlPos.y - pageRect.bottom
                    curlDir.y = pageRect.right - curlPos.x
                }
            }
        }

        // Finally normalize direction vector and do rendering.
        val dist = sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y)
        if (dist != 0.0f) {
            curlDir.x /= dist
            curlDir.y /= dist
            pageCurl?.curl(curlPos, curlDir, radius)
        } else {
            pageCurl?.reset()
        }

        requestRender()
    }

    /**
     * Switches meshes and loads new bitmaps if available. Updated to support 2
     * pages in landscape
     */
    private fun startCurl(page: Int) {
        var pageLeft = pageLeft ?: return
        var pageRight = pageRight ?: return
        var pageCurl = pageCurl ?: return

        when (page) {
            CURL_RIGHT -> {
                // Once right side page is curled, first right page is assigned into
                // curled page. And if there are more bitmaps available new bitmap is
                // loaded into right side mesh.
                // Remove meshes from renderer.
                curlRenderer?.removeCurlMesh(pageLeft)
                curlRenderer?.removeCurlMesh(pageRight)
                curlRenderer?.removeCurlMesh(pageCurl)

                // We are curling right page.
                val curl = pageRight
                pageRight = pageCurl
                this.pageRight = pageCurl
                pageCurl = curl
                this.pageCurl = curl

                if (currentIndex > 0) {
                    pageLeft.setFlipTexture(true)
                    pageLeft.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT))
                    pageLeft.reset()
                    if (renderLeftPage) {
                        curlRenderer?.addCurlMesh(pageLeft)
                    }
                }
                val pageCount = pageProvider?.pageCount ?: 0
                if (currentIndex < pageCount - 1) {
                    updatePage(pageRight.texturePage, currentIndex + 1)
                    pageRight.setRect(
                        curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)
                    )
                    pageRight.setFlipTexture(false)
                    pageRight.reset()
                    curlRenderer?.addCurlMesh(pageRight)
                }

                // Add curled page to renderer.
                pageCurl.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT))
                pageCurl.setFlipTexture(false)
                pageCurl.reset()
                curlRenderer?.addCurlMesh(pageCurl)

                curlState = CURL_RIGHT
            }
            CURL_LEFT -> {
                // On left side curl, left page is assigned to curled page. And if
                // there are more bitmaps available before currentIndex, new bitmap
                // is loaded into left page.

                // Remove meshes from renderer.
                curlRenderer?.removeCurlMesh(pageLeft)
                curlRenderer?.removeCurlMesh(pageRight)
                curlRenderer?.removeCurlMesh(pageCurl)

                // We are curling left page.
                val curl = pageLeft
                pageLeft = pageCurl
                this.pageLeft = pageCurl
                pageCurl = curl
                this.pageCurl = curl

                if (currentIndex > 1) {
                    updatePage(pageLeft.texturePage, currentIndex - 2)
                    pageLeft.setFlipTexture(true)
                    pageLeft
                        .setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT))
                    pageLeft.reset()
                    if (renderLeftPage) {
                        curlRenderer?.addCurlMesh(pageLeft)
                    }
                }

                // If there is something to show on right page add it to renderer.
                val pageCount = pageProvider?.pageCount ?: 0
                if (currentIndex < pageCount) {
                    pageRight.setFlipTexture(false)
                    pageRight.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT))
                    pageRight.reset()
                    curlRenderer?.addCurlMesh(pageRight)
                }

                // How dragging previous page happens depends on view mode.
                if (viewMode == SHOW_ONE_PAGE
                    || (curlState == CURL_LEFT && viewMode == SHOW_TWO_PAGES)
                ) {
                    pageCurl.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT))
                    pageCurl.setFlipTexture(false)
                } else {
                    pageCurl
                        .setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT))
                    pageCurl.setFlipTexture(true)
                }
                pageCurl.reset()
                curlRenderer?.addCurlMesh(pageCurl)

                curlState = CURL_LEFT
            }
        }
    }

    /**
     * Updates curl position.
     */
    private fun updateCurlPos(pointerPos: PointerPosition) {
        // Default curl radius.
        var radius = (curlRenderer?.getPageRect(CURL_RIGHT)?.width() ?: 0.0f) / 3.0
        // TODO: This is not an optimal solution. Based on feedback received so
        // far; pressure is not very accurate, it may be better not to map
        // coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
        // Leaving it as is until get my hands on a real device. On emulator
        // this doesn't work anyway.
        radius *= max(1.0f - pointerPos.pressure, 0.0f)
        // NOTE: Here we set pointerPos to mCurlPos. It might be a bit confusing
        // later to see e.g "mCurlPos.x - mDragStartPos.x" used. But it's
        // actually pointerPos we are doing calculations against. Why? Simply to
        // optimize code a bit with the cost of making it unreadable. Otherwise
        // we had to this in both of the next if-else branches.
        curlPos.set(pointerPos.pos)

        // If curl happens on right page, or on left page on two page mode,
        // we'll calculate curl position from pointerPos.
        if (curlState == CURL_RIGHT
            || (curlState == CURL_LEFT && viewMode == SHOW_TWO_PAGES)
        ) {

            curlDir.x = curlPos.x - dragStartPos.x
            curlDir.y = curlPos.y - dragStartPos.y
            val dist = sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y)

            // Adjust curl radius so that if page is dragged far enough on
            // opposite side, radius gets closer to zero.
            val pageWidth =
                curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)?.width() ?: 0.0f
            var curlLen = radius * Math.PI
            if (dist > (pageWidth * 2.0f) - curlLen) {
                curlLen = max(((pageWidth * 2.0f) - dist).toDouble(), 0.0)
                radius = curlLen / Math.PI
            }

            // Actual curl position calculation.
            if (dist >= curlLen) {
                val translate = (dist - curlLen) / 2.0
                if (viewMode == SHOW_TWO_PAGES) {
                    curlPos.x -= (curlDir.x * translate / dist).toFloat()
                } else {
                    val pageLeftX =
                        (curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)?.left) ?: 0.0f
                    radius = max(min((curlPos.x - pageLeftX).toDouble(), radius), 0.0)
                }
                curlPos.y -= (curlDir.y * translate / dist).toFloat()
            } else {
                val angle = Math.PI * sqrt(dist / curlLen)
                val translate = radius * sin(angle)
                curlPos.x += (curlDir.x * translate / dist).toFloat()
                curlPos.y += (curlDir.y * translate / dist).toFloat()
            }
        }
        // Otherwise we'll let curl follow pointer position.
        else if (curlState == CURL_LEFT) {

            // Adjust radius regarding how close to page edge we are.
            val pageLeftX =
                (curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)?.left) ?: 0.0f
            radius = max(min((curlPos.x - pageLeftX).toDouble(), radius), 0.0)

            val pageRightX =
                (curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT)?.right) ?: 0.0f
            curlPos.x -= min(pageRightX - curlPos.x, radius.toFloat())
            curlDir.x = curlPos.x + dragStartPos.x
            curlDir.y = curlPos.y - dragStartPos.y
        }

        setCurlPos(curlPos, curlDir, radius)
    }

    /**
     * Updates given CurlPage via PageProvider for page located at index.
     */
    private fun updatePage(page: CurlPage, index: Int) {
        // First reset page to initial state.
        page.reset()
        // Ask page provider to fill it up with bitmaps and colors.
        pageProvider?.updatePage(page, pageBitmapWidth, pageBitmapHeight, index)
    }

    /**
     * Updates bitmaps for page meshes.
     */
    private fun updatePages() {
        if (pageBitmapWidth <= 0 || pageBitmapHeight <= 0) {
            return
        }

        val pageProvider = pageProvider ?: return
        val pageLeft = pageLeft ?: return
        val pageRight = pageRight ?: return
        val pageCurl = pageCurl ?: return

        // Remove meshes from renderer.
        curlRenderer?.removeCurlMesh(pageLeft)
        curlRenderer?.removeCurlMesh(pageRight)
        curlRenderer?.removeCurlMesh(pageCurl)

        var leftIdx = currentIndex - 1
        var rightIdx = currentIndex
        var curlIdx = -1
        if (curlState == CURL_LEFT) {
            curlIdx = leftIdx
            --leftIdx
        } else if (curlState == CURL_RIGHT) {
            curlIdx = rightIdx
            ++rightIdx
        }

        if (rightIdx >= 0 && rightIdx < pageProvider.pageCount) {
            updatePage(pageRight.texturePage, rightIdx)
            pageRight.setFlipTexture(false)
            pageRight.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT))
            pageRight.reset()
            curlRenderer?.addCurlMesh(pageRight)
        }
        if (leftIdx >= 0 && leftIdx < pageProvider.pageCount) {
            updatePage(pageLeft.texturePage, leftIdx)
            pageLeft.setFlipTexture(true)
            pageLeft.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT))
            pageLeft.reset()
            if (renderLeftPage) {
                curlRenderer?.addCurlMesh(pageLeft)
            }
        }
        if (curlIdx >= 0 && curlIdx < pageProvider.pageCount) {
            updatePage(pageCurl.texturePage, curlIdx)

            if (curlState == CURL_RIGHT) {
                pageCurl.setFlipTexture(true)
                pageCurl.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_RIGHT))
            } else {
                pageCurl.setFlipTexture(false)
                pageCurl.setRect(curlRenderer?.getPageRect(CurlRenderer.PAGE_LEFT))
            }

            pageCurl.reset()
            curlRenderer?.addCurlMesh(pageCurl)
        }
    }

    companion object {
        // Curl state. We are flipping none, left or right page

        /**
         * Flipping no page.
         */
        const val CURL_NONE = 0

        /**
         * Flipping left page
         */
        const val CURL_LEFT = 1

        /**
         * Flipping right page.
         */
        const val CURL_RIGHT = 2

        // Constants for animationTargetEvent

        const val SET_CURL_TO_LEFT = 1

        const val SET_CURL_TO_RIGHT = 2

        /**
         * Shows one page at the center of view.
         */
        const val SHOW_ONE_PAGE = 1

        /**
         * Shows two pages side by side.
         */
        const val SHOW_TWO_PAGES = 2

        /**
         * Duration of curl animation expressed in milliseconds.
         */
        const val ANIMATION_DURATION_MILLIS = 300

        /**
         * Indicates whether touch pressure is taken into account to modify radius of curl.
         */
        const val TOUCH_PRESSURE_ENABLED = false

        /**
         * Allows curl on last page
         */
        const val ALLOW_LAST_PAGE_CURL = true

        /**
         * Number of slipts to draw within meshes to generate curls
         */
        const val MAX_CURL_SPLITS_IN_MESH = 10

        /**
         * Flag for rendering some lines used for development.
         * Shows curl position and one for the direction from the given position.
         * Comes handy one playing around with different ways for following pointer.
         */
        const val DRAW_CURL_POSITION_IN_MESH = false

        /**
         * Flag for drawing polygon outlines.
         * Seeing polygon outlines gives good insight on how original rectangle is divided.
         */
        const val DRAW_POLYGON_OUTLINES_IN_MESH = false

        /**
         * Flag for enabling shadow rendering.
         */
        const val DRAW_SHADOW_IN_MESH = true

        /**
         * Flag for texture rendering.
         */
        const val DRAW_TEXTURE_IN_MESH = true

        /**
         * Inner color for shadow.
         * Inner color is the color drawn next to surface where shadowed area starts.
         */
        val SHADOW_INNER_COLOR_IN_MESH = floatArrayOf(0.0f, 0.0f, 0.0f, 0.5f)

        /**
         * Outer color for shadow.
         * Outer color is the color the shadow ends to.
         */
        val SHADOW_OUTER_COLOR_IN_MESH = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        /**
         * Default color factor offset to make darker or clearer the area
         * of the texture close to the curl.
         * Value must be between 0.0f and 1.0f (both included).
         * The larger the value, the clearer the area will be.
         * The smaller the value, the darker the area will be.
         */
        const val DEFAULT_COLOR_FACTOR_OFFSET_IN_MESH = 0.3f
    }

    /**
     * Provider to feed 'book' with bitmaps which are used for rendering pages.
     */
    interface PageProvider {
        /**
         * Returns number of available pages.
         */
        val pageCount: Int

        /**
         * Called once new bitmaps/textures are needed. Width and height are in
         * pixels telling the size it will be drawn on screen and following them
         * ensures that aspect ratio remains. But it's possible to return bitmap
         * of any size though.
         * You should use provided CurlPage for storing page information for requested
         * page number.
         * Index is a number between 0 and [pageCount]
         */
        fun updatePage(page: CurlPage, width: Int, height: Int, index: Int)
    }

    /**
     * Observer interface for handling CurlView size changes.
     */
    interface SizeChangedObserver {
        /**
         * Called once CurlView size changes.
         */
        fun onSizeChanged(width: Int, height: Int)
    }

    /**
     * Interface to handle events when current index (i.e. visible page) completely changes.
     */
    interface CurrentIndexChangedListener {
        /**
         * Called when visible page changes.
         *
         * @param view view that raised the event.
         * @param currentIndex new currently visible page.
         */
        fun onCurrentIndexChanged(view: CurlGLSurfaceView, currentIndex: Int)
    }

    /**
     * Simple holder for pointer position.
     */
    private class PointerPosition {
        val pos: PointF = PointF()
        var pressure: Float = 0.0f
    }
}