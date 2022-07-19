package io.github.darvld.wireframe.generation

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.darvld.wireframe.execution.GraphQLCall
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.extensions.addCode
import io.github.darvld.wireframe.extensions.buildFunction
import io.github.darvld.wireframe.extensions.typeNameFor
import io.github.darvld.wireframe.generation.RouteNames.HandlerParameter
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
        beginControlFlow("val result = %M·{", MemberName("kotlin", "runCatching"))
        addStatement("%L(%L)", HandlerParameter, definition.arguments.joinToString(",") { it.name })
        endControlFlow()

        add("\n")
        addStatement(
            format = "return@handler result.%M()",
            MemberName("io.github.darvld.wireframe.execution", "asDataFetcherResult")
        )

        endControlFlow()
    }
}