package main

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/6
// Lanternfish

/*
This was the first puzzle I attempted in Advent of Code ever.
My first brute force solution naturally consumed too much memory for part 2,
and I had to abandon it. I initially approached it much like a video game
framework of entities. I show it here only for the historical records.
Lesson learned: Expect part 2 to have to scale.
Eventually I went back and solved part 2 in Kotlin instead.
*/

type fish struct {
	timer int
}

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(lanternFish),
		Day:         6,
		Title:       "Lanternfish",
		PuzzleInput: "day06_input.txt",
		TestInput:   []string{"day06_test.txt"},
	})
}

type lanternFish struct {
	current []fish
	spawned []fish
}

// Common reads the puzzle input and builds the initial array of fish
func (p *lanternFish) Common(input string) {
	p.current = make([]fish, 0)

	s := common.ReadText(input)
	for _, v := range strings.Split(s, ",") {
		n, err := strconv.Atoi(v)
		if err != nil {
			fmt.Errorf("bad input %s: %v", v, err)
		}
		p.current = append(p.current, fish{timer: n})
	}
}

// Part1 returns the first puzzle result
func (p *lanternFish) Part1(input string) (result int) {
	for i := 0; i < 80; i++ {
		p.generation()
	}
	return len(p.current)
}

// Part2 returns the second puzzle result
func (p *lanternFish) Part2(input string) (result int) {
	fmt.Println("part 2 not implemented in golang yet")
	return 0
}

// cycle computes one cycle for a fish
func (f *fish) cycle(p *lanternFish) {
	if f.timer == 0 {
		p.spawnFish(8)
		f.timer = 6
		return
	}
	f.timer--
}

// spawnFish creates a new fish with an initial timer
func (p *lanternFish) spawnFish(initial int) {
	p.spawned = append(p.spawned, fish{timer: initial})
}

// generation computes all fish cycles for a single generation
func (p *lanternFish) generation() {
	// spawned fish go into a different list to avoid enumeration conflicts
	p.spawned = make([]fish, 0)
	for j := range p.current {
		(&p.current[j]).cycle(p)
	}
	// add spawned fish into the current list
	for _, v := range p.spawned {
		p.current = append(p.current, v)
	}
}
