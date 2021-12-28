package day10

import Puzzle
import java.io.File

fun main() {
    SyntaxScoring().run()
}

// https://adventofcode.com/2021/day/10
/*
Straightforward because it was very similar to a Leetcode problem I once solved.
https://leetcode.com/problems/valid-parentheses/
*/

val syntaxScores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val completeScores = mapOf('(' to 1L, '[' to 2L, '{' to 3L, '<' to 4L)

val autocompleteScores = mutableListOf<Long>()

class SyntaxScoring : Puzzle(10, "Syntax Scoring") {
    override val puzzleInput = "day10_input.txt"
    override val testInput = arrayOf("day10_test.txt")
    override val label1 = "syntax score"
    override val label2 = "autocomplete score"

    override fun part1(input: String): Long {
        autocompleteScores.clear()
        var totalSyntaxScore = 0L
        File(input).forEachLine {
            val parens = Parens(it.length)

            // Part 1 - compute a "syntax score" for invalid parens
            var syntaxScore = 0
            for (c in it.trim()) {
                when (c) {
                    '(', '[', '{', '<' ->
                        parens.push(c)
                    ')', ']', '}', '>' ->
                        if (!parens.validateAndPop(c)) {
                            syntaxScore += syntaxScores[c]!!
                            break
                        }
                }
            }
            totalSyntaxScore += syntaxScore

            // This is actually for Part 2 - Repair incomplete lines.
            // Except we don't actually repair them, we just compute a score.
            if (syntaxScore == 0) {
                // No error score means it's just incomplete.
                // Can't use regular ints because the multiplication exceeds 32-bits.
                var score = 0L
                parens.remaining().forEach { c ->
                    score = 5L * score + completeScores[c]!!
                }
                autocompleteScores.add(score)
            }
        }
        return totalSyntaxScore
    }

    override fun part2(input: String): Long {
        autocompleteScores.sort()
        return autocompleteScores[autocompleteScores.size/2]
    }
}

class Parens(stackDepth: Int) {
    // Didn't necessarily need a class here, but
    // I wanted to experiment with Kotlin classes.

    // This stack is just an array and an index pointer.
    private val stack = CharArray(stackDepth)
    private var depth = 0

    private val matchingBrackets = mapOf(')' to '(', ']' to '[', '}' to '{', '>' to '<')

    // Push a start paren
    fun push(c: Char) {
        stack[depth++] = c
    }

    // Validate an end paren
    fun validateAndPop(c: Char): Boolean {
        if (depth == 0 || stack[--depth] != matchingBrackets[c]) return false
        return true
    }

    // Returns a list of unclosed parens in reverse order
    fun remaining(): CharArray {
        val chars = CharArray(depth)
        for (i in chars.indices) chars[i] = stack[--depth]
        return chars
    }
}
