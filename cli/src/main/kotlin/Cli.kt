import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class Cli(private val runner: Runner) {
    fun run(command: String) : String {
        val arguments: List<String> = command.split(" ")
        if (!valid(arguments)) { return "Invalid arguments. Usage: <operation> <file_path>" }
        val reader: Optional<Iterator<String>> = createReader(arguments[1])
        if (reader.isEmpty) { return "Could not read file at path: ${arguments[1]}" }
        val version: Optional<String> = getVersion(arguments) // por ahora redirige everything al runner 1.0
        // dsp se puede tener map de runners, acorde a las versiones... algo por el estilo

        when (val operation = arguments[0]) {
            "validate" -> return runner.validate(reader.get())
            "execute" -> return runner.execute(reader.get())
            "format" -> {
                val configFilePath: Optional<Iterator<String>> = getConfigReader(version, arguments)
                return runner.format(reader.get(), configFilePath.get())
            }
            "analyze" -> {
                val configFilePath: Optional<Iterator<String>> = getConfigReader(version, arguments)
                return runner.analyze(reader.get(), configFilePath.get())
            }
            else -> return "Unknown operation: $operation"
        }
    }

    private fun getVersion(arguments: List<String>): Optional<String> {
        if (arguments.size < 4) { return Optional.empty() }
        if (arguments[2] != "-v") { return Optional.empty() }
        if (!properFormat(arguments[3])) { return Optional.empty() }
        return Optional.of(arguments[3])
    }

    private fun valid(arguments: List<String>): Boolean { return arguments.size == 2 }

    private fun properFormat(version: String) = version.matches(Regex("\\d+\\.\\d+"))

    private fun correctSizeArgs(version: Optional<String>, argSize: Int): Boolean {
        return if (version.isEmpty) {
            argSize == 3
        } else {
            argSize == 5
        }
    }

    // este se puede inyectar or sth, si se quiere no depender de input=file
    private fun createReader(pathStr: String): Optional<Iterator<String>> {
        val path = Paths.get(pathStr)
        return if (Files.exists(path)) {
            Optional.of(LineIterator(path))
        } else {
            Optional.empty()
        }
    }
    // esto ata configInput a statementInput (both file, both...whatever)
    // not ideal but also maybe even good? depende de lo que busquemos, ig
    // for now, considerando que se recibe file, dejo both así...
    private fun getConfigReader(version: Optional<String>, arguments: List<String>): Optional<Iterator<String>> {
        if (!correctSizeArgs(version, arguments.size)) return Optional.empty()

        val configFilePath = if (version.isEmpty) arguments[3] else arguments[5]

        return createReader(configFilePath)
    }
}
