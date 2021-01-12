/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2021-01-07
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] ascii = new char[R];
        for (int i = 0; i < ascii.length; i++)
            ascii[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();

            int index = 0;
            for (int i = 0; i < ascii.length; i++)
                if (ascii[i] == c) {
                    index = i;
                    BinaryStdOut.write(i, 8);
                    break;
                }

            for (int j = index; j > 0; j--) {
                char t = ascii[j - 1];
                ascii[j - 1] = ascii[j];
                ascii[j] = t;
            }

            BinaryStdOut.flush();
        }
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] ascii = new char[R];
        for (int i = 0; i < ascii.length; i++)
            ascii[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar(8);

            int index = 0;
            for (int i = 0; i < ascii.length; i++)
                if (ascii[c] == i) {
                    index = c;
                    BinaryStdOut.write(ascii[index]);
                    break;
                }

            for (int j = index; j > 0; j--) {
                char t = ascii[j - 1];
                ascii[j - 1] = ascii[j];
                ascii[j] = t;
            }

            BinaryStdOut.flush();
        }
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String command = args[0];

        if (command.equals("-"))
            MoveToFront.encode();

        if (command.equals("+"))
            MoveToFront.decode();
    }
}
