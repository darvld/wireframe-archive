package io.github.darvld.wireframe.generation

import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.wireframe.extensions.buildEnum
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.model.EnumDTO

/**Builds a [TypeSpec] from this enum's type data.*/
internal fun EnumDTO.buildSpec(): TypeSpec = buildEnum(generatedType) {
    markAsGenerated()
    addKdoc(definition.description.orEmpty())

    definition.values.forEach {
        val constant = TypeSpec.anonymousClassBuilder()
            .addKdoc(it.description.orEmpty())
            .build()

        addEnumConstant(it.name, constant)
    }
}