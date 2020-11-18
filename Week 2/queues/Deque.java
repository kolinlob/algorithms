/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-08
 *  Description: A double-ended generic queue or deque is a generalization
 *  of a stack and a queue that supports adding and removing items from either
 *  the front or the back of the data structure.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> head = null;
    private Node<Item> tail = null;
    private int size;

    private class Node<T> {
        public T item;
        public Node<T> next;
        public Node<T> previous;

        public Node(T value, Node<T> next, Node<T> previous) {
            this.item = value;
            this.next = next;
            this.previous = previous;
        }
    }

    // construct an empty deque
    public Deque() {
        this.size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size <= 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node<Item> oldHead = head;
        head = new Node<Item>(item, oldHead, null);

        if (isEmpty()) {
            tail = head;
        }
        else {
            oldHead.previous = head;
        }
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node<Item> oldTail = tail;
        tail = new Node<Item>(item, null, oldTail);

        if (isEmpty()) {
            head = tail;
        }
        else {
            oldTail.next = tail;
        }
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item result = head.item;

        if (size > 1) {
            head = head.next;
            head.previous = null;
        }
        else {
            tail = null;
            head = null;
        }

        size--;

        return result;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item result = tail.item;

        if (size > 1) {
            tail = tail.previous;
            tail.next = null;
        }
        else {
            tail = null;
            head = null;
        }

        size--;

        return result;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private Node<Item> current = head;

            public boolean hasNext() {
                return current != null;
            }

            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();

                Item res = current.item;
                current = current.next;
                return res;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deq = new Deque<String>();

        deq.addFirst("two");
        deq.addFirst("one");
        deq.addLast("three");
        deq.addLast("four");
        deq.addLast("five");

        StdOut.println("");
        StdOut.println("size: " + deq.size());
        StdOut.println("");
        for (String item : deq) {
            StdOut.println(item);
        }

        StdOut.println("");
        StdOut.println("empty: " + deq.isEmpty());
        StdOut.println("");

        StdOut.println(deq.removeFirst());
        StdOut.println(deq.removeLast());
        StdOut.println(deq.removeFirst());
        StdOut.println(deq.removeLast());
        StdOut.println(deq.removeFirst());

        StdOut.println("");
        StdOut.println("empty: " + deq.isEmpty());
    }
}
