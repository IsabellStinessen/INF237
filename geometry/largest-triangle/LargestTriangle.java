import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/mvvh6g/submissions/19598438

/*
    - Find convex hull of points using Andrew's monotone chain algorithm.
    - Use rotating calipers method to find largest triangle in convex hull.
*/

public class LargestTriangle {
    static int n;

    static class Point{
        int x, y;
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    // Calculate area of triangle formed by 3 points
    static double triangleArea(Point a, Point b, Point c){
        return Math.abs((long)a.x * (b.y - c.y) + 
                        (long)b.x * (c.y - a.y) + 
                        (long)c.x * (a.y - b.y)) / 2.0;
    }

    // Calculate orientation of Points
    static long orientation(Point a, Point b, Point c){
        long v = (long)a.x * (b.y - c.y) + 
                (long)b.x * (c.y - a.y) + 
                (long)c.x * (a.y - b.y);
        
        if (v < 0) return -1;   // clockwise
        if (v > 0) return 1;    // counter-clockwise
        return 0;               // collinear
    }
    
    // Calculate squared Euclidean distance between 2 points
    static int distSq(Point a, Point b){
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        return dx * dx + dy * dy;
    }
    
    // Find convex hull of given set of Points (used for large lanterns)
    static Point[] convexHull(Point[] pts){
        int n = pts.length;

        // Sort points by leftmost point. If tie, sort by lowest point
        Arrays.sort(pts, (a, b) -> {
            if (a.x != b.x) return Integer.compare(a.x, b.x);
            return Integer.compare(a.y, b.y);
        });

        ArrayList<Point> hull = new ArrayList<>();

        /*
        Lower hull:
            Iterate from left to right and build bottom part of hull.
            If we have >= 2 points in hull and the last 2 points in hull and current point do not make a left turn, 
            remove last point from hull
        */
        for (Point p : pts){
            while (hull.size() >= 2 &&
                orientation(hull.get(hull.size()-2), hull.get(hull.size()-1), p) <= 0){
                hull.remove(hull.size()-1);
            }
            hull.add(p);
        }

        /*
        Upper hull:
            Iterate from right to left and build upper part of hull.
            If we have > size points in hull and the last 2 points in hull and current point do not make a left turn,
            remove last point from hull
        */
        int size = hull.size();     // where lower hull ends
        for (int i = n - 2; i >= 0; i--){
            Point p = pts[i];
            while (hull.size() > size &&
                orientation(hull.get(hull.size()-2), hull.get(hull.size()-1), p) <= 0){
                hull.remove(hull.size()-1);
            }
            hull.add(p);
        }

        hull.remove(hull.size() - 1); // remove duplicate

        return hull.toArray(new Point[0]);
    }
        
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("geometry/largest-triangle/LargestTriangle.in")
        ); 

        n = Integer.parseInt(br.readLine().trim());

        // Read input for points:
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++){
            String[] parts = br.readLine().trim().split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            points[i] = new Point(x, y);
        }
        
        // Convex hull of points:
        Point[] hull = convexHull(points);
        
        double result = 0;
        n = hull.length;
        
        /* 
        Rotating calipers method to find largest triangle in convex hull:
            - fix points i and j, and find point k that maximizes area of triangle (i, j, k)
            - iterate through points in order and move k forward while area increases
        */
        for (int i = 0; i < n-2; i++) {
            int k = i + 2;

            for (int j = i + 1; j < n-1; j++) {
                if (k <= j) k = j + 1;
                
                while (k + 1 < n && 
                        triangleArea(hull[i], hull[j], hull[k+1]) > triangleArea(hull[i], hull[j], hull[k])) {
                    k++;
                }

                result = Math.max(result, triangleArea(hull[i], hull[j], hull[k]));
            }
        }
        System.out.print(result);
        br.close();
    }
}
