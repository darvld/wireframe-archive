package io.github.darvld.wireframe

import graphql.schema.GraphQLNamedType

public fun interface WireframeCompilerPlugin {
    public fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment)
}