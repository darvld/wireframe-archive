@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.TypeName

internal inline fun TypeName.nullable(): TypeName {
    return if (isNullable) this else copy(nullable = true)
}

internal inline fun TypeName.nonNullable(): TypeName {
    return if (isNullable) copy(nullable = false) else this
}