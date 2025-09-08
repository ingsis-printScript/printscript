package org.example.linter

data class LinterConfiguration(private val data: Map<String, Any>){
    fun getString(key: String): String? {
        return data[key]?.toString()
    }

    fun getBoolean(key: String): Boolean {
        return data[key] as? Boolean ?: false
    }

    fun getInt(key: String): Int? {
        return data[key] as? Int
    }
}