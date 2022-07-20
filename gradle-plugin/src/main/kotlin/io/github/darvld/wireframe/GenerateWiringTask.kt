package io.github.darvld.wireframe

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.net.URLClassLoader
import java.util.*
import kotlin.io.path.pathString

abstract class GenerateWiringTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val sourcesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val projectName: Property<String>

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

        val plugins = ServiceLoader.load(WireframeCompilerPlugin::class.java, pluginLoader)

        val basePackage = packageName.get()
        val rootPath = sourcesRoot.get().asFile.toPath()

        val sources = sourcesRoot.asFileTree.filter { it.extension == "graphqls" }.map {
            val sourcePackageName = rootPath.relativize(it.toPath()).pathString
                .removeSuffix(".graphqls")
                .replace(Regex("""[/\\]"""), ".")
                .removePrefix(basePackage)

            WireframeCompiler.Source(
                sdl = it.readText(),
                fileName = it.name,
                packageName = sourcePackageName
            )
        }
        val generator = WireframeCompiler()

        generator.process(
            project = projectName.get(),
            basePackage = basePackage,
            sources,
            plugins
        ).forEach {
            it.writeTo(outputDir.asFile.get().toPath())
        }
    }
}