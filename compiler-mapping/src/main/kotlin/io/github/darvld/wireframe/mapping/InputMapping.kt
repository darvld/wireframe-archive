package io.github.darvld.wireframe.mapping

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.mapping.InputNames.MapperInputParameter
import io.github.darvld.wireframe.mapping.InputNames.MapperMapFunctionName
import io.github.darvld.wireframe.mapping.InputNames.MapperTargetParameter
import io.github.darvld.wireframe.mapping.extensions.INPUT_MAPPER
import io.github.darvld.wireframe.mapping.extensions.INPUT_TRANSFORM
import io.github.darvld.wireframe.mapping.extensions.MAPPING_TARGET

private object InputNames {
    const val MapperMapFunctionName = "map"
    const val MapperInputParameter = "input"
    const val MapperTargetParameter = "target"
}

internal fun buildInputMapper(
    mapperClass: ClassName,
    mappingTarget: ClassName,
    definition: GraphQLInputObjectType,
    environment: ProcessingEnvironment,
): TypeSpec {
    return buildClass(mapperClass) {
        superclass(INPUT_MAPPER)

        markAsGenerated()
        addKdoc("An [InputMapper] that can be used to convert [${mappingTarget.simpleName}] instances to other formats.")

        primaryConstructor(buildConstructor {
            for (field in definition.fields) {
                // Transform fields are always non-nullable, nullable DTO fields are handled in the `map` method
                val fieldTypeName = INPUT_TRANSFORM.parameterizedBy(environment.typeNameFor(field.type).nonNullable())

                addParameter(ParameterSpec.builder(field.name, fieldTypeName).build())
                addProperty(PropertySpec.builder(field.name, fieldTypeName).initializer(field.name).build())
            }
        })

        addFunction(buildFunction(MapperMapFunctionName) {
            addParameter(MapperInputParameter, mappingTarget)
            addParameter(MapperTargetParameter, MAPPING_TARGET)

            beginControlFlow("return·with(%L)·{", MapperTargetParameter)
            definition.fields.forEach {
                if (it.type.isNullable) {
                    addStatement("%L.%L?.let·{ set(%L, it) }", MapperInputParameter, it.name, it.name)
                } else {
                    addStatement("set(%L, %L.%L)", it.name, MapperInputParameter, it.name)
                }
            }
            endControlFlow()
        })
    }
}