package org.example.interpreter.org.example.interpreter.providers

import org.example.interpreter.org.example.interpreter.Interpreter


class InterpreterProvider11() : InterpreterProvider {
    override fun provide(): Interpreter {
        return interpreter
    }
}