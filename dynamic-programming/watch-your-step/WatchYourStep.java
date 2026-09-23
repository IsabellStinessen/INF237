import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/ffwbjj/submissions/19250546

class WatchYourStep{
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("dynamic-programming/watch-your-step/WatchYourStep.in"));
        
        int xi = sc.nextInt();  // initial coordinates
        int yi = sc.nextInt();
        int xf = sc.nextInt();  // final coordinates
        int yf = sc.nextInt();
        
        int m1x = sc.nextInt(); // coordinates 1. mine
        int m1y = sc.nextInt();
        int m2x = sc.nextInt(); // coordinates 2. mine
        int m2y = sc.nextInt();
        
        int dx = xf-xi;     // distance x-axis
        int dy = yf-yi;     // distance y-axis
        
        long[][] grid = new long[dx+1][dy+1];
        grid[0][0] = 1;
        
        int mine1x = m1x - xi;  // 1. mine pos on grid
        int mine1y = m1y - yi;
        int mine2x = m2x - xi;  // 2. mine pos on grid
        int mine2y = m2y - yi;
        
        for(int x = 0; x <= dx; x++){
            for(int y = 0; y <= dy; y++){
                if ((x == mine1x && y == mine1y) ||
                     (x == mine2x && y == mine2y)){
                         grid[x][y] = 0;    // don't count pos w/mine
                         continue;
                }
                
                if (x > 0){
                    grid[x][y] += grid[x-1][y]; // path right
                }
                if (y > 0){
                    grid[x][y] += grid[x][y-1]; // path up
                }
            }
        }
        
        System.out.print(grid[dx][dy]);
        sc.close();
    }
}