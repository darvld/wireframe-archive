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
            name = definition.name.replaceFirstChar(Char::lowercase) + field.name.replaceFirstChar(Char::uppercase),
            operation = GraphQLOperation.Extension(definition),
            definition = field
        )
    })

    addAll(definition.fieldDefinitions.asSequence().filter { it.arguments.isNotEmpty() }.map {
        RouteData(
            name = it.name,
            operation = GraphQLOperation.Query,
            definition = it
        )
    })
}