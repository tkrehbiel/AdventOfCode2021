package main

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestRotateX(t *testing.T) {
	p := point{1, 2, 3}
	assert.Equal(t, point{1, 3, -2}, p.rotateXminus())
}

func TestRotateY(t *testing.T) {
	p := point{1, 2, 3}
	assert.Equal(t, point{3, 2, -1}, p.rotateYminus())
}

func TestRotateZminus(t *testing.T) {
	p := point{1, 2, 3}
	assert.Equal(t, point{-2, 1, 3}, p.rotateZminus())
}

func TestRotateZplus(t *testing.T) {
	p := point{1, 2, 3}
	assert.Equal(t, point{2, -1, 3}, p.rotateZplus())
}

func TestOrientations(t *testing.T) {
	actual := point{1, 2, -3}

	// Confirmed with a 6-sided die :)

	actual = rotatePoint(actual, 1)
	assert.Equal(t, point{1, -3, -2}, actual)
	actual = rotatePoint(actual, 2)
	assert.Equal(t, point{1, -2, 3}, actual)
	actual = rotatePoint(actual, 3)
	assert.Equal(t, point{1, 3, 2}, actual)
	actual = rotatePoint(actual, 4) // x+y
	assert.Equal(t, point{-3, 2, -1}, actual)

	actual = rotatePoint(actual, 5)
	assert.Equal(t, point{-3, -1, -2}, actual)
	actual = rotatePoint(actual, 6)
	assert.Equal(t, point{-3, -2, 1}, actual)
	actual = rotatePoint(actual, 7)
	assert.Equal(t, point{-3, 1, 2}, actual)
	actual = rotatePoint(actual, 8) // x+y
	assert.Equal(t, point{-1, 2, 3}, actual)

	actual = rotatePoint(actual, 9)
	assert.Equal(t, point{-1, 3, -2}, actual)
	actual = rotatePoint(actual, 10)
	assert.Equal(t, point{-1, -2, -3}, actual)
	actual = rotatePoint(actual, 11)
	assert.Equal(t, point{-1, -3, 2}, actual)
	actual = rotatePoint(actual, 12) // x+y
	assert.Equal(t, point{3, 2, 1}, actual)

	actual = rotatePoint(actual, 13)
	assert.Equal(t, point{3, 1, -2}, actual)
	actual = rotatePoint(actual, 14)
	assert.Equal(t, point{3, -2, -1}, actual)
	actual = rotatePoint(actual, 15)
	assert.Equal(t, point{3, -1, 2}, actual)
	actual = rotatePoint(actual, 16) //x+y
	assert.Equal(t, point{1, 2, -3}, actual)

	actual = rotatePoint(actual, 17)
	assert.Equal(t, point{1, -3, -2}, actual)
	actual = rotatePoint(actual, 18)
	assert.Equal(t, point{1, -2, 3}, actual)
	actual = rotatePoint(actual, 19)
	assert.Equal(t, point{1, 3, 2}, actual)
	actual = rotatePoint(actual, 20) //x+z
	assert.Equal(t, point{-2, 1, -3}, actual)

	actual = rotatePoint(actual, 21)
	assert.Equal(t, point{-2, -3, -1}, actual)
	actual = rotatePoint(actual, 22)
	assert.Equal(t, point{-2, -1, 3}, actual)
	actual = rotatePoint(actual, 23)
	assert.Equal(t, point{-2, 3, 1}, actual)
	actual = rotatePoint(actual, 0) //x-z
	assert.Equal(t, point{1, 2, -3}, actual)
}

func TestRotateReverse(t *testing.T) {
	expected := point{1, 2, -3}

	for i := 1; i <= 24; i++ {
		actual := expected
		for j := 1; j <= i; j++ {
			actual = rotatePoint(actual, j)
		}
		for j := i - 1; j >= 0; j-- {
			actual = rotatePointReverse(actual, j)
		}
		assert.Equal(t, expected, actual)
	}
}
