package org.example.formatter.providers


class FormatterVersionProvider {
    fun with(version: String): FormatterProvider {
        return when (version) {
            "1.0" -> FormatterProvider10()
            "1.1" -> FormatterProvider11()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}
