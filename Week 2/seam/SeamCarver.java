/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-12-18
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SeamCarver {
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private int[][] rgb;
    private boolean transposed;

    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("picture was null");

        rgb = new int[picture.width()][picture.height()];
        for (int row = 0; row < picture.height(); row++)
            for (int col = 0; col < picture.width(); col++)
                rgb[col][row] = picture.getRGB(col, row);

        transposed = false;
    }

    public Picture picture() {
        if (transposed)
            rgb = transposeBack();

        Picture picture = new Picture(width(), height());

        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height(); row++)
                picture.setRGB(col, row, rgb[col][row]);

        return picture;
    }

    public int width() {
        return rgb.length;
    }

    public int height() {
        return rgb[0].length;
    }

    public double energy(int x, int y) {
        validateX(x);
        validateY(y);

        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return 1000.0;

        int xg = xGradient(x, y);
        int yg = yGradient(x, y);

        return Math.sqrt(xg + yg);
    }

    public int[] findVerticalSeam() {
        if (transposed)
            rgb = transposeBack();

        return findSeam(VERTICAL);
    }

    public int[] findHorizontalSeam() {
        if (!transposed)
            rgb = transpose();

        return findSeam(HORIZONTAL);
    }

    public void removeHorizontalSeam(int[] seam) {
        if (!transposed)
            rgb = transpose();

        removeSeam(seam, HORIZONTAL);

        rgb = transposeBack();
    }

    public void removeVerticalSeam(int[] seam) {
        removeSeam(seam, VERTICAL);
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver test = new SeamCarver(picture);

        int[] seamH1 = test.findHorizontalSeam();
        StdOut.printf("H seam length: %d, picture size: %d x %d\n", seamH1.length, test.width(),
                      test.height());
        test.removeHorizontalSeam(seamH1);

        int[] seamV1 = test.findVerticalSeam();
        StdOut.printf("V seam length: %d, picture size: %d x %d\n", seamV1.length, test.width(),
                      test.height());

        test.removeVerticalSeam(seamV1);

        int[] seamH2 = test.findHorizontalSeam();
        StdOut.printf("H seam length: %d, picture size: %d x %d\n", seamH2.length, test.width(),
                      test.height());
        test.removeHorizontalSeam(seamH2);

        int[] seamV2 = test.findVerticalSeam();
        StdOut.printf("V seam length: %d, picture size: %d x %d\n", seamV2.length, test.width(),
                      test.height());

        test.removeVerticalSeam(seamV2);

        StdOut.printf("residual image: %d x %d\n", test.width(), test.height());
    }

    private void validateX(int x) {
        if (x < 0 || x > width() - 1)
            throw new IllegalArgumentException(
                    "x = " + x + " is outside current picture's width of " + width());
    }

    private void validateY(int y) {
        if (y < 0 || y > height() - 1)
            throw new IllegalArgumentException(
                    "y " + y + " is outside current picture's height of " + height());
    }

    private int xGradient(int x, int y) {
        int rgbX1 = rgb[x - 1][y];
        int rgbX2 = rgb[x + 1][y];

        int dxr = ((rgbX2 >> 16) & 0xFF) - ((rgbX1 >> 16) & 0xFF);
        int dxg = ((rgbX2 >> 8) & 0xFF) - ((rgbX1 >> 8) & 0xFF);
        int dxb = ((rgbX2) & 0xFF) - ((rgbX1) & 0xFF);

        return (dxr * dxr) + (dxg * dxg) + (dxb * dxb);
    }

    private int yGradient(int x, int y) {
        int rgbY1 = rgb[x][y - 1];
        int rgbY2 = rgb[x][y + 1];

        int dyr = ((rgbY2 >> 16) & 0xFF) - ((rgbY1 >> 16) & 0xFF);
        int dyg = ((rgbY2 >> 8) & 0xFF) - ((rgbY1 >> 8) & 0xFF);
        int dyb = ((rgbY2) & 0xFF) - ((rgbY1) & 0xFF);

        return (dyr * dyr) + (dyg * dyg) + (dyb * dyb);
    }

    private int[][] transpose() {
        int[][] transposedRgb = new int[height()][width()];

        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height(); row++)
                transposedRgb[row][col] = rgb[col][row];

        transposed = true;
        return transposedRgb;
    }

    private int[][] transposeBack() {
        int[][] normal = new int[height()][width()];

        for (int col = 0; col < width(); col++)
            for (int row = height() - 1; row >= 0; row--)
                normal[row][col] = rgb[col][row];

        transposed = false;
        return normal;
    }

    private int[] findSeam(boolean vertical) {
        if (vertical && transposed)
            rgb = transposeBack();
        else if (!vertical && !transposed)
            rgb = transpose();

        if (width() <= 1) {
            int[] seam = new int[height()];
            Arrays.fill(seam, 0);
            return seam;
        }

        int[] edgeTo = new int[height()];
        double[][] distTo = new double[width()][height()];
        double[][] energy = new double[width()][height()];

        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++) {
                distTo[x][y] = Double.POSITIVE_INFINITY;
                energy[x][y] = energy(x, y);
            }

        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++) {
                distTo[x][y] = energy[x][y];

                if (y > 0)
                    distTo[x][y] +=
                            Double.min(
                                    distTo[x][y - 1],
                                    Double.min(
                                            x > 0 ?
                                            distTo[x - 1][y - 1] : Double.POSITIVE_INFINITY,
                                            x < width() - 1 ?
                                            distTo[x + 1][y - 1] : Double.POSITIVE_INFINITY));
            }

        int lastRow = height() - 1;
        double minSum = distTo[0][lastRow];

        for (int x = 0; x < width(); x++) {
            if (distTo[x][lastRow] < minSum) {
                minSum = distTo[x][lastRow];
                edgeTo[lastRow] = x;
            }
        }

        int lastCol = edgeTo[lastRow];

        for (int row = lastRow - 1; row >= 0; row--) {
            edgeTo[row] = lastCol;
            double min = distTo[lastCol][row];

            if (lastCol - 1 >= 0) {
                if (distTo[lastCol - 1][row] < min) {
                    min = distTo[lastCol - 1][row];
                    edgeTo[row] = lastCol - 1;
                }
            }

            if (lastCol + 1 <= width())
                if (distTo[lastCol + 1][row] < min)
                    edgeTo[row] = lastCol + 1;

            lastCol = edgeTo[row];
        }

        if (!vertical && transposed)
            rgb = transposeBack();

        return edgeTo;
    }

    private void removeSeam(int[] seam, boolean vertical) {

        if (seam == null)
            throw new IllegalArgumentException("seam was null");

        if (width() <= 1)
            throw new IllegalArgumentException("width is too low");

        if (seam.length != height())
            throw new IllegalArgumentException(
                    "incorrect seam length " + seam.length + " instead of " + height());

        validateX(seam[0]);

        for (int row = 1; row < height(); row++) {
            validateX(seam[row]);
            if (Math.abs(seam[row] - seam[row - 1]) > 1)
                throw new IllegalArgumentException("pixels in seam are not adjacent");
        }

        if (vertical && transposed)
            rgb = transposeBack();
        else if (!vertical && !transposed)
            rgb = transpose();

        int[][] newPic = new int[width() - 1][height()];
        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height(); row++)
                if (col < seam[row])
                    newPic[col][row] = rgb[col][row];
                else if (col > seam[row])
                    newPic[col - 1][row] = rgb[col][row];

        rgb = newPic;
    }
}
