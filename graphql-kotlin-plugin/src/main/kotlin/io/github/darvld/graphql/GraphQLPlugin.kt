package io.github.darvld.graphql

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File
import javax.inject.Inject

class GraphQLPlugin @Inject constructor(
    private val objectFactory: ObjectFactory,
) : Plugin<Project> {
    override fun apply(target: Project) {
        configureSourceSets(target)
    }

    private fun configureSourceSets(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->
            val name = "graphql${sourceSet.name.capitalized()}Wiring"
            val sourceDirectorySet = objectFactory.sourceDirectorySet(name, "GraphQL")

            // Set the sources for the GraphQL schema
            sourceDirectorySet.srcDir("src/${sourceSet.name}/graphql")

            // Get the generation task name for this source set
            val taskName = sourceSet.getTaskName("generate", "Wiring")

            // Setup output directory
            val outputDir = project.buildDir.resolve("generated/kotlin/${sourceSet.name}")

            // Register the code generation task
            project.tasks.register(taskName, GenerateWiringTask::class.java) {
                it.description = "Generate kotlin sources for GraphQL definitions."

                it.sourcesRoot.set(File(sourceDirectorySet.sourceDirectories.asPath))
                it.outputDir.set(outputDir)
            }

            // Configure task to run on every build
            project.tasks.named(sourceSet.compileJavaTaskName) {
                it.dependsOn(taskName)
            }

            // Add generated sources to the corresponding kotlin source set
            project.kotlinExtension.sourceSets.getByName(sourceSet.name).kotlin.srcDir(outputDir)
        }
    }
}