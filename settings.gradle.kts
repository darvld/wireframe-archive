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

    plugins {
        kotlin("jvm").version(kotlinVersion)
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

include(":compiler")
include(":ktor-plugin")
include(":gradle-plugin")
