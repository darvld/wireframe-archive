@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.graphql.model.GenerationEnvironment

internal inline fun GenerationEnvironment.buildFile(fileName: String, builder: FileSpec.Builder.() -> Unit): FileSpec {
    return FileSpec.builder(packageName, fileName).apply(builder).build()
}

internal fun Sequence<FunSpec>.pack(packageName: String, fileName: String): FileSpec {
    return FileSpec.builder(packageName, fileName).apply {
        for (function in this@pack) addFunction(function)
    }.build()
}

internal fun TypeSpec.pack(packageName: String): FileSpec {
    return FileSpec.get(packageName, this)
}