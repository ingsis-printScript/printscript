import formatters.BinaryExpressionFormat
import formatters.BooleanExpressionFormat
import formatters.NumberExpressionFormat
import formatters.PrintFunctionFormat
import formatters.StringExpressionFormat
import formatters.SymbolExpressionFormat
import formatters.VariableAssignerFormat
import formatters.VariableDeclaratorFormat
import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator

//context: FormatterProvider es el único lugar donde decidís qué formatos existen en cada versión.

class FormatterProvider(private val ruler: Ruler) {

    fun provideVersion1_0(nodes: PrintScriptIterator<ASTNode>): Formatter {
        val rules = ruler.allRules()
        val formats = listOf(
            BinaryExpressionFormat(),
            NumberExpressionFormat(),
            StringExpressionFormat(),
            VariableDeclaratorFormat(),
            PrintFunctionFormat(),
            SymbolExpressionFormat(),
            VariableAssignerFormat()
        )
        return Formatter(rules, nodes, CompositeASTFormat(formats))
    }

    fun provideVersion1_1(nodes: PrintScriptIterator<ASTNode>): Formatter {
        val rules = ruler.allRules()
        val formats = listOf(
            BinaryExpressionFormat(),
            NumberExpressionFormat(),
            StringExpressionFormat(),
            PrintFunctionFormat(),
            BooleanExpressionFormat(),
            VariableDeclaratorFormat(),
            VariableDeclaratorFormat(),
            SymbolExpressionFormat(),
            VariableAssignerFormat()
        )
        return Formatter(rules, nodes, CompositeASTFormat(formats))
    }
}
