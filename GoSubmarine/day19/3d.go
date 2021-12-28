package main

import "fmt"

// 3D stuff

type point struct {
	x, y, z int
}

func (p point) String() string {
	return fmt.Sprintf("(%d,%d,%d)", p.x, p.y, p.z)
}

// rotateXminus rotates around the X axis
func (p point) rotateXminus() point {
	return point{p.x, p.z, -p.y}
}

// rotateXplus rotates around the X axis
func (p point) rotateXplus() point {
	return point{p.x, -p.z, p.y}
}

// rotateYminus rotates around the Y axis
func (p point) rotateYminus() point {
	return point{p.z, p.y, -p.x}
}

// rotateYplus rotates around the Y axis
func (p point) rotateYplus() point {
	return point{-p.z, p.y, p.x}
}

// rotateZminus rotates around the Z axis
func (p point) rotateZminus() point {
	return point{-p.y, p.x, p.z}
}

// rotateZ rotates around the Z axis
func (p point) rotateZplus() point {
	return point{p.y, -p.x, p.z}
}

// Rotates forwards to the given orientation
func rotatePoint(p point, orientation int) (newPoint point) {
	switch orientation {
	case 1, 2, 3:
		newPoint = p.rotateXminus()
	case 4:
		newPoint = p.rotateXminus().rotateYminus()
	case 5, 6, 7:
		newPoint = p.rotateXminus()
	case 8:
		newPoint = p.rotateXminus().rotateYminus()
	case 9, 10, 11:
		newPoint = p.rotateXminus()
	case 12:
		newPoint = p.rotateXminus().rotateYminus()
	case 13, 14, 15:
		newPoint = p.rotateXminus()
	case 16:
		newPoint = p.rotateXminus().rotateYminus()
	case 17, 18, 19:
		newPoint = p.rotateXminus()
	case 20:
		newPoint = p.rotateXminus().rotateZminus()
	case 21, 22, 23:
		newPoint = p.rotateXminus()
	case 0, 24:
		newPoint = p.rotateXminus().rotateZplus()
	}
	return newPoint
}

// Rotates backwards to the given orientation
func rotatePointReverse(p point, orientation int) (newPoint point) {
	switch orientation {
	case 0, 1, 2:
		newPoint = p.rotateXplus()
	case 3:
		newPoint = p.rotateYplus().rotateXplus()
	case 4, 5, 6:
		newPoint = p.rotateXplus()
	case 7:
		newPoint = p.rotateYplus().rotateXplus()
	case 8, 9, 10:
		newPoint = p.rotateXplus()
	case 11:
		newPoint = p.rotateYplus().rotateXplus()
	case 12, 13, 14:
		newPoint = p.rotateXplus()
	case 15:
		newPoint = p.rotateYplus().rotateXplus()
	case 16, 17, 18:
		newPoint = p.rotateXplus()
	case 19:
		newPoint = p.rotateZplus().rotateXplus()
	case 20, 21, 22:
		newPoint = p.rotateXplus()
	case 23:
		newPoint = p.rotateZminus().rotateXplus()
	}
	return newPoint
}

func translatePoint(p point, t point) point {
	return point{p.x + t.x, p.y + t.y, p.z + t.z}
}
