package sample;


import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class AStar{
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


    public AStar(){
        frame = new Frame();
        height = 10;
        width = 10;
        board = new Node[height][width];
        start = new Node(0,0, 0, 0, 0);
        end = new Node(height - 1,width - 1);
        openSet = new MinHeap(height * width);
        closeSet = new HashSet<>(height * width);
        openSet.insert(start);

    }

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

    public void findPath(){
        running = true;

        while (openSet.numItems > 0) {

            Node currentNode = openSet.remove();
            closeSet.add(currentNode);
            frame.repaint();

            if (currentNode.x == end.x && currentNode.y == end.y){
                path = retracePath(currentNode);
                pathExists = true;
                running = false;
                return;
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
                //frame.repaint();
            }
        }
        running = false;
    }

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
                        System.out.println("diagonal block: " + xcoord + ", " + ycoord);
                        continue;
                    }
                    neighbors.add(board[xcoord][ycoord]);
                }
            }
        }
        return neighbors;
    }

    public List<Node> retracePath(Node node){
        List<Node> nodePath = new ArrayList<>();
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

    // check if diagonal neighbor is cut off by obstacles
    public boolean isDiagonalCutOff(Node node, Node neighborNode) {
        return board[node.x][neighborNode.y].isObstacle && board[neighborNode.x][node.y].isObstacle;
    }

}
