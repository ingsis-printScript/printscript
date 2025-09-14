package org.example.interpreter.org.example.interpreter.providers

import org.example.interpreter.org.example.interpreter.provider.InterpreterProvider
import org.example.interpreter.org.example.interpreter.provider.InterpreterProvider10
import org.example.interpreter.org.example.interpreter.provider.InterpreterProvider11

class InterpreterVersionProvider {
    fun with(version: String): InterpreterProvider {
        return when (version) {
            "1.0" -> InterpreterProvider10()
            "1.1" -> InterpreterProvider11()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}