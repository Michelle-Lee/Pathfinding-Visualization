package sample;

import javafx.scene.layout.StackPane;


public class Node extends StackPane implements Comparable<Node> {
    int x;
    int y;
    double g;
    double h;
    double f;
    int index;
    Boolean isObstacle = false;
    String color = "";
    Node parent = null;


    public Node(int x, int y){

        this.x = x;
        this.y = y;
        this.g = Integer.MAX_VALUE;
        this.h = Integer.MAX_VALUE;
        this.f = Integer.MAX_VALUE;

    }

    public Node(int x, int y, double g, double h, double f) {

        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.f = f;

    }

    @Override
    public int compareTo(Node other) {
        if(this.f < other.f) {return 1;}
        else if(this.f > other.f) {return -1;}
        else {
            if (this.h < other.h) {return 1;}
            else if (this.h > other.h) {return -1;}
            else {
                if (this.g < other.g) {return 1;}
                else if (this.g > other.g) {return -1;}
                else { return 0;}
            }
        }
    }


}
