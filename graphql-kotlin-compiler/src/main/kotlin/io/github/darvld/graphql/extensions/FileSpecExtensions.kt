@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.graphql.model.GenerationEnvironment

internal inline fun GenerationEnvironment.buildFile(fileName: String, builder: FileSpec.Builder.() -> Unit): FileSpec {
    return FileSpec.builder(packageName, fileName).apply(builder).build()
}

internal fun TypeSpec.pack(packageName: String): FileSpec {
    return FileSpec.get(packageName, this)
}