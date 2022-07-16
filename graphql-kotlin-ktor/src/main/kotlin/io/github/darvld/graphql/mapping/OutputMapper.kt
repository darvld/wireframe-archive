package io.github.darvld.graphql.mapping

import graphql.schema.DataFetchingFieldSelectionSet

public abstract class OutputMapper {
    protected infix fun <T> OutputTransform<T>.from(source: MappingSource): T? {
        return mapper(source[sourceField.name])
    }

    protected fun <T> MutableList<Model.Field<*>>.addIfPresent(
        field: OutputTransform<T>,
        from: DataFetchingFieldSelectionSet,
    ) {
        if (from.immediateFields.any { it.name == field.sourceField.name }) add(field.sourceField)
    }
}