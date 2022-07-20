package io.github.darvld.wireframe.ktor.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import graphql.execution.DataFetcherResult
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.subpackage

internal val DATA_FETCHER_RESULT: ClassName = DataFetcherResult::class.asClassName()

internal fun ProcessingEnvironment.dslMarker(): ClassName {
    val projectTitle = projectName.replaceFirstChar(Char::uppercaseChar)
    return ClassName(basePackage.subpackage("routing"), "${projectTitle}RoutingDSL")
}