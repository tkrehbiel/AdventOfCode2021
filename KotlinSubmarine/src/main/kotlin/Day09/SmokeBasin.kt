package Day09

import Puzzle
import java.io.File

fun main() {
    SmokeBasin().run()
}

// https://adventofcode.com/2021/day/9
class SmokeBasin : Puzzle(9, "Smoke Basin") {
    override val puzzleInput = "day09_input.txt"
    override val testInput = arrayOf("day09_test.txt")
    override val label1 = "low point risk levels"
    override val label2 = "three largest basins"

    // Width and height might be different
    private var gridWidth: Int = 0
    private var gridLength: Int = 0

    private var risk: Long = 0L
    private val basinSizes = mutableListOf<Long>()

    // Making 2D arrays is a Kotlin weakness :)
    private var grid: Array<IntArray> = arrayOf()

    // Run one common function to get both part 1 and 2 answers.
    override fun common(input: String) {
        risk = 0L
        basinSizes.clear()

        readInput(input)

        // Brute force iteration over the whole grid.
        // Time and memory usage expands with the size of the grid.
        // For part 1, could possibly pre-compute the sample array for each coordinate as we're loading above.
        for (j in grid.indices) {
            for (i in grid[j].indices) {
                // Part 1 - Is current sample less than all adjacent samples?
                val current = grid[j][i]
                val samples = mutableListOf<Int>()
                if (j > 0) samples.add(grid[j-1][i])
                if (j < grid.size-1) samples.add(grid[j+1][i])
                if (i > 0) samples.add(grid[j][i-1])
                if (i < grid[j].size-1) samples.add(grid[j][i+1])
                var lessThanCount = 0
                samples.forEach {
                    if (current < it) lessThanCount++
                }
                if (lessThanCount == samples.size) {
                    risk += current + 1
                    // Part 2 - Search adjacent samples for bounds of "basin"
                    basinSizes.add(search(grid, i, j))
                }
            }
        }
    }

    override fun part1(input: String): Long {
        return risk
    }

    override fun part2(input: String): Long {
        // Only want the top 3 basins
        basinSizes.sortDescending()
        return basinSizes[0] * basinSizes[1] * basinSizes[2]
    }

    private fun search(grid: Array<IntArray>, x: Int, y: Int): Long {
        var count = 1L
        val current = grid[y][x]
        grid[y][x] = -1
        if (x > 0) count += recurse(grid, current, x-1, y)
        if (y > 0) count += recurse(grid, current, x, y-1)
        if (x < grid[y].size - 1) count += recurse(grid, current, x+1, y)
        if( y < grid.size - 1) count += recurse(grid, current, x, y+1)
        return count
    }

    private fun recurse(grid: Array<IntArray>, compare: Int, x: Int, y: Int): Long {
        if (grid[y][x] <= compare || grid[y][x] == 9) return 0
        var count = 1L
        val current = grid[y][x]
        if (current == -1) return 0 // already counted
        grid[y][x] = -1 // -1 means we already counted this cell
        if (x > 0) count += recurse(grid, current, x-1, y)
        if (y > 0) count += recurse(grid, current, x, y-1)
        if (x < grid[y].size - 1) count += recurse(grid, current, x+1, y)
        if( y < grid.size - 1) count += recurse(grid, current, x, y+1)
        return count
    }

    private fun readInput(fileName: String) {
        val lines = File(fileName).readLines()

        // Width and height might be different
        gridWidth = lines[0].trim().length
        gridLength = lines.size

        // Making 2D arrays is a Kotlin weakness :)
        grid = Array(gridLength) { IntArray(gridWidth) }

        // Converting strings to an integer grid, probably not necessary
        // Ugly in Kotlin
        var row = 0
        lines.forEach { s->
            var col = 0
            s.trim().forEach { c->
                grid[row][col] = c.digitToInt()
                col++
            }
            row++
        }
    }
}
