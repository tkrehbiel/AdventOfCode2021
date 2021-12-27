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

// Common reads the puzzle input into an array of ints
func (p *puzzle) Common(input string) {
	p.numbers = make([]int, 0)
	common.EnumerateLines(input, func(row int, s string) {
		i, err := strconv.Atoi(s)
		if err != nil {
			fmt.Printf("error converting string %s to int: %s\n", s, err)
			panic(err)
		}
		p.numbers = append(p.numbers, i)
	})
	fmt.Printf("read %d numbers\n", len(p.numbers))
}

// Part1 returns the number of increasing values
func (p *puzzle) Part1(input string) (result int) {
	var lastValue int
	for row, value := range p.numbers {
		if row > 0 && value > lastValue {
			result++
		}
		lastValue = value
	}
	return result
}

// Part2 returns the number of increasing values in a 3-row sliding window
func (p *puzzle) Part2(input string) (result int) {
	var lastValue int
	for row := 0; row < len(p.numbers)-2; row++ {
		value := p.numbers[row] + p.numbers[row+1] + p.numbers[row+2]
		if row > 0 && value > lastValue {
			result++
		}
		lastValue = value
	}
	return result
}
