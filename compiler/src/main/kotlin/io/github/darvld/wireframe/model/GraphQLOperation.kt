package io.github.darvld.wireframe.model

import graphql.schema.GraphQLObjectType

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
                else -> throw IllegalArgumentException("Unknown operation type: $type")
            }
        }
    }
}