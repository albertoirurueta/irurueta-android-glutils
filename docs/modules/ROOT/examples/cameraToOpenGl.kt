val projectionMatrix = CameraToOpenGlHelper.computeProjectionMatrixAndReturnNew(
    camera, width, height, 0.1f, 100.0f
)
val modelViewMatrix = CameraToOpenGlHelper.computeModelViewMatrixAndReturnNew(camera)
val modelViewProjectionMatrix =
    CameraToOpenGlHelper.computeModelViewProjectionMatrixAndReturnNew(
        projectionMatrix, modelViewMatrix
    )
