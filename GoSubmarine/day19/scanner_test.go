package main

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestRotateAll(t *testing.T) {
	scanner := &scanner{
		detected: []point{
			{-1, -1, 1},
			{-2, -2, 2},
			{-3, -3, 3},
			{-2, -3, 1},
			{5, 6, -4},
			{8, 0, 7},
		},
	}
	mapped := scanner.makeMapped()
	for i := 0; i < 24; i++ {
		mapped.rotate()
	}
	assert.True(t, matches(scanner.detected, mapped.rotated))
}

func TestRotateBeacons(t *testing.T) {
	scanner := &scanner{
		detected: []point{
			{-1, -1, 1},
			{-2, -2, 2},
			{-3, -3, 3},
			{-2, -3, 1},
			{5, 6, -4},
			{8, 0, 7},
		},
	}
	mapped := scanner.makeMapped()

	expected1 := []point{
		{1, -1, 1},
		{2, -2, 2},
		{3, -3, 3},
		{2, -1, 3},
		{-5, 4, -6},
		{-8, -7, 0},
	}
	expected2 := []point{
		{-1, -1, -1},
		{-2, -2, -2},
		{-3, -3, -3},
		{-1, -3, -2},
		{4, 6, 5},
		{-7, 0, 8},
	}
	expected3 := []point{
		{1, 1, -1},
		{2, 2, -2},
		{3, 3, -3},
		{1, 3, -2},
		{-4, -6, 5},
		{7, 0, 8},
	}
	expected4 := []point{
		{1, 1, 1},
		{2, 2, 2},
		{3, 3, 3},
		{3, 1, 2},
		{-6, -4, -5},
		{0, 7, -8},
	}

	var match1, match2, match3, match4 int
	for i := 0; i < 48; i++ {
		// if we rotate through 48 positions, we should get each match exactly twice
		mapped.rotate()
		if matches(expected1, mapped.rotated) {
			match1++
		}
		if matches(expected2, mapped.rotated) {
			match2++
		}
		if matches(expected3, mapped.rotated) {
			match3++
		}
		if matches(expected4, mapped.rotated) {
			match4++
		}
	}
	assert.Equal(t, 8, match1+match2+match3+match4)
	// theoretically should rotate back to starting position after 24 rotations
	assert.True(t, matches(scanner.detected, mapped.rotated))
}

func matches(expected []point, actual []point) bool {
	for i := range expected {
		if actual[i].x != expected[i].x || actual[i].y != expected[i].y || actual[i].z != expected[i].z {
			return false
		}
	}
	return true
}
