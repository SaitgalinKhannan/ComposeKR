import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val ktorVersion = "2.1.3"
group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.slf4j:slf4j-api:2.0.3")
                implementation("com.sksamuel.scrimage:scrimage-core:4.0.32")
                implementation("com.sksamuel.scrimage:scrimage-filters:4.0.32")
                implementation("net.imagej:ij:1.53v")
                implementation("com.google.api-client:google-api-client:2.0.0")
                implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
                implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ComposeKR"
            packageVersion = "1.0.0"
        }
    }
}
