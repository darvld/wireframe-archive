package io.github.darvld.graphql.model

import com.squareup.kotlinpoet.MemberName

internal enum class GraphQLOperation(internal val routeExtension: MemberName) {
    Query(routeExtension("query")),
    Mutation(routeExtension("mutation")),
    Subscription(routeExtension("subscription"));
}

private fun routeExtension(name: String): MemberName {
    return MemberName("io.github.darvld.graphql.routing", name)
}