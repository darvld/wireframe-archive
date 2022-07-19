package io.github.darvld.wireframe.mapping

public fun interface MappingSource {
    public operator fun get(name: String): Any?
}

public fun interface MappingTarget {
    public operator fun set(name: String, value: Any?)
}