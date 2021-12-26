package Day00

import Puzzle

fun main() {
    Template().run()
}

// https://adventofcode.com/2021/day/1
class Template : Puzzle(1, "Funny Title!") {
    override val puzzleInput = "day01_input.txt"
    override val testInput = arrayOf("day01_test.txt")
    override val label1 = ""
    override val label2 = ""

    override fun part1(input: String): Long {
        TODO("part 1 not implemented")
    }
    override fun part2(input: String): Long {
        TODO("part 2 not implemented")
    }
}
