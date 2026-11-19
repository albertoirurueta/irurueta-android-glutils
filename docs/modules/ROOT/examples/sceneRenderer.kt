/**
 * Any GLSurfaceView.Renderer works with GLTextureView.
 */
class SceneRenderer : GLSurfaceView.Renderer {

    // only modified on the GL thread, through GLTextureView.queueEvent
    var camera: PinholeCamera? = null

    var lastModelViewProjectionMatrix: FloatArray? = null
        private set

    private var converter: CameraConverter? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // fully transparent background, so that views behind this one remain visible
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        // compile shaders, create buffers and textures here
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val converter = CameraConverter(width, height)
        converter.isModelViewProjectionMatrixComputationEnabled = true
        this.converter = converter
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val converter = converter ?: return
        val camera = camera ?: return
        if (converter.camera !== camera) {
            converter.camera = camera
        }
        val modelViewProjectionMatrix = converter.modelViewProjectionMatrix ?: return
        drawScene(modelViewProjectionMatrix)
    }

    private fun drawScene(modelViewProjectionMatrix: FloatArray) {
        lastModelViewProjectionMatrix = modelViewProjectionMatrix
        // use your shader program, upload the matrix with
        // GLES20.glUniformMatrix4fv(mvpHandle, 1, false, modelViewProjectionMatrix, 0)
        // and draw your geometry
    }
}
