import java.io.File
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Ruler(private val rules: Map<String, Rule>) {

    //Metodo de fábrica: fromJsonFile("rules-v10.json")
    //Lee un JSON, lo parsea con kotlinx.serialization, y devuelve un Ruler con ese Map<String, Rule>.
    companion object {
        fun fromJsonFile(filePath: String): Ruler {
            val file = File(filePath)
            val jsonString = file.readText()

            val map = Json.decodeFromString<Map<String, Rule>>(jsonString)
            return Ruler(map)
        }
    }

    fun allRules(): Map<String, Rule> = rules

    fun getRule(name: String): Rule? = rules[name]

    fun isActive(name: String): Boolean = rules[name]?.rule == true

    fun quantity(name: String, default: Int = 4): Int =
        rules[name]?.quantity ?: default
}
