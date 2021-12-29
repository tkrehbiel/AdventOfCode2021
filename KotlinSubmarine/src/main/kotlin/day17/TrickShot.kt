package day17

import Puzzle
import java.io.File

fun main() {
    TrickShot().run()
}

// https://adventofcode.com/2021/day/17
class TrickShot : Puzzle(17, "Trick Shot") {
    override val puzzleInput = "day17_input.txt"
    override val testInput = arrayOf("day17_test.txt")
    override val label1 = "maximum height"
    override val label2 = "number of shots"

    override fun part1(input: String): Long {
        var height = 0
        val target = Target(File(input).readText())
        for (x in horizontalCandidates(target.minX, target.maxX)) {
            // I just arbitrarily picked a vertical test velocity of 1 to 1000.
            // (I unscientifically kept increasing it until it
            // stopped getting new results.)
            for (y in 1..1000) {
                val t = Trajectory(Velocity(x, y))
                if (t.plot(target)) {
                    if (t.maxHeight > height) height = t.maxHeight
                }
            }
        }
        return height.toLong()
    }

    override fun part2(input: String): Long {
        var shots = 0
        val target = Target(File(input).readText())
        for (x in horizontalCandidates(target.minX, target.maxX)) {
            // As above I arbitrarily picked a vertical test velocity of -1000 to 1000.
            // Negative tests will never achieve height but they might
            // still end up in the target zone.
            for (y in -1000..1000) {
                val t = Trajectory(Velocity(x, y))
                if (t.plot(target)) {
                    shots++
                }
            }
        }
        return shots.toLong()
    }
}

// Make a list of all possible horizontal velocities that reach target.
private fun horizontalCandidates(minX: Int, maxX: Int): List<Int> {
    // Note: assumes maxX > minX > 0
    // Pain in the butt to consider positive and negative velocities.
    // So I won't.
    val list = mutableListOf<Int>()
    // We consider 0 just in case the target zone includes 0,0
    for (velocity in 0..maxX) {
        val reach = maxHorizontalReach(velocity)
        if (reach >= minX) list.add(velocity)
    }
    return list
}

// Return the maximum distance that can be reached with drag.
// This allows us to limit the range of horizontal values to test.
private fun maxHorizontalReach(velocity: Int): Int {
    val v = Velocity(velocity,0)
    var x = 0
    while (v.x != 0) {
        x += v.x
        v.drag()
    }
    return x
}

private class Trajectory(launch: Velocity) {
    // note: y coordinates increase going up, not down

    private var current = Point(0,0)
    private var velocity = Velocity(0,0)
    var maxHeight = 0

    init {
        velocity.x = launch.x
        velocity.y = launch.y
    }

    // Plots a trajectory and returns true if it lands in the target zone
    fun plot(t: Target): Boolean {
        // Should never happen since we've already tested horizontal:
        // if (maxHorizontalReach(current.x, velocity.x) < minX) return false
        while (true) {
            // Note: Assumes starting location is never inside the target
            step()
            if (t.contains(current)) return true
            // We can stop if the current vertical passes
            // below the target zone.
            if (current.y + velocity.y < t.minY) break
        }
        return false
    }

    // Move one step
    private fun step() {
        current.move(velocity)
        velocity.drag().gravity()
        if (current.y > maxHeight) {
            maxHeight = current.y
        }
    }
}

// These classes mainly just hold multiple values
// to make them easier to pass around.
// And implement toString() to make debugging easier.

private class Point(var x: Int, var y: Int) {
    fun move(velocity: Velocity) {
        x += velocity.x
        y += velocity.y
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

private class Velocity(var x: Int, var y: Int) {
    fun drag(): Velocity {
        if (x > 0)
            x--
        else if (x < 0)
            x++
        return this
    }

    fun gravity(): Velocity {
        y--
        return this
    }

    override fun toString(): String {
        return "delta $x,$y"
    }
}

private class Target(s: String) {
    var minX: Int = 0
    var maxX: Int = 0
    var minY: Int = 0
    var maxY: Int = 0

    init {
        // example:
        // target area: x=20..30, y=-10..-5
        val regex = Regex("""target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""")
        val match = regex.find(s) ?: throw Exception("Can't parse regular expression")
        minX = match.groups[1]!!.value.toInt()
        maxX = match.groups[2]!!.value.toInt()
        minY = match.groups[3]!!.value.toInt()
        maxY = match.groups[4]!!.value.toInt()
    }

    fun contains(p: Point): Boolean {
        if (p.x < minX) return false
        if (p.x > maxX) return false
        if (p.y < minY) return false
        if (p.y > maxY) return false
        return true
    }

    override fun toString(): String {
        return "$minX..$maxX,$minY..$maxY"
    }
}
