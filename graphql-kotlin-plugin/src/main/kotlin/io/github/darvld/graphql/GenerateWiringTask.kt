package io.github.darvld.graphql

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

abstract class GenerateWiringTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val sourcesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        if (!inputChanges.getFileChanges(sourcesRoot).any()) return

        val sources = sourcesRoot.asFileTree.filter { it.extension == "graphqls" }.map(File::readText)

        val generator = GraphQLCodeGenerator()
        val environment = generator.analyze(packageName.getOrElse(""), sources)

        generator.generate(environment).forEach {
            it.writeTo(outputDir.asFile.get().toPath())
        }
    }
}