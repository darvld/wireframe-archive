package io.github.darvld.wireframe.extensions

import graphql.schema.GraphQLModifiedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLNullableType
import graphql.schema.GraphQLType

public val GraphQLType.isNullable: Boolean
    get() = this is GraphQLNullableType

public fun GraphQLType.unwrapNonNull(): GraphQLType {
    return if (this is GraphQLNonNull) originalWrappedType else this
}

public tailrec fun GraphQLType.unwrapCompletely(): GraphQLType {
    return if (this is GraphQLModifiedType) wrappedType.unwrapCompletely() else this
}