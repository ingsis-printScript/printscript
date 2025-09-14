package org.example.formatter.provider

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.formatter.CompositeASTFormat
import org.example.formatter.Formatter
import org.example.formatter.Ruler
import org.example.formatter.formatters.BinaryExpressionFormat
import org.example.formatter.formatters.NumberExpressionFormat
import org.example.formatter.formatters.PrintFunctionFormat
import org.example.formatter.formatters.StringExpressionFormat
import org.example.formatter.formatters.SymbolExpressionFormat
import org.example.formatter.formatters.VariableAssignerFormat
import org.example.formatter.formatters.VariableDeclaratorFormat

class Provider10(private val ruler: Ruler) : Provider {

    override fun provide(nodes: PrintScriptIterator<ASTNode>): Formatter {
        val rules = ruler.allRules()
        val formats = listOf(
            BinaryExpressionFormat(),
            NumberExpressionFormat(),
            StringExpressionFormat(),
            VariableDeclaratorFormat(),
            PrintFunctionFormat(),
            SymbolExpressionFormat(),
            VariableAssignerFormat()
        )
        return Formatter(rules, nodes, CompositeASTFormat(formats))
    }
}
