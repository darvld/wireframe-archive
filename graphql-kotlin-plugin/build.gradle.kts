plugins {
    kotlin("jvm")

    id("java-gradle-plugin")
    id("maven-publish")
}

java {
    withSourcesJar()
}

gradlePlugin {
    plugins.create("graphqlKotlinPlugin") {
        id = "io.github.darvld.graphql"
        implementationClass = "io.github.darvld.graphql.GraphQLPlugin"
    }
}

dependencies {
    implementation(projects.graphqlKotlinCompiler)
    implementation(libs.kotlin.plugin)
}
