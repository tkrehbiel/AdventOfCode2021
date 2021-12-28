package main

import (
	"math"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/16
// Packet Decoder

/*
This is a Golang version of my Kotlin solution.
I braced myself for another monster, but this one was a fairly straightforward
decoding of a binary stream, like the good old days. I made a serial binary reader
class to simplify the parsing, the rest was typing out the complicated rules in code
and debugging the problems as they occurred.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(packetDecoder),
		Day:         16,
		Title:       "Packet Decoder",
		PuzzleInput: "day16_input.txt",
		TestInput: []string{
			"day16_test1.txt",
			"day16_test2.txt",
			"day16_test3.txt",
			"day16_test4.txt",
			"day16_test5.txt",
			"day16_test6.txt",
			"day16_test7.txt",
		},
	})
}

type packetDecoder struct {
	versionSum int
	packet     packet
}

type packet struct {
	value    int
	size     int
	children []packet
}

func (p *packetDecoder) Common(input string) {}

// Part1 returns the first puzzle result
func (p *packetDecoder) Part1(input string) (result int) {
	p.versionSum = 0
	reader := newBitReader(common.ReadText(input))
	p.packet = getPacket(p, &reader)
	return p.versionSum
}

// Part2 returns the second puzzle result
func (p *packetDecoder) Part2(input string) (result int) {
	// Already calculated in Part1 above:
	return p.packet.value
}

// getPacket decodes the next packet and all of its sub-packets from the bit reader
func getPacket(puzzle *packetDecoder, reader *bitReader) (p packet) {
	version := reader.read(3)
	packetType := reader.read(3)
	p.size = 6

	puzzle.versionSum += version

	if packetType == 4 {
		// literal packet
		more := true
		for more {
			more = reader.read(1) != 0
			decimal := reader.read(4)
			p.value = p.value<<4 | decimal
			p.size += 5
		}
	} else {
		// operator packet
		lenType := reader.read(1)
		p.size++
		if lenType == 0 {
			length := reader.read(15)
			p.size += 15
			for length > 0 {
				sub := getPacket(puzzle, reader)
				p.size += sub.size
				p.children = append(p.children, sub)
				length -= sub.size
			}
		} else {
			count := reader.read(11)
			p.size += 11
			for count > 0 {
				sub := getPacket(puzzle, reader)
				p.size += sub.size
				p.children = append(p.children, sub)
				count--
			}
		}
		operator(packetType, &p)
	}
	return p
}

// operator computes the operator value from the subpackets
func operator(packetType int, op *packet) {
	op.value = 0
	switch packetType {
	case 0:
		for _, p := range op.children {
			op.value += p.value
		}
	case 1:
		product := 1
		for _, p := range op.children {
			product *= p.value
		}
		op.value = product
	case 2:
		min := math.MaxInt
		for _, p := range op.children {
			if p.value < min {
				min = p.value
			}
		}
		op.value = min
	case 3:
		max := math.MinInt
		for _, p := range op.children {
			if p.value > max {
				max = p.value
			}
		}
		op.value = max
	case 5:
		if op.children[0].value > op.children[1].value {
			op.value = 1
		}
	case 6:
		if op.children[0].value < op.children[1].value {
			op.value = 1
		}
	case 7:
		if op.children[0].value == op.children[1].value {
			op.value = 1
		}
	}
}
