package io.github.darvld.graphql.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.model.OutputDTO

internal fun processOutputType(definition: GraphQLObjectType, packageName: String): OutputDTO {
    val generatedName = "${definition.name}DTO"

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