package org.example.interpreter

import org.example.common.ast.Program
import org.example.common.ast.expressions.BinaryExpression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.expressions.LiteralExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.interpreter.result.Results
import org.example.interpreter.visitors.ASTVisitor

class Interpreter : ASTVisitor<Results> {
    override fun visitProgram(node: Program): Results {
        TODO("")
    }
    override fun visitVariableDeclarator(node: VariableDeclarator): Results {
        TODO("")
    }
    override fun visitBinaryExpression(node: BinaryExpression): Results {
        TODO("")
    }
    override fun visitIdentifierExpression(node: IdentifierExpression): Results {
        TODO("")
    }
    override fun visitLiteralExpression(node: LiteralExpression<*>): Results {
        TODO("")
    }
}
