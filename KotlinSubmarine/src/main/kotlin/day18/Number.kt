package day18

import java.io.StringReader

// Number is a model for the entire number structure.
// Internally maintains a binary tree and a list of digits.
// Probably over-engineered this.
// Number and Pair are somewhat interdependent.
class Number constructor() {
    private var root: Pair = Pair(null)
    var pointers = mutableListOf<Pointer>()
    var length = 0

    // Construct from a puzzle input string
    constructor (input: String) : this() {
        if (input != "") parse(input)
    }

    // Construct from the root of a tree
    constructor (pair: Pair) : this() {
        root = pair
    }

    // Parse a string into the internal tree structure
    private fun parse(input: String) {
        val reader = StringReader(input)
        reader.read() // Note: assumes first char is always [
        root = parsePair(null, reader)
        refresh()
    }

    // Parse one pair from the current position of the string reader
    private fun parsePair(parent: Pair?, reader: StringReader): Pair {
        // Note: assumes all input fields are exactly one character long
        val current = makePair(parent, 0, 0)
        var side = 0
        while (reader.ready()) {
            when (val c = reader.read().toChar()) {
                '[' -> {
                    current.child[side] = parsePair(current, reader)
                }
                ']' -> {
                    return current
                }
                ',' -> {
                    side++
                }
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    current.value[side] = c.digitToInt()
                }
            }
        }
        return current
    }

    // "Add" a second number and return the reduced sum
    fun add(n: Number): Number {
        val newParent = sumPairs(this.root, n.root)
        val result = Number(newParent)
        result.refresh()
        result.reduce()
        return result
    }

    // "Refreshes" the internal state of the pointer list.
    // Should be called after any changes in the tree.
    // The pointer list is a list of "pointers" to each digit
    // in the Number, from left to right, as would be found
    // in an "in-order binary tree traversal."
    private fun refresh() {
        length = 0
        pointers = mutableListOf()
        // Build the list of pointers with inorder traversal
        inorder(root, ::build)
    }

    // Your basic recursive inorder traversal.
    // We do this traversal in multiple contexts.
    // So, passes a function to do work on each visited node.
    // For this problem it probably would have been better
    // to do an iterative traversal instead of recursive.
    private fun inorder(p: Pair?,
                        visit: (p: Pair) -> Boolean): Boolean {
        if (p != null) {
            // Recurse down the left path
            if (!inorder(p.child[0], visit)) return false
            // Visit the current node
            if (!visit(p)) return false
            // Recurse down the right path
            if (!inorder(p.child[1], visit)) return false
        }
        return true
    }

    // Traversal function to populate the pointer array
    private fun build(p: Pair): Boolean {
        // Note: Seems excessively complex
        var left: Pointer? = null
        if (p.child[0] == null) {
            left = Pointer(length, this, p, 0)
            pointers.add(left)
            length++
        }
        p.pointer[0] = left

        var right: Pointer? = null
        if (p.child[1] == null) {
            right = Pointer(length, this, p, 1)
            pointers.add(right)
            length++
        }
        p.pointer[1] = right

        return true
    }

    private var wasSplit: Boolean = false
    private var wasExploded: Boolean = false

    // "Reduce" the number, as per the puzzle rules.
    private fun reduce(): Number {
        while (true) {
            wasSplit = false
            wasExploded = false
            inorder(root, ::explode)
            if (!wasExploded) {
                // Rules say only do the "first" that applies
                // So if an explosion happened, don't do a split
                // until there are no more explosions.
                inorder(root, ::split)
            }
            refresh()
            if (!wasSplit && !wasExploded) break
        }
        return this
    }

    // Traversal function to "explode" a pair if it meets the criteria.
    // Operates on pairs from left to right.
    private fun explode(p: Pair): Boolean {
        if (p.depth >= 4) {
            // This has been the hardest part for me:
            // Locating the value to the left/right of here.
            // It's why I built the "pointer array."

            // (Lots of Kotlin semantics to handle nulls.)
            // Lookup the value to the left of this pair.
            p.pointer[0]?.left()?.add(p.value[0])
            // Lookup the value to the right of this pair.
            p.pointer[1]?.right()?.add(p.value[1])

            // Remove this pair.
            // We can safely do this in the middle of traversal
            // because we will be aborting after the first action anyway.
            remove(p)

            wasExploded = true
            // Return false to end the traversal:
            return false
        }
        return true
    }

    // Remove a pair and replace it with a 0 in the parent node
    private fun remove(p: Pair) {
        val parent = p.parent ?: return
        if (p == parent.child[0]) {
            parent.child[0] = null
            parent.value[0] = 0
        }
        if (p == parent.child[1]) {
            parent.child[1] = null
            parent.value[1] = 0
        }
    }

    // Traversal function to "split" numbers in a pair.
    // Operates on pairs from left to right.
    private fun split(p: Pair): Boolean {
        if (splitOne(p, 0)) return false
        if (splitOne(p, 1)) return false
        return true
    }

    // Split one of the numbers in a pair if necessary.
    // Returns true if the number was split.
    private fun splitOne(p: Pair, side: Int): Boolean {
        if (p.value[side] >= 10) {
            // Splitting replaces a single value with a child pair
            val child = makePair(p,
                splitLeft(p.value[side]), splitRight(p.value[side]))
            p.child[side] = child
            p.value[side] = -1
            wasSplit = true
            return true
        }
        return false
    }

    // Returns the puzzle "magnitude" of this Number
    fun magnitude(): Long {
        return root.magnitude()
    }

    override fun toString(): String {
        return root.toString()
    }
}

private fun splitLeft(i: Int): Int {
    return i / 2 // round down
}

private fun splitRight(i: Int): Int {
    return (i+1) / 2 // round up
}
