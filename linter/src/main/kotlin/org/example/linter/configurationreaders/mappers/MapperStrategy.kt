package org.example.linter.configurationreaders.mappers

import com.fasterxml.jackson.databind.ObjectMapper

interface MapperStrategy {
    fun createMapper(): ObjectMapper
    fun getSupportedExtensions(): List<String>
}