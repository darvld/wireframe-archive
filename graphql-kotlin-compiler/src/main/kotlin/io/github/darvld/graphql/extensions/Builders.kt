@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.*
import io.github.darvld.graphql.model.GenerationEnvironment

internal inline fun buildEnum(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.enumBuilder(className).apply(builder).build()
}

internal inline fun buildClass(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.classBuilder(className).apply(builder).build()
}

internal inline fun buildFunction(name: String, builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.builder(name).apply(builder).build()
}

internal inline fun buildConstructor(builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.constructorBuilder().apply(builder).build()
}

internal inline fun FunSpec.Builder.addCode(builder: CodeBlock.Builder.() -> Unit) {
    addCode(CodeBlock.Builder().apply(builder).build())
}

internal inline fun TypeSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}

internal inline fun FunSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}

internal inline fun GenerationEnvironment.buildFile(fileName: String, builder: FileSpec.Builder.() -> Unit): FileSpec {
    return FileSpec.builder(packageName, fileName).apply(builder).build()
}

internal fun TypeSpec.pack(packageName: String): FileSpec {
    return FileSpec.get(packageName, this)
}