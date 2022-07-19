plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(projects.ktorPlugin)
    implementation(libs.kotlinpoet)

    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("compiler") {
        from(components["java"])
    }
}

tasks.test {
    useJUnitPlatform()
}