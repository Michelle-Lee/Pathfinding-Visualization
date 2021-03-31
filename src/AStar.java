package sample;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class AStar {
    Frame frame;
    Node[][] board;
    Node start;
    Node end;
    int height;
    int width;
    MinHeap openSet;
    HashSet<Node> closeSet;

    List<Node> path = new ArrayList<>();
    Boolean running, pathExists = false;


    public AStar(Node[][] grid, Frame frame, int startX, int startY, int endX, int endY, int width, int height) {
        this.frame = frame;
        this.width = width;
        this.height = height;
        board = grid;
        start = board[startX][startY];
        end = board[endX][endY];
        openSet = new MinHeap(height * width);
        closeSet = new HashSet<>(height * width);
        openSet.insert(start);
    }


    // findPath executes one iteration at a time
    // the Timer will loop it through to completion when the start button is clicked

    public void findPath(){
        running = true;

        if (openSet.numItems > 0) {

            // move lowest node to closed set to explore
            Node currentNode = openSet.remove();
            closeSet.add(currentNode);

            if (currentNode.x == end.x && currentNode.y == end.y){
                System.out.println("Path found!");
                path = retracePath(currentNode);
                pathExists = true;
                running = false;
                return;
            }

            // explore neighbors of current node
            List<Node> currNeighbors = findNeighbors(currentNode);
            for (Node n : currNeighbors) {
                if (closeSet.contains(n)) {
                    continue;
                }

                // (re)calculate costs of each neighbor in case of we find lower cost (shorter path)
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
                // frame.repaint();
            }
        }
        running = false;

    }

    // calculate each of the neighbors, exclude the node itself and invalid nodes
    public List<Node> findNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                if (x == 0 && y == 0){
                    continue;
                }

                int xcoord = node.x + x;
                int ycoord = node.y + y;

                if (isValid(xcoord, ycoord) && !board[xcoord][ycoord].isObstacle) {
                    if ((x != 0 && y != 0) && isDiagonalCutOff(node, board[xcoord][ycoord]))  {
                        continue;
                    }
                    neighbors.add(board[xcoord][ycoord]);
                }
            }
        }
        return neighbors;
    }

    // retrace each nodes parent to find path from endpoint
    public List<Node> retracePath(Node node){
        List<Node> nodePath = new ArrayList<>();
        Node curr = node;
        while(curr != start) {
            nodePath.add(curr);
            curr = curr.parent;
        }
        return nodePath;
    }

    // calculate euclidean distance for cost functions
    public double getDist(Node prev, Node curr) {
        return Math.sqrt(Math.pow(curr.x - prev.x,2) + Math.pow(curr.y - prev.y,2));
    }

    // check nodes are within bounds
    public boolean isValid(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    // check if diagonal neighbor is cut off by obstacles
    public boolean isDiagonalCutOff(Node node, Node neighborNode) {
        return board[node.x][neighborNode.y].isObstacle && board[neighborNode.x][node.y].isObstacle;
    }

}
