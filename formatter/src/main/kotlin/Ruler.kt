import java.io.File

class Ruler(private val rules: Map<String, Rule>) {

    companion object {
        fun fromJsonFile(filePath: String): Ruler {
            val file = File(filePath)
            val jsonString = file.readText()

            val map = Json.decodeFromString<Map<String, Rule>>(jsonString)
            return Ruler(map)
        }
    }

    fun getRule(name: String): Rule? = rules[name]

    fun isActive(name: String): Boolean = rules[name]?.rule == true

    fun quantity(name: String, default: Int = 4): Int =
        rules[name]?.quantity ?: default
}
