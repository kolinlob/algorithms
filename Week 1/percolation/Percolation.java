/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int n;
    private final int vLast;

    private boolean[] grid;
    private int countOpen = 0;
    private final WeightedQuickUnionUF wuf;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        this.n = n;
        if (n < 1) {
            throw new IllegalArgumentException("n must be positive integer");
        }

        wuf = new WeightedQuickUnionUF(n * n + 2);
        grid = new boolean[n * n + 2];
        vLast = grid.length - 1;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        int ix = getIndex(row, col);
        if (!isOpen(row, col)) {
            grid[ix] = true;
            countOpen++;
            connectNeighbours(row, col);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        int ix = getIndex(row, col);
        return grid[ix];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        return wuf.find(getIndex(row, col)) == wuf.find(0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return countOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        if (wuf.find(0) == wuf.find(vLast))
            return true;

        for (int b = 1; b <= n; b++) {
            int ix = getIndex(n, b);
            if (isOpen(n, b) && wuf.find(0) == wuf.find(ix)) {
                wuf.union(ix, vLast);
                return true;
            }
        }
        return false;
    }

    // test client (optional)
    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            throw new IllegalArgumentException("usage: java-algs4 PercolationStats 200 100");
        }

        int n = Integer.parseInt(args[0]);
        int row = Integer.parseInt(args[1]);
        int col = Integer.parseInt(args[2]);

        Percolation perc = new Percolation(n);
        perc.open(row, col);
    }

    private void connectNeighbours(int row, int col) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (Math.abs(x + y) == 1) {
                    int x1 = row + x;
                    int y1 = col + y;
                    if (x1 >= 1 && x1 <= n && y1 >= 1 && y1 <= n && isOpen(x1, y1)) {
                        wuf.union(getIndex(row, col), getIndex(x1, y1));
                    }
                }
            }
        }

        if (row == 1) {
            wuf.union(getIndex(row, col), 0);
        }
    }

    private int getIndex(int row, int col) {
        return n * (validate(row) - 1) + validate(col);
    }

    private int validate(int p) {
        if (p < 1 || p > n) {
            throw new IllegalArgumentException("index " + p + " is not between 1 and " + n);
        }
        return p;
    }
}
