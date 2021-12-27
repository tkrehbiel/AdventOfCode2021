package main

import (
	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/3
// I accidentally deleted this one so I had to do it twice.

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         3,
		Title:       "Binary Diagnostic",
		PuzzleInput: "day03_input.txt",
		TestInput:   []string{"day03_test.txt"},
	})
}

type puzzle struct {
	width   int
	numbers []string
}

// Prepare reads the puzzle input
func (p *puzzle) Prepare(input string) {
	p.numbers = make([]string, 0)
	common.EnumerateLines(input, func(row int, s string) {
		if row == 0 {
			p.width = len(s)
		}
		p.numbers = append(p.numbers, s)
	})
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	var gamma, epsilon int
	for i := 0; i < p.width; i++ {
		sums := countBitsAt(p.numbers, i)
		bit := 0
		if sums[1] > sums[0] {
			bit = 1
		}
		gamma = gamma<<1 | bit
		epsilon = epsilon<<1 | (1 - bit)
	}
	return gamma * epsilon
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	oxygens := make([]string, 0)
	oxygens = append(oxygens, p.numbers...)

	count := len(oxygens)
	for i := 0; i < p.width && count > 1; i++ {
		digit := mostCommonBit(countBitsAt(oxygens, i))
		count = filter(oxygens, count, i, digit)
	}
	oxygen := decimal(remaining(oxygens))

	co2s := make([]string, 0)
	co2s = append(co2s, p.numbers...)

	count = len(co2s)
	for i := 0; i < p.width && count > 1; i++ {
		digit := leastCommonBit(countBitsAt(co2s, i))
		count = filter(co2s, count, i, digit)
	}
	co2 := decimal(remaining(co2s))

	return oxygen * co2
}

// countBitsAt counts the number of 0 and 1 bits at the given index
func countBitsAt(numbers []string, index int) (sums [2]int) {
	for row := 0; row < len(numbers); row++ {
		if numbers[row] != "" {
			if numbers[row][index] == '0' {
				sums[0]++
			} else {
				sums[1]++
			}
		}
	}
	return sums
}

// mostCommonBit returns the most common bit in a pair of sums
func mostCommonBit(sums [2]int) byte {
	if sums[1] >= sums[0] {
		return '1'
	}
	return '0'
}

// leastCommonBit returns the least common bit in a pair of sums
func leastCommonBit(sums [2]int) byte {
	if sums[0] <= sums[1] {
		return '0'
	}
	return '1'
}

// filter removes strings that don't have the given bit at the given index
func filter(numbers []string, count int, i int, digit byte) int {
	for row := 0; row < len(numbers) && count > 1; row++ {
		if numbers[row] != "" {
			if numbers[row][i] != digit {
				numbers[row] = ""
				count--
			}
		}
	}
	return count
}

// remaining returns the last remaining string
func remaining(numbers []string) string {
	for i := 0; i < len(numbers); i++ {
		if numbers[i] != "" {
			return numbers[i]
		}
	}
	return ""
}

// decimal converts a binary string to a decimal number
func decimal(s string) int {
	n := 0
	for i := 0; i < len(s); i++ {
		n <<= 1
		if s[i] == '1' {
			n |= 1
		}
	}
	return n
}
