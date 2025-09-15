package org.example.interpreter.providers

import org.example.interpreter.Interpreter

interface InterpreterProvider {
    fun provide(): Interpreter
}
