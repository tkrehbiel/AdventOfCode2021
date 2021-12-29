package day20

// Image represents the infinitely-sized image
class Image(private val background: Boolean) {
    // The arbitrary boundaries of the image
    private val bounds = Bounds(Point(0,0), Point(0,0))
    // List of set pixels in the image
    private val pixels = mutableMapOf<String, Point>()

    private fun isSet(p: Point): Boolean {
        // This needs to account for the infinite size of the grid.
        // If the pixel falls outside the bounds of the image, we
        // need to return the infinite background pixel status.
        val key = p.toString()
        if (bounds.contains(p)) {
            return pixels.containsKey(key)
        }
        return background
    }

    fun set(p: Point) {
        bounds.expand(p)
        val key = p.toString()
        if (pixels.containsKey(key))
            return // already set
        pixels[key] = p
    }

    fun count(): Long {
        return pixels.size.toLong()
    }

    fun enhance(enhancements: BooleanArray): Image {
        // The "infinite background" pixel for the new image
        // will be based on the enhancement array.
        // If array[0] == 1 then the convolutions against 0 pixels
        // will make them infinitely 1.
        // But if the infinite background is already 1, then the
        // convolutions will make the infinite background array[511].
        var newBackground = enhancements[0]
        if (background) newBackground = enhancements[511]
        val newImage = Image(newBackground)
        for (y in bounds.min.y-1..bounds.max.y+1) {
            for (x in bounds.min.x-1..bounds.max.x+1) {
                val p = Point(x,y)
                val index = getEnhancementIndex(p)
                if (enhancements[index]) newImage.set(p)
            }
        }
        return newImage
    }

    fun getEnhancementIndex(p: Point): Int {
        var index = 0
        for (y in p.y-1..p.y+1) {
            for (x in p.x-1..p.x+1) {
                index = index shl 1
                if (isSet(Point(x, y))) {
                    index += 1
                }
            }
        }
        return index
    }

    /*
    fun println() {
        for (y in bounds.min.y..bounds.max.y) {
            var s = ""
            for (x in bounds.min.x..bounds.max.x) {
                if (isSet(Point(x, y)))
                    s += "#"
                else
                    s += "."
            }
            println(s)
        }
    }
    */
}