/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2021-01-07
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            String line = BinaryStdIn.readString();
            CircularSuffixArray csa = new CircularSuffixArray(line);

            int first = 0;
            char[] transformed = new char[csa.length()];

            for (int i = 0; i < csa.length(); i++) {
                if (csa.index(i) == 0)
                    first = i;

                int charIndex = (csa.index(i) + csa.length() - 1) % csa.length();
                transformed[i] = line.charAt(charIndex);
            }

            BinaryStdOut.write(first);
            BinaryStdOut.write(new String(transformed));
            BinaryStdOut.flush();
        }
    }

    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            char[] line = BinaryStdIn.readString().toCharArray();
            
            int[] next = new int[line.length];

            int[] count = new int[R + 1];
            for (int i = 0; i < line.length; i++)
                count[line[i] + 1]++;

            for (int r = 0; r < R; r++)
                count[r + 1] += count[r];

            for (int i = 0; i < line.length; i++)
                next[count[line[i]]++] = i;

            int x = next[first];
            do {
                BinaryStdOut.write(line[x]);
                x = next[x];
            }
            while (x != next[first]);
            BinaryStdOut.flush();
        }
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String command = args[0];

        if (command.equals("-"))
            BurrowsWheeler.transform();

        if (command.equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}
