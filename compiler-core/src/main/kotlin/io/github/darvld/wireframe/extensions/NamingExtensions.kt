@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.Scalars
import graphql.schema.*
import io.github.darvld.wireframe.model.GenerationEnvironment
import io.github.darvld.wireframe.model.GraphQLOperation.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public val IdAlias: ClassName = ClassName("io.github.darvld.wireframe.mapping", "ID")

@OptIn(ExperimentalContracts::class)
public fun GraphQLNamedType.isRouteType(): Boolean {
    contract {
        returns(true) implies (this@isRouteType is GraphQLObjectType)
    }

    if (this !is GraphQLObjectType) return false

    return when (name) {
        Query.graphQLTypeName, Mutation.graphQLTypeName, Subscription.graphQLTypeName -> true
        else -> false
    }
}

public inline fun GenerationEnvironment.typeNameFor(type: GraphQLType): TypeName {
    return type.typeName(packageName)
}

public fun GraphQLType.typeName(packageName: String): TypeName {
    return when (this) {
        is GraphQLNonNull -> originalWrappedType.typeName(packageName).nonNullable()
        is GraphQLList -> listTypeName(packageName)
        is GraphQLScalarType -> scalarTypeName()
        is GraphQLNamedType -> ClassName(packageName, generatedName()).nullable()
        else -> throw IllegalArgumentException("Unsupported type: $this.")
    }
}

public fun GraphQLList.listTypeName(packageName: String): TypeName {
    return LIST.parameterizedBy(originalWrappedType.typeName(packageName)).nullable()
}

public fun GraphQLScalarType.scalarTypeName(): TypeName {
    return when (this) {
        Scalars.GraphQLBoolean -> BOOLEAN
        Scalars.GraphQLFloat -> FLOAT
        Scalars.GraphQLID -> IdAlias
        Scalars.GraphQLInt -> INT
        Scalars.GraphQLString -> STRING
        else -> throw NotImplementedError("Custom scalar type are not supported yet.")
    }.nullable()
}

public fun GraphQLNamedType.generatedName(): String {
    return when (this) {
        is GraphQLEnumType -> name
        else -> "${name}DTO"
    }
}