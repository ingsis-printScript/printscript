package org.example.formatter.provider

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.formatter.Formatter


interface FormatterProvider {
    fun provide(nodes: PrintScriptIterator<ASTNode>): Formatter
}
