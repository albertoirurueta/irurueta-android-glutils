# irurueta-android-glutils

OpenGL utilities for Android: `GLTextureView`, an OpenGL view that lives in the normal view hierarchy, plus pinhole
camera and OpenGL matrix helpers.

[![Develop](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/develop.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/develop.yml)
[![Release](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/main.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/main.yml)
[![Security](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/security.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/security.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.irurueta/irurueta-android-glutils)](https://central.sonatype.com/artifact/com.irurueta/irurueta-android-glutils)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=bugs)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=coverage)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)

## Project Status

| Item | Value |
| --- | --- |
| Language | Kotlin (Java 17 bytecode) |
| Build tool | Gradle (Kotlin DSL) |
| Current development version | `1.1.12-SNAPSHOT` |
| Latest release | `1.1.11` |
| Platform | Android |
| Min / compile SDK | `minSdk` 26, `compileSdk` 36 |
| Kotlin / AGP | Kotlin 2.3.10, Android Gradle Plugin 9.0.1 |
| License | Apache License 2.0 |
| CI | GitHub Actions: `develop` branch builds, release builds (Maven Central publish and docs deploy), weekly security scans |
| Quality | SonarCloud, JaCoCo coverage, Android Lint, Dokka API docs |

## Overview

This library contains:

- **GLTextureView**: a view that draws OpenGL scenes into a texture. Contrary to `GLSurfaceView`, `GLTextureView`
  belongs to the normal view hierarchy, so it is subject to common view animations and effects such as background
  transparency for view composition. `GLTextureView` can be extended or composed into other views, and any renderer
  compatible with `GLSurfaceView` (a `GLSurfaceView.Renderer`) is also compatible with `GLTextureView`.
- **Pinhole camera and camera matrices utilities**: helpers to convert between pinhole cameras and OpenGL
  model-view-projection matrices, and to handle camera and display orientation.

## Documentation

- [Documentation site](https://albertoirurueta.github.io/irurueta-android-glutils/) (Antora), including:
  - [Installation](docs/modules/ROOT/pages/installation.adoc)
  - [Rendering with GLTextureView](docs/modules/ROOT/pages/gltextureview.adoc)
  - [Cameras and OpenGL matrices](docs/modules/ROOT/pages/camera-matrices.adoc)
  - [Camera and display orientation](docs/modules/ROOT/pages/orientation.adoc)
  - [Sample app](docs/modules/ROOT/pages/sample-app.adoc)
  - [Building, testing and contributing](docs/modules/ROOT/pages/building.adoc)
- [API reference](https://albertoirurueta.github.io/irurueta-android-glutils/api/) (Dokka)
- [SonarCloud dashboard](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-glutils)
- [Changelog](CHANGELOG.md)

## Installation

The library is published on Maven Central. Add the dependency to your module's `build.gradle.kts`:

Latest release

```kotlin
implementation("com.irurueta:irurueta-android-glutils:1.1.11")
```

## Quick Example

Subclass `GLTextureView`, configure the EGL context and renderer once in `init`, and forward the activity lifecycle
to the view so its rendering thread is paused and resumed. `SceneRenderer` is any `GLSurfaceView.Renderer`.

```kotlin
import com.irurueta.android.glutils.GLTextureView

class SceneTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GLTextureView(context, attrs, defStyleAttr) {

    val renderer = SceneRenderer()

    init {
        // must be called before setRenderer
        setEGLContextClientVersion(2)
        // RGBA_8888 with a 16-bit depth buffer, so that the background can be transparent
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        isOpaque = false

        setRenderer(renderer)
        // only draw when requestRender() is called, to save battery
        renderMode = RENDER_MODE_WHEN_DIRTY
    }
}

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
```

See [Rendering with GLTextureView](docs/modules/ROOT/pages/gltextureview.adoc) for XML layouts, updating the scene
with `requestRender()`, and debug options.

## License

Licensed under the [Apache License 2.0](LICENSE).
