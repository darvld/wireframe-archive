package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.graphql.extensions.buildClass
import io.github.darvld.graphql.extensions.buildConstructor
import io.github.darvld.graphql.extensions.nullable
import io.github.darvld.graphql.extensions.typeName
import io.github.darvld.graphql.model.GenerationEnvironment
import io.github.darvld.graphql.model.OutputDTO

/**Builds a [TypeSpec] from this output DTO's type data.*/
internal fun OutputDTO.buildSpec(environment: GenerationEnvironment): TypeSpec = buildClass(generatedType) {
    addModifiers(DATA)

    primaryConstructor(buildConstructor {
        definition.fields.asSequence().filter { it.arguments.isEmpty() }.forEach {
            // For output DTOs, all fields are nullable, this allows the server to skip non-requested fields
            val typeName = it.type.typeName(environment.packageName).nullable()

            addParameter(ParameterSpec.builder(it.name, typeName).defaultValue("null").build())
            addProperty(PropertySpec.builder(it.name, typeName).initializer(it.name).build())
        }
    })
}