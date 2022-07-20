package io.github.darvld.wireframe.ktor

import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.ktor.extensions.dslMarker
import io.github.darvld.wireframe.output

public class WireframeKtorPlugin : WireframeCompilerPlugin {
    override fun beforeProcessing(environment: ProcessingEnvironment) {
        val dslName = environment.dslMarker()
        val spec = TypeSpec.annotationBuilder(dslName)
            .addAnnotation(DslMarker::class)
            .build()

        environment.output(dslName, spec)
    }

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