import java.io.*;
import java.util.*;

/**
 * Created by Abhishek on 16-10-2016.
 */
public class hashtagcounter {
    public void mainMethod(String file) {
        /* This will be the FibonacciHeap on which all the actions will be performed */
        FibonacciHeap heap = new FibonacciHeap();
        /* Map to store the hashTag and the FibonacciHeapNode */
        Map<String, FibonacciHeapNode> map = new HashMap<>();
        /* Using the try catch to take care of reading from and writing to a file */
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter write = new PrintWriter("output_file.txt");
            String currentLine;
            /* Keep reading till the end of the file until the line with stop comes */
            while (!(currentLine = br.readLine()).equalsIgnoreCase("stop")) {
                /* If we encounter a number then print the same amount of hashTags in descending order in a file */
                if(currentLine.charAt(0) != '#') {
                    int number = Integer.parseInt(currentLine);
                    /* This stack is kept to keep track of the removed FibonacciHeapNode,
                    as we will have to meld them back in the FibonacciHeap */
                    Stack<FibonacciHeapNode> stack = new Stack<>();
                    /* Remove the max element from the FibonacciHeap and push it to the stack just created above
                    and print the output to the file output_file.txt. Do it for the amount of times asked in the input file. */
                    for (int j = 1; j <= number; j++) {
                        FibonacciHeapNode node = heap.removeMax();
                        stack.push(node);
                        write.print(node.hashTag);
                        if(j != number)
                            write.print(",");
                        else
                            write.println();
                    }

                        /* Meld back the 'number' amount of removed FibonacciHeapNodes */
                    for(int j = 0; j < number; j++) {
                        heap.root = heap.meld(heap.root, stack.pop());
                    }
                }
                /* Else we take note of the hashTag and put it in HashMap and insert it's value in the FibonacciHeap */
                else {
                    int i = 1;
                    String hashTag = "";
                    for (i = 1; currentLine.charAt(i) != ' '; i++) {
                        hashTag = hashTag + currentLine.charAt(i);
                    }
                    /* Store the hashTag count value*/
                    int value = Integer.parseInt(currentLine.substring(++i, currentLine.length()));
                    /* If the hashTag already exists in the map that means it's there in the FibonacciHeap
                    and hence perform increaseKey */
                    if (map.get(hashTag) != null)
                        heap.increaseKey(map.get(hashTag), value);
                    /* Since the hashTag is not present in the map, insert it in the map and FibonacciHeap */
                    else {
                        FibonacciHeapNode node = heap.insert(value, hashTag);
                        map.put(hashTag, node);
                    }
                }
            }
            write.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        hashtagcounter obj = new hashtagcounter();
        obj.mainMethod(args[0]);
        return;
    }
}