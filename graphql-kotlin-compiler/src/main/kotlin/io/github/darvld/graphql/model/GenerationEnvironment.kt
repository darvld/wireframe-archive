package io.github.darvld.graphql.model

/**Provides information about the environment in which the code will be generated.*/
public class GenerationEnvironment internal constructor(
    internal val packageName: String,
    internal val inputTypes: Set<InputDTO>,
    internal val outputTypes: Set<OutputDTO>,
    internal val enumTypes: Set<EnumDTO>,
    internal val routeHandlers: Set<RouteData>,
)