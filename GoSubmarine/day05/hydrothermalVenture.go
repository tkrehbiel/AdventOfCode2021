package main

import (
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/5
// Plotting simple lines on a grid. I used an interface and three types of lines.
// It turned out to be overkill and could have been simplified.
// This is one where the second part of the problem renders the first part of
// your solution somewhat obsolete.

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         5,
		Title:       "Hydrothermal Venture",
		PuzzleInput: "day05_input.txt",
		TestInput:   []string{"day05_test.txt"},
	})
}

type puzzle struct {
	grid1 [][]int
	grid2 [][]int
}

type line interface {
	plot(grid [][]int)
	normalize()
}

// Common makes the grids for a puzzle run.
func (p *puzzle) Common(input string) {
	// Make two grids to compute part 1 and part 2 simultaneously
	// TODO: Assumes input doesn't go beyond 1000x1000
	p.grid1 = makeGrid(1000)
	p.grid2 = makeGrid(1000)
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	// Actually plots the lines for part 1 and 2 simultaneously
	common.EnumerateLines(input, func(row int, s string) {
		// We don't need to store the line data
		if len(s) > 1000 {
			panic("can't handle grids bigger than 1000")
		}
		line := newLine(s)
		if line != nil {
			line.normalize()
			// Don't plot diagonal lines on the first grid for part 1
			if _, ok := line.(*diagonalLine); !ok {
				line.plot(p.grid1)
			}
			line.plot(p.grid2)
		}
	})
	return totalGrid(p.grid1)
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	return totalGrid(p.grid2)
}

// makeGrid allocates a square grid of the given size
func makeGrid(size int) [][]int {
	grid := make([][]int, size)
	for i := range grid {
		grid[i] = make([]int, size)
	}
	return grid
}

// totalGrid returns the number of grid cells that overlap
func totalGrid(grid [][]int) (total int) {
	for j := range grid {
		for i := range grid[j] {
			if grid[j][i] > 1 {
				total++
			}
		}
	}
	return total
}

type horizontalLine struct {
	x1, x2, y int
}

func (l *horizontalLine) plot(grid [][]int) {
	for x := l.x1; x <= l.x2; x++ {
		grid[l.y][x]++
	}
}

func (l *horizontalLine) normalize() {
	if l.x2 < l.x1 {
		swap(&l.x1, &l.x2)
	}
}

type verticalLine struct {
	y1, y2, x int
}

func (l *verticalLine) plot(grid [][]int) {
	for j := l.y1; j <= l.y2; j++ {
		grid[j][l.x]++
	}
}

func (l *verticalLine) normalize() {
	if l.y2 < l.y1 {
		swap(&l.y1, &l.y2)
	}
}

type diagonalLine struct {
	x1, y1, x2, y2 int
}

func (l *diagonalLine) plot(grid [][]int) {
	// TODO: Possibly overly complex

	xdir := 0
	if l.x2 > l.x1 {
		xdir = 1
	} else if l.x2 < l.x1 {
		xdir = -1
	}

	ydir := 0
	if l.y2 > l.y1 {
		ydir = 1
	} else if l.y2 < l.y1 {
		ydir = -1
	}

	x := l.x1
	y := l.y1
	grid[y][x]++
	for x != l.x2 && y != l.y2 {
		x += xdir
		y += ydir
		grid[y][x]++
	}
}

func (l *diagonalLine) normalize() {
	// Normalizing is accounted for in the plot function
}

// swap exchanges the values of two integers
func swap(a, b *int) {
	t := *a
	*a = *b
	*b = t
}

// newLine parses a string into a line interface
func newLine(s string) (l line) {
	fields := strings.Split(s, "->")
	startCoords := strings.Split(strings.TrimSpace(fields[0]), ",")
	endCoords := strings.Split(strings.TrimSpace(fields[1]), ",")
	var err error
	var x1, y1, x2, y2 int
	x1, err = strconv.Atoi(startCoords[0])
	if err != nil {
		panic(err)
	}
	y1, err = strconv.Atoi(startCoords[1])
	if err != nil {
		panic(err)
	}
	x2, err = strconv.Atoi(endCoords[0])
	if err != nil {
		panic(err)
	}
	y2, err = strconv.Atoi(endCoords[1])
	if err != nil {
		panic(err)
	}
	// Possibly overkill to use specific horizontal and vertical lines.
	// The diagonal line plot technically should handle horizontal and vertical too.
	// But I made the horizontal and vertical lines before I knew what to expect in part 2.
	if y1 == y2 {
		return &horizontalLine{x1: x1, x2: x2, y: y1}
	} else if x1 == x2 {
		return &verticalLine{y1: y1, y2: y2, x: x1}
	} else {
		// We also use the diagonalLine type to determine whether to compute part 1 or 2
		return &diagonalLine{x1: x1, y1: y1, x2: x2, y2: y2}
	}
}
