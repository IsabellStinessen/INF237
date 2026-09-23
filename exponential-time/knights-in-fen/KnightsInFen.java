import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/h9cyc6/submissions/19446021

/*
    Parts of program:
        - goalState()
        - knightMoves()
        - static class State
        - encode()
        - bfs()
        - main()
*/

class KnightsInFen{
 
    // for each pos on board, store valid knight moves
    static List<Integer>[] moves = new ArrayList[25];
    // bitmask representation of goal pos for white and black knights
    static int goalWhite;
    static int goalBlack;
    
    // Compute coal state --> initialized once in main()
    static void goalState() {
        int[] gW = {0, 1, 2, 3, 4, 6, 7, 8, 9, 13, 14, 19};
        int[] gB = {5, 10, 11, 15, 16, 17, 18, 20, 21, 22, 23, 24};
        
        for (int w : gW){
            goalWhite |= (1 << w);
        }
        for (int b : gB){
            goalBlack |= (1 << b);
        }
    }
    
    // Precompute knight moves --> initializes once in main()
    static void knightMoves() { 
        // lu, ld, ul, dl, ur, dr, ru, rd   (left, up, right, down - respectively)
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};
        
        for (int i = 0; i < 25; i++){
            moves[i] = new ArrayList<>();
            int x = i/5;    // row
            int y = i%5;    // col
            
            for (int d = 0; d < 8; d++){
                int nx = x + dx[d];     // new possible pos
                int ny = y + dy[d];
                
                if (nx >= 0 && nx < 5 && ny >= 0 && ny < 5){
                    moves[i].add(nx * 5 + ny);  // add correct index
                }
            }
        }
    }
    
    // Represents a board state in BFS:
    static class State{
        int w, b;   // bitmasks for white and black pieces
        int empty;  // pos of empty square
        int steps;  // num of moves taken
        
        State(int w, int b, int e, int s) {
            this.w = w;         
            this.b = b;
            this.empty = e;
            this.steps = s;
        }
    }
    
    // Encode state into single long for HashSet:
    static long encode(int w, int b, int empty){
        // shoft w and b to avoid overlap
        return (((long) w) << 30) | ((long) b) << 5 | empty;
    }
    
    // BFS to find min moves to reach goal state
    static int bfs(int w, int b, int empty){
        Queue<State> q = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        
        // start with initial state
        q.add(new State(w, b, empty, 0));
        visited.add(encode(w, b, empty));
        
        while (!q.isEmpty()){
            State cur = q.poll();
            
            // stop exploting if unsolvable in < 11 moves
            if (cur.steps > 10) continue;
            
            // check if goal state is reached
            if (cur.w == goalWhite && cur.b == goalBlack && cur.empty == 12){
                return cur.steps;  
            }
            
            // check all knight moves from cur empty pos
            for (int next : moves[cur.empty]){
                int nw = cur.w;
                int nb = cur.b;
                
                // Move piece from next into empty square
                if ((nw & (1 << next)) != 0) {
                    // white piece move
                    nw ^= (1 << next);          // remove from old pos
                    nw |= (1 << cur.empty);     // add to empty pos
                } else {
                    // black piece move
                    nb ^= (1 << next);
                    nb |= (1 << cur.empty);
                }
                
                // new empty pos becomes "next"
                long key = encode(nw, nb, next);
                
                // if state hasen't been seen before, explore it
                if (!visited.contains(key)){
                    visited.add(key);
                    q.add(new State (nw, nb, next, cur.steps + 1));
                }
            }
        }
        // no solution within 10 moves
        return -1;
    }
    
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("exponential-time/knights-in-fen/KnightsInFen.in")
        ); 

        // initialize goal state and valid knight moves
        goalState();
        knightMoves();
        
        int n = Integer.parseInt(br.readLine().trim()); // num of input sets
        
        // For each input set:
        for (int i = 0; i < n; i++){
            int whitemask = 0;
            int blackmask = 0;
            int empty = 0;      // pos of empty space
            
            // Original board
            for (int r = 0; r < 5; r++){        // row
                String line = br.readLine();
                for (int c = 0; c < 5; c++){    // char
                    int p = r*5 + c;            // pos
                    char ch = line.charAt(c);
                    
                     if (ch == ' ') {           // empty space
                        empty = p;
                    } else if (ch == '1'){
                        whitemask |= (1 << p);  // add to bitmask
                    } else {
                        blackmask |= (1 << p); 
                    }
                }
            }
            
            // Run bfs to find min moves
            int result = bfs(whitemask, blackmask, empty);
            
            if (result == -1){
                System.out.println("Unsolvable in less than 11 move(s).");
            } else {
                System.out.println("Solvable in " + result + " move(s).");
            }
        }
    br.close();
    }
}