package main

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

func main() {
	// https://adventofcode.com/2021/day/2
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         2,
		Title:       "Dive!",
		PuzzleInput: "day02_input.txt",
		TestInput:   []string{"day02_test.txt"},
	})
}

type puzzle struct {
	position       int
	depth1, depth2 int
	aim            int
}

// Prepare reads the puzzle input
func (t *puzzle) Prepare(input string) {
	t.position = 0
	t.depth1 = 0
	t.depth2 = 0
	t.aim = 0
	common.EnumerateLines(input, func(row int, s string) {
		// format errors are logged and the line skipped
		if len(s) == 0 {
			fmt.Printf("row %d: ignoring empty line\n", row+1)
			return
		}
		fields := strings.Split(s, " ")
		if len(fields) != 2 {
			fmt.Printf("row %d: error splitting '%s' into two fields\n",
				row+1, s)
			return
		}
		value, err := strconv.Atoi(fields[1])
		if err != nil {
			fmt.Printf("row %d: error converting '%s' to int: %s\n",
				row+1, fields[1], err)
			return
		}
		switch strings.ToLower(fields[0]) {
		case "forward":
			t.position += value
			t.depth2 += (t.aim * value)
		case "down":
			t.depth1 += value
			t.aim += value
		case "up":
			t.depth1 -= value
			t.aim -= value
		default:
			fmt.Printf("row %d: unrecognized command '%s'\n",
				row+1, fields[0])
		}
	})
}

// Part1 returns the first puzzle result
func (t *puzzle) Part1(input string) (result int) {
	return t.position * t.depth1
}

// Part2 returns the second puzzle result
func (t *puzzle) Part2(input string) (result int) {
	return t.position * t.depth2
}
