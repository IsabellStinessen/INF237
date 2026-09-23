import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/txoawj/submissions/19670044

class Chewbacca {
    static long n;  // nodes
    static long k;  // out-degree (max children per node)
    static int q;   // num of pairs

    // Finding parent of node in k-ary tree:
        // used to find LCA
    static long parent(long x){
        return (x - 2) / k + 1;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(
            new FileReader("numbers-and-trees/chewbacca/Chewbacca.in")
        );         
        StringBuilder sb = new StringBuilder("");
        String[] parts = br.readLine().trim().split(" ");

        n = Long.parseLong(parts[0]);
        k = Long.parseLong(parts[1]);
        q = Integer.parseInt(parts[2]);

        for (int i = 0; i < q; i++){
            String[] pair = br.readLine().trim().split(" ");

            long x = Long.parseLong(pair[0]);
            long y = Long.parseLong(pair[1]);

            // straight line from root to leaf
            if (k == 1){
                sb.append(Math.abs(x - y)).append("\n"); // aboslute distance
                continue;
            }

            int steps = 0;

            // move up til we find lowest common anscestor (LCA)
            while (x != y){
                if (x > y){
                    x = parent(x);
                } else {
                    y = parent(y);
                }
                steps++;
            }

            sb.append(steps).append("\n");
        }
        System.out.println(sb);
        br.close();
    }
}
