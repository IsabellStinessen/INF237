import java.util.*;
import java.io.*;
import java.math.BigInteger;

// https://uib.kattis.com/courses/INF237/spring26/assignments/txoawj/submissions/19670016

class SmallestMultiple {
    /*
        - lcm(a,b) == (a * b) / gcd(a,b)
            - gcd() method from `java.math.BigInteger`
        - lcm(a,b,c) == lcm(lcm(a,b), c)
    */
    static BigInteger lcm(BigInteger a, BigInteger b){
        return a.divide(a.gcd(b)).multiply(b);  
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("numbers-and-trees/smallest-multiple/SmallestMultiple.in")
        );         
        StringBuilder sb = new StringBuilder("");

        String line;
        while((line = br.readLine()) != null){
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.trim().split(" ");

            BigInteger LCM = new BigInteger(parts[0]);

            for (int i = 1; i < parts.length; i++){
                LCM = lcm(LCM, new BigInteger(parts[i]));  
            }

            sb.append(LCM).append("\n");
        }

        System.out.println(sb);
        br.close();
    }
}
