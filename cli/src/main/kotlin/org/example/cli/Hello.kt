import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument

class Hello : CliktCommand() {
    private val name by argument()

    override fun run() {
        echo("Hello $name!")
    }
}

fun main() = Hello().main(listOf("World"))