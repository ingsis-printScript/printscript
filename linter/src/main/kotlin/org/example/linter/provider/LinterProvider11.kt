package org.example.linter.provider

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Condition
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.configuration.readers.ConfigurationReader
import org.example.common.configuration.readers.mappers.JsonMapper
import org.example.common.configuration.readers.mappers.YamlMapper
import org.example.common.enums.SymbolFormat
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.linter.Linter
import org.example.linter.rules.functionargument.PrintArgumentRule
import org.example.linter.rules.functionargument.ReadInputArgumentRule
import org.example.linter.rules.symbolformat.SymbolFormatRule
import org.example.linter.rules.symbolformat.checker.CamelCaseChecker
import org.example.linter.rules.symbolformat.checker.SnakeCaseChecker
import java.io.InputStream
import kotlin.reflect.KClass

class LinterProvider11 : LinterProvider {
    override fun provide(
        iterator: PrintScriptIterator<Result>,
        inputStream: InputStream,
        errorHandler: ErrorHandler
    ): Linter {
        val symbolFormats = mapOf(
            SymbolFormat.CAMEL_CASE to CamelCaseChecker(),
            SymbolFormat.SNAKE_CASE to SnakeCaseChecker()
        )

        val prohibitedNodes = setOf(BinaryExpression::class)
        val supportedNodes = createSupportedNodes()
        val symbolNodeHandler = createSymbolNodeHandler()
        val readInputNodeHandler = createReadInputNodeHandler()

        val rules = listOf(
            PrintArgumentRule(prohibitedNodes, supportedNodes),
            SymbolFormatRule(symbolFormats, supportedNodes, symbolNodeHandler),
            ReadInputArgumentRule(prohibitedNodes, supportedNodes, readInputNodeHandler)
        )
        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        val linter = Linter(iterator, rules, configurationReader, inputStream, errorHandler)
        return linter
    }

    private fun createSupportedNodes(): Set<KClass<out ASTNode>> {
        val supported = setOf(
            BinaryExpression::class,
            BooleanExpression::class,
            ReadEnvExpression::class,
            ReadInputExpression::class,
            SymbolExpression::class,
            NumberExpression::class,
            StringExpression::class,
            PrintFunction::class,
            VariableAssigner::class,
            VariableDeclarator::class,
            VariableImmutableDeclarator::class,
            Condition::class
        )
        return supported
    }

    private fun createSymbolNodeHandler(): (ASTNode, (SymbolExpression) -> Unit) -> Unit {
        return { node, symbolChecker ->
            when (node) {
                is SymbolExpression -> symbolChecker(node)
                is BinaryExpression -> handleBinaryExpression(node, symbolChecker)
                is PrintFunction -> checkOptionalExpression(node.value, symbolChecker)
                is VariableAssigner -> handleVariableNode(node.symbol, node.value, symbolChecker)
                is VariableDeclarator -> handleVariableNode(node.symbol, node.value, symbolChecker)
                is VariableImmutableDeclarator -> handleVariableNode(node.symbol, node.value, symbolChecker)
                is ReadInputExpression -> checkOptionalExpression(node.value, symbolChecker)
                is ReadEnvExpression -> checkOptionalExpression(node.value, symbolChecker)
                is Condition -> handleCondition(node, symbolChecker)
                is BooleanExpression, is NumberExpression, is StringExpression -> { /* No action needed */ }

                else -> throw IllegalArgumentException("Unsupported node type: $node")
            }
        }
    }

    private fun handleBinaryExpression(node: BinaryExpression, symbolChecker: (SymbolExpression) -> Unit) {
        val handler = createSymbolNodeHandler()
        handler(node.left, symbolChecker)
        handler(node.right, symbolChecker)
    }

    private fun handleVariableNode(
        symbol: SymbolExpression,
        value: OptionalExpression,
        symbolChecker: (SymbolExpression) -> Unit
    ) {
        symbolChecker(symbol)
        checkOptionalExpression(value, symbolChecker)
    }

    private fun handleCondition(node: Condition, symbolChecker: (SymbolExpression) -> Unit) {
        val handler = createSymbolNodeHandler()
        node.ifBlock.forEach { handler(it, symbolChecker) }
        node.elseBlock?.forEach { handler(it, symbolChecker) }
    }

    private fun checkOptionalExpression(value: OptionalExpression, symbolChecker: (SymbolExpression) -> Unit) {
        if (value is OptionalExpression.HasExpression) {
            createSymbolNodeHandler()(value.expression, symbolChecker)
        }
    }

    private fun createReadInputNodeHandler(): (ASTNode, (ReadInputExpression) -> Result) -> Result {
        return { node, visit -> walkReadInput(node, visit) }
    }

    private fun walkReadInput(
        node: ASTNode,
        visit: (ReadInputExpression) -> Result
    ): Result {
        return when (node) {
            is ReadInputExpression -> visit(node)

            is PrintFunction -> walkOptional(node.value, visit)
            is VariableAssigner -> walkOptional(node.value, visit)
            is VariableDeclarator -> walkOptional(node.value, visit)
            is VariableImmutableDeclarator -> walkOptional(node.value, visit)
            is ReadEnvExpression -> walkOptional(node.value, visit)

            is BinaryExpression -> {
                val r1 = walkReadInput(node.left as ASTNode, visit)
                val r2 = walkReadInput(node.right as ASTNode, visit)
                mergeResults(r1, r2)
            }

            is Condition -> {
                val results = buildList {
                    node.ifBlock.forEach { add(walkReadInput(it, visit)) }
                    node.elseBlock?.forEach { add(walkReadInput(it, visit)) }
                }
                results.fold<Result, Result>(Success(Unit)) { acc, r -> mergeResults(acc, r) }
            }

            is SymbolExpression, is NumberExpression, is StringExpression, is BooleanExpression -> Success(Unit)

            else -> Error("Unsupported node type: $node")
        }
    }

    private fun walkOptional(
        value: OptionalExpression,
        visit: (ReadInputExpression) -> Result
    ): Result {
        return if (value is OptionalExpression.HasExpression) {
            walkReadInput(value.expression as ASTNode, visit)
        } else {
            Success(Unit)
        }
    }

    private fun mergeResults(r1: Result, r2: Result): Result {
        return when {
            r1 is Error -> r1
            r2 is Error -> r2
            else -> Success(Unit)
        }
    }
}
