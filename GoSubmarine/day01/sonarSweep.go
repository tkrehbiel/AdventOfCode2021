package main

import (
	"fmt"
	"strconv"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         1,
		Title:       "Sonar Sweep",
		PuzzleInput: "day01_input.txt",
		TestInput:   []string{"day01_test.txt"},
	})
}

type puzzle struct {
	numbers []int // puzzle input
}

// Prepare reads the puzzle input into an array of ints
func (t *puzzle) Prepare(input string) {
	t.numbers = make([]int, 0)
	common.EnumerateLines(input, func(row int, s string) {
		i, err := strconv.Atoi(s)
		if err != nil {
			fmt.Printf("error converting string %s to int: %s\n", s, err)
			panic(err)
		}
		t.numbers = append(t.numbers, i)
	})
	fmt.Printf("read %d numbers\n", len(t.numbers))
}

// Part1 returns the number of increasing values
func (t *puzzle) Part1(input string) (result int) {
	var lastValue int
	for row, value := range t.numbers {
		if row > 0 && value > lastValue {
			result++
		}
		lastValue = value
	}
	return result
}

// Part2 returns the number of increasing values in a 3-row sliding window
func (t *puzzle) Part2(input string) (result int) {
	var lastValue int
	for row := 0; row < len(t.numbers)-2; row++ {
		value := t.numbers[row] + t.numbers[row+1] + t.numbers[row+2]
		if row > 0 && value > lastValue {
			result++
		}
		lastValue = value
	}
	return result
}
