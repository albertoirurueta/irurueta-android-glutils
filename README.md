# irurueta-android-glutils

OpenGL utilities for Android

[![Build Status](https://github.com/albertoirurueta/irurueta-android-glutils/actions/workflows/main.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-glutils/actions)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=code_smells)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=coverage)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)

[![Duplicated lines](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Lines of code](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=ncloc)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)

[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Quality gate](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=alert_status)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)

[![Security](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=security_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Technical debt](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=sqale_index)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-glutils&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-android-glutils)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.irurueta/irurueta-android-glutils/badge.svg)](https://search.maven.org/artifact/com.irurueta/irurueta-android-glutils/1.0.0/aar)

[API Documentation](http://albertoirurueta.github.io/irurueta-android-glutils)

## Overview

This library contains:

- GLTextureView: a view to to draw OpenGL scenes within a texture. Contrary to GLSurfaceView,
    GLTextureView belongs to the normal view hierarchy, allowing the view to be subject to common 
  view animations and effects such as background transparency for view composition.   
  GLTextureView can be extender or composed into other views. Any renderer compatible with 
  GLSurfaceView is also compatible with GLTextureView.
- Pinhole camera and camera matrices utilities.

## Usage

Add the following dependency to your project:

```
implementation 'com.irurueta:irurueta-android-glutils:1.0.4'
```
