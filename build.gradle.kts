import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("java")
  id("org.openjfx.javafxplugin") version "0.0.13"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "scdk-irongoon"
version = "1.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

// This is so it picks up new builds on jitpack
configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

val lwjglVersion = "3.2.3"
var lwjglNatives = ""

if(DefaultNativePlatform.getCurrentOperatingSystem().isWindows) {
  lwjglNatives = "natives-windows"
}

if(DefaultNativePlatform.getCurrentOperatingSystem().isLinux) {
  lwjglNatives = "natives-linux"
}

if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
  lwjglNatives = "natives-macos"
}

repositories {
  mavenCentral()
  mavenLocal() // Uncomment to use mavenLocal version of LoD engine
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation("legend:lod:snapshot") // Uncomment to use mavenLocal version of LoD engine (also comment out next line)
//  implementation("com.github.Legend-of-Dragoon-Modding:Legend-of-Dragoon-Java:main-SNAPSHOT")
  implementation("com.opencsv:opencsv:5.7.1")
  runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
  runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
  runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
  runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
  runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
}

javafx {
  version = "18.0.2"
  modules("javafx.controls", "javafx.fxml")
}

sourceSets {
  main {
    java {
      srcDirs("src/main/java")
      exclude(".gradle", "build", "files")
    }
  }
}

buildscript {
  repositories {
    gradlePluginPortal()
  }
  /*dependencies {
    implementation("com.github.johnrengelman.shadow:7.1.2")
  }*/
}

apply(plugin = "com.github.johnrengelman.shadow")
apply(plugin = "java")

tasks.jar {
  exclude("*.jar")
}
