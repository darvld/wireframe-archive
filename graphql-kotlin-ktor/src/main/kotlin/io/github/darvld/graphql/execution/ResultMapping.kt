package io.github.darvld.graphql.execution

import graphql.execution.DataFetcherResult

public fun <T> Result<T>.asDataFetcherResult(): DataFetcherResult<T> {
    return DataFetcherResult.newResult<T>().apply {
        getOrNull()?.let { data(it) }
        exceptionOrNull()?.let { error(it.message ?: "No error message") }
    }.build()
}