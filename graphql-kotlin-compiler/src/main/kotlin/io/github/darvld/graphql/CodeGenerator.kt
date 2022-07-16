package io.github.darvld.graphql

import com.squareup.kotlinpoet.FileSpec
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.github.darvld.graphql.utils.ExcludedNames
import java.nio.file.Path

public class CodeGenerator(
    private val packageName: String,
    private val sources: List<String>,
    private val outputDirectory: Path,
) {
    public fun generate() {
        val parser = SchemaParser()

        val registry: TypeDefinitionRegistry = sources.asSequence().map {
            parser.parse(it)
        }.fold(TypeDefinitionRegistry()) { current, next ->
            current.merge(next)
        }

        val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(
            /* typeRegistry = */ registry,
            /* wiring = */ RuntimeWiring.newRuntimeWiring().build()
        )

        val files = schema.allTypesAsList.mapNotNull { generateType(it, packageName) }

        files.forEach { it.writeTo(outputDirectory) }
    }

    @Suppress("UNUSED_PARAMETER", "SameParameterValue")
    private fun generateType(type: GraphQLNamedType, packageName: String): FileSpec? {
        // Ignore internal type definitions
        if (type.name.startsWith("_")) return null
        if (type.name in ExcludedNames) return null

        return when (type) {
            is GraphQLEnumType -> generateEnumType(type, packageName)
            is GraphQLObjectType -> generateOutputType(type, packageName)
            is GraphQLInputObjectType -> generateInputType(type, packageName)
            else -> null
        }
    }
}