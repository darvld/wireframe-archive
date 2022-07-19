import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    api(libs.graphql.java)
    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility= JavaVersion.VERSION_11
}

publishing {
    publications.create<MavenPublication>("runtime-mapping") {
        from(components["java"])
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}