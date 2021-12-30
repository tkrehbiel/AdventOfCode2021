package day22

import Puzzle

fun main() {
    ReactorReboot().run()
}

// https://adventofcode.com/2021/day/22
/*
Part 2 is another 3D math puzzle that boils down to: How well do you know
the math of intersecting cubes? And/or do you know a library to do it for you?
I, it turns out, don’t know either, and initially couldn’t solve the problem.
I was 100% sure my algorithm would work at scale, I only needed a working
cube subdivision function, where the bulk of the work lies.
I finally got it to work when I decomposed the intersection into 1D line segments.
 */

class ReactorReboot : Puzzle(22, "Reactor Reboot") {
    override val puzzleInput = "day22_input.txt"
    override val testInput = arrayOf("day22_test1.txt", "day22_test2.txt", "day22_test3.txt")
    override val label1 = "all cubes within 50"
    override val label2 = "all cubes on"

    override fun part1(input: String): Long {
        return PartOneBruteForce.run(input)
    }
    override fun part2(input: String): Long {
        return PartTwo.run(input)
    }
}
