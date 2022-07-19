package io.github.darvld.wireframe.mapping

import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.WireframeCompilerPlugin.Output
import io.github.darvld.wireframe.extensions.buildFile
import io.github.darvld.wireframe.model.GenerationEnvironment

public class WireframeMappingPlugin : WireframeCompilerPlugin {
    override fun processEnvironment(environment: GenerationEnvironment): Sequence<Output> = sequence {
        for (input in environment.inputTypes) {
            yield(environment.buildFile(input.mapperName()) {
                addType(input.buildMapper(environment))
            })
        }

        for (output in environment.outputTypes) {
            yield(environment.buildFile(output.mapperName()) {
                addType(output.buildMapper(environment))
            })
        }
    }.map(::Output)
}