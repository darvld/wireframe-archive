package io.github.darvld.graphql.mapping

public class InputTransform<in T> @PublishedApi internal constructor(
    internal val targetField: Model.Field<*>,
    internal val mapper: (T) -> Any?,
)

public inline fun <reified T> mapTo(target: Model.Field<T>): InputTransform<T> {
    return InputTransform(target) { it }
}

public inline fun <reified T, R> mapTo(target: Model.Field<R>, noinline using: (T) -> R): InputTransform<T> {
    return InputTransform(target, using)
}