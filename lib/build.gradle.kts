import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.publish)
}

val libraryVersion = "1.1.12"

android {
    namespace = "com.irurueta.android.glutils"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testOptions.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val buildNumber = System.getenv("BUILD_NUMBER") ?: ""
        val apkPrefixLabels = listOf("glutils", libraryVersion, buildNumber)
        base.archivesName = apkPrefixLabels.filter({ it != "" }) .joinToString("-")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    packaging {
        resources {
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/LICENSE.md")
        }
    }
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dokka {
    dokkaSourceSets {
        // Since Dokka 2.2 with AGP 9, Dokka also creates a source set per Android variant,
        // which shares src/main/java with the 'main' (androidJvm) source set and makes
        // the pre-generation check fail. Only the 'main' source set is documented.
        // See https://github.com/Kotlin/dokka/issues/3701 and
        // https://github.com/Kotlin/dokka/issues/4458
        named("release") {
            suppress.set(true)
        }
    }
}

sonar {
    properties {
        property("sonar.scanner.skipJreProvisioning", true)
        property("sonar.projectKey", "albertoirurueta_irurueta-android-glutils")
        property("sonar.projectName", "irurueta-android-glutils")
        property("sonar.organization", "albertoirurueta-github")
        property("sonar.host.url", "https://sonarcloud.io")

        // Absolute paths: the Sonar Gradle plugin (7.6+) appends the Android source folders it detects as absolute
        // paths and only deduplicates them against absolute values. A relative path here would be kept twice and
        // fail the analysis with "File ... can't be indexed twice".
        property("sonar.tests",
            listOf(file("src/test/java").absolutePath, file("src/androidTest/java").absolutePath))
        property("sonar.test.inclusions",
            listOf("**/*Test*/**", "src/androidTest/**", "src/test/**"))
        property("sonar.test.exclusions",
            listOf("**/*Test*/**", "src/androidTest/**", "src/test/**"))
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", file("src/main/java").absolutePath)
        property("sonar.exclusions", "**/*Test*/**,*.json,'**/*test*/**,**/.gradle/**,**/R.class")

        val libraries = project.android.sdkDirectory.path + "/platforms/android-36/android.jar"
        property("sonar.libraries", libraries)
        property("sonar.java.libraries", libraries)
        property("sonar.java.test.libraries", libraries)
        property("sonar.binaries", "build/intermediates/javac/debug/classes,build/tmp/kotlin-classes/debug")
        property("sonar.java.binaries", "build/intermediates/javac/debug/classes,build/tmp/kotlin-classes/debug")

        property("sonar.coverage.jacoco.xmlReportPaths",
            listOf("build/reports/coverage/androidTest/debug/connected/report.xml",
                "build/reports/coverage/test/report.xml"))
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.junit.reportsPath",
            listOf("build/test-results/testDebugUnitTest",
                "build/outputs/androidTest-results/connected/debug"))
        property("sonar.android.lint.report", "build/reports/lint-results-debug.xml")
    }
}

dependencies {
    implementation(libs.material)
    api(libs.geometry)
    testImplementation(libs.khronos)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.irurueta.test.utils)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.mockk.android)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary(
        // the published variant
        variant = "release",
        // whether to publish a sources jar
        sourcesJar = true,
        // whether to publish a javadoc jar
        publishJavadocJar = true,
    ))

    publishToMavenCentral()
    signAllPublications()

    coordinates("com.irurueta", "irurueta-android-glutils", libraryVersion)

    pom {
        name.set("irurueta-android-glutils")
        description.set("OpenGL utilities for Android")
        inceptionYear.set("2025")
        url.set("https://github.com/albertoirurueta/irurueta-android-glutils/")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/albertoirurueta/irurueta-android-glutils/blob/main/LICENSE")
                distribution.set("https://github.com/albertoirurueta/irurueta-android-glutils/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("albertoirurueta")
                name.set("Alberto Irurueta")
                email.set("alberto@irurueta.com")
                url.set("https://github.com/albertoirurueta/")
            }
        }
        scm {
            url.set("https://github.com/albertoirurueta/irurueta-android-glutils/")
            connection.set("scm:git:github.com/albertoirurueta/irurueta-android-glutils.git")
            developerConnection.set("scm:git:ssh://github.com/albertoirurueta/irurueta-android-glutils.git")
        }
    }
}
