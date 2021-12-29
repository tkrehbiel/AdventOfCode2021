package day20

// Bounds is just a rectangle
data class Bounds(var min: Point, var max: Point) {
    // Expand the bounds to contain the given point
    fun expand(point: Point) {
        if (point.x < min.x) min.x = point.x
        if (point.y < min.y) min.y = point.y
        if (point.x > max.x) max.x = point.x
        if (point.y > max.y) max.y = point.y
    }

    fun contains(point: Point): Boolean {
        return point.x >= min.x && point.y >= min.y
                && point.x <= max.x && point.y <= max.y
    }
}
