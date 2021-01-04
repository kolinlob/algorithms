/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2021-01-02
 *  Description: Immutable Boggle solver that finds all valid words in a given
 *  Boggle board, using a given dictionary
 *
 *              |  0  |  1  |  2  |  3
 *           ---|-----|-----|-----|-----
 *            0 |  A  |  T  |  E  |  E
 *           ---|-----|-----|-----|-----
 *            1 |  A  |  P  |  Y  |  O
 *           ---|-----|-----|-----|-----
 *            2 |  T  |  I  |  N  |  U
 *           ---|-----|-----|-----|-----
 *            3 |  E  |  D  |  S  |  E
 *           ---|-----|-----|-----|-----
 *              |  0  |  1  |  2  |  3
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver {
    private final TST<Integer> dict;

    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException("null dictionary");

        dict = new TST<>();
        for (int i = 0; i < dictionary.length; i++)
            dict.put(dictionary[i], i);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException();

        SET<String> words = new SET<String>();

        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                boolean[][] visited = new boolean[board.rows()][board.cols()];
                dfs(row, col, visited, letter(row, col, board), words, board);
            }
        }

        return words;
    }

    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;
        int lng = word.length();
        if (lng < 3) return 0;
        if (lng < 5) return 1;
        if (lng < 6) return 2;
        if (lng < 7) return 3;
        if (lng < 8) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int count = 1;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(count++ + ". " + word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private void dfs(int row, int col, boolean[][] visited,
                     String word,
                     SET<String> words, BoggleBoard board) {

        visited[row][col] = true;

        if (!dict.keysWithPrefix(word).iterator().hasNext()) {
            visited[row][col] = false;
            return;
        }

        if (dict.contains(word) && word.length() > 2)
            words.add(word);

        for (Pair pair : adj(row, col, board, visited)) {
            dfs(pair.i, pair.j, visited, word + letter(pair.i, pair.j, board), words, board);
        }

        visited[row][col] = false;
    }

    private Iterable<Pair> adj(int row, int col, BoggleBoard board, boolean[][] visited) {
        Queue<Pair> set = new Queue<>();

        for (int i = row - 1; i <= row + 1; i++) {
            if (i < 0 || i >= board.rows()) continue;
            for (int j = col - 1; j <= col + 1; j++) {
                if (j < 0 || j >= board.cols()) continue;
                if (i == row && j == col) continue;
                if (visited[i][j]) continue;
                set.enqueue(new Pair(i, j));
            }
        }

        return set;
    }

    private String letter(int i, int j, BoggleBoard board) {
        char letter = board.getLetter(i, j);
        return letter == 'Q' ? "QU" : Character.toString(letter);
    }

    private class Pair {
        public final int i;
        public final int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}
