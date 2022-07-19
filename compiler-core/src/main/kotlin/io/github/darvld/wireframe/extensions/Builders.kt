@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.*
import io.github.darvld.wireframe.model.GenerationEnvironment

public inline fun buildEnum(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.enumBuilder(className).apply(builder).build()
}

public inline fun buildClass(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.classBuilder(className).apply(builder).build()
}

public inline fun buildFunction(name: String, builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.builder(name).apply(builder).build()
}

public inline fun buildConstructor(builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.constructorBuilder().apply(builder).build()
}

public inline fun FunSpec.Builder.addCode(builder: CodeBlock.Builder.() -> Unit) {
    addCode(CodeBlock.Builder().apply(builder).build())
}

public inline fun TypeSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}

public inline fun FunSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}

public inline fun GenerationEnvironment.buildFile(
    fileName: String,
    subpackage: String = "",
    builder: FileSpec.Builder.() -> Unit,
): FileSpec {
    val finalPackageName = packageName + subpackage.takeIf { it.isNotEmpty() }?.let { ".$it" }.orEmpty()
    return FileSpec.builder(finalPackageName, fileName).apply(builder).build()
}

public fun TypeSpec.pack(packageName: String): FileSpec {
    return FileSpec.get(packageName, this)
}