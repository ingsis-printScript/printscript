package org.example.cli.factory

class ToolFactory(private val setupData: SetupData) {
    /*fun lexer(reader: Iterator<String>): Lexer {
        return Lexer(
            reader,
            setupData.constructors(),
            setupData.keywordConstructor(),
            setupData.whitespaces()
        )
    }

    fun parser(): Parser {
        return Parser(setupData.statementParsers())
    }

    fun interpreter(): Interpreter {
        return Interpreter()
    }*/
}
