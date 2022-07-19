package io.github.darvld.wireframe.model

import graphql.schema.GraphQLFieldDefinition

/**Contains information about a route handler that will be generated for a given GraphQL operation.*/
public data class RouteData(
    val name: String,
    val operation: GraphQLOperation,
    val definition: GraphQLFieldDefinition,
)
