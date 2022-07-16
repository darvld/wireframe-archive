package io.github.darvld.graphql

import graphql.ExecutionResult
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.github.darvld.graphql.routing.GraphQLDSL
import io.github.darvld.graphql.routing.GraphQLRoute
import io.github.darvld.graphql.routing.GraphQLRouteBuilder
import io.github.darvld.graphql.transport.decodeRequest
import io.github.darvld.graphql.transport.encodeResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.JsonElement

internal object GraphQLContext {
    const val coroutineScope = "graphql.context.scope"
    const val applicationCall = "graphql.context.call"
}

@GraphQLDSL
public fun Route.graphql(
    path: String = "/graphql",
    schemaDefinitions: List<String> = application.loadSchemaDefinitions(),
    block: GraphQLRoute.() -> Unit,
) {
    val registry: TypeDefinitionRegistry = SchemaParser().let { parser ->
        schemaDefinitions.fold(initial = TypeDefinitionRegistry()) { current: TypeDefinitionRegistry, sdl: String ->
            current.merge(parser.parse(sdl))
        }
    }

    val wiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .apply { block(GraphQLRouteBuilder(this)) }
        .build()

    val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(registry, wiring)
    val graph = graphql.GraphQL.newGraphQL(schema).build()

    post(path) {
        val inputBuilder = decodeRequest(call.receiveText())

        if (inputBuilder == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        coroutineScope {
            val input = inputBuilder.graphQLContext {
                it.put(GraphQLContext.coroutineScope, this)
                it.put(GraphQLContext.applicationCall, call)
            }.build()

            val result: ExecutionResult = graph.executeAsync(input).await()
            val response: JsonElement = encodeResponse(result)

            call.respond(response)
        }
    }
}

public fun Application.loadSchemaDefinitions(): List<String> {
    val schema = environment.classLoader.getResource("schema.graphqls")?.readText()
        ?: throw IllegalStateException("Could not load schema definition from resources.")

    return listOf(schema)
}
