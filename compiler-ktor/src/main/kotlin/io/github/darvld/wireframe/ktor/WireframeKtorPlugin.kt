package io.github.darvld.wireframe.ktor

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin

public class WireframeKtorPlugin : WireframeCompilerPlugin {
    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        if (type is GraphQLObjectType) {
            processRoutes(type, environment)
            return
        }

        if (type is GraphQLInputObjectType) {
            processInputType(type, environment)
        }
    }
}