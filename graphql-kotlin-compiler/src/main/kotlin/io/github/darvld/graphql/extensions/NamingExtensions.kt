package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.Scalars
import graphql.schema.*
import io.github.darvld.graphql.model.GraphQLOperation.*

internal val IdAlias = ClassName("io.github.darvld.graphql.mapping", "ID")

internal fun GraphQLNamedType.isRouteType(): Boolean = when (name) {
    Query.name, Mutation.name, Subscription.name -> true
    else -> false
}

internal fun GraphQLType.typeName(packageName: String): TypeName {
    return when (this) {
        is GraphQLNonNull -> originalWrappedType.typeName(packageName).nonNullable()
        is GraphQLList -> listTypeName(packageName)
        is GraphQLScalarType -> scalarTypeName()
        is GraphQLNamedType -> ClassName(packageName, generatedName()).nullable()
        else -> throw IllegalArgumentException("Unsupported type: $this.")
    }
}

internal fun GraphQLList.listTypeName(packageName: String): TypeName {
    return LIST.parameterizedBy(originalWrappedType.typeName(packageName)).nullable()
}

internal fun GraphQLScalarType.scalarTypeName(): TypeName {
    return when (this) {
        Scalars.GraphQLBoolean -> BOOLEAN
        Scalars.GraphQLFloat -> FLOAT
        Scalars.GraphQLID -> IdAlias
        Scalars.GraphQLInt -> INT
        Scalars.GraphQLString -> STRING
        else -> throw NotImplementedError("Custom scalar type are not supported yet.")
    }.nullable()
}

internal fun GraphQLNamedType.generatedName(): String {
    return when (this) {
        is GraphQLEnumType -> name
        else -> "${name}DTO"
    }
}