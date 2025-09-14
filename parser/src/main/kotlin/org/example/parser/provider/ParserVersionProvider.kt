package org.example.parser.provider

class ParserVersionProvider {
    fun with(version: String): ParserProvider {
        return when (version) {
            "1.0" -> ParserProvider10()
            "1.1" -> ParserProvider11()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}