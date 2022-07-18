package io.github.darvld.graphql.extensions

import graphql.schema.GraphQLModifiedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLNullableType
import graphql.schema.GraphQLType

internal val GraphQLType.isNullable: Boolean
    get() = this is GraphQLNullableType

internal fun GraphQLType.unwrapNonNull(): GraphQLType {
    return if (this is GraphQLNonNull) originalWrappedType else this
}

internal tailrec fun GraphQLType.unwrapCompletely(): GraphQLType {
    return if (this is GraphQLModifiedType) wrappedType.unwrapCompletely() else this
}