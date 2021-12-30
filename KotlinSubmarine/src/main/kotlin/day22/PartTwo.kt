package day22

import java.io.File

object PartTwo {

    // This is an optimized solution using subdivided cubes for part 2.
    // The hardest part by far was working out *how* to subdivide a cube.

    fun run(fileName: String): Long {
        val grid = Grid()
        File(fileName).forEachLine {
            val fields = it.trim().split(" ")
            val cube = Cube(fields[1])
            if (fields[0] == "on")
                grid.set(cube)
            else if (fields[0] == "off")
                grid.clear(cube)
        }
        return grid.onCount()
    }

    data class Segment(var min: Int, var max: Int, var intersection: Boolean)

    // Intersects one line segment with another, returning subdivided line segments.
    // Returns 0, 1, 2, or 3 segments.
    fun intersectAxis(minA: Int, maxA: Int, minB: Int, maxB: Int): List<Segment> {
        // I think these are all the possible intersections on one axis:
        // 0123456789
        // ...AA.BB.. // B does not intersect to the right of A
        // ...BB.AA.. // B does not intersect to the left of A
        // ...BxB.... // B encloses A (x)
        // ...AxA.... // B is within the bounds of A (x)
        // ...BxAA... // B intersects the left side of A (x)
        // ...xxA....
        // ..AAxB.... // B intersects the right side of A (x)
        // ...Axx....

        // Check B does not intersect A at all
        // ...AA.BB..
        // ...BB.AA..
        if (minB > maxA) return listOf()
        if (maxB < minA) return listOf()

        // Check if B intersects A exactly
        // ...xxx.....
        if (minB <= minA && maxB >= maxA) {
            return listOf(
                Segment(minA, maxA, true)
            )
        }

        // Check if B inside of A
        // ...AxA....
        if (minB > minA && maxB < maxA) {
            // Return three segments, outside-inside-outside
            return listOf(
                Segment(minA, minB-1, false),
                Segment(minB, maxB, true),
                Segment(maxB+1, maxA, false),
                )
        }

        // Check if B intersects on the left of A
        // ...BxAA...
        // ...xxA....
        // ...xA.....
        if (minB <= minA) {
            val list = mutableListOf<Segment>()
            val s1 = Segment(minB, maxB, true)
            if (s1.min < minA) s1.min = minA
            list.add(s1)
            val s2 = Segment(maxB+1, maxA, false)
            list.add(s2)
            return list
        }

        // Otherwise must be B intersects on the right of A
        // ..AAxB....
        // ...Axx....
        val list = mutableListOf<Segment>()
        val s1 = Segment(minA, minB-1, false)
        if (s1.max >= minA) list.add(s1)
        val s2 = Segment(minB, maxB, true)
        if (s2.max > maxA) s2.max = maxA
        list.add(s2)
        return list
    }

    // Grid maintains a list of cubes that are "on."
    class Grid {
        private val cuboidsOn = mutableListOf<Cube>()
        private val removals = mutableListOf<Cube>()
        private val additions = mutableListOf<Cube>()

        fun onCount(): Long {
            var count = 0L
            for (p in cuboidsOn) {
                count += p.count()
            }
            return count
        }

        // Update the lists after traversal
        private fun cleanup() {
            for (c in additions) {
                cuboidsOn.add(c)
            }
            for (c in removals) {
                cuboidsOn.remove(c)
            }
            removals.clear()
            additions.clear()
        }

        // Enable a cube area.
        // If the cube area intersects any previously-on cube,
        // the cubes are subdivided such that the remaining
        // cubes do not overlap.
        fun set(setCube: Cube) {
            for (existingCube in cuboidsOn) {
                if (setCube.overlaps(existingCube)) {
                    for (subCube in existingCube.intersect(setCube)) {
                        if (!subCube.intersection)
                            additions.add(subCube)
                    }
                    removals.add(existingCube)
                }
            }
            cuboidsOn.add(setCube)
            cleanup()
        }

