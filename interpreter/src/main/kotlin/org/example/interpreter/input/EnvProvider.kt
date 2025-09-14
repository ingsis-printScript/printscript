package org.example.interpreter.org.example.interpreter.input

interface EnvProvider {
    fun readEnv(variableName: String): String?
}