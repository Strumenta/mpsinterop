package com.strumenta.deprecated_mpsinterop.binary
import com.strumenta.deprecated_mpsinterop.logicalmodel.* // ktlint-disable

import java.util.*

class ExternalReference(val modelUUID: UUID, val nodeId: String) : Reference

class ForeignReference(val modelUUID: String, val nodeId: String) : Reference

