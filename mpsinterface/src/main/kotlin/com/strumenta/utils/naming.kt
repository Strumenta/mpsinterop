package com.strumenta.utils

val String.isSimpleName: Boolean
    get() = this.indexOf('.') == -1

val String.isQualifiedName: Boolean
    get() = !isSimpleName
