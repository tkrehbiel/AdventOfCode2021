package main

type scanner struct {
	id          int             // this scanner's index
	detected    []point         // list of beacons detected from scanner perspective
	mapped      []mappedScanner // other mapped scanners from this perspective
	overlapping []*scanner      // overlapping scanners found
}

//orientation int             // orientation of scanner beacons
//rotated     []point         // list of beacons translated
//found       []bool          // found scanner at the given index
//others      []point         // list of other scanner positions

func (s *scanner) copy() scanner {
	newScanner := scanner{
		id:       s.id,
		detected: make([]point, 0),
	}
	// ensures a whole new copy of points is created
	for i := 0; i < len(s.detected); i++ {
		newScanner.detected = append(newScanner.detected,
			point{
				s.detected[i].x,
				s.detected[i].y,
				s.detected[i].z,
			})
	}
	return newScanner
}

func (s *scanner) makeMapped() mappedScanner {
	mapped := mappedScanner{
		id:          s.id,
		source:      s,
		rotated:     make([]point, 0),
		orientation: 0,
		position:    point{0, 0, 0},
	}
	// ensures a whole new copy of points is created
	for i := 0; i < len(s.detected); i++ {
		mapped.rotated = append(mapped.rotated,
			point{
				s.detected[i].x,
				s.detected[i].y,
				s.detected[i].z,
			})
	}
	return mapped
}

func (s *scanner) makeTranslated(orientation int, translate point) mappedScanner {
	mapped := mappedScanner{
		id:          s.id,
		source:      s,
		rotated:     make([]point, 0),
		orientation: 0,
		position:    point{0, 0, 0},
	}
	// ensures a whole new copy of points is created
	for i := 0; i < len(s.detected); i++ {
		var q point
		p := point{
			s.detected[i].x,
			s.detected[i].y,
			s.detected[i].z,
		}
		for j := 0; j < orientation; j++ {
			q = rotatePoint(p, j)
		}
		q = translatePoint(q, translate)
		mapped.rotated = append(mapped.rotated, q)
	}
	return mapped
}

func (s *scanner) addOverlapping(other *scanner) {
	if len(s.overlapping) == 0 {
		s.overlapping = make([]*scanner, 0)
	}
	s.overlapping = append(s.overlapping, other)
}

func (s *scanner) overlapsWith(other *scanner) bool {
	if len(s.overlapping) == 0 {
		return false
	}
	for _, s := range s.overlapping {
		if s == other {
			return true
		}
	}
	return false
}

/*
func (s *scanner) beacons() []point {
	if len(s.rotated) == 0 {
		return s.detected
	}
	return s.rotated
}
*/

type overlapInfo struct {
	points      []point
	originals   []point
	translation point
	orientation int
}

func abs(n int) int {
	if n < 0 {
		return -n
	}
	return n
}

func overlappingPoints(target []point, s2 *mappedScanner, num int) (info overlapInfo, success bool) {
	for i := 0; i < 24; i++ {
		if i == 13 {
			//println("break here")
		}
		potentialTranslations := translations(target, s2.rotated)
		for _, t := range potentialTranslations {
			if abs(t.x) == 1104 &&
				abs(t.y) == 88 &&
				abs(t.z) == 113 {
				//println("break here")
			}
			originals := make([]point, 0)
			points := make([]point, 0)
			for i, p := range s2.rotated {
				translated := point{p.x + t.x, p.y + t.y, p.z + t.z}
				if contains(target, translated) {
					points = append(points, translated)
					originals = append(originals, s2.source.detected[i])
				}
			}
			if len(points) >= num {
				info.points = points
				info.originals = originals
				info.translation = t
				info.orientation = s2.orientation
				return info, true
			}
		}
		s2.rotate()
	}
	return overlapInfo{}, false
}

func contains(points []point, point point) bool {
	// TODO: It is assume each point is unique
	for _, p := range points {
		if p.x == point.x && p.y == point.y && p.z == point.z {
			return true
		}
	}
	return false
}

// translations gets all possible deltas from the second list of points to the first list of points
func translations(scanned []point, rotated []point) []point {
	t := make([]point, 0)
	for i := range scanned {
		for j := range rotated {
			t = append(t, point{
				scanned[i].x - rotated[j].x,
				scanned[i].y - rotated[j].y,
				scanned[i].z - rotated[j].z,
			})
		}
	}
	return t
}

// mappedScanner is a relative view of another scanner
type mappedScanner struct {
	id          int      // id of scanner object
	source      *scanner // scanner object
	orientation int      // orientation relative to its mapper
	rotated     []point  // points relative to mapper
	position    point    // position relative to its mapper
}

// init ensures the beacon array is populated on first-time use
func (s *mappedScanner) init() {
	if len(s.rotated) == 0 {
		s.reset()
	}
}

// reset resets the orientation to the original detected values
func (s *mappedScanner) reset() {
	s.rotated = make([]point, 0)
	s.rotated = append(s.rotated, s.source.detected...)
	s.orientation = 0
}

// rotate all beacons one facing
func (s *mappedScanner) rotate() {
	s.init()
	s.orientation = (s.orientation + 1) % 24
	for i := range s.source.detected {
		p := s.rotated[i]
		var o point
		o = rotatePoint(p, s.orientation)
		s.rotated[i].x = o.x
		s.rotated[i].y = o.y
		s.rotated[i].z = o.z
	}
}

// rotate all beacons one facing
func (s *mappedScanner) rotateReverse() {
	s.init()
	s.orientation = (s.orientation - 1) % 24
	for i := range s.source.detected {
		p := s.rotated[i]
		var o point
		o = rotatePointReverse(p, s.orientation)
		s.rotated[i].x = o.x
		s.rotated[i].y = o.y
		s.rotated[i].z = o.z
	}
}

func (s *mappedScanner) translate(p point) {
	s.init()
	for i := range s.rotated {
		s.rotated[i].x += p.x
		s.rotated[i].y += p.y
		s.rotated[i].z += p.z
	}
}
