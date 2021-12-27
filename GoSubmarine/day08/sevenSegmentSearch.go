package main

import (
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/8
// Seven Segment Search
/*
I just used the easy-to-find "1" and "4" patterns to deduce the ambiguous digits,
so I didn't really need most of the sample input on the left of the |.
This is a Golang translation of my Kotlin solution.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         8,
		Title:       "Seven Segment Search",
		PuzzleInput: "day08_input.txt",
		TestInput:   []string{"day08_test.txt"},
	})
}

type puzzle struct {
	unique int
	total  int
}

// Common computes both parts of the puzzle at the same time
func (p *puzzle) Common(input string) {
	p.unique = 0
	p.total = 0

	common.EnumerateLines(input, func(row int, s string) {
		halves := strings.Split(s, "|")
		samples := strings.Split(strings.TrimSpace(halves[0]), " ")
		digits := strings.Split(strings.TrimSpace(halves[1]), " ")

		// Find and remember the "1" and "4" patterns.
		onePattern, fourPattern := "", ""
		for _, sample := range samples {
			switch len(sample) {
			case 2:
				onePattern = sample
			case 4:
				fourPattern = sample
			}
		}

		value := 0
		for _, digit := range digits {
			n := 0
			switch len(digit) {
			case 2:
				n = 1
				p.unique++
			case 3:
				n = 7
				p.unique++
			case 4:
				n = 4
				p.unique++
			case 7:
				n = 8
				p.unique++
			case 5:
				// Could be "3" "5" or "2"
				if containsPattern(digit, onePattern, 2) {
					// If it contains the "1" pattern, it has to be a "3"
					n = 3
				} else if containsPattern(digit, fourPattern, 3) {
					// If it contains 3 of the values in the "4" pattern, it has to be a "5"
					n = 5
				} else {
					// Otherwise it has to be a "2"
					n = 2
				}
			case 6:
				// Could be "9" "0" or "6"
				if containsPattern(digit, fourPattern, 4) {
					// If it contains the "4" pattern, it has to be a "9"
					n = 9
				} else if containsPattern(digit, onePattern, 2) {
					// If it contains the "1" pattern, it has to be a "0"
					n = 0
				} else {
					// Otherwise it has to be a "6"
					n = 6
				}
			}
			value = value*10 + n
		}
		p.total += value
	})
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	return p.unique
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	return p.total
}

// containsPattern returns true if the string contains at least minChars of the segment pattern
func containsPattern(s string, pattern string, minChars int) bool {
	count := 0
	for i := 0; i < len(pattern); i++ {
		if strings.Contains(s, string(pattern[i])) {
			count++
		}
	}
	return count >= minChars
}
