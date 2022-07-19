package io.github.darvld.wireframe.analysis

import graphql.schema.GraphQLNamedType

internal fun GraphQLNamedType.ignoreForAnalysis(): Boolean {
    return name.startsWith("_")
}