package org.example.common.enums

// issue con BOOLEAN, que es un type que se declara
enum class Type {
    STRING,
    NUMBER,
    BOOLEAN;

    companion object {
        fun fromString(type: String): Type? {
            return Type.entries.find { it.name.equals(type, ignoreCase = true) }
        }
    }
}


//sealed class Type(val name: String) {
//    object NUMBER : Type("number")
//    object STRING : Type("string")
//    object BOOLEAN : Type("boolean")
//    data class Custom(val typeName: String) : Type(typeName)
//}
//
//private fun detectType(token: Token): Type {
//    return when (token.value.lowercase()) {
//        "number" -> Type.NUMBER
//        "string" -> Type.STRING
//        "boolean" -> Type.BOOLEAN
//        else -> Type.Custom(token.value)
//    }
//}
//}