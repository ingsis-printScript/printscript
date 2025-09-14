package org.example.interpreter.org.example.interpreter.providers

import org.example.interpreter.org.example.interpreter.Interpreter


interface InterpreterProvider {
    fun provide(): Interpreter
}
