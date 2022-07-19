plugins {
    kotlin("jvm")

    id("java-gradle-plugin")
    id("maven-publish")
}

java {
    withSourcesJar()
}

gradlePlugin {
    plugins.create("Wireframe") {
        id = "io.github.darvld.wireframe"
        implementationClass = "io.github.darvld.wireframe.WireframePlugin"
    }
}

dependencies {
    implementation(projects.compiler)
    implementation(libs.kotlin.plugin)
}
