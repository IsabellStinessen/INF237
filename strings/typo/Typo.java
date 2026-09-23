package strings.typo;

import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/m4i6d2/submissions/19641971

class Typo{
    // Rolling hash constants
    static final long BASE = 9000007;
    static final long MIX = 1100003;

    /*
    Computes rolling hash of a full string
    64-bit rolling hash to avoid wasting time on %
    */
    static long hash(String s){
        long h = 0;

        for (int i = 0; i < s.length(); i++){
            int value = s.charAt(i) - 'a' + 1;  // 'a' -> 0, 'b' -> 1, ...
            h = h * BASE + value;
        }
        return h;
    }

    // Combines hash and length so strings of diff len don't match
    static long key(long hash, int len){
        return hash ^ (MIX * len);
    }

    /*
    Checks if shortest == longer with longer.charAt(idx) removed.
    Used only after hash match, to avoid false positives. 
    */
    static boolean equalsAfterDeleting(String shorter, String longer, int idx){
        int j = 0;

        for (int i = 0; i < longer.length(); i++){
            if (i == idx) continue;

            if (shorter.charAt(j) != longer.charAt(i)){
                return false;
            }
            j++;
        }
        return true;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("strings/typo/Typo.in")
        ); 

        int n = Integer.parseInt(br.readLine().trim());
        
        String[] words = new String[n];

        /*
        lookup stores all originall words by their hash and length.
        key(hash(word), word.length()) -> words with that hash and length
        */
        Map<Long, List<String>> lookup = new HashMap<>();
        
        int maxLen = 0;

        // Read input and build lookup table
        for (int i = 0; i < n; i++){
            String word = br.readLine().trim();

            words[i] = word;
            maxLen = Math.max(maxLen, word.length());

            long k = key(hash(word), word.length());
            lookup.computeIfAbsent(k, x -> new ArrayList<>()).add(word);
        }

        /*
        Precompute powers of BASE
        pow[i] = BASE^i
        
        Needed for combining hash of left and right parts
        after deleting one char
        */
        long pow[] = new long[maxLen+1];
        pow[0] = 1;

        for (int i = 1; i <= maxLen; i++){
            pow[i]  = pow[i-1] * BASE;
        }

        Set<String> typos = new HashSet<>();

        /*
        A word is a typo if deleting exactly one char from it
        produces another word from the input.
        */
        for (String w : words){
            int len = w.length();

            if (len == 0) continue;

            /*
            prefix[i] -> hash with w.substring(0, i) 
            
            Lets us compute hash after deliting one char
            without creating new string
            */
            long[] prefix = new long[len + 1];

            for (int i = 0; i < len; i++){
                int value = w.charAt(i) - 'a' + 1;
                prefix[i + 1] = prefix[i] * BASE + value;
            }

            // Try deleting each char once
            for (int skip = 0; skip < len; skip++){
                int rightLen = len - skip - 1;

                // Hash of the part before the skipped char
                long leftHash = prefix[skip];

                // Hash of the part after the skipped char
                long rightHash = prefix[len] - prefix[skip + 1] * pow[rightLen];

                // Hash of w with w.charAt(skip) removed
                long deletedHash = leftHash * pow[rightLen] + rightHash;
                long deletedKey = key(deletedHash, len-1);

                // Check if resulting shorter word exists
                List<String> candidates = lookup.get(deletedKey);
                if (candidates == null) continue;

                // Verify equality to avoid hash collision
                for (String candidate : candidates){
                    if (equalsAfterDeleting(candidate, w, skip)){;
                        typos.add(w);
                        break;
                    }
                }
                // No need to test more delitions once w is known to be a typo
                if (typos.contains(w)) break;
            }
        }

        if (typos.isEmpty()){
            System.out.print("NO TYPOS");
        } else{
            StringBuilder sb = new StringBuilder();
            // Print typos in original order
            for (String w : words){
                if (typos.contains(w)){
                    sb.append(w).append("\n");
                }
            }
            System.out.print(sb);
        }
    br.close();
    }
}