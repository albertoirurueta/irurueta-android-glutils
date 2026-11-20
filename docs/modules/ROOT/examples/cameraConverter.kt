val converter = CameraConverter(width, height)
converter.isModelViewProjectionMatrixComputationEnabled = true
converter.setNearFarPlanes(0.1f, 100.0f)

// focal length (in pixels) and principal point (in the center of the viewport)
val focalLength = 1.5 * width
val intrinsics = PinholeCameraIntrinsicParameters(
    focalLength, focalLength, width / 2.0, height / 2.0, 0.0
)
// camera without rotation, placed 5 units away from the origin along the z axis
val rotation = Quaternion()
val center = InhomogeneousPoint3D(0.0, 0.0, 5.0)
converter.camera = PinholeCamera(intrinsics, rotation, center)

// 16-length arrays in column-major order, ready for glUniformMatrix4fv
val projectionMatrix = converter.projectionMatrix
val modelViewMatrix = converter.modelViewMatrix
val modelViewProjectionMatrix = converter.modelViewProjectionMatrix
