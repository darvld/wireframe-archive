package io.github.darvld.wireframe.analysis

import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.model.GraphQLOperation
import io.github.darvld.wireframe.model.RouteData

internal fun processExtensionRoutes(definition: GraphQLObjectType): List<RouteData> = buildList {
    val extensionNames = definition.extensionDefinitions.flatMap { extension ->
        extension.fieldDefinitions.map { it.name!! }
    }

    addAll(definition.fields.asSequence().filter { it.name in extensionNames }.map { field ->
        RouteData(
            name = generateQueryName(definition.name, field.name),
            operation = GraphQLOperation.Extension(definition),
            definition = field
        )
    })

    addAll(definition.fields.asSequence().filter { it.arguments.isNotEmpty() }.map { field ->
        RouteData(
            name = generateQueryName(definition.name, field.name),
            operation = GraphQLOperation.Query,
            definition = field
        )
    })
}