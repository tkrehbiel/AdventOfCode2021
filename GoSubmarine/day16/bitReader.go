package main

type bitReader struct {
	hex      string
	hexIndex int
	bitIndex int
}

// TODO: assumes uppercase
var hexMap map[byte]int = map[byte]int{
	byte('0'): 0,
	byte('1'): 1,
	byte('2'): 2,
	byte('3'): 3,
	byte('4'): 4,
	byte('5'): 5,
	byte('6'): 6,
	byte('7'): 7,
	byte('8'): 8,
	byte('9'): 9,
	byte('A'): 10,
	byte('B'): 11,
	byte('C'): 12,
	byte('D'): 13,
	byte('E'): 14,
	byte('F'): 15,
}

var maskMap map[int]int = map[int]int{
	0: 8,
	1: 4,
	2: 2,
	3: 1,
}

func newBitReader(s string) bitReader {
	return bitReader{hex: s}
}

// reads the next n bits from the reader
// TODO: no error handling
func (b *bitReader) read(n int) int {
	result := 0
	for i := 0; i < n; i++ {
		current := hexMap[b.hex[b.hexIndex]]
		result <<= 1
		if current&maskMap[b.bitIndex] != 0 {
			result++
		}
		b.bitIndex++
		if b.bitIndex > 3 {
			b.bitIndex = 0
			b.hexIndex++
		}
	}
	return result
}
