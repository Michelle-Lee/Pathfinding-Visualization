package sample;

import java.util.PriorityQueue;

public class MinHeap<T extends Node> {
    Node[] items;
    int numItems;

    public MinHeap(int heapCapacity){
        items = new Node[heapCapacity];
        numItems = 0;
    }

    public MinHeap(int heapCapacity, Node node) {
        items = new Node[heapCapacity];
        items[0] = node;
        numItems = 0;

    }
    public void insert(Node newNode){
        newNode.index = numItems;
        items[numItems] = newNode;
        sortUp(newNode);
        numItems++;
    }

    public Node remove() {
        Node root = items[0];
        numItems--;
        items[0] = items[numItems];
        items[0].index = 0;
        sortDown(items[0]);
        return root;
    }

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

    public void updateNode(Node node) {
        sortUp(node);
    }

    public boolean contains(Node node){
        return items[node.index].equals(node);
    }

    public void swap(Node node1, Node node2) {
        items[node1.index] = node2;
        items[node2.index] = node1;

        int tempIndex = node1.index;
        node1.index = node2.index;
        node2.index = tempIndex;
    }

    public void heapToString(){
        for(int i=0;i<numItems;i++){
            for(int j=0;j<Math.pow(2,i)&&j+Math.pow(2,i)<=numItems;j++){
                System.out.print(items[j+(int)Math.pow(2,i)-1].f+" ");

            }
            System.out.println();
        }
    }
    public void printHeap(){
        System.out.println("numitems: " + numItems);
        for (int i = 0; i<numItems; i++){
            System.out.println("heap array: "+items[i].f);
        }
    }

    public void printHeapCoord(){
        System.out.println("numItems: " + numItems);
        for (int i = 0; i<numItems; i++){
            System.out.println("heap (x, y): "+items[i].x+", "+items[i].y);
        }
    }
}