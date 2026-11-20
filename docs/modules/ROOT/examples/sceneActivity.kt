/**
 * Forwards activity lifecycle events to the view, so that its rendering thread is paused and
 * resumed.
 */
class SceneActivity : Activity() {

    private lateinit var sceneView: SceneTextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sceneView = SceneTextureView(this)
        setContentView(sceneView)
    }

    override fun onResume() {
        super.onResume()
        sceneView.onResume()
    }

    override fun onPause() {
        sceneView.onPause()
        super.onPause()
    }
}
