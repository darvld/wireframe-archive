package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLEnumType

/**Generates a [TypeSpec] for a GraphQL enum type as a Kotlin enum.*/
internal fun generateEnumType(typeDefinition: GraphQLEnumType): TypeSpec {
    val type = TypeSpec.enumBuilder(typeDefinition.name)

    typeDefinition.values.forEach {
        type.addEnumConstant(it.name)
    }

    return type.build()
}