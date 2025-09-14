package org.example.interpreter.org.example.interpreter.provider

import org.example.interpreter.org.example.interpreter.Interpreter


interface InterpreterProvider {
    fun provide(): Interpreter
}
