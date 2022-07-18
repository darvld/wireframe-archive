package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

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