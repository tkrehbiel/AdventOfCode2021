package common

import (
	"fmt"
	"time"
)

// PuzzleRunner is the required interface to use the puzzle framework
type PuzzleRunner interface {
	Common(input string)
	Part1(input string) int
	Part2(input string) int
}

// PuzzleConfig contains key information about a puzzle and how to run it
type PuzzleConfig struct {
	Puzzle      PuzzleRunner
	Day         int
	Title       string
	PuzzleInput string
	TestInput   []string
}

// Run executes a given puzzle
func Run(config PuzzleConfig) {
	fmt.Printf("Advent of Code 2021, Day %d - %s\n", config.Day, config.Title)
	for _, input := range config.TestInput {
		parts(config, input)
	}
	parts(config, config.PuzzleInput)
}

// parts runs both part 1 and part 2 of a puzzle in sequence with a given input file
func parts(config PuzzleConfig, input string) {
	fmt.Printf("Using %s\n", input)

	start := time.Now()

	// Run the common code first
	config.Puzzle.Common(addPath(input))

	// Then run part1 and part2 in sequence
	part1(config.Puzzle, input)
	part2(config.Puzzle, input)

	elapsed := time.Now().Sub(start)
	fmt.Printf("Runtime: %v\n", elapsed)
}

func part1(runner PuzzleRunner, input string) {
	start := time.Now()
	result1 := runner.Part1(addPath(input))
	elapsed := time.Now().Sub(start)
	fmt.Printf("Part 1: %d (%v)\n", result1, elapsed)
}

func part2(runner PuzzleRunner, input string) {
	start := time.Now()
	result2 := runner.Part2(addPath(input))
	elapsed := time.Now().Sub(start)
	fmt.Printf("Part 2: %d (%v)\n", result2, elapsed)
}

func addPath(file string) string {
	return fmt.Sprintf("../../Inputs/%s", file)
}
