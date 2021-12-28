package Day13

import Puzzle
import java.io.File

fun main() {
    TransparentOrigami().run()
}

// https://adventofcode.com/2021/day/13
/*
Fairly straightforward. The only trick here is not to try to make a pixel grid,
but rather just keep a list of points. (I mean, you probably *could* make a pixel grid,
and it might work for this puzzle since it would only be 1000x1000 or so,
but it would waste a lot of time and memory, and it’s easier without it.)
*/

class TransparentOrigami : Puzzle(13, "Transparent Origami") {
    override val puzzleInput = "day13_input.txt"
    override val testInput = arrayOf("day13_test.txt")
    override val label1 = "dots after one fold"
    override val label2 = "part 2 is not a number"

    override fun part1(input: String): Long {
        fold(input, 1)
        return list.size.toLong()
    }
    override fun part2(input: String): Long {
        fold(input, Int.MAX_VALUE)
        printGrid()
        return 0 // There's no numerical value for part 2 of this puzzle
    }
}

private val list = mutableListOf<Coordinate>()

private fun fold(input: String, n: Int) {
    var count = 0
    list.clear()
    File(input).forEachLine {
        val s = it.trim()
        if (s.startsWith("fold along y=")) {
            val y = s.substring(13).toInt()
            if (count < n) verticalFoldAt(y)
            count++
            //println("After vertical fold: ${list.size}")
        } else if (s.startsWith("fold along x=")) {
            val x = s.substring(13).toInt()
            if (count < n) horizontalFoldAt(x)
            count++
            //println("After horizontal fold: ${list.size}")
        } else if (s.isNotEmpty()) {
            val numbers = s.split(',')
            val x = numbers[0].toInt()
            val y = numbers[1].toInt()
            list.add(Coordinate(x, y))
        }
    }
}

fun printGrid() {
    val grid = Array(height()) { "".padEnd(width(), '.') }
    for (n in list) {
        // TIL: If you want to replace one char in a string,
        // use replaceRange(0, 1, "X")
        // NOT replaceRange(0, 0, "X")
        grid[n.y] = grid[n.y].replaceRange(n.x, n.x+1, "#")
    }
    for (s in grid) {
        println(s)
    }
}

private fun verticalFoldAt(y: Int) {
    val overlapped = mutableListOf<Coordinate>()
    for (n in list) {
        if (n.y > y) {
            val newY = y - (n.y - y)
            val overlap = find(n.x, newY)
            if (overlap != null) overlapped.add(overlap)
            n.y = newY
        }
    }
    remove(overlapped)
}

private fun horizontalFoldAt(x: Int) {
    val overlapped = mutableListOf<Coordinate>()
    for (n in list) {
        if (n.x > x) {
            val newX = x - (n.x - x)
            val overlap = find(newX, n.y)
            if (overlap != null) overlapped.add(overlap)
            n.x = newX
        }
    }
    remove(overlapped)
}

// Remove overlapping points so there's no duplicates in the list
private fun remove(removals: MutableList<Coordinate>) {
    for (n in removals) {
        list.remove(n)
    }
}

// Find a point by x/y coordinate in the list.
// Could speed up with a hashmap but no need for this puzzle.
private fun find(x: Int, y: Int): Coordinate? {
    for (n in list) {
        if (n.x == x && n.y == y) return n
    }
    return null
}

// Return maximum width in the list of points
private fun width(): Int {
    var max = 0
    for (n in list) {
        if (n.x > max) max = n.x
    }
    return max+1
}

// Return maximum height in the list of points
private fun height(): Int {
    var max = 0
    for (n in list) {
        if (n.y > max) max = n.y
    }
    return max+1
}

private data class Coordinate(var x: Int, var y: Int)
