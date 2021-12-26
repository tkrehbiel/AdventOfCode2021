package Day08

import Puzzle
import java.io.File

fun main() {
    SevenSegmentSearch().run()
}

// https://adventofcode.com/2021/day/8
class SevenSegmentSearch : Puzzle(8, "Seven Segment Search") {
    override val puzzleInput = "day08_input.txt"
    override val testInput = arrayOf("day08_test.txt")
    override val label1 = "unique digits"
    override val label2 = "sum of all digits"

    private var unique = 0
    private var total = 0

    // Run one common search to get both part 1 and 2 answers.
    override fun common(input: String) {
        unique = 0
        total = 0
        File(input).forEachLine { it ->
            val fields = it.trim().split("|")
            val patterns = fields[0].trim().split(" ")
            val digits = fields[1].trim().split(" ")

            // Take note of key patterns for "1" and "4"
            // We can use those to deduce other patterns.
            // Otherwise we can ignore that part of the input.
            var onePattern = ""
            var fourPattern = ""
            patterns.forEach { p ->
                when (p.length) {
                    2 -> onePattern = p
                    4 -> fourPattern = p
                }
            }

            var value = 0
            digits.forEach { d ->
                var digit = 0
                // Part 1 - Find unique digits "1", "7", "4", "8"
                // Only need to check length of digit strings.
                when (d.length) {
                    2, 3, 4, 7 -> unique++
                }
                // Part 2 - Decode the actual numbers.
                when (d.length) {
                    2 -> digit = 1
                    3 -> digit = 7
                    4 -> digit = 4
                    7 -> digit = 8
                    5 -> {
                        // Length 5 could be "2", "3", or "5"
                        // We know it's a 3 if it contains the "1" pattern
                        if (containsPattern(d, onePattern, 2)) {
                            digit = 3
                        } else {
                            // If it contains at least 3 of "4" pattern segments, it's a 5.
                            // Otherwise, 2 is the only other option.
                            digit = if (containsPattern(d, fourPattern, 3)) {
                                5
                            } else {
                                2
                            }
                        }
                    }
                    6 -> {
                        // Length 6 could be "0", "6" or "9"
                        // If it contains all the "4" pattern, it's a 9.
                        if (containsPattern(d, fourPattern, 4)) {
                            digit = 9
                        } else {
                            // Now if it contains the "1" pattern, it's a 0
                            // Otherwise it has to be 6.
                            digit = if (containsPattern(d, onePattern, 2)) {
                                0
                            } else {
                                6
                            }
                        }
                    }
                }
                value = value * 10 + digit
            }
            total += value
        }
    }

    override fun part1(input: String): Long {
        return unique.toLong()
    }

    override fun part2(input: String): Long {
        return total.toLong()
    }

    // Returns true if the string contains at least "minMatches" of the letters in the pattern string
    private fun containsPattern(s: String, pattern: String, minMatches: Int) : Boolean {
        var count = 0
        s.forEach { c ->
            if (pattern.contains(c)) count++
        }
        return count >= minMatches
    }

}
