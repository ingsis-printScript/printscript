package org.example.formatter

import org.example.formatter.formatters.ASTFormat
import org.example.ast.ASTNode

//context: org.example.formatter.CompositeASTFormat no sabe nada de tipos concretos (solo delega).
//Cada ASTFormat implementa canHandle y sabe cuándo aplicarse.
class CompositeASTFormat (
    private val formats: List<ASTFormat>
): ASTFormat {
    override fun canHandle(node: ASTNode): Boolean {
        // el composite "soporta" cualquier nodo que alguno de sus hijos soporte
        return formats.any { it.canHandle(node) }
    }

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val formatter = formats.firstOrNull { it.canHandle(node) }
            ?: throw IllegalArgumentException("No formatter found for node type: ${node::class.simpleName}")

        formatter.formatNode(node, result, rules, nestingLevel)

    }
}