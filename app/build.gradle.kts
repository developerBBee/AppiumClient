import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "jp.developer.bbee"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
        }

        jvmMain.dependencies {
            // Note, if you develop a library, you should use compose.desktop.common.
            // compose.desktop.currentOs should be used in launcher-sourceSet
            // (in a separate module for demo project and in testMain).
            // With compose.desktop.common you will also lose @Preview functionality
            implementation(compose.desktop.currentOs)
            implementation(compose.materialIconsExtended)
            implementation("io.appium:java-client:9.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "AppiumClientKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AppiumClient"
            packageVersion = "1.0.0"
        }
    }
}

kotlin {
    jvmToolchain(21)
}
