import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(projects.compilerCore)
    implementation(projects.runtimeMapping)
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
    publications.create<MavenPublication>("compiler-mapping") {
        from(components["java"])
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}