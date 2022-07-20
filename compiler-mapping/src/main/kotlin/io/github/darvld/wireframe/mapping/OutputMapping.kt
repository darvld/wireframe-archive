package io.github.darvld.wireframe.mapping

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.mapping.OutputNames.MapperMapFunctionName
import io.github.darvld.wireframe.mapping.OutputNames.MapperOptionalParameterSuffix
import io.github.darvld.wireframe.mapping.OutputNames.MapperProjectFunctionName
import io.github.darvld.wireframe.mapping.OutputNames.MapperProjectionSetParameter
import io.github.darvld.wireframe.mapping.OutputNames.MapperSourceParameter
import io.github.darvld.wireframe.mapping.extensions.*

private object OutputNames {
    const val MapperMapFunctionName = "map"
    const val MapperSourceParameter = "source"
    const val MapperOptionalParameterSuffix = "Data"

    const val MapperProjectFunctionName = "project"
    const val MapperProjectionSetParameter = "selectionSet"
}

internal fun buildOutputMapper(
    mapperClass: ClassName,
    mappingTarget: ClassName,
    definition: GraphQLObjectType,
    environment: ProcessingEnvironment,
): TypeSpec {
    return buildClass(mapperClass) {
        superclass(OUTPUT_MAPPER)

        markAsGenerated()
        addKdoc("An [OutputMapper] that can be used to create to [${mappingTarget.simpleName}] instances from other formats.")

        val extensionFields = definition.getExtensionFields().toList()
        val nonExtensionFields = definition.fields.filter { it !in extensionFields }

        primaryConstructor(buildConstructor {
            for (field in nonExtensionFields) {
                val fieldTypeName = OUTPUT_TRANSFORM.parameterizedBy(environment.typeNameFor(field.type))

                addParameter(ParameterSpec.builder(field.name, fieldTypeName).build())
                addProperty(PropertySpec.builder(field.name, fieldTypeName).initializer(field.name).build())
            }
        })

        addFunction(buildFunction(MapperMapFunctionName) {
            returns(mappingTarget)
            addParameter(MapperSourceParameter, MAPPING_SOURCE)

            addCode {
                add("return·%T(\n", mappingTarget)
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
                        type = environment.typeNameFor(field.type).nullable()
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