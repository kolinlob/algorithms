/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-18
 *  Description: Immutable data-type implementing an A* search to solve n-by-n
 *  slider puzzles
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    private final Stack<Board> solution;
    private final int moves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("initial board is null");
        }

        Queue<Board> gameTree = new Queue<Board>();
        MinPQ<Node> pq = new MinPQ<Node>(new ByPriority());
        MinPQ<Node> twinpq = new MinPQ<Node>(new ByPriority());

        pq.insert(new Node(initial, null));
        twinpq.insert(new Node(initial.twin(), null));

        Node current = pq.delMin();
        Node twinCurrent = twinpq.delMin();

        gameTree.enqueue(current.board);

        while (!current.board.isGoal() && !twinCurrent.board.isGoal()) {
            for (Board neighBoard : current.board.neighbors()) {
                if (current.previous != null && neighBoard.equals(current.previous.board))
                    continue;

                gameTree.enqueue(neighBoard);
                pq.insert(new Node(neighBoard, current));
            }

            for (Board twinBoard : twinCurrent.board.neighbors()) {
                if (twinCurrent.previous != null && twinBoard.equals(twinCurrent.previous.board))
                    continue;

                twinpq.insert(new Node(twinBoard, twinCurrent));
            }

            current = pq.delMin();
            twinCurrent = twinpq.delMin();
            gameTree.enqueue(current.board);
        }

        if (current.board.isGoal()) {
            moves = current.moves;

            solution = new Stack<Board>();
            while (current != null) {
                solution.push(current.board);
                current = current.previous;
            }
        }
        else {
            moves = -1;
            solution = null;
        }
    }

    private class ByPriority implements Comparator<Node> {
        public int compare(Node that, Node another) {
            return Integer.compare(that.board.manhattan() + that.moves,
                                   another.board.manhattan() + another.moves);
        }
    }

    private class Node {
        private final Board board;
        private final int moves;
        private final Node previous;

        Node(Board board, Node previous) {
            this.board = board;
            this.previous = previous;

            int m = 0;
            Node prev = this.previous;
            while (prev != null) {
                m++;
                prev = prev.previous;
            }
            this.moves = m;
        }

        public String toString() {
            String sb = "priority  = " + (this.board.manhattan() + this.moves) + "\n"
                    + "moves     = " + this.moves + "\n"
                    + "manhattan = " + this.board.manhattan() + "\n"
                    + "board     = " + this.board;
            return sb;
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solution != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    // test client
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();

        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
