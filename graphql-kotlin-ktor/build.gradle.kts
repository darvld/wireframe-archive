import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    api(libs.graphql.java)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.serialization)

    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("runtime") {
        from(components["java"])
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}