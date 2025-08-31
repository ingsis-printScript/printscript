package org.example.cli

import org.example.ast.ASTNode

class ClassicRunner : Runner {
    override fun validate(fileReader: Iterator<String>): String {
        try {
            val ast = buildAst(fileReader)
            return "Validation successful: $ast" // no se si pasar el ast tho
        } catch (e: Exception) {
            return "Validation failed: ${e.message}"
        }
    }

    override fun execute(fileReader: Iterator<String>): String {
        try {
            val ast = buildAst(fileReader)
            val interpreter = ClassicFactory.interpreter()
            val result = interpreter.visitStatement(ast) // ya no usamos program
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
        val lexer = ClassicFactory.lexer(fileReader)
        // algo pasa en el medio que idk que es
        // se llama al lexer para lex
        // idk como se carga el buffer
        // ese buffer se pasa a parser para hacer parse
        val parser = ClassicFactory.parser()
        return parser.parse(listOf()) // aca recibe el buffer cargado
    }
}