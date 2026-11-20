val camera = OpenGlToCameraHelper.computePinholeCameraAndReturnNew(
    projectionMatrix, viewMatrix, width, height
)
camera.decompose()
val intrinsics = camera.intrinsicParameters
val center = camera.cameraCenter
val rotation = camera.cameraRotation
