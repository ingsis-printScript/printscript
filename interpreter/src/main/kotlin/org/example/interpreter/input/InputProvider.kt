package org.example.interpreter.org.example.interpreter.input

interface InputProvider {
    fun readInput(prompt: String): String
}