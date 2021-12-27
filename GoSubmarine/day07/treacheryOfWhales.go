package main

import (
	"io/ioutil"
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/7
// Treachery of Whales

/*
I went with brute force again, rather than try anything fancy. Time is money!
Basically just turned the problem explanation into code and out came the answer.
This was the first day that I got both stars on the day, rather than backfilling
the previous days.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         7,
		Title:       "Treachery of Whales",
		PuzzleInput: "day07_input.txt",
		TestInput:   []string{"day07_test.txt"},
	})
}

type puzzle struct {
	positions   []int
	maxPosition int
}

type optimal struct {
	position int
	fuel     int
}

// Common runs before Part1 and Part2, and loads the puzzle input
func (p *puzzle) Common(input string) {
	p.positions = getInput(input)
	p.maxPosition = max(p.positions)
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	var min1 optimal
	for i := 0; i <= p.maxPosition; i++ {
		var fuel1 int
		for _, v := range p.positions {
			// Part 1 - one fuel per position
			fuel1 += abs(v - i)
		}
		min1.setMin(i, fuel1)
	}
	return min1.fuel
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	var min2 optimal
	for i := 0; i <= p.maxPosition; i++ {
		var fuel2 int
		for _, v := range p.positions {
			// Part 2 - increasing fuel per position
			// More brute force ftw
			// There might be a calculation to do this without
			// a loop but I didn't feel like finding it. Time is money!
			cost := 1
			for j := 0; j < abs(v-i); j++ {
				fuel2 += cost
				cost++
			}
		}
		min2.setMin(i, fuel2)
	}
	return min2.fuel
}

// max returns the maximum value in a slice of integers
func max(values []int) (max int) {
	for _, v := range values {
		if v > max {
			max = v
		}
	}
	return max
}

func abs(i int) int {
	if i < 0 {
		return -i
	}
	return i
}

func (r *optimal) setMin(position int, fuel int) {
	if fuel < r.fuel || position == 0 {
		r.position = position
		r.fuel = fuel
	}
}

// getInput returns the input file as a slice of integers
func getInput(fileName string) (values []int) {
	bytes, err := ioutil.ReadFile(fileName)
	if err != nil {
		panic(err)
	}
	trimmed := strings.TrimRight(string(bytes), "\r\n")
	for _, s := range strings.Split(trimmed, ",") {
		i, err := strconv.Atoi(s)
		if err != nil {
			panic(err)
		}
		values = append(values, i)
	}
	return values
}
