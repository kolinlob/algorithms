/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-16
 *  Description: A faster, sorting-based solution. Given a point p, the
 *  following method determines whether p participates in a set of 4 or more
 *  collinear points.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    private int segmentsCount = 0;
    private LineSegment[] segments = new LineSegment[4];

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        Point[] po = new Point[points.length];

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("null argument at index " + i);
            }

            for (int j = i + 1; j < points.length; j++) {
                if (points[j] == null) {
                    throw new IllegalArgumentException("null argument at index " + j);
                }

                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("duplicated point: " + points[j]);
                }
            }
            po[i] = points[i];
        }

        Point[] tmp = new Point[4];

        for (int p = 0; p < po.length; p++) {
            Point[] copy = Arrays.copyOf(po, po.length);
            Arrays.sort(copy, po[p].slopeOrder());
            int length = 1;

            for (int q = 1; q < copy.length; q++) {
                if (p > q) continue;

                if (po[p].slopeTo(copy[q]) == po[p].slopeTo(copy[q - 1])) {
                    length++;
                }
                else {
                    if (length >= 3) {
                        if (segmentsCount > 0 && segments.length == segmentsCount) {
                            segments = resize(segmentsCount * 2, segments);
                        }

                        Arrays.sort(copy, q - length, q);
                        // LineSegment newline = new LineSegment(po[p], copy[q - 1]);
                        // segments[segmentsCount++] = newline;
                        for (int e = 0; e < segmentsCount; e += 2) {

                        }
                    }
                    length = 1;
                }
            }

            if (length >= 3) {
                if (segmentsCount > 0 && segments.length == segmentsCount) {
                    segments = resize(segmentsCount * 2, segments);
                }

                Arrays.sort(copy, copy.length - length, copy.length);
                segments[segmentsCount++] = new LineSegment(po[p], copy[copy.length - 1]);
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return segmentsCount;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segments, segmentsCount);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        FastCollinearPoints collinear = new FastCollinearPoints(points);

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            StdDraw.setPenColor(StdDraw.RED);
            p.draw();
            // StdDraw.pause(50);
        }
        StdDraw.show();

        // print and draw the line segments
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            StdDraw.setPenColor(StdDraw.BLUE);
            segment.draw();
            // StdDraw.pause(500);
            StdDraw.show();
        }
    }

    private LineSegment[] resize(int capacity, LineSegment[] arr) {
        LineSegment[] copy = new LineSegment[capacity];

        for (int i = 0; i < arr.length; i++) {
            copy[i] = arr[i];
        }

        return copy;
    }
}
