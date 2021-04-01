package sample;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Frame extends JPanel implements MouseInputListener, ActionListener, ChangeListener {

    // GRID
    JFrame grid;
    Node[][] board;
    int tileSize;
    int numTilesX, numTilesY;
    int gridWidth, gridHeight;

    // SETUP
    ArrayList<Node> obstacles = new ArrayList<>();
    int startX = -1;
    int startY = -1;
    int endX = -1;
    int endY = -1;
    int obstacleCount = 0;


    // CONTROLS
    int initW = 80;
    int initH = 50;
    int min = 10;
    int max = 100;
    int step = 5;
    SpinnerNumberModel dimX = new SpinnerNumberModel(initW, min, max, step);
    SpinnerNumberModel dimY = new SpinnerNumberModel(initH, min, max, step);
    JSpinner spinnerX = new JSpinner(dimX);
    JSpinner spinnerY = new JSpinner(dimY);
    JButton startButton = new JButton("Start");
    JLabel widthLabel = new JLabel("Width");
    JLabel heightLabel = new JLabel("Height");

    AStar astar;
    Timer timer;

    public Frame() {

        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        gridWidth = 910;
        gridHeight = 610;
        calculateBoard(numTilesX, numTilesY);

        // initialize JFrame
        grid = new JFrame();
        grid.revalidate();
        grid.setContentPane(this);
        grid.getContentPane().setPreferredSize(new Dimension(gridWidth, gridHeight));
        grid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        grid.setLayout(new FlowLayout());
        grid.pack();
        grid.setLocationRelativeTo(null);
        grid.setVisible(true);

        grid.addMouseListener(this);
        grid.addMouseMotionListener(this);
        grid.getContentPane().validate();

        // controls
        startButton.addActionListener(this);
        startButton.setActionCommand("StartButton");

        spinnerX.addChangeListener(this);
        spinnerY.addChangeListener(this);

        grid.add(startButton);
        grid.add(widthLabel);
        grid.add(spinnerX);
        grid.add(heightLabel);
        grid.add(spinnerY);

        // try: change closed set to an arrayList
        // use the timer iterations to change colors of each additional closed set
        // use a cycled linked list of colors and change the head at each timer iteration
        timer = new Timer(15, new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                if (astar != null) {
                    if(astar.pathExists) {
                        ((Timer)e.getSource()).stop();
                        startButton.setEnabled(false);
                    }
                    else {
                        astar.findPath();
                    }
                    repaint();
                }
            }
        });
        timer.setInitialDelay(1);

        revalidate();

    }

    public static void main(String[] args){
        new Frame();
    }

    public void calculateBoard(int numX, int numY) {
        tileSize = Math.min((gridWidth - 10) / numTilesX, tileSize = (gridHeight - 10 ) / numTilesY);

        board = new Node[numTilesX][numTilesY];
        for(int i = 0; i < numTilesX; i++){
            for(int j = 0; j < numTilesY; j++) {
                board[i][j] = new Node(i, j );
            }
        }

        repaint();
    }
    public void stateChanged(ChangeEvent e) {

        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        //revalidate();
        calculateBoard(numTilesX, numTilesY);

    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "StartButton" && startX != -1 && startY != -1 && endX != -1 && endY != -1){

            astar = new AStar(board, startX, startY, endX, endY, numTilesX, numTilesY);
            timer.start();
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw grid
        g.setColor(Color.lightGray);
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                g.drawRect(i * tileSize, j * tileSize, tileSize, tileSize);

            }
        }

        // draw obstacles
        g.setColor(Color.black);
        for (int i = 0; i < obstacleCount; i++){
            g.fillRect(obstacles.get(i).x * tileSize, obstacles.get(i).y * tileSize, tileSize, tileSize);
        }


        // paint start
        if (startX != -1 && startY != -1) {
            g.setColor(Color.decode("#355C7D"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(startX), getCoord(startY), tileSize, tileSize);

        // paint end
        if (endX != -1 && endY != -1) {
            g.setColor(Color.decode("#6C5B7B"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(endX), getCoord(endY), tileSize, tileSize);


        if (astar != null) {

            // paint openSet
            g.setColor(Color.decode("#F67280"));
            for (int i = 0; i < astar.openSet.numItems; i++){
                Node openNode = astar.openSet.items[i];
                if ((openNode == astar.start) || (openNode == astar.end)){
                    continue;
                }
                g.fillRect(openNode.x * tileSize, openNode.y * tileSize, tileSize, tileSize);
            }

            // paint closed set
            g.setColor(Color.decode("#ffb4a2"));
            for (Node c : astar.closeSet){
                if (c == astar.start || c == astar.end || c.isPainted){
                    continue;
                }
                g.fillRect(c.x * tileSize, c.y * tileSize, tileSize, tileSize);
            }

            // paint neighbors
            g.setColor(Color.decode("#F8B195"));
            for (Node n : astar.currNeighbors) {
                if (n == astar.start || n == astar.end){
                    continue;
                }
                g.fillRect(n.x * tileSize, n.y * tileSize, tileSize, tileSize);
            }

            // paint path
            if (astar.pathExists) {
                g.setColor(Color.decode("#F0A35E"));
                for (int i = astar.path.size() - 1; i >= 0; i--) {
                    int pathX = astar.path.get(i).x;
                    int pathY = astar.path.get(i).y;
                    if ((astar.path.get(i) == astar.start) || (astar.path.get(i) == astar.end)){
                        continue;
                    }
                    g.fillRect(pathX * tileSize, pathY * tileSize, tileSize, tileSize);
                }
            }

        }

    }

    // create start and end points with mouse click
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX() / tileSize;
        int mouseY = (e.getY() - 20) / tileSize;
        if (!isValid(mouseX, mouseY)) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)){
            if(startX == -1 || startY == -1) {
                startX = mouseX;
                startY = mouseY;
            }
            else if ((startX != -1 && startY != -1) && (endX == -1 || endY == -1)) {
                endX = mouseX;
                endY = mouseY;
            }
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            if (mouseX == startX && mouseY == startY) {
                startX = -1;
                startY = -1;
            }
            else if (mouseX == endX && mouseY == endY) {
                endX = -1;
                endY = -1;
            }
        }
        repaint(getCoord(mouseX), getCoord(mouseY), tileSize, tileSize);
    }

    // create obstacles with mouse drag
    public void mouseDragged (MouseEvent e){

        int mouseX = e.getX() / tileSize;
        int mouseY = e.getY() / tileSize;
        if (!isValid(mouseX, mouseY) || isEndpoint(mouseX, mouseY)) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e) && !board[mouseX][mouseY].isObstacle) {
            this.board[mouseX][mouseY].isObstacle = true;
            obstacles.add(this.board[mouseX][mouseY]);
            obstacleCount++;
        }
        else if (SwingUtilities.isRightMouseButton(e) && board[mouseX][mouseY].isObstacle) {
            this.board[mouseX][mouseY].isObstacle = false;
            obstacles.remove(this.board[mouseX][mouseY]);
            obstacleCount--;
        }
        repaint();
    }

    // check that node is within borders and not start/end point
    public boolean isValid(int x, int y) {
        return (x >= 0 && (x < numTilesX) && y >= 0 && (y < numTilesY));
    }

    public boolean isEndpoint(int x, int y) {
        return ((x == startX && y == startY) || (x == endX && y == endY));
    }

    public int getCoord(int coord) {
        return coord * tileSize;
    }

    public void mouseMoved (MouseEvent e) { }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    // ******* method to start pathfinder
}
