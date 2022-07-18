package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.*
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.execution.GraphQLCall
import io.github.darvld.graphql.extensions.typeName
import io.github.darvld.graphql.model.GenerationEnvironment
import io.github.darvld.graphql.model.GraphQLOperation
import io.github.darvld.graphql.routing.GraphQLRoute

/**Generates the route handlers contained in this type. The [handlerType] *must* be one of [GraphQLOperation] types.*/
internal fun GenerationEnvironment.generateRouteHandlers(handlerType: GraphQLObjectType): Sequence<FunSpec> {
    return handlerType.fields.asSequence().map { route ->
        generateHandler(GraphQLOperation.valueOf(handlerType.name), route)
    }
}

private fun GenerationEnvironment.generateHandler(
    handlerType: GraphQLOperation,
    handlerDefinition: GraphQLFieldDefinition,
): FunSpec {
    val builder = FunSpec.builder(handlerDefinition.name)
    builder.receiver(GraphQLRoute::class.asTypeName())

    builder.addParameter(
        name = "handler",
        type = LambdaTypeName.get(
            parameters = handlerDefinition.arguments.map { ParameterSpec(it.name, it.type.typeName(packageName)) },
            returnType = handlerDefinition.type.typeName(packageName),
            receiver = GraphQLCall::class.asTypeName()
        ).copy(suspending = true)
    )

    val body = CodeBlock.builder()
    body.beginControlFlow("%M(%S)", handlerType.routeExtension, handlerDefinition.name)

    handlerDefinition.arguments.forEach { argument ->
        val extractor = buildFieldExtractor(
            extractor = { CodeBlock.of("request.getArgument<%T>(%S)", it, argument.name) },
            argument.type,
        )

        body.addStatement("val·%L = %L", argument.name, extractor)
    }

    body.add("\n")
    body.beginControlFlow("val result = %M·{", MemberName("kotlin", "runCatching"))
    body.addStatement("handler(%L)", handlerDefinition.arguments.joinToString(",") { it.name })
    body.endControlFlow()

    body.add("\n")
    body.addStatement(
        format = "return@%L result.%M()",
        handlerType.routeExtension.simpleName,
        MemberName("io.github.darvld.graphql.execution", "asDataFetcherResult")
    )

    body.endControlFlow()
    builder.addCode(body.build())
    return builder.build()
}
