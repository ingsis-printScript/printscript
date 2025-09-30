package org.example.linter.provider

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.configuration.readers.ConfigurationReader
import org.example.common.configuration.readers.mappers.JsonMapper
import org.example.common.configuration.readers.mappers.YamlMapper
import org.example.common.enums.SymbolFormat
import org.example.common.results.Result
import org.example.linter.Linter
import org.example.linter.rules.functionargument.PrintArgumentRule
import org.example.linter.rules.symbolformat.SymbolFormatRule
import org.example.linter.rules.symbolformat.checker.CamelCaseChecker
import org.example.linter.rules.symbolformat.checker.SnakeCaseChecker
import java.io.InputStream
import kotlin.reflect.KClass

class LinterProvider10() : LinterProvider {
    override fun provide(iterator: PrintScriptIterator<Result>, inputStream: InputStream, errorHandler: ErrorHandler): Linter {
        val symbolFormats = mapOf(
            SymbolFormat.CAMEL_CASE to CamelCaseChecker(),
            SymbolFormat.SNAKE_CASE to SnakeCaseChecker()
        )

        val prohibitedNodes = setOf(BinaryExpression::class)
        val supportedNodes = createSupportedNodes()
        val symbolNodeHandler = createSymbolNodeHandler()

        val rules = listOf(
            PrintArgumentRule(prohibitedNodes, supportedNodes),
            SymbolFormatRule(symbolFormats, supportedNodes, symbolNodeHandler)
        )
        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        val linter = Linter(iterator, rules, configurationReader, inputStream, errorHandler)
        return linter
    }

    private fun createSupportedNodes(): Set<KClass<out ASTNode>> {
        val supported = setOf(
            BinaryExpression::class,
            SymbolExpression::class,
            NumberExpression::class,
            StringExpression::class,
            PrintFunction::class,
            VariableAssigner::class,
            VariableDeclarator::class
        )
        return supported
    }

    private fun createSymbolNodeHandler(): (ASTNode, (SymbolExpression) -> Unit) -> Unit {
        return { node, symbolChecker ->
            when (node) {
                is SymbolExpression -> symbolChecker(node)
                is BinaryExpression -> {
                    createSymbolNodeHandler()(node.left, symbolChecker)
                    createSymbolNodeHandler()(node.right, symbolChecker)
                }
                is PrintFunction -> {
                    checkOptionalExpression(node.value, symbolChecker)
                }
                is VariableAssigner -> {
                    symbolChecker(node.symbol)
                    checkOptionalExpression(node.value, symbolChecker)
                }
                is VariableDeclarator -> {
                    symbolChecker(node.symbol)
                    checkOptionalExpression(node.value, symbolChecker)
                }
                is NumberExpression -> {}
                is StringExpression -> {}
                else -> throw IllegalArgumentException("Unsupported node type: $node")
            }
        }
    }

    private fun checkOptionalExpression(value: OptionalExpression, symbolChecker: (SymbolExpression) -> Unit) {
        if (value is OptionalExpression.HasExpression) {
            createSymbolNodeHandler()(value.expression, symbolChecker)
        }
    }
}
