# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.11] - 2026-03-05

### Changed

- Upgraded the build to Android Gradle Plugin 9.0.1, Gradle 9.4.0 and Kotlin 2.3.10.
- Updated `irurueta-geometry` (exposed as an `api` dependency) to 1.5.0.
- Updated Compose BOM, Robolectric, MockK, Sonar and Maven publish plugin versions.

## [1.1.10] - 2025-12-27

### Changed

- Raised `minSdk` from 24 to 26.
- Upgraded Kotlin to 2.3.0 and Android Gradle Plugin to 8.13.2.
- Updated `irurueta-geometry` (exposed as an `api` dependency) to 1.4.0.
- Updated Dokka, Sonar, MockK, Compose BOM and Maven publish plugin versions.

## [1.1.9] - 2025-09-23

### Changed

- Upgraded Kotlin to 2.2.20 and Android Gradle Plugin to 8.13.0.
- Updated `irurueta-geometry` (exposed as an `api` dependency) to 1.3.2.
- Updated AndroidX test, Material, Compose BOM, Robolectric and Sonar versions.

## [1.1.8] - 2025-07-29

### Changed

- Dependency-only release: updated the `irurueta-android-test-utils` test dependency to 1.3.0. No library source
  changes.

## [1.1.6] - 2025-07-27

### Changed

- Unit tests now use the shared `irurueta-android-test-utils` library instead of a local test helper; MockK
  updated to 1.14.5. No library source changes.

## [1.1.5] - 2025-07-19

### Changed

- Migrated Kotlin JVM target configuration to the `compilerOptions` DSL.
- Updated Compose BOM and Maven publish plugin versions. No library source changes.

## [1.1.4] - 2025-07-14

### Changed

- Upgraded Kotlin to 2.2.0, Android Gradle Plugin to 8.11.1 and Gradle to 8.14.3.
- Updated Maven Central publishing configuration for the Maven publish plugin 0.33.0.
- Updated Robolectric and Compose BOM versions. No library source changes.

## [1.1.3] - 2025-06-16

### Changed

- Build tooling only: updated Gradle wrapper to 8.14.2. No library source changes.

## [1.1.2] - 2025-06-12

### Fixed

- Corrected the published Maven POM description to "OpenGL utilities for Android".

## [1.1.1] - 2025-06-10

### Added

- Complete KDoc documentation for `GLTextureView` and its internal classes.

## [1.1.0] - 2025-06-10

### Changed

- **Breaking:** renamed `GLTextureView.RENDERMODE_WHEN_DIRTY` and `GLTextureView.RENDERMODE_CONTINUOUSLY` to
  `RENDER_MODE_WHEN_DIRTY` and `RENDER_MODE_CONTINUOUSLY`.
- **Breaking:** raised `minSdk` from 21 to 24; now compiled against SDK 36 and targeting Java 17.
- Migrated the build to Gradle Kotlin DSL with a version catalog and the Vanniktech Maven publish plugin.
- Updated `irurueta-geometry` (exposed as an `api` dependency) from 1.1.0 to 1.3.0.
- Added a Compose sample app and reworked the unit test suite.

## [1.0.9] - 2021-12-20

### Changed

- Publishing workflow update only. No library source changes.

## [1.0.8] - 2021-12-19

### Removed

- Unused `vertex_shader` and `fragment_shader` raw resources from the library.

## [1.0.7] - 2021-12-18

### Changed

- `irurueta-geometry` is now exposed as an `api` dependency, so its types are available to consumers without
  declaring it explicitly.

## [1.0.6] - 2021-12-16

### Changed

- Maven Central publishing workflow fixes only. No library source changes.

## [1.0.5] - 2021-12-16

### Changed

- Maven Central publishing workflow fixes only. No library source changes.

## [1.0.4] - 2021-12-16

### Changed

- Updated Kotlin Gradle plugin to 1.6.10 and added extra publication logging. No library source changes.

## [1.0.3] - 2021-12-16

### Changed

- Increased Maven publication timeouts. No library source changes.

## [1.0.2] - 2021-12-15

### Changed

- Set the library `versionName` and removed the changing-module flag from the `irurueta-geometry` dependency.
- Updated `kotlin-reflect` test dependency to 1.6.0.

## [1.0.1] - 2021-12-15

### Changed

- Sample app dependency update (ConstraintLayout). No library source changes.

## [1.0.0] - 2021-12-15

### Added

- Initial release.
- `GLTextureView`: a `TextureView`-based view for rendering OpenGL ES scenes inside the normal view hierarchy,
  compatible with any `GLSurfaceView.Renderer`.
- `CameraToOpenGlHelper` and `OpenGlToCameraHelper` to convert between pinhole camera parameters and OpenGL
  projection/view matrices.
- `CameraConverter`, `CameraToDisplayOrientation` and `OrientationHelper` utilities for camera matrices and
  camera-to-display orientation handling.

[Unreleased]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.11...HEAD
[1.1.11]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.10...1.1.11
[1.1.10]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.9...1.1.10
[1.1.9]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.8...1.1.9
[1.1.8]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.6...1.1.8
[1.1.6]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.5...1.1.6
[1.1.5]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.4...1.1.5
[1.1.4]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.3...1.1.4
[1.1.3]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.2...1.1.3
[1.1.2]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.1...1.1.2
[1.1.1]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.9...1.1.0
[1.0.9]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.8...1.0.9
[1.0.8]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.7...1.0.8
[1.0.7]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.6...1.0.7
[1.0.6]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.5...1.0.6
[1.0.5]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.4...1.0.5
[1.0.4]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.3...1.0.4
[1.0.3]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.2...1.0.3
[1.0.2]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/albertoirurueta/irurueta-android-glutils/compare/1.0.0b...1.0.1
[1.0.0]: https://github.com/albertoirurueta/irurueta-android-glutils/releases/tag/1.0.0b
