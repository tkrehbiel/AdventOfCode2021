package day18

// These "pointers" were intended to make it easy
// to find the pairs to the left and right, but I think
// it ended up adding more complexity than I intended.
class Pointer(private var index: Int,
              var number: Number,
              var pair: Pair,
              var side: Int) {
    fun left(): Pointer? {
        if (index > 0) {
            return number.pointers[index-1]
        }
        return null
    }

    fun right(): Pointer? {
        if (index < number.length-1) {
            return number.pointers[index+1]
        }
        return null
    }

    fun add(n: Int) {
        pair.value[side] += n
    }

    override fun toString(): String {
        return "${pair.value[side]}=${pair}:${side}"
    }
}
