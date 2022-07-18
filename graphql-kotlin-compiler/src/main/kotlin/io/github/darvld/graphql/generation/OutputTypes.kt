package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.extensions.*
import io.github.darvld.graphql.mapping.OutputMapper
import io.github.darvld.graphql.model.GenerationEnvironment
import io.github.darvld.graphql.model.OutputDTO

/**Builds a [TypeSpec] from this output DTO's type data.*/
internal fun OutputDTO.buildSpec(environment: GenerationEnvironment): TypeSpec = buildClass(generatedType) {
    addModifiers(DATA)

    primaryConstructor(buildConstructor {
        for (field in definition.fields) {
            // Exclude extensions from being generated as fields
            if (field.name in extensionNames) continue

            // For output DTOs, all fields are nullable, this allows the server to skip non-requested fields
            val typeName = field.type.typeName(environment.packageName).nullable()

            addParameter(ParameterSpec.builder(field.name, typeName).defaultValue("null").build())
            addProperty(PropertySpec.builder(field.name, typeName).initializer(field.name).build())
        }
    })
}

/**Generates a [TypeSpec] describing an [OutputMapper] for this DTO. This allows to translate to the business layer's
 * output from, for example, a MongoDB document structure.*/
internal fun OutputDTO.buildMapper(environment: GenerationEnvironment): TypeSpec {
    val mapperName = ClassName(generatedType.packageName, name.removeSuffix("DTO") + "Mapper")

    return buildClass(mapperName) {
        superclass(OUTPUT_MAPPER)

        primaryConstructor(buildConstructor {
            definition.fields.forEach {
                val fieldTypeName = OUTPUT_TRANSFORM.parameterizedBy(it.type.typeName(environment.packageName))

                addParameter(ParameterSpec.builder(it.name, fieldTypeName).build())
                addProperty(PropertySpec.builder(it.name, fieldTypeName).initializer(it.name).build())
            }
        })

        addFunction(buildFunction("map") {
            returns(generatedType)
            addParameter("source", MAPPING_SOURCE)

            addCode {
                add("return·%T(\n", generatedType)
                indent()

                for (field in definition.fields) {
                    add("%L·=·", field.name)

                    // Primitives and simple types can be retrieved automatically
                    if (field.type.unwrapCompletely() !is GraphQLObjectType) {
                        add("%L·from·source,\n", field.name)
                        continue
                    }

                    // Object types (or types wrapping an object type) may require injection
                    // By default, use the passed argument, otherwise attempt to extract it from the source
                    val artificialParameter = ParameterSpec.builder(
                        name = field.name + "Data",
                        type = field.type.typeName(environment.packageName).nullable()
                    ).defaultValue("null").build()

                    addParameter(artificialParameter)
                    add("%L·?:·(%L·from·source),\n", artificialParameter.name, field.name)
                }

                unindent()
                add(")\n")
            }
        })

        addFunction(buildFunction("project") {
            returns(FIELD_SET)

            addParameter("selectionSet", DATA_FETCHING_SELECTION_FIELD_SET)
            beginControlFlow("return·buildList·{")
            for (field in definition.fields) {
                addStatement("addIfPresent(%L, selectionSet)", field.name)
            }
            endControlFlow()
        })
    }
}