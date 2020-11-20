/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-18
 *  Description: Immutable data type that models an n-by-n board with sliding
 *  tiles
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class Board {
    private final int[][] tiles;
    private final int n;
    private int row1;
    private int col1;
    private int row2;
    private int col2;
    private final int hammingDistance;
    private final int manhattanDistance;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("argument 'tiles' is null");
        }

        if (tiles.length < 2) {
            throw new IllegalArgumentException("board too small");
        }

        this.n = tiles.length;
        this.tiles = new int[n][n];

        for (int r = 0; r < n; r++) {
            if (tiles[r].length != n) {
                throw new IllegalArgumentException("non square board");
            }

            this.tiles[r] = tiles[r].clone();
        }

        row1 = StdRandom.uniform(n);
        col1 = StdRandom.uniform(n);

        row2 = StdRandom.uniform(n);
        col2 = StdRandom.uniform(n);

        hammingDistance = calculateHammingDistance();
        manhattanDistance = calculateManhattanDistance();
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    private int calculateHammingDistance() {
        int distance = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int current = tiles[row][col];

                if (current == 0)
                    continue;

                distance += (current == row * n + col + 1) ? 0 : 1;
            }
        }

        return distance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    private int calculateManhattanDistance() {
        int distance = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int current = tiles[row][col];
                if (current == 0)
                    continue;

                int target = row * n + col + 1;
                if (current == target)
                    continue;

                int expectedRow = (current - 1) / n;
                distance += Math.abs(expectedRow - row);
                int expectedCol = current - 1 - expectedRow * n;
                distance += Math.abs(expectedCol - col);
            }
        }

        return distance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.hammingDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }

        if (y == null) {
            return false;
        }

        if (this.getClass() != y.getClass()) {
            return false;
        }

        return Arrays.deepEquals(tiles, ((Board) y).tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();
        int emptyRow = 0;
        int emptyCol = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (tiles[row][col] == 0) {
                    emptyRow = row;
                    emptyCol = col;
                    break;
                }
            }
        }

        if (emptyRow > 0) {
            int[][] arr = copy(this.tiles);
            arr[emptyRow][emptyCol] = arr[emptyRow - 1][emptyCol];
            arr[emptyRow - 1][emptyCol] = 0;

            neighbors.enqueue(new Board(arr));
        }

        if (emptyRow < n - 1) {
            int[][] arr = copy(this.tiles);
            arr[emptyRow][emptyCol] = arr[emptyRow + 1][emptyCol];
            arr[emptyRow + 1][emptyCol] = 0;

            neighbors.enqueue(new Board(arr));
        }

        if (emptyCol > 0) {
            int[][] arr = copy(this.tiles);
            arr[emptyRow][emptyCol] = arr[emptyRow][emptyCol - 1];
            arr[emptyRow][emptyCol - 1] = 0;

            neighbors.enqueue(new Board(arr));
        }

        if (emptyCol < n - 1) {
            int[][] arr = copy(this.tiles);
            arr[emptyRow][emptyCol] = arr[emptyRow][emptyCol + 1];
            arr[emptyRow][emptyCol + 1] = 0;

            neighbors.enqueue(new Board(arr));
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] copy = copy(tiles);

        while (tiles[row1][col1] == 0) {
            row1 = StdRandom.uniform(n);
            col1 = StdRandom.uniform(n);
        }

        while (tiles[row2][col2] == 0 || row2 - row1 + col2 - col1 == 0) {
            row2 = StdRandom.uniform(n);
            col2 = StdRandom.uniform(n);
        }

        copy[row2][col2] = tiles[row1][col1];
        copy[row1][col1] = tiles[row2][col2];
        return new Board(copy);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] arr1 = {
                { 1, 3 },
                { 2, 0 },
                };

        int[][] arr2 = {
                { 2, 6, 4 },
                { 7, 1, 3 },
                { 5, 8, 0 }
        };

        int[][] arr3 = {
                { 8, 1, 3 },
                { 4, 0, 2 },
                { 7, 6, 5 }
        };

        int[][] arr4 = {
                { 2, 6, 4, 9 },
                { 7, 1, 3, 10 },
                { 5, 8, 11, 12 },
                { 13, 14, 15, 0 }
        };

        StdOut.println("----constructor+hamming+manhattan----");

        for (int[][] test : new int[][][] { arr1, arr2, arr3, arr4 }) {
            Board board = new Board(test);
            StdOut.print("board: " + board);
            StdOut.println("hamming: " + board.hamming());
            StdOut.println("manhattan: " + board.manhattan());
            StdOut.println();

            StdOut.println("--------neighbors---------");
            for (Board b : board.neighbors()) {
                StdOut.println(b);
            }
        }

        StdOut.println("--------equals----------");
        Board board1 = new Board(arr3);
        Board board2 = new Board(arr3);
        StdOut.println("board1.equals(board2): " + board1.equals(board2));

        Board board3 = new Board(arr1);
        StdOut.println("board1.equals(board3): " + board1.equals(board3));

        StdOut.println("---------twin----------");
        StdOut.println("board3: " + board3);
        StdOut.println("board3 twin: " + board3.twin());
        StdOut.println("board3 twin: " + board3.twin());
        StdOut.println("board3 twin: " + board3.twin());

        Board board4 = new Board(new int[][] {
                { 1, 3 },
                { 2, 0 }
        });
        StdOut.println("board4: " + board4);
        StdOut.println("board4 twin: " + board4.twin());
        StdOut.println("board4 twin: " + board4.twin());
        StdOut.println("board4 twin: " + board4.twin());
    }

    private int[][] copy(int[][] oldBoard) {
        int[][] newBoard = new int[n][n];
        for (int r = 0; r < n; r++) {
            newBoard[r] = oldBoard[r].clone();
        }
        return newBoard;
    }
}
