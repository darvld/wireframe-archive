package io.github.darvld.wireframe.execution

import graphql.execution.DataFetcherResult
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.application.*

public typealias GraphQLHandler = suspend GraphQLCall.() -> DataFetcherResult<out Any?>

public class GraphQLCall(
    public val call: ApplicationCall,
    public val request: DataFetchingEnvironment,
)
