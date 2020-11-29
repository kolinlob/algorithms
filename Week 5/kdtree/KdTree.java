/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-11-25
 *  Description: A mutable data type that uses a 2d-tree to build a binary
 *  search tree with points in the nodes, using the x- and y-coordinates of the
 *  points as keys in strictly alternating sequence.
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class KdTree {
    private static final boolean VERTICAL = true;
    private Node root;

    public KdTree() {
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("called insert() with a null p");
        if (contains(p)) return;

        root = insert(root, p, VERTICAL, new RectHV(0.0, 0.0, 1.0, 1.0));
    }

    private Node insert(Node node, Point2D point, boolean orientation, RectHV rect) {
        if (node == null)
            return new Node(point, orientation, rect, 1);
        else {
            int compare = orientation == VERTICAL
                          ? Double.compare(point.x(), node.point.x())
                          : Double.compare(point.y(), node.point.y());

            if (compare < 0)
                node.lb = insert(node.lb, point, !orientation, lbRect(node));
            else
                node.rt = insert(node.rt, point, !orientation, rtRect(node));

            node.size = 1 + size(node.lb) + size(node.rt);
            return node;
        }
    }

    private RectHV lbRect(Node parent) {
        return parent.orientation == VERTICAL
               ? new RectHV(parent.rect.xmin(),
                            parent.rect.ymin(),
                            parent.point.x(),
                            parent.rect.ymax())
               :
               new RectHV(parent.rect.xmin(),
                          parent.rect.ymin(),
                          parent.rect.xmax(),
                          parent.point.y());
    }

    private RectHV rtRect(Node parent) {
        return parent.orientation == VERTICAL
               ? new RectHV(parent.point.x(),
                            parent.rect.ymin(),
                            parent.rect.xmax(),
                            parent.rect.ymax())
               :
               new RectHV(parent.rect.xmin(),
                          parent.point.y(),
                          parent.rect.xmax(),
                          parent.rect.ymax());
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("called contains() with a null p");
        return get(root, p) != null;
    }

    private Point2D get(Node node, Point2D point) {
        if (point == null) throw new IllegalArgumentException("calls get() with a null point");
        if (node == null) return null;

        if (node.point.equals(point))
            return point;

        int compare = node.orientation == VERTICAL
                      ? Double.compare(point.x(), node.point.x())
                      : Double.compare(point.y(), node.point.y());

        if (compare < 0)
            return get(node.lb, point);

        return get(node.rt, point);
    }

    public void draw() {
        draw(root);
    }

    private void draw(Node node) {
        if (node == null)
            return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.point.x(), node.point.y());

        StdDraw.setPenRadius();
        if (node.orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
        }

        draw(node.lb);
        draw(node.rt);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("called range() with null rect");

        ArrayList<Point2D> result = new ArrayList<Point2D>();

        if (size() == 0)
            return result;

        range(root, VERTICAL, rect, result);

        return result;
    }

    private void range(Node node, boolean orientation, RectHV rect, ArrayList<Point2D> res) {
        if (node == null)
            return;

        Point2D splitter = orientation == VERTICAL
                           ? new Point2D(node.point.x(), rect.ymin())
                           : new Point2D(rect.xmin(), node.point.y());

        if (rect.contains(splitter)) {
            range(node.lb, !orientation, rect, res);
            range(node.rt, !orientation, rect, res);
        }
        else if (orientation == VERTICAL)
            range(rect.xmax() < node.point.x() ? node.lb : node.rt, !orientation, rect, res);
        else
            range(rect.ymax() < node.point.y() ? node.lb : node.rt, !orientation, rect, res);

        if (rect.contains(node.point))
            res.add(node.point);
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("called nearest() with null p");

        if (size() == 0)
            return null;

        return nearest(root, p, null);
    }

    private Point2D nearest(Node node, Point2D p, Point2D n) {
        if (node == null)
            return n;

        Point2D nearest = n;

        if (nearest == null || node.point.distanceSquaredTo(p) < nearest.distanceSquaredTo(p))
            nearest = node.point;

        if (node.rect.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
            if (node.orientation == VERTICAL) {
                if (p.x() < node.point.x()) {
                    nearest = nearest(node.lb, p, nearest);
                    nearest = nearest(node.rt, p, nearest);
                }
                else {
                    nearest = nearest(node.rt, p, nearest);
                    nearest = nearest(node.lb, p, nearest);
                }
            }
            else {
                if (p.y() < node.point.y()) {
                    nearest = nearest(node.lb, p, nearest);
                    nearest = nearest(node.rt, p, nearest);
                }
                else {
                    nearest = nearest(node.rt, p, nearest);
                    nearest = nearest(node.lb, p, nearest);
                }
            }
        }

        return nearest;
    }

    private class Node {
        private final Point2D point;
        private final boolean orientation;
        private final RectHV rect;
        private Node lb, rt;
        private int size;

        public Node(Point2D point, boolean orientation, RectHV rect, int size) {
            this.point = point;
            this.orientation = orientation;
            this.rect = rect;
            this.size = size;
        }
    }


    public static void main(String[] args) {
        KdTree kdtree = new KdTree();

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println();

        Point2D point = new Point2D(0.5, 0.5);
        StdOut.println("contains point " + point + ":\t" + kdtree.contains(point));
        StdOut.println("insert point " + point);

        kdtree.insert(point);
        StdOut.println();

        StdOut.println("contains point " + point + ":\t" + kdtree.contains(point));

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println();

        StdOut.println("insert same point " + point);
        kdtree.insert(point);

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println();

        Point2D smol = new Point2D(0.1, 0.1);

        StdOut.println("contains point " + smol + ":\t" + kdtree.contains(smol));
        StdOut.println("insert point " + smol);

        kdtree.insert(smol);

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println("contains point " + smol + ":\t" + kdtree.contains(smol));
        StdOut.println();

        Point2D big = new Point2D(0.70, 0.85);

        StdOut.println("contains point " + big + ":\t" + kdtree.contains(big));
        StdOut.println("insert point " + big);

        kdtree.insert(big);

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println("contains point " + big + ":\t" + kdtree.contains(big));
        StdOut.println();

        Point2D onemore = new Point2D(0.27, 0.15);

        StdOut.println("contains point " + onemore + ":\t" + kdtree.contains(onemore));
        StdOut.println("insert point " + onemore);

        kdtree.insert(onemore);

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println("contains point " + onemore + ":\t" + kdtree.contains(onemore));
        StdOut.println();

        Point2D last = new Point2D(0.75, 0.67);

        StdOut.println("contains point " + last + ":\t" + kdtree.contains(last));
        StdOut.println("insert point " + last);

        kdtree.insert(last);

        StdOut.println("size:\t\t\t\t" + kdtree.size());
        StdOut.println("is empty:\t\t\t" + kdtree.isEmpty());
        StdOut.println("contains point " + last + ":\t" + kdtree.contains(last));
        StdOut.println();

        kdtree.draw();
        StdDraw.show();

        StdOut.println("range: ");

        for (Point2D p : kdtree.range(new RectHV(0, 0, 0.5, 0.5)))
            StdOut.println(p);
    }
}
