import java.lang.StringBuilder;
import java.io.*;
import java.util.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/dmyo7v/submissions/19364249

class JustForSidekicks{
    /*
    Fewick Tree / Binary Indexed Tree
    
    Store count of each gem instead of value, so query to happens in O(1) time.
    Maintain 6 Fenwick trees, one for each gem type.
    */

    // Add value k at position i in a Fenwick tree
    static void add(int i, int k, int[] tree){
        while (i < tree.length) {
            tree[i] += k;
            i += i & -i;
        }
    }
    // Compute prefix sum from index 1 to i
    static int sumPoint(int i, int[] tree){
        int sum = 0;
        while (i > 0){
            sum += tree[i];
            i -= i & -i;
        }
        return sum;
    }
    // Compute prefix sum in range [from, to]
    static int sumPart(int from, int to, int[] tree){
        return sumPoint(to, tree) - sumPoint(from-1, tree);
    }
    
    public static void main(String[] args) throws IOException{
        // Faster input/output than Scanner and printing before all is calculated
        BufferedReader br = new BufferedReader(
            new FileReader("segment-trees/just-for-sidekicks/JustForSidekicks.in")
        );        
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder("");
        
        int n = Integer.parseInt(st.nextToken());
        int q = Integer.parseInt(st.nextToken());
        
        // valueType[i] stores current value for gem type i
        st = new StringTokenizer(br.readLine());
        int[] valueTypes = new int[7]; //V1-V6
        for (int i = 1; i <=6; i++){
            valueTypes[i] = Integer.parseInt(st.nextToken());
        }
        
        // initial gem types
        String s = br.readLine();
        
        // gemType[i] --> gem type at position i
        int[] gemType = new int[n+1];
        
        // tree[i] --> Fenwick tree storing counts of gem of type i
        int[][] tree = new int[7][n+1];
        
        // initialize gem types and populate Fenwick trees
        for (int i = 0; i < n; i++){
            int type = s.charAt(i) - '0'; // convert char --> int
            gemType[i+1] = type;
            
            // increase count of this type at this position
            add(i + 1, 1, tree[type]);
        }
            
        // Process queries
        for (int i = 0; i < q; i++){
            st = new StringTokenizer(br.readLine());
            int query = Integer.parseInt(st.nextToken());
            
            // Query 1: change gem at pos k to type p
            if (query == 1){
                int k = Integer.parseInt(st.nextToken());
                int p = Integer.parseInt(st.nextToken());
                
                int oldType = gemType[k];
                
                if (oldType != p){
                    // remove gem from old type tree
                    add(k, -1, tree[oldType]);
                    // add gem to new type tree
                    add(k, 1, tree[p]);
                    // update stored type
                    gemType[k] = p;
                }
            } 
            // Query 2: change the value of all gems of type p to value v
            else if (query == 2){ 
                int p = Integer.parseInt(st.nextToken());
                int v = Integer.parseInt(st.nextToken());
                
                valueTypes[p] = v;
            } 
            // Query 3: compute the total value of gems in range [from, to]
            else{ 
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                
                long sum = 0;
                
                /*
                For each gem type:
                    1. calculate how many gems of that type exists in range
                    2. multiply the value of that type
                */
                for (int t = 1; t <= 6; t++){
                    int count = sumPart(from, to, tree[t]);
                    sum += (long) count * valueTypes[t];
                }
                
                sb.append(sum).append("\n");
            }
        }
        System.out.print(sb.toString());
        br.close();
    }
}