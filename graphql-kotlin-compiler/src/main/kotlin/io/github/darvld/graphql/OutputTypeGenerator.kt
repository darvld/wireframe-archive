package io.github.darvld.graphql

import com.squareup.kotlinpoet.*
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.utils.generatedName
import io.github.darvld.graphql.utils.typeName

internal fun generateOutputType(typeDefinition: GraphQLObjectType, packageName: String): FileSpec {
    val generatedTypeName = typeDefinition.generatedName()

    val file = FileSpec.builder(packageName, generatedTypeName)
    val type = TypeSpec.Companion.classBuilder(generatedTypeName)

    type.addModifiers(KModifier.DATA)

    val constructor = FunSpec.constructorBuilder()

    // Generate fields as properties as long as they don't have any arguments
    typeDefinition.fields.asSequence().filter { it.arguments.isEmpty() }.forEach {
        // Get the type for this property, for output DTOs, all fields are nullable
        val typeName = it.type.typeName(packageName).copy(nullable = true)

        constructor.addParameter(ParameterSpec.builder(it.name, typeName).defaultValue("null").build())
        type.addProperty(PropertySpec.builder(it.name, typeName).initializer(it.name).build())
    }
    type.primaryConstructor(constructor.build())

    file.addType(type.build())
    return file.build()
}