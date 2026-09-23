import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/e237ce/submissions/19145338?tab=submission-history

class AutonomyReach{
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("graphs/autonomy-reach/AutonomyReach.in"));
        
        int rows = sc.nextInt();
        int cols = sc.nextInt();

        char[][] grid = new char[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        
        Queue<Integer> rowQueue = new LinkedList<>();
        Queue<Integer> colQueue = new LinkedList<>();
        
        for (int i = 0; i < rows; i++){
            char[] line = sc.next().toCharArray();
            for (int j = 0; j < cols; j++){
                char c = line[j];
                grid[i][j] = c;
                // Add starting position(s) here in case of multiple
                if(c == 'S') {
                    rowQueue.add(i);
                    colQueue.add(j);
                    visited[i][j] = true;
                }
            }
        }
        
        System.out.println(BFS(grid, visited, rowQueue, colQueue));
        sc.close();
    }
    
    public static int BFS(
        char[][] grid, 
        boolean[][] visited, 
        Queue<Integer> rowQueue, 
        Queue<Integer> colQueue
    ){
        int answer = 0;
        
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Directions for neighbour check:
        int[] dirRow = {-1, 1, 0, 0};
        int[] dirCol = {0, 0, 1, -1};
        
        while (!rowQueue.isEmpty()) {
            int row = rowQueue.poll();
            int col = colQueue.poll();
            
            if (grid[row][col] == 'P') answer++;
            
            for (int i = 0; i < 4; i++){
                int newRow = row + dirRow[i];
                int newCol = col + dirCol[i];
                
                if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) continue; // outside of grid
                if (visited[newRow][newCol]) continue;
                
                char ch = grid[newRow][newCol];
                if (ch == '#' || ch == 'W') continue;
                
                visited[newRow][newCol] = true;
                rowQueue.add(newRow);
                colQueue.add(newCol);
            }
        }
        return answer;
    }
    
}
