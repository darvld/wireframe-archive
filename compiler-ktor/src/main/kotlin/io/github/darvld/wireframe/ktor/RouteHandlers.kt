package io.github.darvld.wireframe.ktor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.darvld.wireframe.execution.GraphQLCall
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.generation.buildFieldExtractor
import io.github.darvld.wireframe.ktor.RouteNames.HandlerParameter
import io.github.darvld.wireframe.model.GenerationEnvironment
import io.github.darvld.wireframe.model.RouteData
import io.github.darvld.wireframe.routing.GraphQLRoute

private object RouteNames {
    const val HandlerParameter = "routeHandler"
}

/**Builds a handler extension for this route.*/
internal fun RouteData.buildSpec(environment: GenerationEnvironment): FunSpec = buildFunction(name) {
    receiver(GraphQLRoute::class.asTypeName())

    markAsGenerated()
    addAnnotation(DSL_MARKER)
    addKdoc(definition.description.orEmpty())

    addParameter(
        name = HandlerParameter,
        type = LambdaTypeName.get(
            parameters = definition.arguments.map { ParameterSpec(it.name, environment.typeNameFor(it.type)) },
            returnType = DATA_FETCHER_RESULT.parameterizedBy(environment.typeNameFor(definition.type)),
            receiver = GraphQLCall::class.asTypeName(),
        ).copy(suspending = true)
    )

    addCode {
        beginControlFlow("handler(%S, %S)", operation.graphQLTypeName, definition.name)

        definition.arguments.forEach { argument ->
            val extractor = environment.buildFieldExtractor(
                extractor = { CodeBlock.of("request.getArgument<%T>(%S)", it, argument.name) },
                argument.type,
            )

            addStatement("val·%L = %L", argument.name, extractor)
        }

        add("\n")
        addStatement("%L(%L)", HandlerParameter, definition.arguments.joinToString(",") { it.name })

        endControlFlow()
    }
}