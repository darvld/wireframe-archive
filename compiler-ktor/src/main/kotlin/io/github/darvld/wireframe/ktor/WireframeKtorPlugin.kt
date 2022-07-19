package io.github.darvld.wireframe.ktor

import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.WireframeCompilerPlugin.Output
import io.github.darvld.wireframe.extensions.buildFile
import io.github.darvld.wireframe.model.GenerationEnvironment

public class WireframeKtorPlugin : WireframeCompilerPlugin {
    override fun processEnvironment(environment: GenerationEnvironment): Sequence<Output> = sequence {
        for (input in environment.inputTypes) {
            yield(environment.buildFile(input.name + "Decoder") {
                addFunction(input.buildDecoder(environment))
            })
        }

        for ((filename, routes) in environment.routeHandlers.groupBy { it.operation.outputFileName }) {
            yield(environment.buildFile(filename) {
                for (route in routes) addFunction(route.buildSpec(environment))
            })
        }
    }.map(::Output)
}