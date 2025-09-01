package org.example.interpreter.visitors

import org.example.ast.ASTNode

// El AST define nodos (Program, BinaryExpression, etc.) y cómo se relacionan.
// El Visitor define qué hacer con cada tipo de nodo, sin meter esa lógica dentro del propio nodo.
// Ventaja: Extensibilidad evita usar muchos "when"

interface ASTVisitor<T> {
    fun visit(node: ASTNode): T
}
