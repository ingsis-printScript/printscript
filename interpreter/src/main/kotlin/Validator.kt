package org.example.interpreter

import org.example.ast.ASTNode
import org.example.interpreter.result.Results
import org.example.interpreter.visitors.ASTVisitor

class Validator: ASTVisitor<> {
    override fun visit(node: ASTNode): Results {
        TODO("Not yet implemented")
    }

}