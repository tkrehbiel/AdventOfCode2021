package main

import (
	"math"

	"github.com/tkrehbiel/adventofcode2021/gosubmarine/common"
)

// https://adventofcode.com/2021/day/15
// Chiton
/*
This is the first ever Dijkstra shortest path implementation I've ever written or needed.
For this updated Golang implementation, I used a priority queue
(using a heap) which sped up the puzzle solution to less than a second,
where before, with a slow queue, it took about 15 minutes.
*/

func main() {
	common.Run(common.PuzzleConfig{
		Puzzle:      new(puzzle),
		Day:         15,
		Title:       "Chiton",
		PuzzleInput: "day15_input.txt",
		TestInput:   []string{"day15_test1.txt", "day15_test2.txt"},
	})
}

type puzzle struct {
	input         []string
	width, height int
	vertices      []vertex
	points        map[int]*vertex
	pQueue        *PriorityQueue
}

const infinite = math.MaxInt32

// Technically we only need the end vertex
type edge struct {
	start, end *vertex
}

type vertex struct {
	edges    []edge
	x, y     int
	value    int
	distance int        // for Dijkstra
	prev     *vertex    // for Dijkstra
	dequeued bool       // for Dijkstra
	item     *QueueItem // for Dijkstra
}

// Common runs before Part1 and Part2
func (p *puzzle) Common(input string) {
	p.input = make([]string, 0)
	common.EnumerateLines(input, func(row int, s string) {
		p.input = append(p.input, s)
	})
}

// Part1 returns the first puzzle result
func (p *puzzle) Part1(input string) (result int) {
	p.makeGrid(p.input)
	return p.shortestPath()
}

// Part2 returns the second puzzle result
func (p *puzzle) Part2(input string) (result int) {
	p.makeGrid(p.expandGrid(5))
	return p.shortestPath()
}

// Expand the grid size for part two
func (p *puzzle) expandGrid(multiplier int) []string {
	input2 := make([]string, len(p.input)*multiplier)
	for y := 0; y < multiplier; y++ {
		for j, s := range p.input {
			outRow := len(p.input)*y + j
			bytes := make([]byte, len(s)*multiplier)
			for x := 0; x < multiplier; x++ {
				for i, c := range s {
					outCol := len(s)*x + i
					value := int(c) - int('0') + x + y
					for value > 9 {
						value -= 9
					}
					bytes[outCol] = '0' + byte(value)
				}
				input2[outRow] = string(bytes)
			}
		}
	}
	return input2
}

// makeGrid populates the graph from input strings
func (p *puzzle) makeGrid(input []string) {
	// convert the grid into a graph
	p.vertices = make([]vertex, 0)
	p.height = 0
	for row, s := range input {
		if row == 0 {
			p.width = len(s)
		}
		for x := 0; x < p.width; x++ {
			// TODO: super duper assumes ASCII input files
			value := int(s[x]) - int('0')
			v := vertex{
				x:        x,
				y:        row,
				value:    value,
				distance: infinite,
			}
			p.vertices = append(p.vertices, v)
		}
		p.height++
	}

	// build point map to vertices for easy indexing
	p.points = make(map[int]*vertex)
	for i := range p.vertices {
		v := &p.vertices[i]
		p.points[p.key(v.x, v.y)] = v
	}

	// populate edges
	for y := 0; y < p.height; y++ {
		for x := 0; x < p.width; x++ {
			node := p.getPoint(x, y)
			node.edges = make([]edge, 0)
			// I thought about removing the up and left directions,
			// which worked for the test input,
			// but failed on the main puzzle input.
			if x > 0 {
				node.edges = append(node.edges, edge{start: node, end: p.getPoint(x-1, y)})
			}
			if y > 0 {
				node.edges = append(node.edges, edge{start: node, end: p.getPoint(x, y-1)})
			}
			if x <= p.width {
				node.edges = append(node.edges, edge{start: node, end: p.getPoint(x+1, y)})
			}
			if y <= p.height {
				node.edges = append(node.edges, edge{start: node, end: p.getPoint(x, y+1)})
			}
		}
	}
}

// Dijkstra implementation more-or-less copied straight from Wikipedia
func (p *puzzle) shortestPath() int {
	for i := range p.vertices {
		p.vertices[i].distance = infinite
	}
	p.getPoint(0, 0).distance = 0

	p.pQueue = initQueue(p.vertices)

	// find shortest path (with a priority queue)
	for queueReady(p.pQueue) {
		item := queuePop(p.pQueue)
		node := item.value
		node.dequeued = true
		if node.x == p.width-1 && node.y == p.height-1 {
			// At the destination point, can stop
			break
		}
		for i := range node.edges {
			neighbor := node.edges[i].end
			if neighbor != nil && !neighbor.dequeued {
				n := node.distance + neighbor.value // "length" = grid value
				if n < neighbor.distance {
					neighbor.distance = n
					neighbor.prev = node // prev stores the path for later
					updatePriority(p.pQueue, neighbor.item, n)
				}
			}
		}
	}

	// count up the values of the shortest path
	total := 0
	v := p.getPoint(p.width-1, p.height-1)
	for v.prev != nil {
		//fmt.Printf("%d,%d\n", v.x, v.y)
		total += v.value
		v = v.prev
	}

	return total
}

func (p *puzzle) key(x, y int) int {
	return y*p.width + x
}

func (p *puzzle) getPoint(x, y int) *vertex {
	return p.points[p.key(x, y)]
}
