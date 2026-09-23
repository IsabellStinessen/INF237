import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/h9cyc6/submissions/19423381

class ColoringGraphs {
    static int N;                   // vertices
    static Integer[] order;         // ordered list of indices with most neighbours
    static int[] adjMask;           // bitmask representation of graph
    static int[] colourMask;        // bitmask per colour
    
    // check if vertex v can use colour c
    static boolean canUse(int v, int c){
        return (adjMask[v] & colourMask[c]) == 0;
        /*
            adjMask[c]      --> bitmask of all neighbours of v
            colourMask[c]   --> bitmask of all vertices already coloured c
            
            So, we check if there is an overlap, meaning that a neighbour of v already have colour c
        */
    }
    
    // Try to colour vertex v
    static boolean canColour(int i, int maxColour){
        if (i == N) return true; // all vertices coloured
        
        int v = order[i];
        
        for (int c = 0; c < maxColour; c++){
            if (canUse(v, c)){
                colourMask[c] |= (1 << v); // add
                
                if (canColour(i + 1, maxColour)) return true;
                
                colourMask[c] ^= (1 << v); // remove / backtrack
            }
        }
        return false;
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("exponential-time/coloring-graphs/ColoringGraphs.in")
        ); 

        N = Integer.parseInt(br.readLine().trim());
        
        // Build adjacency bitmasks:
        adjMask = new int[N];
        for (int i = 0; i < N; i++){
            String parts[] = br.readLine().trim().split(" ");
            
            for (String p : parts){
                int v = Integer.parseInt(p);
                adjMask[i] |= (1 << v); // add v to bitMask[i]
            }
        }
        // Order vertices by degree:
        order = new Integer[N];
        for (int i = 0; i < N; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.bitCount(adjMask[b]) - Integer.bitCount(adjMask[a]));  // num of 1-bits -> degrees
        
        // Try increasing num of colours:
        for (int c = 1; c <= N; c++){
            colourMask = new int[c];

            if (canColour(0, c)) {
                System.out.print(c);
                return;
            }
        }
    br.close();
    }
}