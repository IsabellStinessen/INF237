import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/mvvh6g/submissions/19598391

/*
    - Find convex hull for pine trees and aspens using Andrew's monotone chain algorithm.
    - Use Sutherland-Hodgman polygon clipping to find intersection of the two convex hulls.
    - Calculate area of the intersection polygon using shoelace formula.
*/
class ForestEvolution{
    static int P;               // pine trees
    static int A;               // aspens
    static Point[] pines;       // coords of pine trees
    static Point[] aspens;      // coords of aspen trees
    static Point[] pineHull;    // convex hull of pine trees
    static Point[] aspenHull;   // convex hull of aspen trees
    
    static class Point{
        double x, y;
        Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    
    // Calculate orientation of Points
    static int orientation(Point a, Point b, Point c){
        double v = a.x * (b.y - c.y) + 
                b.x * (c.y - a.y) + 
                c.x * (a.y - b.y);
        
        if (v < 0) return -1;   // clockwise
        if (v > 0) return 1;    // counter-clockwise
        return 0;               // collinear
    }
    
    // Calculate squared Euclidean distance between 2 points
    static double distSq(Point a, Point b){
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return dx * dx + dy * dy;
    }
    
    // Find convex hull of given set of Points
    static Point[] convexHull(Point[] pts){
        int n = pts.length;
        
        // Sort points by leftmost point. If tie, sort by lowest point
        Arrays.sort(pts, (a, b) -> {
            if (a.x != b.x) return Double.compare(a.x, b.x);
            return Double.compare(a.y, b.y);
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
        int size = hull.size();             // where lower hull ends
        for (int i = n - 2; i >= 0; i--){
            Point p = pts[i];
            while (hull.size() > size &&
                orientation(hull.get(hull.size()-2), hull.get(hull.size()-1), p) <= 0){
                    hull.remove(hull.size()-1);
            }
            hull.add(p);
        }
        
        hull.remove(hull.size() - 1);   // remove duplicate

        // // Ensure hull is in counter-clockwise order
        if (hull.size() >= 3 && orientation(hull.get(0), hull.get(1), hull.get(2)) < 0){
            Collections.reverse(hull);
        }
        
        return hull.toArray(new Point[0]);
    }
    
    // Calculate area of polygon using shoelace formula
    static double calculateArea(Point[] pts){
        double area = 0;
        int n = pts.length;
        for (int i = 0; i < n; i++){
            Point a = pts[i];               // current point
            Point b = pts[(i+1) % n];       // next point (wrap around when i = n-1)
            area += (a.x * b.y) - (b.x * a.y);  // signed area of parallelogram spanned by (0,0), a, b
        }
        return Math.abs(area) / 2.0;
    }

    // Find intersection point of infinite lines (a -> b) and (c -> d)
    static Point intersect(Point a, Point b, Point c, Point d){
        // Line (a -> b) represented as A1x + B1y = C1
        double A1 = b.y - a.y;
        double B1 = a.x - b.x;
        double C1 = A1 * a.x + B1 * a.y;

        // Line (c -> d) represented as A2x + B2y = C2
        double A2 = d.y - c.y;
        double B2 = c.x - d.x;
        double C2 = A2 * c.x + B2 * c.y;

        double det = A1 * B2 - A2 * B1;
        if (Math.abs(det) < 1e-9) return null; // parallel lines

        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;
        return new Point(x, y);     // intersection point
    }

    // Check if point is inside or on the left of line (a -> b)
    static boolean isInside(Point a, Point b, Point p){
        return orientation(a, b, p) >= 0; // left or on the line
    }

    /*
    Sutherland-Hodgman polygon clipping algorithm:
        - subject: polygon to be clipped (pine hull)
        - clip: convex polygon to clip against (aspen hull)
        - For each edge (a,b) in clip, iterate through edges (p,q) in subject:
            - If q is inside or left of line (a -> b):
                - If p is outside or right of line (a -> b), add intersection of (p,q) with (a,b) to output
                - Add q to output
            - Else if p is inside or left of line (a -> b), add intersection of (p,q) with (a,b) to output
    */
    static Point[] clipping(Point[] subject, Point[] clip){
        // Initial output: all points in subject polygon
        List<Point> output = new ArrayList<>(Arrays.asList(subject));

        int n = clip.length;
        for (int i = 0; i < n; i++){
            if (output.isEmpty()) break; // no intersection

            Point a = clip[i];
            Point b = clip[(i + 1) % n];

            List<Point> input = new ArrayList<>(output);
            output.clear();

            int m = input.size();
            if (m == 0) break;
            for (int j = 0; j < m; j++){
                Point p = input.get(j);
                Point q = input.get((j + 1) % m);

                if (isInside(a, b, q)){         // q is inside or left of line (a -> b)
                    if (!isInside(a, b, p)){    // p is outside or right of line (a -> b)
                        Point inter = intersect(p, q, a, b);
                        if (inter != null) output.add(inter);
                    }
                    output.add(q);
                } else if (isInside(a, b, p)){  // p is inside or left of line (a -> b) but q is outside
                    Point inter = intersect(p, q, a, b);
                    if (inter != null) output.add(inter);
                }
            }
        }

        if (output.size() < 3 || clip.length < 3) return new Point[0]; // no valid polygon
        return output.toArray(new Point[0]);
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("geometry/forest-evolution/ForestEvolution.in")
        ); 

        String[] parts = br.readLine().trim().split(" ");
        
        P = Integer.parseInt(parts[0]);
        A = Integer.parseInt(parts[1]);
        pines = new Point[P];
        aspens = new Point[A];
        
        // Cannot form polygon, so area is 0:
        if (P < 3 || A < 3){
            System.out.print("0.0");
            return;
        }
        
        // Read input for pine trees:
        for (int i = 0; i < P; i++){ 
            String[] coords = br.readLine().trim().split(" ");
            
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            
            pines[i] = new Point(x, y);
        }
        // Read input for aspen trees:
        for (int i = 0; i < A; i++){ 
            String[] coords = br.readLine().trim().split(" ");
            
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            
            aspens[i] = new Point(x, y);
        }
        
        // Convex hull for pine trees and aspen trees:
        pineHull = convexHull(pines);
        aspenHull = convexHull(aspens);
        
        // Find intersection of the two convex hulls:
        Point[] intersection = clipping(pineHull, aspenHull);
        // Calculate area of the intersection polygon:
        double area = calculateArea(intersection);
        
        if (intersection.length < 3){ 
            System.out.print("0.0");
        }
        else {
            System.out.printf("%.20f\n", area);
        }
        br.close();
    }
}