package io.github.darvld.wireframe.mapping

public class OutputTransform<out R> @PublishedApi internal constructor(
    internal val sourceField: Model.Field<*>,
    internal val mapper: (Any?) -> R?,
)

public inline fun <reified T, R> mapFrom(from: Model.Field<T>, crossinline using: (T) -> R): OutputTransform<R> {
    return OutputTransform(from) {
        if (it is T) using(it) else null
    }
}

public inline fun <reified T> mapFrom(from: Model.Field<T>): OutputTransform<T> {
    return OutputTransform(from) { it as? T }
}

public inline fun <reified T> mapFromReference(on: Model.Field<*>): OutputTransform<T> {
    return OutputTransform(on) {
        throw IllegalStateException("Reference-based transforms may not be invoked for mapping. Pass the required reference by parameter instead.")
    }
}
