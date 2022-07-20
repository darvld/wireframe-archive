@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.Scalars
import graphql.schema.*
import io.github.darvld.wireframe.ProcessingEnvironment

public val IdAlias: ClassName = ClassName("io.github.darvld.wireframe.mapping", "ID")

public fun generateNameFor(type: GraphQLNamedType): String = when (type) {
    is GraphQLEnumType -> type.name
    else -> "${type.name}DTO"
}

public fun ProcessingEnvironment.typeNameFor(type: GraphQLType): TypeName {
    return when (type) {
        is GraphQLNonNull -> typeNameFor(type.originalWrappedType).nonNullable()
        is GraphQLList -> listTypeNameFor(type)
        is GraphQLScalarType -> scalarTypeNameFor(type)
        is GraphQLNamedType -> resolve(type).nullable()
        else -> throw IllegalArgumentException("Unsupported type: $this.")
    }
}

private fun ProcessingEnvironment.listTypeNameFor(type: GraphQLList): TypeName {
    return LIST.parameterizedBy(typeNameFor(type.originalWrappedType)).nullable()
}

private fun scalarTypeNameFor(type: GraphQLScalarType): TypeName = when (type) {
    Scalars.GraphQLBoolean -> BOOLEAN
    Scalars.GraphQLFloat -> FLOAT
    Scalars.GraphQLID -> IdAlias
    Scalars.GraphQLInt -> INT
    Scalars.GraphQLString -> STRING
    else -> throw NotImplementedError("Custom scalar types are not supported yet.")
}.nullable()