package main

import (
	"container/heap"
)

// Priority Queue implementation from
// https://pkg.go.dev/container/heap#example-package-PriorityQueue
// Priority queue speeds up Dikstra's algorithm considerably

// QueueItem is something we manage in a priority queue.
type QueueItem struct {
	value    *vertex // The value of the item; arbitrary.
	priority int     // The priority of the item in the queue.
	// The index is needed by update and is maintained by the heap.Interface methods.
	index int // The index of the item in the heap.
}

// PriorityQueue implements heap.Interface and holds QueueItems.
type PriorityQueue []*QueueItem

func (pq PriorityQueue) Len() int { return len(pq) }

func (pq PriorityQueue) Less(i, j int) bool {
	return pq[i].priority < pq[j].priority
}

func (pq PriorityQueue) Swap(i, j int) {
	pq[i], pq[j] = pq[j], pq[i]
	pq[i].index = i
	pq[j].index = j
}

func (pq *PriorityQueue) Push(x interface{}) {
	n := len(*pq)
	item := x.(*QueueItem)
	item.index = n
	*pq = append(*pq, item)
}

func (pq *PriorityQueue) Pop() interface{} {
	old := *pq
	n := len(old)
	item := old[n-1]
	old[n-1] = nil  // avoid memory leak
	item.index = -1 // for safety
	*pq = old[0 : n-1]
	return item
}

// update modifies the priority and value of an Item in the queue.
func (pq *PriorityQueue) update(item *QueueItem, value *vertex, priority int) {
	item.value = value
	item.priority = priority
	heap.Fix(pq, item.index)
}

// initQueue initializes the queue from a list of graph vertices
func initQueue(vertices []vertex) *PriorityQueue {
	// Create a priority queue, put the items in it, and
	// establish the priority queue (heap) invariants.
	pq := make(PriorityQueue, len(vertices))
	for i := range vertices {
		vertices[i].prev = nil
		vertices[i].dequeued = false
		pq[i] = &QueueItem{
			value:    &vertices[i],
			priority: vertices[i].distance,
			index:    i,
		}
		vertices[i].item = pq[i]
	}
	heap.Init(&pq)
	return &pq
}

// queueReady returns true if the queue is not empty
func queueReady(pq *PriorityQueue) bool {
	return pq.Len() > 0
}

// queuePop gets the smallest priority item from the queue
func queuePop(pq *PriorityQueue) *QueueItem {
	return heap.Pop(pq).(*QueueItem)
}

// updatePriority updates the priority of an item in the queue
func updatePriority(pq *PriorityQueue, item *QueueItem, priority int) {
	pq.update(item, item.value, priority)
}
