package io.github.darvld.graphql.model

import com.squareup.kotlinpoet.MemberName

internal enum class GraphQLOperation(val routeExtension: MemberName, val outputName: String) {
    Query(routeExtension("query"), "Queries"),
    Mutation(routeExtension("mutation"), "Mutations"),
    Subscription(routeExtension("subscription"), "Subscriptions");
}

private fun routeExtension(name: String): MemberName {
    return MemberName("io.github.darvld.graphql.routing", name)
}