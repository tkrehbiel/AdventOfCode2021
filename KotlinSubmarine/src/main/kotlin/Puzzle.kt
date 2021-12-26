// An abstract framework for running puzzles
abstract class Puzzle(private val day: Int,
                      private val title: String) {

    abstract val label1: String // Label for puzzle output of part 1
    abstract val label2: String // Label identifying the puzzle output of part 2

    // Main puzzle input file eg. dayXX_input.txt
    // Set to "" to skip puzzle input
    abstract val puzzleInput: String
    // One or more puzzle test files
    abstract val testInput: Array<String>

    // Run puzzles with test input, then real input.
    fun run() {
        println("Advent of Code 2021, Day ${this.day} - ${this.title}")
        println()
        for (s in testInput) {
            reset()
            parts("Test Input ($s)", s)
        }
        if (!puzzleInput.isEmpty()) {
            reset()
            parts("Puzzle Input ($puzzleInput)", puzzleInput)
        }
    }

    // Optional code to reset state between puzzles
    internal open fun reset() {}

    // Optional code to run before part1() and part2()
    // Can be used if a puzzle runs the same code to get both answers.
    internal open fun common(input: String) {}

    // Run part 1 and part 2 with a given input file
    private fun parts(heading: String, input: String) {
        println(heading)
        common(getPath(input))
        try {
            println("Part 1: ${part1(getPath(input))} ($label1)")
        }
        catch (e: NotImplementedError) {
            println("Part 1 not implemented")
        }
        try {
            println("Part 2: ${part2(getPath(input))} ($label2)")
        }
        catch (e: NotImplementedError) {
            println("Part 2 not implemented")
        }
        println()
    }

    // Add the right path to an input filename
    private fun getPath(input: String): String {
        return "inputs/$input"
    }

    // TODO: Snapshot visual state in order to create an animated visualization
    internal fun snapshot(display: List<String>) {
        if (display.isEmpty()) return
        // for (s in display) println(s)
    }

    // A stub for translating internal state into rows of text for visualizations
    internal open fun visualize() {
        snapshot(listOf())
    }

    // Run part 1 and return an answer
    internal abstract fun part1(input: String): Long

    // Run part 2 and return an answer
    internal abstract fun part2(input: String): Long
}
