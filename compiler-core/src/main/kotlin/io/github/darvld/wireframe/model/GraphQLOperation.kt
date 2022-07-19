package io.github.darvld.wireframe.model

import graphql.schema.GraphQLObjectType

public sealed interface GraphQLOperation {
    public val graphQLTypeName: String
    public val outputFileName: String

    public object Query : GraphQLOperation {
        override val graphQLTypeName: String = "Query"
        override val outputFileName: String = "Queries"
    }

    public object Mutation : GraphQLOperation {
        override val graphQLTypeName: String = "Mutation"
        override val outputFileName: String = "Mutations"
    }

    public object Subscription : GraphQLOperation {
        override val graphQLTypeName: String = "Subscription"
        override val outputFileName: String = "Subscriptions"
    }

    public class Extension(extendedType: GraphQLObjectType) : GraphQLOperation {
        override val graphQLTypeName: String = extendedType.name
        override val outputFileName: String = extendedType.name + "Queries"
    }

    public companion object {
        public fun operationFor(type: GraphQLObjectType): GraphQLOperation {
            return when (type.name) {
                "Query" -> Query
                "Mutation" -> Mutation
                "Subscription" -> Subscription
                else -> throw IllegalArgumentException("Unknown operation type: $type")
            }
        }
    }
}