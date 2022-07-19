package io.github.darvld.wireframe

import com.squareup.kotlinpoet.FileSpec
import io.github.darvld.wireframe.model.GenerationEnvironment
import java.nio.file.Path

public interface WireframeCompilerPlugin {
    /**Encapsulates an output element from the code generator. Use it to write the generated code to an [Appendable],
     * or as a file in a target directory.*/
    @JvmInline
    public value class Output(private val spec: FileSpec) {
        public val name: String
            get() = spec.name

        public val packageName: String
            get() = spec.packageName

        public fun writeTo(directory: Path) {
            spec.writeTo(directory)
        }

        public fun writeTo(out: Appendable) {
            spec.writeTo(out)
        }
    }

    public fun processEnvironment(environment: GenerationEnvironment): Sequence<Output>
}