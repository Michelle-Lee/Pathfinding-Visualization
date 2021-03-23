package sample;

import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class AStar {
    Node[][] board;
    Node start;
    Node end;
    int height;
    int width;
    MinHeap openSet;
    HashSet closeSet;
    Boolean running, finish, pathExist;
    ArrayList<Node> obstacles;

    public AStar(){
        height = 10;
        width = 10;
        board = new Node[height][width];
        start = new Node(0,0, 0, 0, 0);
        end = new Node(height - 1,width - 1);
        openSet = new MinHeap(height * width);
        closeSet = new HashSet(height * width);
        openSet.insert(start);
        running = false;
        finish = false;
        pathExist = false;

    }

    public AStar(Node[][] grid, int startX, int startY, int endX, int endY) {
        board = grid;
        start = new Node(startX, startY, 0, 0, 0);
        end = new Node(endX, endY);
        openSet = new MinHeap(height * width);
        closeSet = new HashSet(height * width);
        openSet.insert(start);
        running = false;
        finish = false;
        pathExist = false;
    }

    public void start(){
        running = true;
        findPath();
        finish = true;
    }

    public List<Node> findPath(){
        List<Node> path = new ArrayList<Node>();
        while (openSet.numItems > 0) {

            Node currentNode = openSet.remove();
            closeSet.add(currentNode);
            System.out.println("current x, y: "+currentNode.x+", "+currentNode.y);
            System.out.println();

            if (currentNode.x == end.x && currentNode.y == end.y){
                System.out.println("hit end");
                path = retracePath(currentNode);
                pathExist = true;
                return path;
            }
            List<Node> currNeighbors = findNeighbors(currentNode);
            for (Node n : currNeighbors) {
                if (closeSet.contains(n)) {
                    continue;
                }

                double newNeighborCost = currentNode.g + getDist(currentNode, n);
                if (newNeighborCost < n.g || !openSet.contains(n)) {
                    n.g = newNeighborCost;
                    n.h = getDist(n, end);
                    n.f = n.g + n.h;
                    n.parent = currentNode;

                    if(!openSet.contains(n)){
                        openSet.insert(n);
                    } else {
                        openSet.updateNode(n);
                    }
                }
            }
        }
        return path;
    }

    public List<Node> findNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<Node>();
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                if(x == 0 && y == 0){
                    continue;
                }

                int xcoord = node.x + x;
                int ycoord = node.y + y;
                if(isValid(xcoord, ycoord)) {
                    if(board[xcoord][ycoord] == null){
                        board[xcoord][ycoord] = new Node(xcoord, ycoord);
                    }
                    neighbors.add(board[xcoord][ycoord]);
                }

            }
        }
        return neighbors;
    }

    public List<Node> retracePath(Node node){
        List<Node> nodePath = new ArrayList<Node>();
        Node curr = node;
        while(curr != start) {
            nodePath.add(curr);
            curr = curr.parent;
        }
        return nodePath;
    }

    public double getDist(Node prev, Node curr) {
        return Math.sqrt(Math.pow(curr.x - prev.x,2) + Math.pow(curr.y - prev.y,2));
    }

    public boolean isValid(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }


/*    public static void main(String[] args){
        AStar solution = new AStar();
        System.out.println("test1");
        List<Node> p = solution.findPath();
        System.out.println(p);
        for (Node n : p) {
            System.out.println("(x, y): " + n.x + ", " + n.y);
        }
    }*/

}
