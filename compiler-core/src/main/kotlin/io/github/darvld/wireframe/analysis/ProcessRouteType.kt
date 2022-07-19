package io.github.darvld.wireframe.analysis

import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.model.GraphQLOperation
import io.github.darvld.wireframe.model.RouteData

internal fun processRouteType(definition: GraphQLObjectType): List<RouteData> {
    val routeKind = GraphQLOperation.operationFor(definition)

    return definition.fields.map { routeDefinition ->
        RouteData(routeDefinition.name, routeKind, routeDefinition)
    }
}