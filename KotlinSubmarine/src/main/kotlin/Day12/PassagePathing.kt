package Day12

import Puzzle
import java.io.File

fun main() {
    PassagePathing().run()
}

// https://adventofcode.com/2021/day/12
/*
I hated this one with a burning passion. I don’t like graphs or pathing problems,
and it took me a long time to work out the recursion. I initially went down
the wrong road and tried to do it without maintaining a list of paths,
which, well, didn’t work.
There are undoubtedly cleaner solutions than this.
But: I learned how to pass functions as parameters in Kotlin, so yay.
*/

class PassagePathing : Puzzle(12, "Passage Pathing") {
    override val puzzleInput = "day12_input.txt"
    override val testInput = arrayOf("day12_test1.txt","day12_test2.txt","day12_test3.txt")
    override val label1 = "paths with one small cave"
    override val label2 = "paths with two small caves"

    private lateinit var firstPath: Path

    override fun common(input: String) {
        super.common(input)
        nodes.clear()
        // Read input file
        File(input).forEachLine {
            val sides = it.trim().split('-')
            val n1 = add(sides[0])
            val n2 = add(sides[1])
            n1.connected.add(n2)
            n2.connected.add(n1)
        }
        firstPath = Path()
        firstPath.nodes.add(start!!)
    }

    override fun part1(input: String): Long {
        foundPaths.clear()
        search(start!!, firstPath, 0, ::noneAllowed)
        return foundPaths.size.toLong()
    }
    override fun part2(input: String): Long {
        foundPaths.clear()
        search(start!!, firstPath, 0, ::oneAllowed)
        return foundPaths.size.toLong()
    }
}

private var start: Node? = null
private val nodes = hashMapOf<String, Node>() // probably didn't need to be a hashmap
private val foundPaths = mutableListOf<Path>()

private fun add(name: String): Node {
    if (nodes.contains(name))
        return nodes[name]!!
    val n = Node(name)
    nodes[name] = n
    if (name == "start") {
        start = n
    }
    return n
}

// Passes in a function to see if a new node is allowed in the path.
// "allowed" is a function which determines whether a small cave is allowed to be used.
private fun search(current: Node, path: Path, depth: Int,
           allowed: (path: Path, find: Node) -> Boolean) {

    //The "depth" param was only used to indent for printing debugging info.
    //var indent = ""
    //for (i in 0..depth) indent += " "
    //println("${indent}at: $path")

    if (current.name == "end") {
        foundPaths.add(path)
        return
    }

    //println("${indent}testing $list")
    for (n in current.connected) {
        if (n.name == "start") continue
        if (allowed(path, n)) {
            val np = newPath(path, n)
            search(n, np, depth + 1, allowed)
        }
    }

    //println("${indent}finished")
}

// Function passed into search() to allow no duplicate small caves
private fun noneAllowed(path: Path, newNode: Node): Boolean {
    if (newNode.big) return true
    if (newNode.name == "end") return true
    if (path.duplicates(newNode) > 0) return false
    return true
}

// Function passed into search() to allow one duplicate small cave
private fun oneAllowed(path: Path, newNode: Node): Boolean {
    if (newNode.big) return true
    if (newNode.name == "end") return true
    if (path.duplicates(newNode) > 0 && path.usedSmallCave()) return false
    return true
}

private fun newPath(old: Path, node: Node): Path {
    val path = Path()
    for (n in old.nodes) {
        path.nodes.add(n)
    }
    path.nodes.add(node)
    path.usedDuplicate = old.usedDuplicate
    return path
}

// Node is a single cave or path node
private class Node(var name: String): Comparable<Node> {
    // connected caves
    val connected = mutableListOf<Node>()
    // big returns true if the cave/node is "big" and can be used more than once
    val big: Boolean = (name.uppercase() == name)

    override fun toString(): String {
        return name
    }

    override fun compareTo(other: Node): Int {
        if (name == "start") return -1
        if (name == "end") return 1
        return name.compareTo(other.name)
    }
}

// Path is used to maintain the state of one potential path
private class Path {
    var nodes = mutableListOf<Node>()
    var usedDuplicate = false

    override fun toString(): String {
        var s = "-"
        for (n in nodes) s += n.name + "-"
        return s
    }

    fun usedSmallCave(): Boolean {
        // Hashmaps in Kotlin are so annoying
        val map: MutableMap<String, Int> = HashMap()
        for (n in nodes) {
            if (n.big) continue
            if (n.name == "start" || n.name == "end") continue
            if (map.contains(n.name)) {
                // I can't believe I have to do all this crap just to add 1
                val i: Int? = map[n.name]
                if (i != null) map[n.name] = i + 1
            }
            else {
                map[n.name] = 1
            }
        }
        for (i in map.values) {
            if (i > 1) return true
        }
        return false
    }

    fun duplicates(node: Node): Int {
        var count = 0
        for (n in nodes) {
            if (n.name == "start" || n.name == "end") continue
            if (n.name == node.name) count++
        }
        return count
    }
}
