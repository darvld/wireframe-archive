package io.github.darvld.wireframe.ktor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.execution.GraphQLCall
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.ktor.extensions.*
import io.github.darvld.wireframe.ktor.extensions.DATA_FETCHER_RESULT
import io.github.darvld.wireframe.ktor.extensions.generateQueryName
import io.github.darvld.wireframe.output
import io.github.darvld.wireframe.routing.GraphQLRoute

private const val HandlerParameter = "routeHandler"

internal fun processRoutes(definition: GraphQLObjectType, environment: ProcessingEnvironment) {
    val operation = GraphQLOperation.operationFor(definition)
    val routes = definition.getRouteFields(operation)

    for (route in routes) environment.output(
        packageName = environment.basePackage.subpackage("routing"),
        fileName = operation.outputFileName,
        spec = buildRoute(generateQueryName(definition, route), operation, route, environment)
    )
}

internal fun buildRoute(
    name: String,
    operation: GraphQLOperation,
    definition: GraphQLFieldDefinition,
    environment: ProcessingEnvironment,
): FunSpec = buildFunction(name) {
    receiver(GraphQLRoute::class.asTypeName())

    markAsGenerated()
    addAnnotation(environment.dslMarker())
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