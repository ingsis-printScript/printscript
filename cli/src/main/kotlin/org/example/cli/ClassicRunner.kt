package org.example.cli

import org.example.ast.ASTNode
import org.example.ast.expressions.StringExpression
import org.example.common.Position

class ClassicRunner : Runner {
    override fun validate(fileReader: Iterator<String>): String {
        try {
            val ast = buildAst(fileReader)
            return "Validation successful: $ast" // no se si pasar el ast tho
        } catch (e: Exception) {
            return "Validation failed: ${e.message}"
        }
    } // todo: chau try catch, recibo Result
    // TODO: que no devuelva String, puede incluso devolver Result...
    //  ...es como se comunica el interpreter after all

    override fun execute(fileReader: Iterator<String>): String {
        try {
            val ast = buildAst(fileReader)
            // val interpreter = ToolFactory.interpreter()
            // val result = interpreter.visitStatement(ast) // ya no usamos program
            return "Execution successful: $ast" // no se si pasar el ast tho
        } catch (e: Exception) {
            return "Execution failed: ${e.message}"
        }
    }

    override fun format(fileReader: Iterator<String>, configReader: Iterator<String>): String {
        TODO("Not yet implemented")
    }

    override fun analyze(fileReader: Iterator<String>, configReader: Iterator<String>): String {
        TODO("Not yet implemented")
    }

    private fun buildAst(fileReader: Iterator<String>): ASTNode {
        // val lexer = ToolFactory.lexer(fileReader)
        // algo pasa en el medio que idk que es
        // se llama al lexer para lex
        // idk como se carga el buffer
        // ese buffer se pasa a parser para hacer parse
        // val parser = ToolFactory.parser()
        // return parser.parse(listOf()) // aca recibe el buffer cargado
        return StringExpression("hola", Position(0, 0)) // solo para que compile
    }
}
