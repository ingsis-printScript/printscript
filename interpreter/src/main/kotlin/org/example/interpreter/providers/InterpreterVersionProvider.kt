package org.example.interpreter.org.example.interpreter.providers

class InterpreterVersionProvider {
    fun with(version: String): InterpreterProvider {
        return when (version) {
            "1.0" -> InterpreterProvider10()
            "1.1" -> InterpreterProvider11()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}