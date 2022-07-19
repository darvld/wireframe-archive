package io.github.darvld.wireframe.mapping

public abstract class Model {
    @JvmInline
    public value class Field<@Suppress("unused") T>(public val name: String)

    private val fieldDefinitions: MutableSet<Field<Any?>> = mutableSetOf()
    public val fields: Set<Field<Any?>> get() = fieldDefinitions

    protected inline fun <reified T> field(name: String): Field<T> = Field(name)
}