package io.github.darvld.graphql.generation

import com.squareup.kotlinpoet.*
import graphql.schema.GraphQLObjectType
import io.github.darvld.graphql.extensions.generatedName
import io.github.darvld.graphql.extensions.nullable
import io.github.darvld.graphql.extensions.typeName
import io.github.darvld.graphql.model.GenerationEnvironment

/**Generates a [TypeSpec] for a GraphQL output type as a Kotlin DTO.*/
internal fun GenerationEnvironment.generateOutputType(typeDefinition: GraphQLObjectType): TypeSpec {
    val generatedTypeName = typeDefinition.generatedName()
    val type = TypeSpec.classBuilder(generatedTypeName).addModifiers(KModifier.DATA)

    val constructor = FunSpec.constructorBuilder()

    // Generate fields as properties so long as they don't have any arguments
    typeDefinition.fields.asSequence().filter { it.arguments.isEmpty() }.forEach {
        // Get the type for this property, for output DTOs, all fields are nullable
        val typeName = it.type.typeName(packageName).nullable()

        constructor.addParameter(ParameterSpec.builder(it.name, typeName).defaultValue("null").build())
        type.addProperty(PropertySpec.builder(it.name, typeName).initializer(it.name).build())
    }
    type.primaryConstructor(constructor.build())

    return type.build()
}