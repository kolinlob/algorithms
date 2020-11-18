/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-08
 *  Description: A randomized generic queue is similar to a stack or queue,
 *  except that the item removed is chosen uniformly at random among items
 *  in the data structure.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private int size;
    private Item[] arr;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        arr = (Item[]) new Object[2];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size <= 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (size > 0 && size == arr.length) {
            resize(size * 2);
        }

        arr[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int idx = StdRandom.uniform(0, size);

        Item item = arr[idx];
        Item last = arr[--size];

        arr[idx] = last;
        arr[size] = null;

        if (size > 0 && size == arr.length / 4) {
            resize(arr.length / 2);
        }

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int idx = StdRandom.uniform(0, size);

        Item item = arr[idx];

        return item;
    }

    private class RandomIterator<T> implements Iterator<T> {
        private int iterated = 0;
        private final int[] shuffledIndexes;
        private final RandomizedQueue<T> rq;

        public RandomIterator(RandomizedQueue<T> rq) {
            this.rq = rq;

            shuffledIndexes = new int[rq.size];
            for (int i = 0; i < shuffledIndexes.length; i++) {
                shuffledIndexes[i] = i;
            }

            StdRandom.shuffle(shuffledIndexes);
        }

        public boolean hasNext() {
            return iterated < size;
        }

        public T next() {
            if (!hasNext()) throw new NoSuchElementException();

            return rq.arr[shuffledIndexes[iterated++]];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomIterator<Item>(this);
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];

        for (int i = 0; i < size; i++) {
            copy[i] = arr[i];
        }

        arr = copy;
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> pawPatrolRQ = new RandomizedQueue<String>();

        pawPatrolRQ.enqueue("Marshall");
        pawPatrolRQ.enqueue("Rubble");
        pawPatrolRQ.enqueue("Chase");
        pawPatrolRQ.enqueue("Rocky");
        pawPatrolRQ.enqueue("Zuma");
        pawPatrolRQ.enqueue("Skye");

        StdOut.println("size: " + pawPatrolRQ.size());
        StdOut.println("empty: " + pawPatrolRQ.isEmpty());
        StdOut.println("");

        for (String pup : pawPatrolRQ) {
            StdOut.println(pup);
            StdOut.println(pawPatrolRQ.sample());
        }

        StdOut.println("");

        StdOut.println(pawPatrolRQ.dequeue());
        StdOut.println(pawPatrolRQ.dequeue());
        StdOut.println(pawPatrolRQ.dequeue());
        StdOut.println(pawPatrolRQ.dequeue());
        StdOut.println(pawPatrolRQ.dequeue());
        StdOut.println(pawPatrolRQ.dequeue());

        StdOut.println("size: " + pawPatrolRQ.size());
        StdOut.println("empty: " + pawPatrolRQ.isEmpty());
    }
}
