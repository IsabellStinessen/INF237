import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/ffwbjj/submissions/19250474

class Knapsack{
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("dynamic-programming/knapsack/Knapsack.in"));

        // unknown num of cases
        while(sc.hasNextInt()){
            int c = sc.nextInt(); // capacity of knapsack
            int n = sc.nextInt(); // items in knapsack
            
            int[] V = new int[n]; // values
            int[] W = new int[n]; // weights
            
            for(int i = 0; i < n; i++){
                int v = sc.nextInt();   // value
                int w = sc.nextInt();   // weight
                
                V[i] = v;
                W[i] = w;
            }
            knapsack(V, W, c, n);
        }
        sc.close();
    }
    
    public static void knapsack(int[] V, int[] W, int c, int n){
            // stores max value achievable using first i items with j cap
        int[][] mem = new int[n+1][c+1];
        
        // finding collection with best value
        for (int i = 1; i <= n; i++){      // iterate through items
            for (int j = 0; j <= c; j++){  // iterate through capacities
                if (W[i-1] <= j){ // if item's weight <= current cap
                        // val + max val from remaining cap
                    int include = V[i-1] + mem[i-1][j-W[i-1]];
                        // best val from prev items
                    int exclude = mem[i-1][j];
                    mem[i][j] = Math.max(include, exclude);
                } else{
                    mem[i][j] = mem[i-1][j];
                }
            }
        }
        
        // finding indices for said items
        int i = n;
        int j = c;
        List<Integer> indices = new ArrayList<>();
        
        while (i > 0 && j > 0) {
            if (mem[i][j] != mem[i-1][j]){  // value changed -> new item added
                indices.add(i-1);
                j -= W[i-1];
            }
            i--;
        }
        
        /*  OUTPUT:
            For each test case:
                line 1: n chosen items
                line 2: indices of chosen items
        */
        System.out.println(indices.size());
        // printed in reverse, but doesn't matter
        for (int in : indices) System.out.print(in + " ");
        System.out.println();
    }
}