package day20

import Puzzle
import java.io.File

fun main() {
    TrenchMap().run()
}

// https://adventofcode.com/2021/day/20
/*
A straightforward image processing convolution matrix, with a minor twist:
Handling the infinite edges with enhancement[0] and enhancement[511].
Most of the work is in the Image class.
*/

class TrenchMap : Puzzle(20, "Trench Map") {
    override val puzzleInput = "day20_input.txt"
    override val testInput = arrayOf("day20_test.txt")
    override val label1 = "2 enhancements"
    override val label2 = "50 enhancements"

    private var startingImage = Image(false)
    private lateinit var previousImage: Image
    private lateinit var enhancements: BooleanArray

    override fun common(input: String) {
        super.common(input)

        // Read and populate the initial image
        val lines = File(input).readLines()

        val s = lines[0]
        enhancements = BooleanArray(s.length)
        for (i in s.indices) {
            if (s[i] == '#') enhancements[i] = true
        }

        startingImage = Image(false)
        for (row in 2 until lines.size) {
            for (i in lines[row].indices) {
                if (lines[row][i] == '#')
                    startingImage.set(Point(i, row-2))
            }
        }
    }

    override fun part1(input: String): Long {
        previousImage = startingImage
        for (i in 0 until 2) {
            val image = previousImage.enhance(enhancements)
            previousImage = image
        }
        return previousImage.count()
    }

    override fun part2(input: String): Long {
        for (i in 2 until 50) {
            val image = previousImage.enhance(enhancements)
            previousImage = image
        }
        return previousImage.count()
    }
}
