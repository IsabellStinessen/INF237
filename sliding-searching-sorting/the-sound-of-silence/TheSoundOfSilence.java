import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/kotchz/submissions/19202396

class TheSoundOfSilence{
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("sliding-searching-sorting/the-sound-of-silence/TheSoundOfSilence.in"));
        StringBuilder resultSB = new StringBuilder(); // SB for shorter run-time
        
        /*
        Silene - sequence of *m* samples where |hi|-|lo| <= c
        */
        
        int n = sc.nextInt(); // number of samples
        int m = sc.nextInt(); // required length of silence
        int c = sc.nextInt(); // max noise level dif within silence
        
        int[] samples = new int[n];     // stores values
            // *Stored here with i<=0, but problem expects x<=1
                // so will have to account for that later
        for(int i = 0; i < n; i++){
            samples[i] = sc.nextInt();
        }
        
        Deque<Integer> minDeque = new ArrayDeque<>();   // stores indices
        Deque<Integer> maxDeque = new ArrayDeque<>();  
        
        for(int i = 0; i < n; i++){
            // remove indices x in minDeque where samples[x] >= samples[i]
                // so smallest is always first
            while(!minDeque.isEmpty() && samples[minDeque.peekLast()] >= samples[i]){
                minDeque.pollLast();
            }
            minDeque.addLast(i);
            
            // remove indices x in maxDeque where samples[x] <= samples[i]
                // so biggest is always first
            while(!maxDeque.isEmpty() && samples[maxDeque.peekLast()] <= samples[i]){
                maxDeque.pollLast();
            }
            maxDeque.addLast(i);
            
            // remove indices outside of window
            if(i >= m) {
                while(!minDeque.isEmpty() && minDeque.peekFirst() <= i-m){
                    minDeque.pollFirst();
                }
                while(!maxDeque.isEmpty() && maxDeque.peekFirst() <= i-m){
                    maxDeque.pollFirst();
                }
            }
            
            // check window ending at i
            if (i >= m-1) {
                int min = samples[minDeque.peekFirst()];
                int max = samples[maxDeque.peekFirst()];
                if (max-min <= c) {
                    // add starting index (1-based)
                    int index = i-m+2;
                    resultSB.append(index).append("\n");
                }
            }
        }
        
        
        if (resultSB.length() == 0) resultSB.append("NONE");
        System.out.print(resultSB.toString());
        
        sc.close();
    }
}