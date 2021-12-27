package main

import (
	"bufio"
	"errors"
	"os"
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/4
// A large portion of this puzzle involved parsing the input file.

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         4,
		Title:       "Giant Squid",
		PuzzleInput: "day04_input.txt",
		TestInput:   []string{"day04_test.txt"},
	})
}

type puzzle struct {
	drawing   []int
	boards    []board
	lastScore int
}

type board struct {
	won     bool
	grid    [5][5]int
	matches [5][5]bool
}

// Common reads the input and gets ready to run the puzzle.
func (p *puzzle) Common(input string) {
	p.drawing = make([]int, 0)
	p.boards = make([]board, 0)
	p.readInput(input)
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	var firstScore int
	for _, number := range p.drawing {
		for j := range p.boards {
			b := &p.boards[j]
			if !b.won {
				b.call(number)
				if b.wins() {
					b.won = true
					total := b.score() * number
					if firstScore == 0 {
						firstScore = total
					}
					p.lastScore = total
				}
			}
		}
	}
	return firstScore
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	// This was actually computed in Part1() above:
	return p.lastScore
}

// call marks the given number as called on the board
func (b *board) call(number int) {
	for j := 0; j < 5; j++ {
		for i := 0; i < 5; i++ {
			if b.grid[j][i] == number {
				b.matches[j][i] = true
			}
		}
	}
}

// wins returns true if the board has a winning row or column
func (b *board) wins() bool {
	for i := 0; i < 5; i++ {
		if b.columnWins(i) {
			return true
		}
	}
	for j := 0; j < 5; j++ {
		if b.rowWins(j) {
			return true
		}
	}
	return false
}

// score returns the puzzle score of the board
func (b *board) score() (n int) {
	// add up non-matching numbers
	for i := 0; i < 5; i++ {
		for j := 0; j < 5; j++ {
			if !b.matches[i][j] {
				n += b.grid[i][j]
			}
		}
	}
	return n
}

// rowWins returns true if the given row is finished
func (b *board) rowWins(row int) bool {
	for i := 0; i < 5; i++ {
		if !b.matches[row][i] {
			return false
		}
	}
	return true
}

// columnWins returns true if the given column is finished
func (b *board) columnWins(col int) bool {
	for j := 0; j < 5; j++ {
		if !b.matches[j][col] {
			return false
		}
	}
	return true
}

func (p *puzzle) readInput(input string) {
	file, err := os.Open(input)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	// This is a horrible data file format
	// that should never be used in real life.
	// Never ever do this ever in real code.
	scanner := bufio.NewScanner(file)

	// First line is a list of numbers
	if !scanner.Scan() {
		panic(errors.New("can't read first line"))
	}
	p.drawing = parseDrawings(scanner.Text())

	// Beyond lies the even worse formatted bingo boards
	for scanner.Scan() {
		// first line is a blank
		// then 5 lines of numbers
		var boardValues [5]string
		for row := 0; row < 5; row++ {
			if !scanner.Scan() {
				panic(errors.New("can't read row"))
			}
			boardValues[row] = scanner.Text()
		}
		p.boards = append(p.boards, parseBoard(boardValues))
	}

	if err := scanner.Err(); err != nil {
		panic(err)
	}
}

// parseDrawings parses a list of numbers into an array
func parseDrawings(s string) (values []int) {
	trimmed := strings.TrimRight(s, "\r\n")
	for _, s := range strings.Split(trimmed, ",") {
		i, err := strconv.Atoi(s)
		if err != nil {
			panic(err)
		}
		values = append(values, i)
	}
	return values
}

// parseBoard parses a bingo board string into a board struct
func parseBoard(values [5]string) (b board) {
	for j := 0; j < 5; j++ {
		for i := 0; i < 5; i++ {
			var err error
			x := i * 3
			b.grid[j][i], err = strconv.Atoi(strings.TrimSpace(values[j][x : x+2]))
			if err != nil {
				panic(err)
			}
		}
	}
	return b
}
