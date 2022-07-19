package io.github.darvld.wireframe.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.model.OutputDTO

internal fun processOutputType(definition: GraphQLObjectType, packageName: String): OutputDTO {
    val generatedName = generateNameFor(definition)

    val extensions = definition.extensionDefinitions.flatMap { extension ->
        extension.fieldDefinitions.map { it.name!! }
    }

    return OutputDTO(
        name = generatedName,
        generatedType = ClassName(packageName, generatedName),
        definition = definition,
        extensionNames = extensions,
    )
}