package org.example.interpreter.visitors

import org.example.common.ast.Program
import org.example.common.ast.expressions.BinaryExpression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.expressions.LiteralExpression
import org.example.common.ast.statements.FunctionCall
import org.example.common.ast.statements.VariableAssigner
import org.example.common.ast.statements.VariableDeclarator

//El AST define nodos (Program, BinaryExpression, etc.) y cómo se relacionan.
//El Visitor define qué hacer con cada tipo de nodo, sin meter esa lógica dentro del propio nodo.
//Ventaja: Extensibilidad evita usar muchos "when"

interface ASTVisitor<T> {
    fun visitProgram(node: Program): T
    fun visitVariableDeclarator(node: VariableDeclarator): T
    fun visitVariableAssigner(node: VariableAssigner): T
    fun visitFunctionCall(node: FunctionCall): T
    fun visitBinaryExpression(node: BinaryExpression): T
    fun visitIdentifierExpression(node: IdentifierExpression): T
    fun visitLiteralExpression(node: LiteralExpression<*>): T
}

