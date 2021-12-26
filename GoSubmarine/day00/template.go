package main

import (
	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         1,
		Title:       "Cool Name",
		PuzzleInput: "day01_input.txt",
		TestInput:   []string{"day01_test.txt"},
	})
}

type puzzle struct {
	data int
}

// Prepare reads the puzzle input
func (t *puzzle) Prepare(input string) {
	common.EnumerateLines(input, func(row int, s string) {
	})
}

// Part1 returns the first puzzle result
func (t *puzzle) Part1(input string) (result int) {
	return result
}

// Part2 returns the second puzzle result
func (t *puzzle) Part2(input string) (result int) {
	return result
}
