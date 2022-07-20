package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.output

internal fun processOutputType(definition: GraphQLObjectType, environment: ProcessingEnvironment) {
    val generatedType = environment.resolve(definition)
    val extensionFields = definition.getExtensionFields().toList()

    val spec = buildClass(generatedType) {
        addModifiers(KModifier.DATA)

        markAsGenerated()
        addKdoc(definition.description.orEmpty())

        primaryConstructor(buildConstructor {
            for (field in definition.fields) {
                if (field in extensionFields) continue

                // For output DTOs, all fields are nullable, this allows the server to skip non-requested fields
                val typeName = environment.typeNameFor(field.type).nullable()
                val property = PropertySpec.Companion.builder(field.name, typeName)
                    .addKdoc(field.description.orEmpty())
                    .initializer(field.name)
                    .build()

                addParameter(ParameterSpec.Companion.builder(field.name, typeName).defaultValue("null").build())
                addProperty(property)
            }
        })
    }

    environment.output(generatedType, spec)
}