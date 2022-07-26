package io.github.darvld.wireframe.mapping

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.extensions.isRouteType
import io.github.darvld.wireframe.mapping.extensions.mapperName
import io.github.darvld.wireframe.output

public class WireframeMappingPlugin : WireframeCompilerPlugin {
    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        if (type.isRouteType()) return

        val target = environment.resolve(type)
        val mapper = target.mapperName()

        if (type is GraphQLInputObjectType) {
            environment.output(mapper, buildInputMapper(mapper, target, type, environment))
            return
        }

        if (type is GraphQLObjectType) {
            environment.output(mapper, buildOutputMapper(mapper, target, type, environment))
        }
    }
}