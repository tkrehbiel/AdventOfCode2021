package day11

import java.io.File
import Puzzle

fun main() {
    DumboOctopus().run()
}

// https://adventofcode.com/2021/day/11

class DumboOctopus : Puzzle(11, "Dumbo Octopus") {
    override val puzzleInput = "day11_input.txt"
    override val testInput = arrayOf("day11_test.txt")
    override val label1 = "flashes in first 100 steps"
    override val label2 = "steps until synchronization"

    private lateinit var grid: Model
    private var flashes: Long = 0

    // Read the input file in the common pre-step
    override fun common(input: String) {
        super.common(input)
        flashes = 0L
        grid = Model(File(input).readLines())
    }

    override fun part1(input: String): Long {
        for (i in 1..100) {
            flashes += grid.step()
        }
        return flashes
    }
    override fun part2(input: String): Long {
        // Note: Assumes the synchronization happens *after* first 100 steps
        var steps = 100L
        while (!grid.synchronized) {
            grid.step()
            steps++
        }
        return steps
    }
}

private class Model(strings: List<String>) {
    // Maybe didn't save much multiplication by using a single array
    private var grid = IntArray(strings[0].trim().length * strings.size)
    private val width = strings[0].length
    private val height = strings.size
    var synchronized = false

    init {
        // Turn the strings into an int array for computation ease
        var i = 0
        for (s in strings) {
            for (c in s.trim()) {
                grid[i++] = c.digitToInt()
            }
        }
    }

    private var flashes = 0

    // Perform a single step, returns the number of flashes
    fun step(): Int {
        flashes = 0
        synchronized = false
        for (i in grid.indices) {
            increase(i % width, i / width)
        }
        if (flashes >= grid.size) {
            synchronized = true
        }
        reset()
        return flashes
    }

    // Increase energy of one cell and adjacent if needed
    private fun increase(x: Int, y: Int) {
        if( y >= height ) return
        if (x >= width ) return
        if( x >= 0 && y >= 0) {
            val i = y*width+x
            if (grid[i] <= 9) {
                grid[i]++
                if (grid[i] > 9 ) {
                    flashes++
                    adjacent(x, y)
                }
            }
        }
    }

    // Increase energy of adjacent cells
    private fun adjacent(x: Int, y: Int) {
        increase(x-1, y-1)
        increase(x, y-1)
        increase(x+1, y-1)
        increase(x-1, y)
        increase(x+1, y)
        increase(x-1, y+1)
        increase(x, y+1)
        increase(x+1, y+1)
    }

    private fun reset() {
        for (i in grid.indices) {
            if (grid[i] > 9) grid[i] = 0
        }
    }
}
