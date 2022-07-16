package io.github.darvld.graphql

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

abstract class GenerateWiringTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val sourcesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        if (!inputChanges.getFileChanges(sourcesRoot).any()) return
        println("Generating wiring. Sources root is ${sourcesRoot.get()}. Output directory is ${outputDir.get()}")
    }
}