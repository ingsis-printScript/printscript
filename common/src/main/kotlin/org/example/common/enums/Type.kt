package org.example.common.enums

enum class Type {
    STRING,
    NUMBER;

    companion object {
        fun fromString(type: String): Type? {
            return Type.entries.find { it.name.equals(type, ignoreCase = true) }
        }
    }
}
