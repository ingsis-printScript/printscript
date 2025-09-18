package org.example.interpreter.input

import java.util.Scanner

class ConsoleInputProvider : InputProvider {

    private val scanner = Scanner(System.`in`)

    override fun readInput(): String {
        return scanner.nextLine()
    }
}
