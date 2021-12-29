package day18

// The Pair class holds the fundamental data for a binary tree.
// (Could also be thought of as a Node.)
class Pair(var parent: Pair?) {
    // Depth of this node from the root.
    // This is only for determining whether to "explode" a pair.
    var depth = 0

    // Value of left and right numbers in the pair.
    // Only used if there are no children in that direction.
    val value = IntArray(2)

    // Pointers for the left and right sides of the pair.
    // Pointers are used to move left and right across node boundaries.
    val pointer = arrayOfNulls<Pointer>(2)

    // Left and right children of this node.
    val child = arrayOfNulls<Pair>(2)

    // Magnitude computes the puzzle magnitude for a node and children
    fun magnitude(): Long {
        var left = 0L
        var right = 0L

        left += if (child[0] != null)
            child[0]!!.magnitude()
        else
            value[0].toLong()

        right += if (child[1] != null)
            child[1]!!.magnitude()
        else
            value[1].toLong()

        return (3 * left) + (2 * right)
    }

    override fun toString(): String {
        var left = value[0].toString()
        if (child[0] != null)
            left = child[0].toString()
        var right = value[1].toString()
        if (child[1] != null)
            right = child[1].toString()
        return "[${left},${right}]"
    }
}

// Create a Pair and initialize
fun makePair(parent: Pair?, left: Int, right: Int): Pair {
    val node = Pair(parent)
    if (parent != null) {
        node.depth = parent.depth + 1
    }
    node.value[0] = left
    node.value[1] = right
    return node
}

// Sum two pairs, which in practice just means
// making a new parent pair containing the two pairs.
fun sumPairs(pair1: Pair, pair2: Pair): Pair {
    val parent = Pair(null)
    pair1.parent = parent
    pair2.parent = parent
    parent.child[0] = pair1
    parent.value[0] = -1
    parent.child[1] = pair2
    parent.value[1] = -1
    increaseDepth(pair1)
    increaseDepth(pair2)
    return parent
}

// Increase the depth property of all children in the tree
private fun increaseDepth(pair: Pair) {
    pair.depth++
    if (pair.child[0] != null)
        increaseDepth(pair.child[0]!!)
    if (pair.child[1] != null)
        increaseDepth(pair.child[1]!!)
}
