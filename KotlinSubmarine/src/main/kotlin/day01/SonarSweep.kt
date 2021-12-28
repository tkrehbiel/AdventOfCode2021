package day01

import Puzzle
import java.io.File

fun main() {
    SonarSweep().run()
}

// https://adventofcode.com/2021/day/1
class SonarSweep : Puzzle(1, "Sonar Sweep") {
    override val label1 = "increases"
    override val label2 = "3-row increases"

    override val puzzleInput = "day01_input.txt"
    override val testInput = arrayOf("day01_test.txt")

    // Part 1 - Just a simple comparison for each line
    override fun part1(input: String): Long {
        var total = 0L
        var row = 0
        var lastValue = 0
        File(input).forEachLine {
            val i = it.toInt()
            if (row > 0 && i > lastValue) total++
            lastValue = i
            row++
        }
        return total
    }

    // Part 2 - Compare 3-row sum for each line
    override fun part2(input: String): Long {
        // Uses a 3-element array to keep track of the sliding window of values
        // So we don't have to read the file into memory beforehand
        var total = 0L
        var row = 0
        var lastValue = 0
        val previous = IntArray(3)
        File(input).forEachLine {
            val i = it.toInt()
            val tally = previous[0] + previous[1] + previous[2]
            if (row > 2 && tally > lastValue) total++
            lastValue = tally
            previous[2] = previous[1]
            previous[1] = previous[0]
            previous[0] = i
            row++
        }
        return total
    }
}

