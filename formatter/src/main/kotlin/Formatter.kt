import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator

class Formatter(
    private val rules: Map<String, Rule>,
    private val nodes: PrintScriptIterator<ASTNode>
) {
    fun format(): String {
        TODO()
    }


}