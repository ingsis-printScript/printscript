package org.example.cli

interface Runner {
    fun validate(fileReader: Iterator<String>) : String
    fun execute(fileReader: Iterator<String>) : String
    fun format(fileReader: Iterator<String>, configReader: Iterator<String>) : String
    fun analyze(fileReader: Iterator<String>, configReader: Iterator<String>) : String
}
