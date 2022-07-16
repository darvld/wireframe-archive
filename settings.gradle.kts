@file:Suppress("UnstableApiUsage", "LocalVariableName")

import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

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

rootProject.name = "graphql-kotlin"

include(":graphql-kotlin-ktor")
include(":graphql-kotlin-compiler")
