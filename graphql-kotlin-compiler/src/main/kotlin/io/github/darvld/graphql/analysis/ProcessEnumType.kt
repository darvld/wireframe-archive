package io.github.darvld.graphql.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLEnumType
import io.github.darvld.graphql.model.EnumDTO

internal fun processEnumType(definition: GraphQLEnumType, packageName: String): EnumDTO {
    return EnumDTO(
        name = definition.name,
        generatedType = ClassName(packageName, definition.name),
        definition = definition
    )
}