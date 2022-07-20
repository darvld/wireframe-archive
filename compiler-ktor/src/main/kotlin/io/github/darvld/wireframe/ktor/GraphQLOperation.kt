package io.github.darvld.wireframe.ktor

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.extensions.getExtensionFields

internal sealed interface GraphQLOperation {
    val graphQLTypeName: String
    val outputFileName: String

    object Query : GraphQLOperation {
        override val graphQLTypeName: String = "Query"
        override val outputFileName: String = "Queries"
    }

    object Mutation : GraphQLOperation {
        override val graphQLTypeName: String = "Mutation"
        override val outputFileName: String = "Mutations"
    }

    object Subscription : GraphQLOperation {
        override val graphQLTypeName: String = "Subscription"
        override val outputFileName: String = "Subscriptions"
    }

    class Extension(extendedType: GraphQLObjectType) : GraphQLOperation {
        override val graphQLTypeName: String = extendedType.name
        override val outputFileName: String = extendedType.name + "Queries"
    }

    companion object {
        fun operationFor(type: GraphQLObjectType): GraphQLOperation {
            return when (type.name) {
                "Query" -> Query
                "Mutation" -> Mutation
                "Subscription" -> Subscription
                else -> Extension(type)
            }
        }
    }
}

internal fun GraphQLObjectType.getRouteFields(operation: GraphQLOperation): Sequence<GraphQLFieldDefinition> {
    return when (operation) {
        is GraphQLOperation.Extension -> getExtensionRoutes()
        else -> fields.asSequence()
    }
}

internal fun GraphQLObjectType.getExtensionRoutes(): Sequence<GraphQLFieldDefinition> {
    return getExtensionFields() + fields.asSequence().filter {
        it.arguments.isNotEmpty()
    }.distinctBy { it.name }
}