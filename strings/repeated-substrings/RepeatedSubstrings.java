import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/m4i6d2/submissions/19641955

class RepeatedSubstring {
    /*
    Builds the suffix array for the string
    (https://www.geeksforgeeks.org/dsa/suffix-array-set-2-a-nlognlogn-algorithm/)
        
        SuffixArray[i] stores the starting index of the i-th 
        suffix in lexiographical order
    */
    static int[] buildSuffixArray(String s){
        int n = s.length();

        // suffixArray stores suffix starting pos
        Integer[] suffixArray = new Integer[n];

        // rank[i] -> curr rank of suffix starting at i
        int[] rank = new int[n];

        // temp arr used when recalculating ranks
        int[] tempRank = new int[n];

        // Initially, rank suff by their first char
        for (int i = 0; i < n; i++){
            suffixArray[i] = i;
            rank[i] = s.charAt(i);
        }

        /*
        Sort suffixes by first 1, 2, 4, 8, ... chars.
        Each suff is compared using
            1. curr rank
            2. rank of the suffix len chars ahead 
        */
        for (int len = 1; len < n; len *= 2){
            final int currLen = len;

            Arrays.sort(suffixArray, (a,b) -> {
                if (rank[a] != rank[b]){
                    return Integer.compare(rank[a], rank[b]);
                }
                int nextRankA = (a + currLen < n) ? rank[a + currLen] : -1;
                int nextRankB = (b + currLen < n) ? rank[b + currLen] : -1;

                return Integer.compare(nextRankA, nextRankB);
            });

            // The first suff in sorted order gets rank 0
            tempRank[suffixArray[0]] = 0;

            // Assign new ranks after sorting
            for (int i = 1; i < n; i++){
                int prev = suffixArray[i - 1];
                int curr = suffixArray[i];

                boolean diff = (
                    rank[prev] != rank[curr] ||
                    ((prev + currLen < n ? rank[prev + currLen] : -1)
                    != (curr + currLen < n ? rank[curr + currLen] : -1))
                );
                tempRank[curr] = tempRank[prev] + (diff ? 1 : 0);
            }
            
            // copy new ranks back into rank
            for (int i = 0; i < n; i++){
                rank[i] = tempRank[i];
            }
            // If all suffixes have unique ranks, array is complete
            if (rank[suffixArray[n-1]] == n - 1){
                break;
            }
        }

        // Convert Integer[] to int[]
        int[] result = new int[n];

        for (int i = 0; i < n; i++){
            result[i] = suffixArray[i];
        }

        return result;
    }


    /*
    Build Longest Common Prefix using Kasai's algorithm
    (https://www.geeksforgeeks.org/dsa/kasais-algorithm-for-construction-of-lcp-array-from-suffix-array/)

        lcp[i] stores the longest common prefix length between:
        suffixArray[i] and suffixArray[i + 1]
    */
    static int[] buildLCP(String s, int[] suffixArray){
        int n = suffixArray.length;

        int[] lcp = new int[n];

        // rank[i] -> where suff starting at i appears in suffixArray
        int[] rank = new int[n];

        for (int i = 0; i < n; i++){
            rank[suffixArray[i]] = i;
        }

        int k = 0;

        for (int i = 0; i < n; i++){
            if (rank[i] == n - 1){
                k = 0;
                continue;
            }

            int nextSuff = suffixArray[rank[i] + 1];

            // Compare chars while they match
            while (i + k < n && 
                nextSuff + k < n && 
                s.charAt(i + k) == s.charAt(nextSuff + k)
            ){
                k++;
            }
            lcp[rank[i]] = k;

            /* 
            Next comparison can reuse k - 1 chars,
            because we move one char forwars in the original string
            */
            if (k > 0) k--;
        }
        return lcp;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("strings/repeated-substrings/RepeatedSubstrings.in")
        ); 

        String word = br.readLine().trim();
        int n = word.length();

        int[] suffixArray = buildSuffixArray(word);
        int[] lcp = buildLCP(word, suffixArray);

        // largest LCP value in the len of the longest repeated substring
        int maxLen = 0;

        for (int x : lcp){
            if (x > maxLen){
                maxLen = x;
            }
        }

        /*
        Since sufficArray is sorted leciographically,
        the first suffix with LCP = maxLen gives the
        lex smallest longest repeated substring 
        */
        for (int i = 0; i < n-1; i++){
            if (lcp[i] == maxLen){
                int start = suffixArray[i];
                System.out.println(word.substring(start, start + maxLen));
                return;
            }
        }
    br.close();
    }
}
