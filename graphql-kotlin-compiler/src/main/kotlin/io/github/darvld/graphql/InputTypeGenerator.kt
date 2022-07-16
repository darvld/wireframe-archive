package io.github.darvld.graphql

import com.squareup.kotlinpoet.*
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.graphql.utils.generatedName
import io.github.darvld.graphql.utils.typeName

internal fun generateInputType(typeDefinition: GraphQLInputObjectType, packageName: String): FileSpec {
    val generatedTypeName = typeDefinition.generatedName()

    val file = FileSpec.builder(packageName, generatedTypeName)
    val type = TypeSpec.Companion.classBuilder(generatedTypeName)

    type.addModifiers(KModifier.DATA)

    val constructor = FunSpec.constructorBuilder()
    typeDefinition.fields.forEach {
        val typeName = it.type.typeName(packageName)

        constructor.addParameter(ParameterSpec.builder(it.name, typeName).build())
        type.addProperty(PropertySpec.builder(it.name, typeName).initializer(it.name).build())
    }
    type.primaryConstructor(constructor.build())

    file.addType(type.build())
    return file.build()
}