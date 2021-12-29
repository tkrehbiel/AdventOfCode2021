package day20

data class Point(var x: Int, var y: Int) {
    override fun toString(): String {
        // This is used as the key into the pixel map
        return "$x,$y"
    }
}
