package day15

import Puzzle
import java.io.File

fun main() {
    Template().run()
}

// https://adventofcode.com/2021/day/15
/*
I worked out a recursive path solution in Kotlin, but it was too slow.
I include it here only for posterity. It works on the test input, but
I don't know if it would ever get the right answer on the real input.
I re-wrote it from scratch in Golang using a Dijkstra shortest-path algorithm.
 */

class Template : Puzzle(15, "Chiton") {
    override val puzzleInput = "" //"day15_input.txt"
    override val testInput = arrayOf("day15_test1.txt", "day15_test2.txt")
    override val label1 = "lowest risk path"
    override val label2 = "lowest risk path (x5)"

    override fun part1(input: String): Long {
        val lines = File(input).readLines()

        height = lines.size
        width = lines[0].length
        grid = Array(height) { IntArray(width) }
        exclude = Array(height) { BooleanArray(width) }
        tested = 0
        tooLong = 0
        shortestSoFar = Long.MAX_VALUE

        for (j in lines.indices) {
            for (i in lines[j].indices) {
                grid[j][i] = lines[j][i].digitToInt()
            }
        }

        val path = ChitonPath(ChitonPoint(0, 0), null)
        path.createSmallestRoute(ChitonPoint(width-1, height-1))

        println("found: $path")
        println("total: ${path.getTotal()}")

        println("probed $tested paths")
        println("aborted $tooLong too long paths")

        return path.getTotal()
    }

    override fun part2(input: String): Long {
        TODO("part 2 not implemented")
    }
}

private var width: Int = 0
private var height: Int = 0
private var grid = Array(1) { IntArray(1) }
private var exclude = Array(1) { BooleanArray(1) }
private var tested = 0
private var tooLong = 0
private var shortestSoFar = Long.MAX_VALUE

private class ChitonPath(start: ChitonPoint, val parent: ChitonPath?) {
    var currentX: Int = 0
    var currentY: Int = 0
    val points = mutableListOf<ChitonPoint>()

    init {
        points.add(start)
        currentX = start.x
        currentY = start.y
    }

    fun createSmallestRoute(target: ChitonPoint): Boolean {
        while (this.currentX != target.x || this.currentY != target.y) {
            if (getTotal() > shortestSoFar) {
                // No need to continue this path if it's already too long.
                tooLong++
                return false
            }

            val list = neighbors()
            if (list.size == 0) return false // no path to destination

            val minNeighbor = minValue(list)
            if (minNeighbor == Int.MAX_VALUE) return false // no path

            // Assumes could be more than one possible direction
            var smallestTotal = Long.MAX_VALUE
            var smallestDirection: ChitonPoint? = null
            for (p in list) {
                val testPath = ChitonPath(ChitonPoint(p.x, p.y), this)
                //println("probing: $testPath")
                val success = testPath.createSmallestRoute(target)
                tested++
                if (success) {
                    //println("probed: $testPath")
                    val n = testPath.getTotal()
                    if (n < smallestTotal) {
                        smallestTotal = n
                        smallestDirection = p
                    }
                    if (n < shortestSoFar) {
                        shortestSoFar = n
                        println("Smallest so far $shortestSoFar")
                    }
                }
            }
            if (smallestDirection == null) {
                //println("no path from (${start.x},${start.y}) to (${target.x},${target.y})")
                return false
            }
            exclude[currentY][currentX] = true
            extend(smallestDirection)
        }
        return true
    }

    fun extend(p: ChitonPoint) {
        points.add(p)
        currentX = p.x
        currentY = p.y
    }

    fun neighbors(): MutableList<ChitonPoint> {
        val list = mutableListOf<ChitonPoint>()
        if (canGoDirection(currentX+1, currentY)) list.add(ChitonPoint(currentX+1, currentY))
        if (canGoDirection(currentX, currentY+1)) list.add(ChitonPoint(currentX, currentY+1))
        if (canGoDirection(currentX, currentY-1)) list.add(ChitonPoint(currentX, currentY-1))
        if (canGoDirection(currentX-1, currentY)) list.add(ChitonPoint(currentX-1, currentY))
        return list
    }

    fun canGoDirection(x: Int, y: Int): Boolean {
        // Can't go outside grid
        if (x < 0 || y < 0 || x >= width || y >= height) return false
        // Path can't overlap itself
        if (contains(ChitonPoint(x, y))) return false
        // Path can't extend into parent path(s) either
        var path = parent
        while (path != null) {
            if (path.contains(ChitonPoint(x, y))) return false
            path = path.parent
        }
        //if (exclude[y][x]) return false
        return true
    }

    fun getTotal(): Long {
        var total = 0L
        if (parent != null) total += parent.getTotal()
        for (i in 0 until points.size) {
            // Skip very first node because it doesn't count in the total
            if (parent == null && i == 0) continue
            total += gridValue(points[i])
        }
        return total
    }

    fun contains(p: ChitonPoint): Boolean {
        points.forEach {
            if (it == p) return true
        }
        return false
    }

    override fun toString(): String {
        var s = ""
        if (parent != null) {
            s += "[${parent}]"
        }
        for (n in points) {
            s += "${n}"
        }
        s += "=${getTotal()}"
        return s
    }
}

private class ChitonPoint(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is ChitonPoint) {
            return other.x == x && other.y == y
        }
        return false
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    override fun hashCode(): Int {
        return x * 1000 + y
    }
}

private fun gridValue(p: ChitonPoint): Int {
    return grid[p.y][p.x]
}

private fun minValue(list: MutableList<ChitonPoint>): Int {
    var min: Int = Int.MAX_VALUE
    for (p in list) {
        if (grid[p.y][p.x] < min) min = grid[p.y][p.x]
    }
    return min
}
