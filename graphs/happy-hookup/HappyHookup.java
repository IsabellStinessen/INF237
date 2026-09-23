import java.util.*;
import java.io.*;

// http://uib.kattis.com/courses/INF237/spring26/assignments/e237ce/submissions/19145367

class HappyHookup{
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("graphs/happy-hookup/HappyHookup.in"));

        int stations = sc.nextInt();
        int connections = sc.nextInt();
        
        boolean connected = false;
        int meetPoint = -1;
        
        Map<Integer, HashSet<Integer>> directTrains = new HashMap<>();
        for(int i = 1; i <= connections; i++){
            int start = sc.nextInt();
            int end = sc.nextInt();
            directTrains.putIfAbsent(start, new HashSet<>());
            directTrains.get(start).add(end);
        }
        
        int startA = sc.nextInt();
        int startB = sc.nextInt(); 
            // Looking for first suitable stations, as travel length
            // doesn't have to be equal
        HashSet<Integer> visitedA = new HashSet<>();
        Queue<Integer> toSearchA = new LinkedList<>();
        visitedA.add(startA);
        toSearchA.add(startA);

        // 1. Creating set (visitedA) og stations A can get to, 
        // while checking if it's the starting station for B, to stop early
        while(!toSearchA.isEmpty()){
            int current = toSearchA.poll();
            
            if (current == startB){
                connected = true;
                meetPoint = current;
                break;
            }
            
            HashSet<Integer> next = directTrains.get(current);
            if(next == null) continue;
            
            for (int x : next){
                if(!visitedA.contains(x)){
                    visitedA.add(x);
                    toSearchA.add(x);
                }
            }
        }
        
        // 2. Checking if stations B can get to are in visitedA
        if (!connected){
            HashSet<Integer> visitedB = new HashSet<>();
            Queue<Integer> toSearchB = new LinkedList<>();
            visitedB.add(startB);
            toSearchB.add(startB); 
            
            while(!toSearchB.isEmpty()){
                int current = toSearchB.poll();

                if(visitedA.contains(current)){
                    connected = true;
                    meetPoint = current;
                    break;
                }
                
                HashSet<Integer> next = directTrains.get(current);
                if(next == null) continue;
                
                for (int x : next){
                    if(!visitedB.contains(x)){
                        visitedB.add(x);
                        toSearchB.add(x);
                    }
                }
            }
        }
            
        if (connected) {
            System.out.println("yes");
            System.out.print(meetPoint);
        } else System.out.print("no");
        sc.close();
    }
}