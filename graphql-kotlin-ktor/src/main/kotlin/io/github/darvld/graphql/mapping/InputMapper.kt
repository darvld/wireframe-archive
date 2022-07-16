package io.github.darvld.graphql.mapping

public abstract class InputMapper {
    protected fun <T> MappingTarget.set(transform: InputTransform<T>, input: T) {
        set(transform.targetField.name, transform.mapper(input))
    }
}