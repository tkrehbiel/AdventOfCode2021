package main

import (
	"math"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/14
// Extended Polymerization
/*
This is a Golang translation of my original Kotlin solution.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         14,
		Title:       "Extended Polymerization",
		PuzzleInput: "day14_input.txt",
		TestInput:   []string{"day14_test.txt"},
	})
}

type puzzle struct {
	initial      string
	instructions map[string]rune
	pairs        map[string]int
	counts       map[rune]int
}

// Common reads the initial puzzle input.
func (p *puzzle) Common(input string) {
	p.instructions = make(map[string]rune)
	common.EnumerateLines(input, func(row int, s string) {
		if row == 0 {
			p.initial = s
		} else {
			if len(s) > 0 {
				parts := strings.Split(s, " -> ")
				p.instructions[parts[0]] = rune(parts[1][0])
			}
		}
	})
}

// Part1 runs the puzzle for 10 iterations
func (p *puzzle) Part1(input string) (result int) {
	p.initMaps()
	return p.run(10)
}

// Part2 runs the puzzle for 40 iterations
func (p *puzzle) Part2(input string) (result int) {
	p.initMaps()
	return p.run(40)
}

// initMaps prepares the letter count and pairs maps
func (p *puzzle) initMaps() {
	p.counts = make(map[rune]int)
	for _, c := range p.initial {
		p.counts[c]++
	}

	p.pairs = make(map[string]int)
	for i := 0; i < len(p.initial)-1; i++ {
		pair := string(p.initial[i]) + string(p.initial[i+1])
		p.pairs[pair]++
	}
}

// run iterates the puzzle a given number of times
func (p *puzzle) run(iterations int) int {
	for i := 0; i < iterations; i++ {
		p.iterate()
	}
	return max(p.counts) - min(p.counts)
}

// iterate runs one iteration of the puzzle
func (p *puzzle) iterate() {
	newPairs := make(map[string]int)
	for pair, count := range p.pairs {
		insert := p.instructions[pair]
		pair1 := string(pair[0]) + string(insert)
		pair2 := string(insert) + string(pair[1])
		p.counts[insert] += count
		newPairs[pair1] += count
		newPairs[pair2] += count
	}
	p.pairs = newPairs
}

// min gets the minimum count in the letter map
func min(counts map[rune]int) int {
	minValue := math.MaxInt64
	for _, v := range counts {
		if v < minValue {
			minValue = v
		}
	}
	return minValue
}

// max gets the maximum count in the letter map
func max(counts map[rune]int) int {
	maxValue := -1
	for _, v := range counts {
		if v > maxValue {
			maxValue = v
		}
	}
	return maxValue
}
