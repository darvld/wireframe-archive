package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.*
import io.github.darvld.graphql.execution.GraphQLCall
import io.github.darvld.graphql.extensions.addCode
import io.github.darvld.graphql.extensions.buildFunction
import io.github.darvld.graphql.extensions.typeNameFor
import io.github.darvld.graphql.model.GenerationEnvironment
import io.github.darvld.graphql.model.RouteData
import io.github.darvld.graphql.routing.GraphQLRoute

/**Builds a handler extension for this route.*/
internal fun RouteData.buildSpec(environment: GenerationEnvironment): FunSpec = buildFunction(name) {
    receiver(GraphQLRoute::class.asTypeName())

    addParameter(
        name = "routeHandler",
        type = LambdaTypeName.get(
            parameters = definition.arguments.map { ParameterSpec(it.name, environment.typeNameFor(it.type)) },
            returnType = environment.typeNameFor(definition.type),
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
        addStatement("routeHandler(%L)", definition.arguments.joinToString(",") { it.name })
        endControlFlow()

        add("\n")
        addStatement(
            format = "return@handler result.%M()",
            MemberName("io.github.darvld.graphql.execution", "asDataFetcherResult")
        )

        endControlFlow()
    }
}