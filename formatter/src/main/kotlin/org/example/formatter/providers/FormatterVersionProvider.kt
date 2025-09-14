package org.example.formatter.providers

import org.example.formatter.Ruler

class FormatterVersionProvider {
    fun with(version: String): FormatterProvider {
        return when (version) {
            "1.0" -> FormatterProvider10(Ruler(mapOf())) // TODO: rule map
            "1.1" -> FormatterProvider11(Ruler(mapOf())) // TODO: rule map
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}
