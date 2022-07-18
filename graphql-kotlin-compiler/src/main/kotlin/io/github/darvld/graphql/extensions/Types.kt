package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import graphql.schema.DataFetchingFieldSelectionSet
import io.github.darvld.graphql.mapping.*

internal val INPUT_MAPPER = InputMapper::class.asClassName()
internal val INPUT_TRANSFORM = InputTransform::class.asClassName()

internal val OUTPUT_MAPPER = OutputMapper::class.asClassName()
internal val OUTPUT_TRANSFORM = OutputTransform::class.asClassName()

internal val MAPPING_TARGET = MappingTarget::class.asClassName()
internal val MAPPING_SOURCE = MappingSource::class.asClassName()

internal val FIELD_SET = ClassName("io.github.darvld.graphql.mapping", "FieldSet")
internal val DATA_FETCHING_SELECTION_FIELD_SET = DataFetchingFieldSelectionSet::class.asClassName()