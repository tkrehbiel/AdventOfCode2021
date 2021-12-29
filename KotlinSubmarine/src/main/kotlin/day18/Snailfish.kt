package day18

import Puzzle
import java.io.File

fun main() {
    Snailfish().run()
}

// https://adventofcode.com/2021/day/18
/*
It was a lazy, rainy Saturday and I was interrupted quite a bit during this one,
so it took most of the day to complete it. Possibly the longest Kotlin code
I’ve written to date. I recognized it as a binary tree problem right away,
but I struggled to dredge up how they work out of my memory because
nobody in real life actually works with binary trees. Eventually I looked up
the “in-order binary tree traversal” algorithm I needed after going in circles
with it for a while. In the end, I built a Number class that did all the work,
and probably over-engineered it. Brute force for part 2.
*/

class Snailfish : Puzzle(18, "Snailfish") {
    override val puzzleInput = "day18_input.txt"
    override val testInput = arrayOf(
        "day18_test1.txt",
        "day18_test2.txt",
        "day18_test3.txt",
        "day18_test4.txt",
        "day18_test5.txt",
    )
    override val label1 = "magnitude"
    override val label2 = "largest magnitude of any two"

    private val allNumbers = mutableListOf<String>()

    override fun part1(input: String): Long {
        allNumbers.clear()
        var runningSum: Number? = null
        File(input).forEachLine {
            val current = Number(it)
            allNumbers.add(it)
            runningSum = if (runningSum != null) {
                runningSum!!.add(current)
            } else {
                current
            }
        }
        return runningSum!!.magnitude()
    }
    override fun part2(input: String): Long {
        // Yeah, just going through them all.
        var max = 0L
        for (i in 0 until allNumbers.size) {
            for (j in 0 until allNumbers.size) {
                // Comparing index numbers here because
                // I'm not sure about object equality in Kotlin.
                if (j != i) {
                    // As it turns out, I get the same answer
                    // considering just m1 or both m1 and m2.
                    val m1 = Number(allNumbers[i]).add(Number(allNumbers[j])).magnitude()
                    val m2 = Number(allNumbers[j]).add(Number(allNumbers[i])).magnitude()
                    if (m1 > max) max = m1
                    if (m2 > max) max = m2
                }
            }
        }
        return max
    }
}
