package io.github.darvld.graphql

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLEnumType

internal fun generateEnumType(typeDefinition: GraphQLEnumType, packageName: String): FileSpec {
    val file = FileSpec.builder(packageName, typeDefinition.name)
    val type = TypeSpec.enumBuilder(typeDefinition.name)

    typeDefinition.values.forEach {
        type.addEnumConstant(it.name)
    }

    file.addType(type.build())
    return file.build()
}