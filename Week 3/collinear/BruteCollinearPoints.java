/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-15
 *  Description: Simple brute force algorithm that examines 4 points at a time
 *  and checks whether they all lie on the same line segment, returning all such
 *  line segments.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BruteCollinearPoints {
    private int segmentsCount = 0;
    private LineSegment[] segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        Point[] po = new Point[points.length];
        segments = new LineSegment[4];

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

        for (int p = 0; p < po.length; p++) {
            for (int q = 0; q < po.length; q++) {
                if (q <= p) continue;
                for (int r = 0; r < po.length; r++) {
                    if (r <= q) continue;
                    for (int s = 0; s < po.length; s++) {
                        if (s <= r) continue;

                        double slope1 = po[p].slopeTo(po[q]);
                        double slope2 = po[p].slopeTo(po[r]);

                        if (slope1 < slope2 || slope1 > slope2) {
                            continue;
                        }

                        double slope3 = po[p].slopeTo(po[s]);
                        if (slope2 < slope3 || slope2 > slope3) {
                            continue;
                        }

                        Point[] tuple = {
                                po[p], po[q], po[r], po[s]
                        };

                        Arrays.sort(tuple);

                        LineSegment segment = new LineSegment(tuple[0], tuple[3]);

                        if (segmentsCount > 0 && segmentsCount == segments.length) {
                            segments = resize(segmentsCount * 2, segments);
                        }

                        segments[segmentsCount++] = segment;
                    }
                }
            }
        }
    }

    public int numberOfSegments() {
        return segmentsCount;
    }

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

        BruteCollinearPoints collinear = new BruteCollinearPoints(points);

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            StdDraw.setPenColor(StdDraw.RED);
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            StdDraw.setPenColor(StdDraw.BLUE);
            segment.draw();
        }
        StdDraw.show();
    }

    private LineSegment[] resize(int capacity, LineSegment[] arr) {
        LineSegment[] copy = new LineSegment[capacity];

        for (int i = 0; i < arr.length; i++) {
            copy[i] = arr[i];
        }

        return copy;
    }
}
