package org.example.cli

import org.example.interpreter.Interpreter
import org.example.lexer.Lexer
import org.example.lexer.constructors.*
import org.example.parser.Parser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser

// acá inyecto lo que necesito para 1.0, me armo otro para 1.1
class ClassicFactory {
    fun lexer(fileReader : Iterator<String>) : Lexer {
        val constructors = listOf(
            NumberTokenConstructor(),
            OperatorTokenConstructor(),
            PunctuationTokenConstructor(),
            StringTokenConstructor(),
            SymbolTokenConstructor()
        )

        return Lexer(fileReader, constructors, KeywordTokenConstructor(), listOf(' ', '\t', '\n'))
    }

    fun parser() : Parser {
        val statementParsers = listOf(
            VariableAssignationParser(),
            VariableDeclarationParser(),
            VariableDeclarationAssignationParser(),
            PrintParser()
        )
        return Parser(statementParsers)
    }

    fun interpreter() : Interpreter {
        return Interpreter()
    }

    companion object {
        fun lexer(fileReader: Iterator<String>): Lexer {
            return ClassicFactory().lexer(fileReader)
        }

        fun parser(): Parser {
            return ClassicFactory().parser()
        }

        fun interpreter() : Interpreter {
            return ClassicFactory().interpreter()
        }
    }

}