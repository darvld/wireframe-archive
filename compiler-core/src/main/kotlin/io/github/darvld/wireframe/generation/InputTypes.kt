package io.github.darvld.wireframe.generation

import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.wireframe.extensions.buildClass
import io.github.darvld.wireframe.extensions.buildConstructor
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.extensions.typeName
import io.github.darvld.wireframe.model.GenerationEnvironment
import io.github.darvld.wireframe.model.InputDTO

/**Builds a [TypeSpec] from this input DTO's type data.*/
internal fun InputDTO.buildSpec(environment: GenerationEnvironment): TypeSpec = buildClass(generatedType) {
    addModifiers(DATA)

    markAsGenerated()
    addKdoc(definition.description.orEmpty())

    primaryConstructor(buildConstructor {
        for (field in definition.fields) {
            val fieldTypeName = field.type.typeName(environment.packageName)
            val property = PropertySpec.builder(field.name, fieldTypeName)
                .addKdoc(field.description.orEmpty())
                .initializer(field.name)
                .build()

            addParameter(field.name, fieldTypeName)
            addProperty(property)
        }
    })
}