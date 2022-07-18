package io.github.darvld.graphql.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.graphql.model.InputDTO

internal fun processInputType(definition: GraphQLInputObjectType, packageName: String): InputDTO {
    val generatedName = "${definition.name}DTO"

    return InputDTO(
        name = generatedName,
        generatedType = ClassName(packageName, generatedName),
        definition = definition,
    )
}