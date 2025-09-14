import org.example.interpreter.input.ConsoleInputProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class ConsoleInputProviderTest {

    @Test
    fun `should read input from console`() {
        val simulatedInput = "Hola Mundo\n"
        val inputStream: InputStream = ByteArrayInputStream(simulatedInput.toByteArray())
        System.setIn(inputStream)

        val provider = ConsoleInputProvider()
        val result = provider.readInput("Escriba algo")

        assertEquals("Hola Mundo", result)
    }

    @Test
    fun `should return empty string when input is empty`() {
        val simulatedInput = "\n"
        val inputStream: InputStream = ByteArrayInputStream(simulatedInput.toByteArray())
        System.setIn(inputStream)

        val provider = ConsoleInputProvider()
        val result = provider.readInput("Ingrese algo")

        assertEquals("", result)
    }
}
