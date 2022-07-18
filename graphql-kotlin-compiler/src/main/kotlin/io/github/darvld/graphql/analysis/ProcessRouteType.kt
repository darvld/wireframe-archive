package io.github.darvld.graphql.analysis

import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.model.GraphQLOperation
import io.github.darvld.graphql.model.RouteData

internal fun processRouteType(definition: GraphQLObjectType): List<RouteData> {
    val routeKind = GraphQLOperation.operationFor(definition)

    return definition.fields.map { routeDefinition ->
        RouteData(routeDefinition.name, routeKind, routeDefinition)
    }
}