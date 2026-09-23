package geometry.undetected;

import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/cgavhf/submissions/19400410

class UnDetected{
    /*
    UnionFind to keep track if overlapping circles (sensors)
        - minX: leftmost point on x-axis for sensors in union
        - maxX: rightmost point on x-axis for sensors in union
            - minX & maxX of root is used to check whether there is a possible path
    */
    static class UnionFind{
        int[] parent;
        int[] minX;
        int[] maxX;
        
        UnionFind(int size){
            parent = new int[size];
            minX = new int[size];
            maxX = new int[size];
            for (int i = 0; i < size; i++) parent[i] = i; // initialize all as their own parent
        }
        
        public int find(int i){
            if (parent[i] != i) {
                parent[i] = find(parent[i]);
            }
            return parent[i];
        }
        public void union(int i, int j){
            int irep = find(i);
            int jrep = find(j);
            
            parent[irep] = jrep;
            // update minX & maxX for root:
            minX[jrep] = Math.min(minX[irep], minX[jrep]);
            maxX[jrep] = Math.max(maxX[irep], maxX[jrep]);
        }
    }
    
    // Calculate distance between to circle centres:
    static double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    // Check whether two circles overlap:
    static boolean circlesOverlap(double d, int r1, int r2){
        return d < r1 + r2;
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("geometry/undetected/UnDetected.in")
        ); 

        int n = Integer.parseInt(br.readLine().trim()); // sensors
        int[][] c = new int[n][3]; // array of circles (x, y, r)
        
        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i++){
            String[] parts = br.readLine().trim().split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int r = Integer.parseInt(parts[2]);
            c[i][0] = x; 
            c[i][1] = y; 
            c[i][2] = r; 
            
            // minX & maxX of circle:
            uf.minX[i] = x-r;
            uf.maxX[i] = x+r;
            
            // check if it overlaps with prior circles 
            for (int j = 0; j < i; j++){
                double d = distance(x, y, c[j][0], c[j][1]);
                if(circlesOverlap(d, r, c[j][2])){
                    uf.union(i, j);
                }
            }
            
            int root = uf.find(i); // root always has updates minX & maxX
            if (uf.minX[root] <= 0 && uf.maxX[root] >= 200){ // no path
                System.out.print(i);
                return;
            }
        }
        System.out.print(n); // if all circles allow for path
        br.close();
    }
}