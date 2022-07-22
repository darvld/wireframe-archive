@file:Suppress("UnstableApiUsage", "LocalVariableName")

import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    val kotlinVersion: String by settings
    val nexusPluginVersion: String by settings

    plugins {
        kotlin("jvm").version(kotlinVersion)
        id("io.github.gradle-nexus.publish-plugin").version(nexusPluginVersion)
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "wireframe"

include(":gradle-plugin")

include(":compiler-core")
include(":compiler-ktor")
include(":compiler-mapping")

include(":runtime-ktor")
include(":runtime-mapping")
