/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2021-01-07
 *  Description: Implementation of a fundamental data structure, which describes
 *  the abstraction of a sorted array of the n circular suffixes of a string of
 *  length n
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    private final CircularSuffix[] suffixes;
    private final char[] text;

    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("null argument");

        text = s.toCharArray();

        int n = s.length();
        this.suffixes = new CircularSuffix[n];
        for (int i = 0; i < n; i++)
            suffixes[i] = new CircularSuffix(i);
        Arrays.sort(suffixes);
    }

    public int length() {
        return suffixes.length;
    }

    public int index(int i) {
        if (i < 0 || i >= suffixes.length)
            throw new IllegalArgumentException("out of range");

        return suffixes[i].index;
    }

    public static void main(String[] args) {
        String s = args[0];
        CircularSuffixArray a = new CircularSuffixArray(s);

        StdOut.printf("length: %d\n", a.length());

        int c = 0;
        for (CircularSuffix suffix : a.suffixes) {
            StdOut.printf("%2d | %s | %2d\n", c, suffix, a.index(c++));
        }
    }

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final int index;

        private CircularSuffix(int index) {
            this.index = index;
        }

        public int compareTo(CircularSuffix that) {
            if (this == that) return 0;
            for (int i = 0; i < text.length; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return 0;
        }

        public String toString() {
            return new String(text, index, text.length - index) + new String(text, 0, index);
        }

        private char charAt(int i) {
            return text[(index + i) % text.length];
        }
    }
}
