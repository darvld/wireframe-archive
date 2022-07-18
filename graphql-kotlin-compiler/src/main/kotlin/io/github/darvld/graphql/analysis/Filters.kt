package io.github.darvld.graphql.analysis

import graphql.schema.GraphQLNamedType

internal fun GraphQLNamedType.ignoreForAnalysis(): Boolean {
    return name.startsWith("_")
}