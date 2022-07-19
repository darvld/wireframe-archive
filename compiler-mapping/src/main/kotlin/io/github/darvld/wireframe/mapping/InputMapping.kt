package io.github.darvld.wireframe.mapping

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.mapping.InputNames.MapperInputParameter
import io.github.darvld.wireframe.mapping.InputNames.MapperMapFunctionName
import io.github.darvld.wireframe.mapping.InputNames.MapperTargetParameter
import io.github.darvld.wireframe.model.GenerationEnvironment
import io.github.darvld.wireframe.model.InputDTO

private object InputNames {
    const val MapperMapFunctionName = "map"
    const val MapperInputParameter = "input"
    const val MapperTargetParameter = "target"
}

/**Generates a [TypeSpec] describing an [InputMapper] for this DTO. This allows to translate from the business layer's
 * input to, for example, a MongoDB document structure.*/
internal fun InputDTO.buildMapper(environment: GenerationEnvironment): TypeSpec {
    val mapperName = ClassName(generatedType.packageName, mapperName())

    return buildClass(mapperName) {
        superclass(INPUT_MAPPER)

        markAsGenerated()
        addKdoc("An [InputMapper] that can be used to convert [$name] instances to other formats.")

        primaryConstructor(buildConstructor {
            definition.fields.forEach {
                val fieldTypeName = INPUT_TRANSFORM.parameterizedBy(it.type.typeName(environment.packageName))

                addParameter(ParameterSpec.builder(it.name, fieldTypeName).build())
                addProperty(PropertySpec.builder(it.name, fieldTypeName).initializer(it.name).build())
            }
        })

        addFunction(buildFunction(MapperMapFunctionName) {
            addParameter(MapperInputParameter, generatedType)
            addParameter(MapperTargetParameter, MAPPING_TARGET)

            beginControlFlow("return·with(%L)·{", MapperTargetParameter)
            definition.fields.forEach {
                addStatement("set(%L, %L.%L)", it.name, MapperInputParameter, it.name)
            }
            endControlFlow()
        })
    }
}