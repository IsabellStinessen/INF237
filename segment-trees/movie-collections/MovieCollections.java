import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/dmyo7v/submissions/19364666

/*
OBS: I have also solved this problem using Fenwick trees, which was a bit faster.
However, as the curriculum of the week was about Segments Trees and SQRT decomposition,
I though I should solve at least one of the problems using this.
*/

class MovieCollections{
    
    /*
    Segment Tree that stores counts of movies at positions in the stack.
    Each leaf represents a position and store either:
        0 --> no movie at this position
        1 --> a movie at this position
        
    Internal nodes store the sum of their children, allowing to quickly
    compute how many movies exist in a given range.
    */
    static class SegmentTree{
        int n;          // num of leaves
        int[] tree;     // tree array (size 2*n)
        
        public SegmentTree(int[] ar){
            n = ar.length;
            tree = new int[n*2];
            
            // copy input array into leaf layer of tree [n ... 2n-1]
            System.arraycopy(ar, 0, tree, n, n);
            
            // Build tree bottom up: each parent stores sum of its two children
            for (int i = n-1; i > 0; i--){
                tree[i] = tree[2*i] + tree[2*i + 1];
            }
        }
        /*
        Update a single position in array.
        Change the leaf value, and recompute all ancestors
        */
        public void update(int i, int value){
            i += n;             // move to leaf position
            tree[i] = value;
            int newValue;
            
            // Move up the tree updating parent nodes
            while(i>1){
                i /= 2;
                newValue = tree[2*i] + tree[2*i + 1];
                
                if (tree[i] != newValue){
                    tree[i] = newValue;
                } else return; // stop early of nothing changes
            }
        }
        /*
        Range sum query.
        Returns num of movies in positions [from, to)
        
        Used to count how many movies are above a given movie
        */
        public int sum(int from, int to){ // from-->inclusive, to-->exclusive
            from += n;          // move to leaf position
            to += n;
            
            int sum = 0;
            
            while(from < to){
                // If from is a right child (odd), include it
                if((from & 1) == 1){
                    sum += tree[from];
                    from ++;
                }
                // If to is a right boundary (odd), include the left sibling
                if((to & 1) == 1){
                    to--;
                    sum += tree[to];
                }
                // Move to parent level
                from /= 2;
                to /= 2;
            }
            return sum;
        }
    }
    
    public static void main(String[] args) throws IOException{
        
        BufferedReader br = new BufferedReader(
            new FileReader("segment-trees/movie-collections/MovieCollections.in")
        );         
        StringBuilder sb = new StringBuilder();
        
        int t = Integer.parseInt(br.readLine()); // num of test cases
        
        // For each test case
        while (t-- > 0){
            
            StringTokenizer st = new StringTokenizer(br.readLine());

            int m = Integer.parseInt(st.nextToken()); // num of movies
            int r = Integer.parseInt(st.nextToken()); // num of requests
            
            // Reserve m+r positions in stack, so we can move r movies to top
            int size = m+r;
            
            int[] pos = new int[m+1];     // pos[i] = current position of movie i
            int[] arr = new int[size+1];  // initial array used to build segment tree
            
            /*
            Initial stack layout:
                - pos 1..2          --> empty (reserved)
                - pos r+1...r+m     --> movies
            */
            for (int i = 1; i <= m; i ++){
                pos[i] = r + i;
                arr[pos[i]] = 1;
            }
            
            // Build segment tree from arr
            SegmentTree seg = new SegmentTree(arr);
            
            // next available position at the top of the stack
            int nextFree = r;
            
            // Process movie requests
            st = new StringTokenizer(br.readLine());
            for (int i = 0; i < r; i++){
                int movie = Integer.parseInt(st.nextToken());
                
                int p = pos[movie];     // current pos of movie
                
                /*
                Count how many movies are above the one requested
                    --> num of movies in positions [1 ... p-1]
                    */
                int above = seg.sum(1, p);
                sb.append(above).append(" ");

                // Remove the movie from its current position
                seg.update(p, 0);

                // Move the movie to the top of the stack
                pos[movie] = nextFree;
                seg.update(nextFree, 1);

                nextFree--; // next top position
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
        br.close();
    }
}