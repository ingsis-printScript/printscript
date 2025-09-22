package org.example.common.configuration.configurationreaders

import com.fasterxml.jackson.module.kotlin.readValue
import org.example.common.configuration.configurationreaders.mappers.MapperStrategy
import java.io.File

class ConfigurationReader(private val strategies: List<MapperStrategy>) {

    fun read(filePath: String): Map<String, Any> {
        val file = File(filePath)

        if (!file.exists()) {
            throw IllegalArgumentException("Configuration file not found: $filePath")
        }

        val extension = File(filePath).extension.lowercase()

        val strategy = strategies.firstOrNull {
            extension in it.getSupportedExtensions()
        } ?: throw IllegalArgumentException("Unsupported format: $extension")

        return try {
            val mapper = strategy.createMapper()
            mapper.readValue<Map<String, Any>>(file)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid format in file: $filePath. Error: ${e.message}")
        }
    }
}
