package io.github.darvld.wireframe.ktor.extensions

import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ktor.GraphQLOperation.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal fun generateQueryName(typeName: String, fieldName: String): String {
    return typeName.replaceFirstChar(Char::lowercase) + fieldName.replaceFirstChar(Char::uppercase)
}

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