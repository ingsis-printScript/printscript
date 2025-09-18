package org.example.formatter

import kotlinx.serialization.json.*
import java.io.File

class Ruler(private val rules: Map<String, Rule>) {

    companion object {
        fun fromJsonFile(filePath: String): Ruler {
            val raw = File(filePath).readText()
            val rules = parseFlexibleRules(raw)
            return Ruler(rules)
        }

        private fun parseFlexibleRules(raw: String): Map<String, Rule> {
            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(raw)
            require(root is JsonObject) { "Rules JSON must be an object" }

            val out = mutableMapOf<String, Rule>()
            for ((key, value) in root) {
                out[key] = toRule(value)
            }
            return out
        }

        private fun toRule(el: JsonElement): Rule {
            return when (el) {
                is JsonObject -> {
                    // Forma canónica: { "rule": bool, "quantity": int? }
                    val rule = el["rule"]?.jsonPrimitive?.booleanOrNull
                    val qty = el["quantity"]?.jsonPrimitive?.intOrNull
                    when {
                        rule != null -> Rule(rule = rule, quantity = qty)
                        // Si no vino "rule" pero vino solo "quantity", asumimos rule=true
                        qty != null -> Rule(rule = true, quantity = qty)
                        else -> Rule(rule = true, quantity = null)
                    }
                }
                is JsonPrimitive -> {
                    el.booleanOrNull?.let { return Rule(rule = it, quantity = null) }
                    el.intOrNull?.let { return Rule(rule = true, quantity = it) }
                    // Por si viene número grande o string numérica
                    el.longOrNull?.let { return Rule(rule = true, quantity = it.toInt()) }
                    el.content.toIntOrNull()?.let { return Rule(rule = true, quantity = it) }
                    // Fallback razonable
                    Rule(rule = true, quantity = null)
                }
                else -> Rule(rule = true, quantity = null)
            }
        }

        // Helpers de JsonPrimitive (por si tu versión no los trae)
        private val JsonPrimitive.intOrNull: Int?
            get() = try { this.int } catch (_: Throwable) { null }
        private val JsonPrimitive.longOrNull: Long?
            get() = try { this.long } catch (_: Throwable) { null }
        private val JsonPrimitive.booleanOrNull: Boolean?
            get() = try { this.boolean } catch (_: Throwable) { null }
    }

    fun allRules(): Map<String, Rule> = rules
    fun getRule(name: String): Rule? = rules[name]
    fun isActive(name: String): Boolean = rules[name]?.rule == true
    fun quantity(name: String, default: Int = 4): Int = rules[name]?.quantity ?: default
}
