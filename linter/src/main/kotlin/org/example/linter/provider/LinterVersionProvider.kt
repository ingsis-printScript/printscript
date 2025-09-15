package org.example.linter.provider

class LinterVersionProvider {
    fun with(version: String): LinterProvider {
        when (version) {
            "1.0" -> return LinterProvider10()
            "1.1" -> return TODO("LinterProvider11() not implemented yet")
            else -> throw IllegalArgumentException("Unsupported linter version: $version")
        }
    }
}
