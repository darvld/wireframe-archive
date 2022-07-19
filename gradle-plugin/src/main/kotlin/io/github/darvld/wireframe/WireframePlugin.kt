package io.github.darvld.wireframe

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

@Suppress("unused")
class WireframePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureExtension(target)
        configureSourceSets(target)
    }

    private fun configureExtension(project: Project) {
        val wiring = project.extensions.create(WiringExtension.ProjectExtensionName, WiringExtension::class.java)

        wiring.packageName.set(project.group.toString())
    }

    private fun configureSourceSets(project: Project) {
        val wiring = project.extensions.getByName(WiringExtension.ProjectExtensionName) as WiringExtension

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName("main") { sourceSet ->
            // Set the sources for the GraphQL schema
            val sourceSetRoot = sourceSet.allSource.sourceDirectories.firstOrNull()

            // Get the generation task name for this source set
            val taskName = sourceSet.getTaskName("generate", "Wiring")

            // Setup output directory
            val outputDir = project.buildDir.resolve("generated/kotlin/${sourceSet.name}")

            // Register the code generation task
            project.tasks.register(taskName, GenerateWiringTask::class.java) { task ->
                task.description = "Generate kotlin sources for GraphQL definitions."
                task.packageName.set(wiring.packageName)

                val sources = wiring.sourcesRoot.orNull
                if (sources != null) {
                    task.sourcesRoot.set(sources)
                } else {
                    task.sourcesRoot.set(sourceSetRoot)
                }

                val output = wiring.outputDirectory.orNull
                if (output != null) {
                    task.outputDir.set(output)
                } else {
                    task.outputDir.set(outputDir)
                }
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