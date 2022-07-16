package io.github.darvld.graphql

import org.junit.jupiter.api.Test
import kotlin.io.path.createTempDirectory

class CodeGeneratorTest {
    @Test
    fun `test generation`() {
        val sources = listOf(CodeGenerator::class.java.classLoader.getResource("sample.graphqls")!!.readText())

        val outputDirectory = createTempDirectory()
        println("Using directory: $outputDirectory")

        CodeGenerator(packageName = "", sources, outputDirectory).generate()

        println("Done")
    }
}