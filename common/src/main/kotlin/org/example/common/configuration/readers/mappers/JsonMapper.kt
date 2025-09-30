package org.example.common.configuration.readers.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JsonMapper : MapperStrategy {
    override fun createMapper() = ObjectMapper().registerModule(KotlinModule.Builder().build())
    override fun getSupportedExtensions() = listOf("json")
}
