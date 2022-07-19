package io.github.darvld.wireframe.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLEnumType
import io.github.darvld.wireframe.model.EnumDTO

internal fun processEnumType(definition: GraphQLEnumType, packageName: String): EnumDTO {
    val generatedName = generateNameFor(definition)

    return EnumDTO(
        name = generatedName,
        generatedType = ClassName(packageName, generatedName),
        definition = definition
    )
}