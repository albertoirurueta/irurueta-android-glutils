// in a real app: OrientationHelper.getCameraDisplayOrientation(context, cameraId)
converter.orientation = CameraToDisplayOrientation.ORIENTATION_90_DEGREES

// same camera, expressed in Android view coordinates (y axis pointing downwards)
val viewCamera = converter.viewCamera
val viewCenter = converter.viewCameraCenter
