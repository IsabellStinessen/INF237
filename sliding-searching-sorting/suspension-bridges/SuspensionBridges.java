import java.util.*;
import java.io.*;

class Main{
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("sliding-searching-sorting/suspension-bridges/SuspensionBridges.in"));
        
        double d = (double) sc.nextInt();   // horizontal distance
        double s = (double) sc.nextInt();   // vertical sag / lowest point
        
        // Checks for *a* between very small (positive) and very large number
        double left = 1e-6;
        double right = 1e6;
        
        // Binary search for a:
            // Equation for a: a + s = a * cosh(d/2a)
            // Aim: f(a) = (a * cosh(d/2a)) - (a + s) = 0
        for (int i = 0; i < 100; i++){
            double mid = (left+right) / 2.0;
            double f = mid * Math.cosh(d / (2.0*mid));
            if (f > mid + s) left = mid;   // a too small
            else right = mid;              // a too large
        }
        
        double a = (left + right) / 2.0;
        
        // Length(a,d) = 2a * sinh(d/2a)
        double cableLength = (2.0*a) * Math.sinh(d / (2.0*a));
        
        System.out.print(cableLength);
        sc.close();
    }
    
}