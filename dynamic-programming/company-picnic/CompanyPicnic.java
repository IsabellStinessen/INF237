import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/t9wkpu/submissions/19524680

public class CompanyPicnic {
    static int n;                   // num of employees
    static double[] speed;          // speed of each employee
    static List<Integer>[] tree;    // each supervisor --> list of employees
    static Result[][] dp;           // dp[x][1/0] --> best result if x is pared with parent(1) or not (0)

    static class Result{
        int pairs;
        double sum;
        Result(int pairs, double sum){
            this.pairs = pairs;
            this.sum = sum;
        }
    }

    //Compare two results:
    static Result better(Result a, Result b){
        // Priority: max pairs
        if(a.pairs != b.pairs){
            return (a.pairs > b.pairs) ? a : b;
        }
        // If equal pairs --> max sum
        return (a.sum > b.sum) ? a : b;
    }
    // Add two results:
    static Result add(Result a, Result b){
        return new Result(a.pairs + b.pairs, a.sum + b.sum);
    }

    // DFS for processing tree bottom up
    static void dfs(int u){
        dp[u][0] = new Result(0, 0); // u not matched with parent
        dp[u][1] = new Result(0, 0); // u matchet with parent

        // Process children first
        for (int v : tree[u]){
            dfs(v);
        }

        // CASE 1: u matched with parent --> cannot match with children
        Result res1 = new Result(0, 0);
        for (int v : tree[u]){
            res1 = add(res1, dp[v][0]);     // add best result for each child when not paired with u
        }
        dp[u][1] = res1;

        // CASE 2: u not matches with parent
        // OPTION A: don't match u with any children
        Result base = new Result(0, 0);
        for (int v : tree[u]){
            base = add(base, dp[v][0]);     // add best result for each child when not paired with u
        }
        Result best = base;
        
        // OPTION B: match u with ONE child
        for (int v : tree[u]){
            Result candidate = new Result(0, 0);

            // Add all other children as dp[x][0] (not matched)
            for (int x : tree[u]){
                if (x == v) continue;
                candidate = add(candidate, dp[x][0]);
            }

            // Chosen child v --> dp[v][1]
            candidate = add(candidate, dp[v][1]);

            // Add this edge
            candidate.pairs += 1;
            candidate.sum += Math.min(speed[u], speed[v]);

            best = better(best, candidate);
        }
        dp[u][0] = best;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("dynamic-programming/company-picnic/CompanyPicnic.in")
        );

        n = Integer.parseInt(br.readLine().trim());

        speed = new double[n];
        tree = new ArrayList[n];
        dp = new Result[n][2];

        String[] names = new String[n];
        String[] parentNames = new String[n];
        Map<String, Integer> nameToId = new HashMap<>();

        for (int i = 0; i < n; i++){
            tree[i] = new ArrayList<>();
        }
        // Read input
        for (int i = 0; i < n; i++){
            String[] parts = br.readLine().trim().split(" ");
            names[i] = parts[0];
            speed[i] = Double.parseDouble(parts[1]);
            parentNames[i] = parts[2];

            nameToId.put(names[i], i);
        }

        // Build tree
        int root = -1;
        for (int i = 0; i < n; i++){
            if (parentNames[i].equals("CEO")){
                root = i;
            } else {
                int parent = nameToId.get(parentNames[i]);
                tree[parent].add(i);
            }
        }

        // Run DP
        dfs(root);

        Result ans = dp[root][0];               // Root (CEO) never matched with parent
        double finalSum = ans.sum / ans.pairs;  // Average speed accross pairs

        System.out.print(ans.pairs + " " + finalSum);
        br.close();
    }
}
