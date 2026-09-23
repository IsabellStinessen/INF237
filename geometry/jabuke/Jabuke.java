package geometry.jabuke;

import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/cgavhf/submissions/19400335

class Jabuke{
    
    // Using Barycentric Coordinate Method to check if whether a given point is inside a triangle
        // https://www.geeksforgeeks.org/dsa/check-whether-a-given-point-lies-inside-a-triangle-or-not/
    static boolean insideTriangle(int[][] c, int Px, int Py){
        int xA = c[0][0], yA = c[0][1];
        int xB = c[1][0], yB = c[1][1];
        int xC = c[2][0], yC = c[2][1];
        
        double denom = ((yB-yC) * (xA - xC) + (xC - xB) * (yA - yC));
        double a = ((yB - yC) * (Px - xC) + (xC - xB) * (Py - yC)) / denom;
        double b = ((yC - yA) * (Px - xC) + (xA - xC) * (Py - yC)) / denom;
        double d = 1 - a - b; // arr called c 
        
        double eps = 1e-9; // fix floating point rounding
        
        if (Math.abs(denom) < eps) return false; // zero area
        
        return (a >= -eps && b >= -eps && d >= -eps);
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("geometry/jabuke/Jabuke.in")
        );         
        int count = 0;
        
        int[][] c = new int[3][2]; // coordinates of vertices --> to calculate area
        for(int i = 0; i<3; i++){
            String[] parts = br.readLine().trim().split(" ");
            c[i][0] = Integer.parseInt(parts[0]);
            c[i][1] = Integer.parseInt(parts[1]);
        }
        
        // Calculate area of triangle:
        double area = Math.abs(
            c[0][0] * (c[1][1] - c[2][1]) +
            c[1][0] * (c[2][1] - c[0][1]) +
            c[2][0] * (c[0][1] - c[1][1])
            ) / 2.0;
        System.out.println(String.format("%.1f", area)); // style: one digit after decimal point
           
            
        int n = Integer.parseInt(br.readLine().trim()); // num of trees
        for (int i = 0; i < n; i++){
            String[] parts = br.readLine().trim().split(" ");
            
            int a = Integer.parseInt(parts[0]); // coords for apple tree
            int b = Integer.parseInt(parts[1]);
            
            if(insideTriangle(c, a, b)) count++;
        }
        System.out.print(count);
        br.close();
    }
}