/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests

import com.intellij.openapi.util.io.FileUtil
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

object GenerateInRangeExpressionTestData {
    private val TEST_DATA_DIR = File("compiler/testData/codegen/box/ranges/contains")
    private val GENERATED_DIR = File(TEST_DATA_DIR, "generated")

    private val PREAMBLE_MESSAGE = "Auto-generated by ${GenerateInRangeExpressionTestData::class.java.simpleName}. Do not edit!"

    private fun generateMatrixTestCase(
        fileName: String,
        rangeExpressions: List<String>,
        elementExpressions: List<String>,
        header: String = ""
    ) {
        PrintWriter(File(GENERATED_DIR, fileName)).use {
            it.generateTestCaseBody(
                header, rangeExpressions, elementExpressions
            )
        }
    }

    private fun PrintWriter.generateTestCaseBody(
        header: String,
        rangeExpressions: List<String>,
        elementExpressions: List<String>
    ) {
        println("// KJS_WITH_FULL_RUNTIME")
        println("// $PREAMBLE_MESSAGE")
        println("// WITH_RUNTIME")
        println()
        println(header)
        println()

        val rangeValNames = generateGlobalValDefinitions(rangeExpressions, "range")

        val elementValNames = generateGlobalValDefinitions(elementExpressions, "element")

        val testFunctions = StringWriter()
        val testFunctionsWriter = PrintWriter(testFunctions)

        println("fun box(): String {")
        rangeValNames.zip(rangeExpressions).forEachIndexed { i, (rangeValName, rangeExpression) ->
            elementValNames.zip(elementExpressions).forEachIndexed { j, (elementValName, elementExpression) ->
                val functionName = "testR${i}xE${j}"

                println("    $functionName()")

                testFunctionsWriter.generateTestCaseFunction(functionName, rangeValName, rangeExpression, elementValName, elementExpression)
            }
        }
        println("    return \"OK\"")
        println("}")
        println()
        println(testFunctions.toString())
    }

    private fun PrintWriter.generateGlobalValDefinitions(expressions: List<String>, prefix: String): List<String> {
        val valNames = expressions.indices.map { "$prefix$it" }
        valNames.zip(expressions).forEach { (name, expression) -> println("val $name = $expression") }
        println()
        return valNames
    }

    private fun PrintWriter.generateTestCaseFunction(
        functionName: String,
        rangeValName: String,
        rangeExpression: String,
        elementValName: String,
        elementExpression: String
    ) {
        println("fun $functionName() {")
        println("    // with possible local optimizations")
        println("    if ($elementExpression in $rangeExpression != $rangeValName.contains($elementExpression)) throw AssertionError()")
        println("    if ($elementExpression !in $rangeExpression != !$rangeValName.contains($elementExpression)) throw AssertionError()")
        println("    if (!($elementExpression in $rangeExpression) != !$rangeValName.contains($elementExpression)) throw AssertionError()")
        println("    if (!($elementExpression !in $rangeExpression) != $rangeValName.contains($elementExpression)) throw AssertionError()")
        println("    // no local optimizations")
        println("    if ($elementValName in $rangeExpression != $rangeValName.contains($elementValName)) throw AssertionError()")
        println("    if ($elementValName !in $rangeExpression != !$rangeValName.contains($elementValName)) throw AssertionError()")
        println("    if (!($elementValName in $rangeExpression) != !$rangeValName.contains($elementValName)) throw AssertionError()")
        println("    if (!($elementValName !in $rangeExpression) != $rangeValName.contains($elementValName)) throw AssertionError()")
        println("}")
        println()
    }

    private fun generateRangeOperatorTestCase(
        name: String,
        aExpression: String,
        op: String,
        bExpression: String,
        elementExpressions: List<String>
    ) {
        generateMatrixTestCase(
            name,
            listOf(
                "$aExpression $op $bExpression",
                "$bExpression $op $aExpression"
            ),
            elementExpressions
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (!TEST_DATA_DIR.exists()) throw AssertionError("${TEST_DATA_DIR.path} doesn't exist")

        FileUtil.delete(GENERATED_DIR)
        GENERATED_DIR.mkdirs()

        val charLiterals = listOf("'0'", "'1'", "'2'", "'3'", "'4'")

        val integerLiterals =
            listOf("(-1)", "0", "1", "2", "3", "4").flatMap {
                listOf("$it.toByte()", "$it.toShort()", it, "$it.toLong()")
            }
        val floatingPointLiterals =
            listOf("(-1)", "0", "1", "2", "3", "4").flatMap {
                listOf("$it.toFloat()", "$it.toDouble()")
            }

        generateRangeOperatorTestCase(
            "charRangeLiteral.kt",
            "'1'",
            "..",
            "'3'",
            charLiterals
        )
        generateRangeOperatorTestCase(
            "charUntil.kt",
            "'1'",
            "until",
            "'3'",
            charLiterals
        )
        generateRangeOperatorTestCase(
            "charDownTo.kt",
            "'3'",
            "downTo",
            "'1'",
            charLiterals
        )

        generateRangeOperatorTestCase(
            "intRangeLiteral.kt",
            "1",
            "..",
            "3",
            integerLiterals
        )
        generateRangeOperatorTestCase(
            "intUntil.kt",
            "1",
            "until",
            "3",
            integerLiterals
        )
        generateRangeOperatorTestCase(
            "intDownTo.kt",
            "3",
            "downTo",
            "1",
            listOf("1")
        )

        generateRangeOperatorTestCase(
            "longRangeLiteral.kt",
            "1L",
            "..",
            "3L",
            integerLiterals
        )
        generateRangeOperatorTestCase(
            "longUntil.kt",
            "1L",
            "until",
            "3L",
            integerLiterals
        )
        generateRangeOperatorTestCase(
            "longDownTo.kt",
            "3L",
            "downTo",
            "1L",
            listOf("1L")
        )

        generateRangeOperatorTestCase(
            "floatRangeLiteral.kt",
            "1.0F",
            "..",
            "3.0F",
            floatingPointLiterals
        )

        generateRangeOperatorTestCase(
            "doubleRangeLiteral.kt",
            "1.0",
            "..",
            "3.0",
            floatingPointLiterals
        )

        generateMatrixTestCase(
            "arrayIndices.kt",
            listOf("intArray.indices", "objectArray.indices", "emptyIntArray.indices", "emptyObjectArray.indices"),
            integerLiterals,
            """val intArray = intArrayOf(1, 2, 3)
                    |val objectArray = arrayOf(1, 2, 3)
                    |val emptyIntArray = intArrayOf()
                    |val emptyObjectArray = arrayOf<Any>()
                """.trimMargin()
        )

        generateMatrixTestCase(
            "collectionIndices.kt",
            listOf("collection.indices", "emptyCollection.indices"),
            integerLiterals,
            """val collection = listOf(1, 2, 3)
                    |val emptyCollection = listOf<Any>()
                """.trimMargin()
        )

        generateMatrixTestCase(
            "charSequenceIndices.kt",
            listOf("charSequence.indices", "emptyCharSequence.indices"),
            integerLiterals,
            """val charSequence: CharSequence = "123"
                    |val emptyCharSequence: CharSequence = ""
                """.trimMargin()
        )
    }
}
