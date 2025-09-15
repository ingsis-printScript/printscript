package org.example.interpreter.providers

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.interpreter.Interpreter

interface InterpreterProvider {
    fun provide(iterator: PrintScriptIterator<ASTNode>): Interpreter
}
