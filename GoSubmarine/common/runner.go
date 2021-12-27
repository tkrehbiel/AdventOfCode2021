package common

import (
	"fmt"
	"time"
)

// PuzzleRunner is the interface for a puzzle runner
type PuzzleRunner interface {
	Common(input string)
	Part1(input string) int
	Part2(input string) int
}

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

func parts(config PuzzleConfig, input string) {
	fmt.Printf("Using %s\n", input)

	start := time.Now()

	config.Puzzle.Common(addPath(input))

	part1(config, input)
	part2(config, input)

	elapsed := time.Now().Sub(start)
	fmt.Printf("Runtime: %v\n", elapsed)
}

func part1(config PuzzleConfig, input string) {
	start := time.Now()
	result1 := config.Puzzle.Part1(addPath(input))
	elapsed := time.Now().Sub(start)
	fmt.Printf("Part 1: %d (%v)\n", result1, elapsed)
}

func part2(config PuzzleConfig, input string) {
	start := time.Now()
	result2 := config.Puzzle.Part2(addPath(input))
	elapsed := time.Now().Sub(start)
	fmt.Printf("Part 2: %d (%v)\n", result2, elapsed)
}

func addPath(file string) string {
	return fmt.Sprintf("../../Inputs/%s", file)
}
