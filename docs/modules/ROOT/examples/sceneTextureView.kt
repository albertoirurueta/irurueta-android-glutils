/**
 * Translucent view drawing an OpenGL ES 2.0 scene. It can be declared in an XML layout.
 */
class SceneTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GLTextureView(context, attrs, defStyleAttr) {

    val renderer = SceneRenderer()

    init {
        // must be called before setRenderer
        setEGLContextClientVersion(2)
        // RGBA_8888 with a 16-bit depth buffer, so that the background can be transparent
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        isOpaque = false

        setRenderer(renderer)
        // only draw when requestRender() is called, to save battery
        renderMode = RENDER_MODE_WHEN_DIRTY
    }
}
