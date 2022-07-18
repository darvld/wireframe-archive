package io.github.darvld.graphql.extensions

import com.squareup.kotlinpoet.asClassName
import io.github.darvld.graphql.mapping.*

internal val INPUT_MAPPER = InputMapper::class.asClassName()
internal val INPUT_TRANSFORM = InputTransform::class.asClassName()

internal val OUTPUT_MAPPER = OutputMapper::class.asClassName()
internal val OUTPUT_TRANSFORM = OutputTransform::class.asClassName()

internal val MAPPING_TARGET = MappingTarget::class.asClassName()
internal val MAPPING_SOURCE = MappingSource::class.asClassName()