package io.github.darvld.wireframe.mapping

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import graphql.schema.DataFetchingFieldSelectionSet

public val INPUT_MAPPER: ClassName = InputMapper::class.asClassName()
public val INPUT_TRANSFORM: ClassName = InputTransform::class.asClassName()

public val OUTPUT_MAPPER: ClassName = OutputMapper::class.asClassName()
public val OUTPUT_TRANSFORM: ClassName = OutputTransform::class.asClassName()

public val MAPPING_TARGET: ClassName = MappingTarget::class.asClassName()
public val MAPPING_SOURCE: ClassName = MappingSource::class.asClassName()

public val FIELD_SET: ClassName = ClassName("io.github.darvld.wireframe.mapping", "FieldSet")
public val DATA_FETCHING_SELECTION_FIELD_SET: ClassName = DataFetchingFieldSelectionSet::class.asClassName()