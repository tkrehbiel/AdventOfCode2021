package Day06

import Puzzle
import java.io.File

fun main() {
    Lanternfish().run()
}

// https://adventofcode.com/2021/day/6
class Lanternfish : Puzzle(6, "Lanternfish") {
    override val puzzleInput = "day06_input.txt"
    override val testInput = arrayOf("day06_test.txt")
    override val label1 = "after 80 days"
    override val label2 = "after 256 days"

    private var ages = longArrayOf()
    private var generation = 0

    // Part 1 - 80 generations
    override fun part1(input: String): Long {
        readFile(input)
        visualize()
        iterateTo(80)
        return getTotal()
    }

    // Part 2 - 256 generations
    override fun part2(input: String): Long {
        // Don't re-read file or reset the array!
        // We are continuing from where we left off in part 1.
        iterateTo(256)
        return getTotal()
    }

    private fun iterateTo(generations: Int) {
        while (generation < generations) {
            rotate()
            visualize()
            generation++
        }
    }

    // Rotate the ages of the lanternfish
    private fun rotate() {
        val n = ages[0]
        for (i in 1..8) {
            ages[i-1] = ages[i]
        }
        ages[6] += n
        ages[8] = n
    }

    // Get the total from the age array
    private fun getTotal(): Long {
        var count = 0L
        for (i in 0..8) {
            count += ages[i]
        }
        return count
    }

    // Read an input file and initialize state
    private fun readFile(fileName: String) {
        // Reset globals here too
        ages = longArrayOf(0L,0L,0L,0L,0L,0L,0L,0L,0L)
        generation = 0

        File(fileName).readText()
            .trim()
            .split(',')
            .forEach {
                ages[it.toInt()]++
            }
    }

    override fun visualize() {
        snapshot(listOf("${ages[0]},${ages[1]},${ages[2]},${ages[3]},${ages[4]},${ages[5]},${ages[6]},${ages[7]},${ages[8]}"))
    }

    /*
    private fun printState(ages: IntArray) {
        var s = ""
        for (i in 0..8) {
            for (j in 1..ages[i]) {
                s += "$i,"
            }
        }
        println(s)
    }
    */
}
