package main

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/19
// The infamous Beacon Scanner
/*
Initially tried to solve this in Kotlin, but abandoned that code unfinished,
then re-wrote it from scratch in Golang. Never got it.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         19,
		Title:       "Beacon Scanner",
		PuzzleInput: "", //"day19_input.txt",
		TestInput:   []string{"day19_test1.txt", "day19_test2.txt"},
	})
}

type puzzle struct {
	scanners []scanner
}

// Common reads the puzzle input into an array of scanners
func (p *puzzle) Common(input string) {
	p.scanners = readInput(input)
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	findOverlappingScanners(p.scanners)

	// Okay _now_ what? I have all this data but
	// I'm not sure what to do with it.
	for _, s := range p.scanners {
		fmt.Printf("scanner %d:\n", s.id)
		for i := 0; i < len(s.mapped); i++ {
			if s.mapped[i].source != nil {
				fmt.Printf("  scanner %d at: %v (relative to %d)\n",
					s.mapped[i].id, s.mapped[i].position, s.id)
			}
		}
	}

	return len(p.scanners)
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	return len(p.scanners) * 2
}

type overlap struct {
	viewer *scanner
	mapped *mappedScanner
}

func findOverlappingScanners(scanners []scanner) []overlap {
	overlaps := make([]overlap, 0)
	for i := 0; i < len(scanners); i++ {
		s1 := &scanners[i]
		for j := 0; j < len(scanners); j++ {
			s2 := &scanners[j]
			if j != i && !s1.overlapsWith(s2) {
				mapped := s2.makeMapped()
				if info, ok := overlappingPoints(s1.detected, &mapped, 12); ok {
					fmt.Printf("scanner %d detected %d overlapping points with scanner %d by rotating its points to orientation %d and translating %v\n",
						i,
						len(info.points),
						j,
						info.orientation,
						info.translation)
					overlaps = append(overlaps, overlap{
						viewer: s1,
						mapped: &mapped,
					})
					// TODO: don't think we need these anymore:
					mapped.position = info.translation
					s1.mapped[j] = mapped
					s1.addOverlapping(s2)
					s2.addOverlapping(s1)
				}
			}
		}
	}
	return overlaps
}

func readInput(fileName string) []scanner {
	var index int
	scanners := make([]scanner, 0)
	var sc *scanner
	common.EnumerateLines(fileName, func(row int, s string) {
		if strings.HasPrefix(s, "---") {
			// TODO: assumes the scanners are in order in the file
			scanners = append(scanners, scanner{id: index, detected: make([]point, 0)})
			sc = &scanners[len(scanners)-1]
			index++
		} else if len(s) > 0 {
			fields := strings.Split(s, ",")
			sc.detected = append(sc.detected, parseBeacon(fields))
		}
	})
	for i := range scanners {
		//scanners[i].others = make([]point, len(scanners))
		//scanners[i].found = make([]bool, len(scanners))
		scanners[i].overlapping = make([]*scanner, 0)
		scanners[i].mapped = make([]mappedScanner, len(scanners))
	}
	return scanners
}

func parseBeacon(fields []string) point {
	x, err := strconv.Atoi(fields[0])
	if err != nil {
		panic(err)
	}

	y, err := strconv.Atoi(fields[1])
	if err != nil {
		panic(err)
	}

	z, err := strconv.Atoi(fields[2])
	if err != nil {
		panic(err)
	}

	return point{x: x, y: y, z: z}
}
