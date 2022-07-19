import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(libs.graphql.java)
    implementation(libs.kotlinpoet)
    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

publishing {
    publications.create<MavenPublication>("compiler-core") {
        from(components["java"])
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
}