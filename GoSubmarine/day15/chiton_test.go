package main

import (
	"testing"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// TestProfiling is to run the go profiler to find slow parts.
// (The queue implementation was the slow part.)
func TestProfiling(t *testing.T) {
	config := common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         15,
		Title:       "Chiton",
		PuzzleInput: "day15_input.txt",
		TestInput:   []string{"day15_test1.txt", "day15_test2.txt"},
	}

	config.Puzzle.Common("../../Inputs/day15_input.txt")
	config.Puzzle.Part2("../../Inputs/day15_input.txt")
}
