package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.extensions.idTypeAlias
import io.github.darvld.wireframe.extensions.isRouteType

public class WireframeBasePlugin : WireframeCompilerPlugin {
    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        if (type.isRouteType()) return

        if (type is GraphQLEnumType) {
            processEnumType(type, environment)
            return
        }

        if (type is GraphQLInputObjectType) {
            processInputType(type, environment)
            return
        }

        if (type is GraphQLObjectType) {
            processOutputType(type, environment)
        }
    }

    override fun beforeProcessing(environment: ProcessingEnvironment) {
        val typeName = environment.idTypeAlias()
        val spec = TypeAliasSpec.builder(typeName.simpleName, STRING).build()

        environment.output(typeName.packageName, typeName.simpleName) { addTypeAlias(spec) }
    }
}
