package io.github.darvld.graphql.analysis

import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.model.GraphQLOperation
import io.github.darvld.graphql.model.RouteData

internal fun processExtensionRoutes(definition: GraphQLObjectType): List<RouteData> = buildList {
    val extensionNames = definition.extensionDefinitions.flatMap { extension ->
        extension.fieldDefinitions.map { it.name }
    }

    addAll(definition.fieldDefinitions.asSequence().filter { it.name in extensionNames }.map { field ->
        RouteData(
            name = generateQueryName(definition.name, field.name),
            operation = GraphQLOperation.Extension(definition),
            definition = field
        )
    })

    addAll(definition.fieldDefinitions.asSequence().filter { it.arguments.isNotEmpty() }.map { field ->
        RouteData(
            name = generateQueryName(definition.name, field.name),
            operation = GraphQLOperation.Query,
            definition = field
        )
    })
}

private fun generateQueryName(typeName: String, fieldName: String): String {
    return typeName.replaceFirstChar(Char::lowercase) + fieldName.replaceFirstChar(Char::uppercase)
}