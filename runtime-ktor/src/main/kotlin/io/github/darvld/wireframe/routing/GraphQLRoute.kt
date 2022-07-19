package io.github.darvld.wireframe.routing

import graphql.schema.DataFetcher
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import io.github.darvld.wireframe.GraphQLContext
import io.github.darvld.wireframe.execution.GraphQLCall
import io.github.darvld.wireframe.execution.GraphQLHandler
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture

public interface GraphQLRoute {
    @GraphQLDSL
    public fun handler(typeName: String, fieldName: String, handler: GraphQLHandler)

    @GraphQLDSL
    public fun resolver(typeName: String, resolver: TypeResolver)
}

@GraphQLDSL
public inline fun GraphQLRoute.query(name: String, crossinline handler: GraphQLHandler) {
    handler("Query", name) { handler() }
}

@GraphQLDSL
public inline fun GraphQLRoute.mutation(name: String, crossinline handler: GraphQLHandler) {
    handler("Mutation", name) { handler() }
}

@JvmInline
internal value class GraphQLRouteBuilder(private val wiring: RuntimeWiring.Builder) : GraphQLRoute {
    override fun handler(typeName: String, fieldName: String, handler: GraphQLHandler) {
        wiring.type(typeName) {
            it.dataFetcher(fieldName, suspendingDataFetcher(handler))
        }
    }

    override fun resolver(typeName: String, resolver: TypeResolver) {
        wiring.type(typeName) {
            it.typeResolver(resolver)
        }
    }

    private fun suspendingDataFetcher(block: GraphQLHandler): DataFetcher<Any> {
        return DataFetcher { env ->
            val scope: CoroutineScope = env.graphQlContext.get(GraphQLContext.coroutineScope)
            val applicationCall: ApplicationCall = env.graphQlContext.get(GraphQLContext.applicationCall)

            val call = GraphQLCall(applicationCall, env)

            return@DataFetcher scope.async { block(call) }.asCompletableFuture()
        }
    }
}