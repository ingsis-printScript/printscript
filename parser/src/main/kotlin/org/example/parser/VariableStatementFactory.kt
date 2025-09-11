package org.example.parser

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Statement
import org.example.common.Range
import org.example.common.enums.Type

typealias VariableStatementFactory = (SymbolExpression, Type, Range, OptionalExpression) -> Statement
