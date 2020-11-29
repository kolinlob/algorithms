/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-25
 *  Description: A mutable data type that represents a set of points in the unit
 *  square. Implemented with use of red–black binary search tree.
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {

    private final SET<Point2D> set;

    public PointSET() {
        set = new SET<Point2D>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        set.add(p);
    }

    public boolean contains(Point2D p) {
        return set.contains(p);
    }

    public void draw() {
        for (Point2D p : set)
            StdDraw.point(p.x(), p.y());
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("called range() with a null rect");

        SET<Point2D> intersection = new SET<Point2D>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                intersection.add(p);
            }
        }

        return intersection;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("called nearest() with a null p");

        if (set.isEmpty())
            return null;

        Point2D minPoint = null;
        for (Point2D point : set) {
            if (point.equals(p))
                return point;

            double distance = p.distanceSquaredTo(point);
            if (minPoint == null) {
                minPoint = point;
                continue;
            }

            if (distance < p.distanceSquaredTo(minPoint)) {
                minPoint = point;
            }
        }

        return minPoint;
    }


    public static void main(String[] args) {
        PointSET points = new PointSET();

        StdOut.println("is empty: " + points.isEmpty());

        StdOut.println("inserting 10 points");

        points.insert(new Point2D(0.206107, 0.095492));
        points.insert(new Point2D(0.975528, 0.654508));
        points.insert(new Point2D(0.024472, 0.345492));
        points.insert(new Point2D(0.793893, 0.095492));
        points.insert(new Point2D(0.793893, 0.904508));
        points.insert(new Point2D(0.975528, 0.345492));
        points.insert(new Point2D(0.206107, 0.904508));
        points.insert(new Point2D(0.500000, 0.000000));
        points.insert(new Point2D(0.024472, 0.654508));
        points.insert(new Point2D(0.500000, 1.000000));

        StdOut.println("is empty: " + points.isEmpty());
        StdOut.println("size: " + points.size());

        StdOut.println();
        StdOut.println("------------------------------------------------");
        StdOut.println();

        Point2D center = new Point2D(0.5, 0.5);
        StdOut.println("min point to center: " + points.nearest(center));

        StdOut.println();
        StdOut.println("------------------------------------------------");
        StdOut.println();

        RectHV query = new RectHV(0.00, 0.12, 0.78, 0.89);
        StdOut.println("selected rectangle: " + query);

        int count = 0;
        for (Point2D p : points.range(query)) {
            StdOut.println(p);
            count++;
        }

        StdOut.println("intersects with " + count + " points");

        StdOut.println();
        StdOut.println("---END-OF-TRANSITION---");
        StdOut.println();
    }
}
