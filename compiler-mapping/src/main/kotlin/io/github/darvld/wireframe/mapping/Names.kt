package io.github.darvld.wireframe.mapping

import io.github.darvld.wireframe.model.InputDTO
import io.github.darvld.wireframe.model.OutputDTO

internal const val MAPPER_NAME_SUFFIX = "Mapper"

@JvmName("outputMapperName")
internal fun OutputDTO.mapperName(): String {
    return definition.name + MAPPER_NAME_SUFFIX
}

@JvmName("inputMapperName")
internal fun InputDTO.mapperName(): String {
    return definition.name + MAPPER_NAME_SUFFIX
}
