package io.github.darvld.graphql.model

import com.squareup.kotlinpoet.ClassName
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType

internal typealias InputDTO = TypeData<GraphQLInputObjectType>
internal typealias OutputDTO = TypeData<GraphQLObjectType>
internal typealias EnumDTO = TypeData<GraphQLEnumType>

/**Contains information about a DTO that will be generated for a given GraphQL type.*/
internal data class TypeData<T : GraphQLType>(
    /**A short name for the DTO, used as filename.*/
    val name: String,
    /**The [ClassName] of the generated Kotlin type.*/
    val generatedType: ClassName,
    /**The GraphQL type this DTO should be generated from.*/
    val definition: T,
    /**Contains the names of the extension fields present in the graphql type.*/
    val extensionNames: List<String> = emptyList(),
)