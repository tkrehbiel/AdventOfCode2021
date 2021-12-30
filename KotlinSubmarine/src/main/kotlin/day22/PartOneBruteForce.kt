package day22

import java.io.File

object PartOneBruteForce {

    // This is a brute force implementation that only works on part one.

    fun run(fileName: String): Long {
        val grid = Grid()
        File(fileName).forEachLine {
            val fields = it.trim().split(" ")
            val cuboid = Cuboid(fields[1])
            if (fields[0] == "on")
                grid.set(cuboid)
            else if (fields[0] == "off")
                grid.clear(cuboid)
        }
        return grid.onCount().toLong()
    }

    data class Cube(var x: Int, var y: Int, var z: Int) {
        fun key(): String {
            return "($x,$y,$z)"
        }
    }

    data class Cuboid(var min: Cube, var max: Cube) {
        constructor(s: String): this(Cube(0,0,0), Cube(0,0,0)) {
            // Example: x=-22..26,y=-27..20,z=-29..19
            // Let's see how Kotlin does regular expressions
            val regex = Regex("""x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""")
            val match = regex.find(s) ?: throw Exception("Can't parse regular expression")
            min.x = match.groups[1]!!.value.toInt()
            max.x = match.groups[2]!!.value.toInt()
            min.y = match.groups[3]!!.value.toInt()
            max.y = match.groups[4]!!.value.toInt()
            min.z = match.groups[5]!!.value.toInt()
            max.z = match.groups[6]!!.value.toInt()
        }

        fun aMinX(): Int { return max(-51, min.x) }
        fun aMaxX(): Int { return min(51, max.x) }
        fun aMinY(): Int { return max(-51, min.y) }
        fun aMaxY(): Int { return min(51, max.y) }
        fun aMinZ(): Int { return max(-51, min.z) }
        fun aMaxZ(): Int { return min(51, max.z) }

        fun min(a: Int, b: Int): Int {
            if (a < b) return a
            return b
        }

        fun max(a: Int, b: Int): Int {
            if (a > b) return a
            return b
        }
    }

    class Grid {
        private val onMap = mutableSetOf<String>()

        fun onCount(): Int {
            return onMap.size
        }

        fun set(p: Cube) {
            if (p.x >= -50 &&
                p.x <= 50 &&
                p.y >= -50 &&
                p.y <= 50 &&
                p.z <= 50) {
                onMap.add(p.key())
            }
        }

        fun clear(p: Cube) {
            onMap.remove(p.key())
        }

        fun set(c: Cuboid) {
            for (z in c.aMinZ()..c.aMaxZ()) {
                for (y in c.aMinY()..c.aMaxY()) {
                    for (x in c.aMinX()..c.aMaxX()) {
                        this.set(Cube(x, y, z))
                    }
                }
            }
        }

        fun clear(c: Cuboid) {
            for (z in c.aMinZ()..c.aMaxZ()) {
                for (y in c.aMinY()..c.aMaxY()) {
                    for (x in c.aMinX()..c.aMaxX()) {
                        this.clear(Cube(x, y, z))
                    }
                }
            }
        }
    }
}
