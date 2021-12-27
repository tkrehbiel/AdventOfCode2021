package main

import (
	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         0,
		Title:       "Puzzle Template",
		PuzzleInput: "day01_input.txt",
		TestInput:   []string{"day01_test.txt"},
	})
}

type puzzle struct {
	lines int
}

// Common runs before Part1 and Part2, and can be used
// if the code for part 1 and 2 is the same.
// This should initialize state in the puzzle struct,
// as the same struct is used for both the test runs and the puzzle run.
// It doesn't *have* to read the puzzle input here, but it can.
func (p *puzzle) Common(input string) {
	p.lines = 0
	common.EnumerateLines(input, func(row int, s string) {
		p.lines++
	})
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	return p.lines
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	return p.lines * 2
}
