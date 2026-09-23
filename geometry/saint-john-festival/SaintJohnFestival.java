import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/mvvh6g/submissions/19598264

/*
    - Find convex hull of large sky lanterns using Andrew's monotone chain algortihm.
    - Check if each small sky lantern is inside the convex hull using binary search and triangle check.
*/

class SaintJohnFestival{
    static int L;           // num of large sky lanterns
    static int S;           // num of small sky lanterns
    static Point[] large;   // coords of large lanterns
    static Point[] small;   // coords of small lanterns

    static class Point{
        int x, y;
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    
    // Calculate orientation of Points
    static int orientation(Point a, Point b, Point c){
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

    /*
    Check if point p is inside convex hull using binary search and triangle check:
        - Ensure p lies between (hull[0] -> hull[1]) and (hull[0] -> hull[n-1]). If not, return false.
        - Binary search to find the sector of the hull that p is in. We want to find largest i (left) such that orientation(hull[0], hull[i], p) >= 0
        - Check if p is to the left of line from hull[i] to hull[i+1]. If so, return true. Else, return false.
    */
    static boolean isInside(Point[] hull, Point p){
        int n = hull.length;
        Point p0 = hull[0];     // anchor point

        // Outside wedge
        if (orientation(p0, hull[1], p) < 0) return false;      // check if p is to the right of line from p0 to hull[1]
        if (orientation(p0, hull[n - 1], p) > 0) return false;  // check if p is to the left of line from p0 to hull[n-1]

        // Binary search
        int left = 1, right = n - 1;
        while (right - left > 1){
            int mid = (left + right) / 2;
            if (orientation(p0, hull[mid], p) >= 0){    // if p is to the left of line from p0 to hull[mid], move left up
                left = mid;
            } else {
                right = mid;                            // if p is to the right of line from p0 to hull[mid], move right down
            }
        }

        // Triangle check
        return orientation(hull[left], hull[left + 1], p) >= 0;     // check if p is to the left of line from hull[left] to hull[left+1]
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("geometry/saint-john-festival/SaintJohnFestival.in")
        );         
        
        int answer = 0;
        
        // Read input for large lanterns:
        L = Integer.parseInt(br.readLine().trim());
        large = new Point[L];
        
        for (int i = 0; i < L; i++){
            String[] coords = br.readLine().trim().split(" ");
            
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            
            large[i] = new Point(x, y);
        }
        
        // Read input for small lanterns;
        S = Integer.parseInt(br.readLine().trim());
        small = new Point[S];
         
        for (int i = 0; i < S; i++){
            String[] coords = br.readLine().trim().split(" ");
            
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            
            small[i] = new Point(x, y);
        }    
        
        // Get convex hull of large lanterns
        Point[] hull = convexHull(large);

        // Ensure hull is in counter-clockwise order
        if (orientation(hull[0], hull[1], hull[2]) < 0){   
            Collections.reverse(Arrays.asList(hull));
        }
        
        // Check if each small lantern is inside the convex hull of large lanterns
        for (int i = 0; i < S; i++){
            if (isInside(hull, small[i])) answer++;
        }
        
        System.out.print(answer);
        br.close();
    }
}