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
        jvmMain.dependencies {
            // Note, if you develop a library, you should use compose.desktop.common.
            // compose.desktop.currentOs should be used in launcher-sourceSet
            // (in a separate module for demo project and in testMain).
            // With compose.desktop.common you will also lose @Preview functionality
            implementation(compose.desktop.currentOs)
            implementation("io.appium:java-client:9.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

            implementation(project(":shared"))
            implementation(project(":appiumDriver"))
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
        }
    }
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
