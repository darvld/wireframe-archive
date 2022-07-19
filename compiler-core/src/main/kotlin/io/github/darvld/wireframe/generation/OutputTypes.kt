package io.github.darvld.wireframe.generation

import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.model.GenerationEnvironment
import io.github.darvld.wireframe.model.OutputDTO
import io.github.darvld.wireframe.model.nonExtensionFields

/**Builds a [TypeSpec] from this output DTO's type data.*/
internal fun OutputDTO.buildSpec(environment: GenerationEnvironment): TypeSpec = buildClass(generatedType) {
    addModifiers(DATA)

    markAsGenerated()
    addKdoc(definition.description.orEmpty())

    primaryConstructor(buildConstructor {
        for (field in nonExtensionFields) {
            // For output DTOs, all fields are nullable, this allows the server to skip non-requested fields
            val typeName = field.type.typeName(environment.packageName).nullable()
            val property = PropertySpec.builder(field.name, typeName)
                .addKdoc(field.description.orEmpty())
                .initializer(field.name)
                .build()

            addParameter(ParameterSpec.builder(field.name, typeName).defaultValue("null").build())
            addProperty(property)
        }
    })
}