package common

import (
	"bufio"
	"os"
)

// EnumerateLines reads a file line by line and calls the given function
func EnumerateLines(fileName string, process func(row int, s string)) {
	if process == nil {
		return
	}

	file, err := os.Open(fileName)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	row := 0
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		process(row, scanner.Text())
		row++
	}

	if err := scanner.Err(); err != nil {
		panic(err)
	}
}
