package com.alext.rtree.misc

import com.alext.rtree.document.NotThreadSafe

/**
 * A simple stack implementation using single linked list.
 * Unlike [java.util.Stack], this implementation ignore synchronization
 * on each operation in order to maximize performance on search.
 */
@NotThreadSafe
class Stack<T> : Iterable<T> {

  private class Node<T>(
    val item: T,
    val next: Node<T>? = null
  )

  override fun iterator(): Iterator<T> {
    var current: Node<T>? = head
    return object : Iterator<T> {
      override fun hasNext(): Boolean {
        return current != null
      }

      override fun next(): T {
        if (!hasNext()) {
          throw NoSuchElementException()
        }
        val item = current!!.item
        current = current!!.next
        return item
      }
    }
  }

  private var size: Int = 0
  private var head: Node<T>? = null
  val empty: Boolean get() = head == null

  fun size(): Int {
    return size
  }

  /**
   * Add an item to stack
   *
   * @param item The item to add
   */
  fun push(item: T) {
    val previous = head
    head = Node(item = item, next = previous)
    size++
  }

  /**
   * Remove and return the top item of the stack
   *
   * @throws NoSuchElementException if this stack is empty
   */
  fun pop(): T {
    if (empty) {
      throw NoSuchElementException("Underflow")
    }
    val item = head!!.item
    head = head!!.next
    size--
    return item
  }

  /**
   * Return but does not remove the top item of the stack.
   *
   * @throws NoSuchElementException if this stack is empty
   */
  fun peek(): T? {
    if (empty) {
      throw NoSuchElementException("Underflow")
    }
    return head!!.item
  }
}
