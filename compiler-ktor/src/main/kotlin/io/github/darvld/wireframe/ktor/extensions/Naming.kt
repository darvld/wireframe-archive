package io.github.darvld.wireframe.ktor.extensions

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.extensions.isRouteType

internal fun generateQueryName(parentType: GraphQLObjectType, queryField: GraphQLFieldDefinition): String {
    if (parentType.isRouteType()) return queryField.name
    return parentType.name.replaceFirstChar(Char::lowercase) + queryField.name.replaceFirstChar(Char::uppercase)
}