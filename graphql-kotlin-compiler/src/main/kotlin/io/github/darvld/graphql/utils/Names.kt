package io.github.darvld.graphql.utils

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.Scalars
import graphql.schema.*

internal val ExcludedNames: List<String> by lazy {
    listOf("Query", "Mutation", "Subscription")
}

internal fun GraphQLType.typeName(packageName: String): TypeName {
    return when (this) {
        is GraphQLNonNull -> originalWrappedType.typeName(packageName).copy(nullable = false)
        is GraphQLList -> listTypeName(packageName)
        is GraphQLScalarType -> scalarTypeName().copy(nullable = true)
        is GraphQLNamedType -> ClassName(packageName, generatedName()).copy(nullable = true)
        else -> throw IllegalArgumentException("Unsupported type: $this.")
    }
}

internal fun GraphQLList.listTypeName(packageName: String): TypeName {
    return LIST.parameterizedBy(originalWrappedType.typeName(packageName))
}

internal fun GraphQLScalarType.scalarTypeName(): TypeName {
    return when (this) {
        Scalars.GraphQLBoolean -> BOOLEAN
        Scalars.GraphQLFloat -> FLOAT
        Scalars.GraphQLID -> STRING
        Scalars.GraphQLInt -> INT
        Scalars.GraphQLString -> STRING
        else -> throw NotImplementedError("Custom scalar type are not supported yet.")
    }
}

internal fun GraphQLNamedType.generatedName(): String {
    return when (this) {
        is GraphQLEnumType -> name
        else -> "${name}DTO"
    }
}