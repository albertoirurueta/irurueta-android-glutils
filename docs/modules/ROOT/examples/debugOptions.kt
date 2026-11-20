// check glGetError after every call and log every call made by the renderer
view.debugFlags = GLTextureView.DEBUG_CHECK_GL_ERROR or GLTextureView.DEBUG_LOG_GL_CALLS

// try to keep the EGL context (and its textures, buffers and shaders) while paused
view.preserveEGLContextOnPause = true
