package org.example.common.configuration.readers.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class YamlMapper : MapperStrategy {
    override fun createMapper() = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
    override fun getSupportedExtensions() = listOf("yaml", "yml")
}
