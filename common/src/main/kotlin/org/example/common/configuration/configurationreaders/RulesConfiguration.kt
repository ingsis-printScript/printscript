package org.example.common.configuration.configurationreaders

data class RulesConfiguration(private val data: Map<String, Any>) {
    fun getString(key: String): String? {
        return data[key]?.toString()
    }

    fun getBoolean(key: String): Boolean {
        return data[key] as? Boolean ?: false
    }

    fun getInt(key: String, default: Int = 0): Int =
        (data[key] as? Number)?.toInt() ?: default
}