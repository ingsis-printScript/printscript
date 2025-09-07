package org.example.linter.configurationreaders.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JsonMapper : MapperStrategy {
    override fun createMapper() = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
    override fun getSupportedExtensions() = listOf("json")
}