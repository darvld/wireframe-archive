plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.graphqlKotlinKtor)
    implementation(libs.kotlinpoet)

    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}