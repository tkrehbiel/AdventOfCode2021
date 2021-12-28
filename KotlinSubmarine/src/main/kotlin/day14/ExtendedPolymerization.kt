package day14

import Puzzle
import java.io.File

fun main() {
    ExtendedPolymerization().run()
}

// https://adventofcode.com/2021/day/14
/*
Another annoying one where brute force on part 1 runs out of memory on part 2.
The clue is in the offhand “polymer grows quickly” remark. I knew my first
solution wasn't going to work for part 2 but you’re scored on how fast you get
an answer, not how elegant or efficient the code is. So I got the star for part 1
fairly quickly, then I had to rewrite it from scratch for part 2.

I didn't think a map of letter pairs would work so I went off on several false starts,
but it turned out it worked fine in the end.

Just for fun I re-wrote the solution in Golang, and it looked cleaner than Kotlin.
*/

class ExtendedPolymerization : Puzzle(14, "Extended Polymerization") {
    override val puzzleInput = "day14_input.txt"
    override val testInput = arrayOf("day14_test.txt")
    override val label1 = "after 10 steps"
    override val label2 = "after 40 steps"

    private var initial: String = ""
    private val instructions = mutableMapOf<String, Char>()

    override fun common(input: String) {
        super.common(input)

        val lines = File(input).readLines()
        initial = lines[0]

        // Assumes instructions begin on the third line
        instructions.clear()
        for (i in 2 until lines.size) {
            val parts = lines[i].split("->")
            val pair = parts[0].trim()
            val insertion = parts[1].trim()[0]
            instructions[pair] = insertion
        }
    }

    override fun part1(input: String): Long {
        return polymerization(initial, instructions, 10)
    }

    override fun part2(input: String): Long {
        // Technically could let part 1 run for 30 more iterations, but we start over.
        return polymerization(initial, instructions, 40)
    }
}

private fun polymerization(initial: String,
                           instructions: MutableMap<String, Char>,
                           iterations: Int): Long {
    val counts = initCounts(initial)
    var pairs = initPairs(initial)
    for (i in 0 until iterations) {
        pairs = process(pairs, counts, instructions)
    }
    return max(counts) - min(counts)
}

private fun process(map: MutableMap<String, Long>,
                    counts: MutableMap<Char, Long>,
                    instructions: MutableMap<String, Char>): MutableMap<String, Long> {
    val newMap = mutableMapOf<String, Long>()
    for (pair in map.keys) {
        val insert = instructions[pair]!!
        val pair1: String = pair[0].toString() + insert
        val pair2: String = insert.toString() + pair[1]
        val count = map[pair]!!
        add(counts, insert, count)
        add(newMap, pair1, count)
        add(newMap, pair2, count)
    }
    return newMap
}

private fun initCounts(initial: String): MutableMap<Char, Long> {
    val counts = mutableMapOf<Char, Long>()
    counts.clear()
    for (c in initial) increment(counts, c)
    return counts
}

private fun initPairs(initial: String): MutableMap<String, Long> {
    val pairs = mutableMapOf<String, Long>()
    for (i in 0 until initial.length - 1) {
        val pair = initial.substring(i, i + 2)
        add(pairs, pair, 1)
    }
    return pairs
}

// I can't believe I have to write a function for this
// Kotlin is neat but stuff like this seems ridiculous
// Based on https://www.techiedelight.com/increment-value-map-kotlin/
private fun <K> add(map: MutableMap<K, Long>, key: K, n: Long) {
    val value = if (map.containsKey(key)) map[key] else 0
    map[key] = value!! + n
}

private fun <K> increment(map: MutableMap<K, Long>, key: K) {
    add(map, key, 1)
}

private fun min(map: MutableMap<Char, Long>): Long {
    var min = Long.MAX_VALUE
    for (c in map.keys) {
        if (map[c]!! < min) min = map[c]!!
    }
    return min
}

private fun max(map: MutableMap<Char, Long>): Long {
    var max = Long.MIN_VALUE
    for (c in map.keys) {
        if (map[c]!! > max) max = map[c]!!
    }
    return max
}

/*
Old brute force code for posterity:

private fun bruteForce(initial: String, instructions: MutableMap<String, Char>, iterations: Long) {
    // I have a bad feeling they're going to set the iterations
    // to a value that's too high to process like this in part 2
    // Yeah.. in part two it goes from 10 to 40 iterations.
    // And yeah, it becomes impossible to brute force it.
    var s = initial
    for (i in 0 until iterations) {
        s = bruteProcess(s, instructions)
        //println("length after ${i+1}: ${s.length}")
        //println(s)
    }
    //for (c in counts.keys) {
    //    println("number of $c: ${counts[c]}")
    //}
    var result = max(counts) - min(counts)
    println("first attempt result: $result")
}

private fun bruteProcess(s: String, instructions: MutableMap<String, Char>): String {
    var builder = StringBuilder(s.length * 2)
    var final = '0'
    for (i in 0 until s.length - 1) {
        val pair = s.substring(i, i + 2)
        val insert: Char = instructions[pair]!!
        //println("Insert $insert between $pair")
        builder.append(pair[0])
        builder.append(insert)
        increment(counts, insert)
        final = pair[1]
    }
    builder.append(final)
    return builder.toString()
}
*/
