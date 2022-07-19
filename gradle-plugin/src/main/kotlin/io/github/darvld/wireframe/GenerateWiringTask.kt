package io.github.darvld.wireframe

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.net.URLClassLoader
import java.util.*

abstract class GenerateWiringTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val sourcesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:InputFiles
    abstract val pluginJars: ConfigurableFileCollection

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        if (!inputChanges.getFileChanges(sourcesRoot).any() && !inputChanges.getFileChanges(pluginJars).any()) return

        val pluginLoader = URLClassLoader(
            /* urls = */ pluginJars.files.map { it.toURI().toURL() }.toTypedArray(),
            /* parent = */ WireframeCompilerPlugin::class.java.classLoader
        )

        val plugins = ServiceLoader.load(WireframeCompilerPlugin::class.java, pluginLoader).toList()

        val sources = sourcesRoot.asFileTree.filter { it.extension == "graphqls" }.map(File::readText)
        val generator = WireframeCompiler()
        val environment = generator.analyze(packageName.getOrElse(""), sources)

        generator.generate(environment, plugins).forEach {
            it.writeTo(outputDir.asFile.get().toPath())
        }
    }
}