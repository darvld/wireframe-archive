package io.github.darvld.wireframe.mapping.extensions

import com.squareup.kotlinpoet.ClassName
import io.github.darvld.wireframe.extensions.subpackage

internal const val MAPPER_NAME_SUFFIX = "Mapper"

internal fun ClassName.mapperName(): ClassName {
    return ClassName(packageName.subpackage("mapping"), simpleName.removeSuffix("DTO") + MAPPER_NAME_SUFFIX)
}