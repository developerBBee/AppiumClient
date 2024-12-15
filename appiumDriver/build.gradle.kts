plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.appium:java-client:9.3.0")
    testImplementation(kotlin("test"))

    implementation(project(":shared"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}