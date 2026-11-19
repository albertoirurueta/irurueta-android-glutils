// runs on the GL thread, so the renderer state is never touched concurrently
view.queueEvent { renderer.camera = camera }
// with RENDER_MODE_WHEN_DIRTY, a new frame is only drawn when requested
view.requestRender()
