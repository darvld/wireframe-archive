package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.extensions.*
import io.github.darvld.graphql.generation.OutputNames.MapperMapFunctionName
import io.github.darvld.graphql.generation.OutputNames.MapperOptionalParameterSuffix
import io.github.darvld.graphql.generation.OutputNames.MapperProjectFunctionName
import io.github.darvld.graphql.generation.OutputNames.MapperProjectionSetParameter
import io.github.darvld.graphql.generation.OutputNames.MapperSourceParameter
import io.github.darvld.graphql.mapping.OutputMapper
import io.github.darvld.graphql.model.GenerationEnvironment
import io.github.darvld.graphql.model.OutputDTO
import io.github.darvld.graphql.model.nonExtensionFields

private object OutputNames {
    const val MapperMapFunctionName = "map"
    const val MapperSourceParameter = "source"
    const val MapperOptionalParameterSuffix = "Data"

    const val MapperProjectFunctionName = "project"
    const val MapperProjectionSetParameter = "selectionSet"
}

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

/**Generates a [TypeSpec] describing an [OutputMapper] for this DTO. This allows to translate to the business layer's
 * output from, for example, a MongoDB document structure.*/
internal fun OutputDTO.buildMapper(environment: GenerationEnvironment): TypeSpec {
    val mapperName = ClassName(generatedType.packageName, mapperName())

    return buildClass(mapperName) {
        superclass(OUTPUT_MAPPER)

        markAsGenerated()
        addKdoc("An [OutputMapper] that can be used to create to [$name] instances from other formats.")

        primaryConstructor(buildConstructor {
            for (field in nonExtensionFields) {
                val fieldTypeName = OUTPUT_TRANSFORM.parameterizedBy(field.type.typeName(environment.packageName))

                addParameter(ParameterSpec.builder(field.name, fieldTypeName).build())
                addProperty(PropertySpec.builder(field.name, fieldTypeName).initializer(field.name).build())
            }
        })

        addFunction(buildFunction(MapperMapFunctionName) {
            returns(generatedType)
            addParameter(MapperSourceParameter, MAPPING_SOURCE)

            addCode {
                add("return·%T(\n", generatedType)
                indent()

                for (field in nonExtensionFields) {
                    add("%L·=·", field.name)

                    // Primitives and simple types can be retrieved automatically
                    if (field.type.unwrapCompletely() !is GraphQLObjectType) {
                        add("%L·from·%L,\n", field.name, MapperSourceParameter)
                        continue
                    }

                    // Object types (or types wrapping an object type) may require injection
                    // By default, use the passed argument, otherwise attempt to extract it from the source
                    val artificialParameter = ParameterSpec.builder(
                        name = field.name + MapperOptionalParameterSuffix,
                        type = field.type.typeName(environment.packageName).nullable()
                    ).defaultValue("null").build()

                    addParameter(artificialParameter)
                    add("%L·?:·(%L·from·%L),\n", artificialParameter.name, field.name, MapperSourceParameter)
                }

                unindent()
                add(")\n")
            }
        })

        addFunction(buildFunction(MapperProjectFunctionName) {
            returns(FIELD_SET)

            addParameter(MapperProjectionSetParameter, DATA_FETCHING_SELECTION_FIELD_SET)
            beginControlFlow("return·buildList·{")
            for (field in nonExtensionFields) {
                addStatement("addIfPresent(%L, %L)", field.name, MapperProjectionSetParameter)
            }
            endControlFlow()
        })
    }
}