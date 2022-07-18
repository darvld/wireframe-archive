package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.graphql.extensions.buildEnum
import io.github.darvld.graphql.model.EnumDTO

/**Builds a [TypeSpec] from this enum's type data.*/
internal fun EnumDTO.buildSpec(): TypeSpec = buildEnum(generatedType) {
    definition.values.forEach { addEnumConstant(it.name) }
}