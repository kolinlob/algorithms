/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-08
 *  Description: Permutation takes an integer k as a command-line argument;
 *  reads a sequence of strings from standard input using StdIn.readString();
 *  and prints exactly k of them, uniformly at random.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        
        while (!StdIn.isEmpty()) {
            rq.enqueue(StdIn.readString());
        }

        for (int i = 0; i < k; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}