        // Clear a cube area.
        // This is accomplished by checking to see if the cleared cube area
        // intersects any existing enabled cube areas. If so, the cubes are
        // subdivided and the overlapping "cleared" area is removed from the list.
        fun clear(clearCube: Cube) {
            for (existingCube in this.cuboidsOn) {
                if (clearCube.overlaps(existingCube)) {
                    if (!clearCube.contains(existingCube)) {
                        // Only sub-divide if the existing cube partially overlaps
                        for (subCube in existingCube.intersect(clearCube)) {
                            if (subCube.count() > 0 && !subCube.intersection)
                                additions.add(subCube)
                        }
                    }
                    removals.add(existingCube)
                }
            }
            cleanup()
        }
    }

    // Just a 3D point or pixel but they call it a "cube"
    data class Point(var x: Int, var y: Int, var z: Int) {
        override fun toString(): String {
            return "$x,$y,$z"
        }
    }

    // A Cuboid contains a cube of, ahem, cubes. Ie. it's a cube of pixels.
    class Cube() {
        private var min = Point(0,0,0)
        private var max = Point(0,0,0)

        var intersection: Boolean = false // hack to indicate which cuboid is intersecting

        constructor(a: Point, b: Point): this() {
            min = a
            max = b
        }

        constructor(s: String): this(Point(0,0,0), Point(0,0,0)) {
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

        fun overlaps(other: Cube): Boolean {
            if (other.max.x < min.x) return false
            if (other.min.x > max.x) return false
            if (other.max.y < min.y) return false
            if (other.min.y > max.y) return false
            if (other.max.z < min.z) return false
            if (other.min.z > max.z) return false
            return true
        }

        fun contains(other: Cube): Boolean {
            return other.min.x >= min.x &&
                    other.max.x <= max.x &&
                    other.min.y >= min.y &&
                    other.max.y <= max.y &&
                    other.min.z >= min.z &&
                    other.max.z <= max.z
        }

        private fun width(): Int {
            return this.max.x - this.min.x + 1
        }

        private fun height(): Int {
            return this.max.y - this.min.y + 1
        }

        private fun depth(): Int {
            return this.max.z - this.min.z + 1
        }

        // Returns number of cubes in the cuboid (ie. w*h*d)
        // Ie. the area of the cuboid.
        fun count(): Long {
            return width().toLong() * height().toLong() * depth().toLong()
        }

        // Intersect one cube with another.
        // This is 95% of the puzzle solution.
        fun intersect(other: Cube): List<Cube> {
            // Intersects the line segments along each axis, and combines them.
            val xSegments = intersectAxis(min.x, max.x, other.min.x, other.max.x)
            val ySegments = intersectAxis(min.y, max.y, other.min.y, other.max.y)
            val zSegments = intersectAxis(min.z, max.z, other.min.z, other.max.z)

            if (xSegments.isEmpty() ||
                ySegments.isEmpty() ||
                zSegments.isEmpty()) return listOf()

            // Now make cubes out of the 3 axes and return
            val list = mutableListOf<Cube>()
            for (z in zSegments) {
                for (y in ySegments) {
                    for (x in xSegments) {
                        val cube = Cube(
                            Point(x.min, y.min, z.min),
                            Point(x.max, y.max, z.max))
                        // Mark the cube that intersects
                        if (x.intersection &&
                            y.intersection &&
                            z.intersection) {
                            cube.intersection = true
                        }
                        list.add(cube)
                    }
                }
            }

            return list
        }

        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (other is Cube) {
                return (other.min.x == min.x) && (other.min.y == min.y) && (other.min.z == min.z) &&
                        (other.max.x == max.x) && (other.max.y == max.y) && (other.max.z == max.z)
            }
            return super.equals(other)
        }

        override fun toString(): String {
            return "[${min.x},${min.y},${min.z};${max.x},${max.y},${max.z}](${count()})"
        }

        override fun hashCode(): Int {
            // Automatically generated to avoid a warning
            var result = min.hashCode()
            result = 31 * result + max.hashCode()
            result = 31 * result + intersection.hashCode()
            return result
        }
    }
}
