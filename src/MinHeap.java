package sample;


public class MinHeap<T extends Node> {
    Node[] items;
    int numItems;

    public MinHeap(int heapCapacity){
        items = new Node[heapCapacity];
        numItems = 0;
    }

    // insert from the bottom, bubble up to correct position
    public void insert(Node newNode){
        newNode.index = numItems;
        items[numItems] = newNode;
        sortUp(newNode);
        numItems++;
    }

    // remove from the top, replace with last element, bubble down
    public Node remove() {
        Node root = items[0];
        numItems--;
        items[0] = items[numItems];
        items[0].index = 0;
        sortDown(items[0]);
        return root;
    }

    // swap up until node is in correct position
    public void sortUp(Node node){
        int parentIndex = (node.index - 1) / 2;
        while(node.compareTo(items[parentIndex]) > 0) {
            swap(items[parentIndex], node);
            if(node.index == 0) {
                break;
            }
            parentIndex = (node.index - 1) / 2;
        }
    }
    
    // swap down until node in correct position
    public void sortDown(Node node){
        int leftIndex = node.index * 2 + 1;
        int rightIndex = node.index * 2 + 2;
        int indexToSwap = 0;

        while (leftIndex < numItems){
            indexToSwap = leftIndex;
            if (rightIndex < numItems) {
                if (items[leftIndex].compareTo(items[rightIndex]) < 0) {
                    indexToSwap = rightIndex;
                }
            }

            if(node.compareTo(items[indexToSwap]) < 0) {
                swap(node, items[indexToSwap]);
            }
            else {
                return;
            }
            leftIndex = node.index * 2 + 1;
            rightIndex = node.index * 2 + 2;
        }
        return;
    }

    // if node has new cost values, update
    public void updateNode(Node node) { 
        sortUp(node);
    }

    // contains
    public boolean contains(Node node){
        return items[node.index].equals(node);
    }

    // swap two nodes and their respective indices
    public void swap(Node node1, Node node2) {
        items[node1.index] = node2;
        items[node2.index] = node1;

        int tempIndex = node1.index;
        node1.index = node2.index;
        node2.index = tempIndex;
    }
}
