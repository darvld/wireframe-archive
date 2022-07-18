package io.github.darvld.graphql.analysis

import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.model.GraphQLOperation
import io.github.darvld.graphql.model.RouteData

internal fun processRouteType(definition: GraphQLObjectType): List<RouteData> {
    val routeKind = GraphQLOperation.valueOf(definition.name)

    return definition.fieldDefinitions.map { routeDefinition ->
        RouteData(routeDefinition.name, routeKind, routeDefinition)
    }
}