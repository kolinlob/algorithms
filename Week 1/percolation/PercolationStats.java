/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96d;
    private final double[] openSitesWhenPercolated;
    private final int trials;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer");
        }

        if (trials <= 0) {
            throw new IllegalArgumentException("trials must be a positive integer");
        }

        this.trials = trials;

        openSitesWhenPercolated = new double[trials];

        for (int t = 0; t < trials; t++) {
            Percolation perc = new Percolation(n);

            while (!perc.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);

                if (!perc.isOpen(row, col)) {
                    perc.open(row, col);
                }
            }

            openSitesWhenPercolated[t] = perc.numberOfOpenSites() / (double) (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(openSitesWhenPercolated);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(openSitesWhenPercolated);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ((CONFIDENCE_95 * stddev()) / Math.sqrt(trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ((CONFIDENCE_95 * stddev()) / Math.sqrt(trials));
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("usage: java-algs4 PercolationStats 200 100");
        }

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(n, trials);

        System.out.printf("mean\t\t\t\t= %.10f%n", ps.mean());
        System.out.printf("stddev\t\t\t= %.10f%n", ps.stddev());
        System.out.printf("95%% confidence interval\t=[%.10f, %.10f]%n",
                          ps.confidenceLo(),
                          ps.confidenceHi());
    }
}
