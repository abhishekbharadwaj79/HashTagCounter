/**
 * Created by Abhishek on 15-10-2016.
 */
public class FibonacciHeapNode {
    int degree;
    int data;
    FibonacciHeapNode child;
    FibonacciHeapNode prev;
    FibonacciHeapNode next;
    FibonacciHeapNode parent;
    boolean childCut = false;
    String hashTag;

    public FibonacciHeapNode(int key, String hashTag) {
        prev = this;
        next = this;
        this.data = key;
        this.hashTag = hashTag;
    }
}
