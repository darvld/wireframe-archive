package io.github.darvld.wireframe.analysis

import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLNamedType

internal const val DTO_SUFFIX = "DTO"

internal fun generateNameFor(definition: GraphQLNamedType): String {
    return when (definition) {
        is GraphQLEnumType -> definition.name
        else -> definition.name + DTO_SUFFIX
    }
}

internal fun generateQueryName(typeName: String, fieldName: String): String {
    return typeName.replaceFirstChar(Char::lowercase) + fieldName.replaceFirstChar(Char::uppercase)
}