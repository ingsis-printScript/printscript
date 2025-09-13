package org.example.interpreter.input

interface InputProvider {
    fun readInput(prompt: String): String
}