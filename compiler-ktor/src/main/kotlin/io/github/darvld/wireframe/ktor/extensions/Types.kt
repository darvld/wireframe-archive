package io.github.darvld.wireframe.ktor.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import graphql.execution.DataFetcherResult
import io.github.darvld.wireframe.routing.GraphQLDSL

public val DATA_FETCHER_RESULT: ClassName = DataFetcherResult::class.asClassName()
public val DSL_MARKER: ClassName = GraphQLDSL::class.asClassName()