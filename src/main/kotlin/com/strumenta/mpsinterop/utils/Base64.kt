package com.strumenta.mpsinterop.utils

import java.util.Arrays

/**
 * Variation of Base64 encoding, with 10 digits, lower- and uppercase latin characters, '$' and '_' characters, basically, regular ASCII chars
 * with isJavaIdentifierPart == true, for the sake of use in generated code, e.g. method names.
 *
 * This class is not thread-safe, uses internal buffers to save memory on (de-)serialize, do not share it between threads.
 *
 * Author of the original class: Artem Tikhomirov
 */
object JavaFriendlyBase64 {
    private val MIN_CHAR = '$'
    private val MAX_CHAR = 'z'
    // length shall be 2^^6 = 64 (10 digits + 2x26 letters + '$' and '_'. ASCII chars with isJavaIdentifierPart == true
    // Important: charAt(0) shall be '0', we use this to strip leading zeros.
    private val myIndexChars = "0123456789abcdefghijklmnopqrstuvwxyz\$_ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    private val myCharToValue = IntArray(MAX_CHAR - MIN_CHAR + 1)
    private val myBufferLong = CharArray(11) // ceil(sizeof(long) / sizeof(indexChars)) = ceil(64 bits / 6) = 11;
    private val myBufferInt = CharArray(6) // ceil(32 bits / 6) = ceil(5.33) = 6;

    init {
        Arrays.fill(myCharToValue, -1)
        for (i in myIndexChars.indices) {
            val charValue = myIndexChars[i].toInt()
            myCharToValue[charValue - MIN_CHAR.toInt()] = i
        }
    }

    fun toString(v: Long): String {
        var v = v
        for (i in myBufferLong.indices.reversed()) {
            myBufferLong[i] = myIndexChars[v.toInt() and 0x3F]
            v = v.ushr(6)
        }
        // strip leading zeros, up to last digit, which is kept anyway (if it's zero, fine)
        for (i in 0 until myBufferLong.size - 1) {
            if (myBufferLong[i] != '0') {
                return String(myBufferLong, i, myBufferLong.size - i)
            }
        }
        return String(myBufferLong, myBufferLong.size - 1, 1)
    }

    @Throws(IllegalArgumentException::class)
    fun parseLong(text: String): Long {
        var result: Long = 0
        var i = 0
        val x = text.length
        var shift = 0
        while (i < x) {
            result = (result shl shift.toInt())
            val c = text[i]
            if (c - MIN_CHAR < 0 || c - MIN_CHAR >= myCharToValue.size) {
                throw IllegalArgumentException(String.format("String \"%s\" cannot be parsed as long value: invalid character \"%c\" in position %d", text, c, i))
            }
            val value = myCharToValue[c - MIN_CHAR]
            if (value < 0) {
                throw IllegalArgumentException(String.format("String \"%s\" cannot be parsed as long value: invalid character \"%c\" in position %d", text, c, i))
            }
            result = result or value.toLong()
            i++
            shift = 6
        }
        return result
    }

    // at least 5, at most 6 character string encoding. Leading zero is removed only if it's sixth symbol.
    fun indexValue(v: Int): String {
        var v = v
        myBufferInt[5] = myIndexChars[v and 0x3F]
        v = v shr 6
        myBufferInt[4] = myIndexChars[v and 0x3F]
        v = v shr 6
        myBufferInt[3] = myIndexChars[v and 0x3F]
        v = v shr 6
        myBufferInt[2] = myIndexChars[v and 0x3F]
        v = v shr 6
        myBufferInt[1] = myIndexChars[v and 0x3F]
        v = v shr 6
        // 5 times x 6 bits = we've got only 2 bits left of integer's total 32
        v = v and 0x3
        if (v != 0) {
            myBufferInt[0] = myIndexChars[v]
            return String(myBufferInt)
        }
        return String(myBufferInt, 1, 5)
    }

}
