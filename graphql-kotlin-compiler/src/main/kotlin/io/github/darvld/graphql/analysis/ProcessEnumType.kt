package io.github.darvld.graphql.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLEnumType
import io.github.darvld.graphql.model.EnumDTO

internal fun processEnumType(definition: GraphQLEnumType, packageName: String): EnumDTO {
    val generatedName = generateNameFor(definition)

    return EnumDTO(
        name = generatedName,
        generatedType = ClassName(packageName, generatedName),
        definition = definition
    )
}