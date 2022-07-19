package io.github.darvld.wireframe.model

/**Provides information about the environment in which the code will be generated.*/
public class GenerationEnvironment internal constructor(
    public val packageName: String,
    public val inputTypes: Set<InputDTO>,
    public val outputTypes: Set<OutputDTO>,
    public val enumTypes: Set<EnumDTO>,
    public val routeHandlers: Set<RouteData>,
)