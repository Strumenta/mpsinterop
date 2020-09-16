package com.strumenta.deprecated_mpsinterop.utils

val String.isSimpleName: Boolean
    get() = this.indexOf('.') == -1

val String.isQualifiedName: Boolean
    get() = !isSimpleName
