import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek on 15-10-2016.
 */
public class FibonacciHeap {
    FibonacciHeapNode root;

    public  FibonacciHeap() {
        root = null;
    }

    /* Inserts a new key in the Fibonacci Heap
    *
    * @param key: the value of the new FibonacciHeapNode that needs to be inserted
    * @return a newly inserted FibonacciHeapNode
    */
    public FibonacciHeapNode insert(int key, String hashTag) {
        FibonacciHeapNode node = new FibonacciHeapNode(key, hashTag);
        root = meld(root, node);
        return node;
    }

    /* Increases the value of the given node and if the value is greater than the parent then does a cascading Cut of the node
    *
    * @param node: FibonacciHeapNode for which data needs to be increased
    * @param value: Value by which node's data needs to be increased
    */
    public void increaseKey(FibonacciHeapNode node, int value) {
        node.data = node.data + value;
        if(node.parent != null && node.data > node.parent.data ) {
            cascadingCut(node);
        }
         /* If the data of the increased node is greater than root then make this node as root */
        if(node.data > root.data) {
            root = node;
        }
    }

    /* Cuts a node from it's parent and meld it at top level
    * If the node's parent has childCut value as true then it cuts that also out of a tree and does that recursively
    *
    * @param node: FibonacciHeapNode that needs to be cut
    */
    public void cascadingCut(FibonacciHeapNode node) {
        /* As this node is going to be cut and put at top level of the fibonacci heap, mark it's childCut as false*/
        node.childCut = false;

        /* Base Case: If their is no parent of this node then we are done, just return */
        if(node.parent == null)
            return;

        /* If the node has siblings then make the appropriate changes in the doubly linked list */
        if(node.next != node) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }

        /* If the node is child of it's parent then make appropriate changes */
        if(node.parent.child == node) {
            /* If the node has siblings then make the next node as child of it's parent */
            if(node.next != node)
                node.parent.child = node.next;
            /* If their are no siblings of the node then mark the child of the parent as null */
            else
                node.parent.child = null;
        }

        /* As one node is going to be cut from children of the parent, we decrease the degree count of parent by 1 */
        node.parent.degree -= 1;

        /* Before melding at top level we make node as a fibonacci heap node with only one node at the top level */
        node.next = node;
        node.prev = node;

        /* Meld root and the newly cut node */
        root = meld(root, node);
        /* If the node's parent's childCut value is true then do a cascadingCut of that node
        * so as to maintain the childCut property */
        if(node.parent.childCut)
            cascadingCut(node.parent);
        /* If the node's parent's childcut value is false, then make it true so as  to note that this node has lost a child */
        else
            node.parent.childCut = true;

        /* Mark the node's parent as null, as this is the node at top level and not associated with it's parent anymore */
        node.parent = null;
    }

    /* Removes the maximum value in the FibonacciHeap, does a pairwise merge and returns the maximum FibonacciHeapNode
    *
    * @return FibonacciHeapNode value of the maximum node, which is root of the heap */
    public FibonacciHeapNode removeMax() {
        /* If the root is null then return null */
        if(root == null) {
           return null;
        }

        /* Store the maximum node that has to be returned */
        FibonacciHeapNode maxNode = root;

        /* Remove the maxNode from the doubly linked list of all the roots
        *  If there is only one node then mark root as null */
        if(root.next == root) {
            root = null;
        }
        /* If there are more nodes then mark the next node as root for now */
        else {
            root.prev.next = root.next;
            root.next.prev = root.prev;
            root = root.next;
        }

        /* If child of maxNode is not null then mark it's parent field and all it's siblings' parent field as false,
        as their parent will be removed */
        if(maxNode.child != null) {
            FibonacciHeapNode curr = maxNode.child;
            do {
                curr.parent = null;
                curr = curr.next;
            }
            while(curr != maxNode.child);
        }

        /* Meld root and child of root */
        root = meld(root, maxNode.child);

        /* If there are no entries then we are done */
        if(root == null)
            return maxNode;

        /* Pair wise merge all the remaining nodes */
        pairwiseMerge();

        /* Set the next and prev pointer of maxNode as maxNode, its child as null and degree equal to zero */
        maxNode.next = maxNode.prev = maxNode;
        maxNode.child = null;
        maxNode.degree = 0;
        return maxNode;
    }

    /* Meld all trees of the same order until there are no trees of same order left */
    public void pairwiseMerge() {
        /* This list keeps track of the degree count of all the nodes at the root level */
        List<FibonacciHeapNode> degreeCountTable = new ArrayList<>();

        /* We need to make sure we are not visiting the same node twice, so we will add all the nodes to another list */
        List<FibonacciHeapNode> arrayList = new ArrayList<>();
        /* Adding all the nodes to the second List */
        for(FibonacciHeapNode curr = root; arrayList.isEmpty() || arrayList.get(0) != curr; curr = curr.next) {
            arrayList.add(curr);
        }

        /* Traverse the list and do the pairwise merge wherever necessary */
        for(FibonacciHeapNode curr: arrayList) {
            /* Merge until degreeCountTable has null for the current node */
            while(true) {
                /* Put null at all the places until the degree of the current node in degreeCountTable */
                while (curr.degree >= degreeCountTable.size())
                    degreeCountTable.add(null);
                /* If the degree count is null for the current node in degreeCountTable then no need of merge and break out of the loop */
                if (degreeCountTable.get(curr.degree) == null) {
                    degreeCountTable.set(curr.degree, curr);
                    break;
                }

                /* Else merge the current node and the one in degreeCountTable */
                FibonacciHeapNode otherNode = degreeCountTable.get(curr.degree);
                degreeCountTable.set(curr.degree, null);

                /* Check which of the two roots is larger */
                FibonacciHeapNode minNode = otherNode.data < curr.data ? otherNode : curr;
                FibonacciHeapNode maxNode = otherNode.data < curr.data ? curr : otherNode;

                /* Make the smaller one child of larger one and make appropriate changes */
                minNode.next.prev = minNode.prev;
                minNode.prev.next = minNode.next;

                minNode.next = minNode;
                minNode.prev = minNode;

                maxNode.child = meld(maxNode.child, minNode);

                minNode.parent = maxNode;
                /* Mark childCut of minNode as false, because now it can loose another child */
                minNode.childCut = false;

                /* Increase the degree of the maxNode as a node with smaller value is added to it */
                maxNode.degree += 1;

                /* Continue merging this tree */
                curr = maxNode;
            }
            /* Update the global root by comparing the current node to the current root */
            if(curr.data >= root.data)
                root = curr;
        }
    }


    /*  Meld two FibonacciHeaps with given roots
    *
    * @param node1: root of the first FibonacciHeap
    * @param node2: root of the second FibonacciHeap
    * @return root of the melded FibonacciHeap
    */
    public FibonacciHeapNode meld(FibonacciHeapNode node1, FibonacciHeapNode node2) {
        if (node1 == null && node2 == null)
            return null;
        else if (node1 == null)
            return node2;
        else if (node2 == null)
            return node1;
        /* If root of both the FibonacciHeaps is not null then meld them and return the root with greater value */
        else {
            FibonacciHeapNode temp = node1.next;
            node1.next = node2.next;
            node1.next.prev = node1;
            node2.next = temp;
            node2.next.prev = node2;

            return node1.data > node2.data ? node1 : node2;
        }
    }
}