package io.github.darvld.wireframe.analysis

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.wireframe.model.InputDTO

internal fun processInputType(definition: GraphQLInputObjectType, packageName: String): InputDTO {
    val generatedName = generateNameFor(definition)

    return InputDTO(
        name = generatedName,
        generatedType = ClassName(packageName, generatedName),
        definition = definition,
    )
}