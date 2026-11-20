val view = GLTextureView(context)
view.setEGLContextClientVersion(2)
view.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
view.isOpaque = false
view.setRenderer(renderer)
view.renderMode = GLTextureView.RENDER_MODE_WHEN_DIRTY
container.addView(view)

// GLTextureView is a regular view: it can be animated, faded or transformed
view.animate().alpha(0.5f).rotation(45.0f).setDuration(300L)
