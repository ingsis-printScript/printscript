package constructors

import org.example.common.Position
import org.example.token.Token
import java.util.*

interface TokenConstructor {
    fun constructToken(input: String, offset: Int, position: Position): Optional<Token>
}
