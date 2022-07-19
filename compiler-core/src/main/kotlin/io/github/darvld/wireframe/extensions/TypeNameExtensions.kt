@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.TypeName

public inline fun TypeName.nullable(): TypeName {
    return if (isNullable) this else copy(nullable = true)
}

public inline fun TypeName.nonNullable(): TypeName {
    return if (isNullable) copy(nullable = false) else this
}