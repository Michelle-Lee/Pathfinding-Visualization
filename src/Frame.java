package sample;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

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
    int initW = 40;
    int initH = 20;
    int min = 10;
    int max = 200;
    int step = 5;
    SpinnerNumberModel dimX = new SpinnerNumberModel(initW, min, max, step);
    SpinnerNumberModel dimY = new SpinnerNumberModel(initH, min, max, step);
    JSpinner spinnerX = new JSpinner(dimX);
    JSpinner spinnerY = new JSpinner(dimY);
    JButton startButton = new JButton("Start");
    JLabel widthLabel = new JLabel("Width");
    JLabel heightLabel = new JLabel("Height");
    int[] nodeSize;
    String[] nodeColor;

    AStar astar;
    Timer timer;
    int pathCounter = 1;

    public Frame() {

        // DIMENSIONS
        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        gridWidth = 910;
        gridHeight = 610;
        calculateBoard();

        // INITIALIZE JFRAME
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

        // CONTROLS
        startButton.addActionListener(this);
        startButton.setActionCommand("StartButton");

        spinnerX.addChangeListener(this);
        spinnerY.addChangeListener(this);

        grid.add(startButton);
        grid.add(widthLabel);
        grid.add(spinnerX);
        grid.add(heightLabel);
        grid.add(spinnerY);

        // ANIMATION & COLORS
        nodeSize = new int[]{tileSize*1/4, tileSize*3/8, tileSize*2/4, tileSize*5/8, tileSize*3/4};
        nodeColor = new String[]{"#7f479b","#6765b7","#487fc9","#2ba0d1","#55bcce"};


        // timer for path exploring animation: timer action runs off every 15 ms
        timer = new Timer(50, new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                if (astar != null) {
                    if(astar.pathExists && pathCounter == astar.path.size() - 1) {
                        ((Timer)e.getSource()).stop();
                        startButton.setEnabled(false);
                    }
                    else if (!astar.pathExists){
                        astar.findPath(); // pathfinder iterates every 15 ms
                    }
                    repaint();
                }
            }
        });
        timer.setInitialDelay(1);
        revalidate();
    }

    // **** MAIN ****
    public static void main(String[] args){
        new Frame();
    }

    // calculate tile size and board dimensions for AStar
    public void calculateBoard() {
        tileSize = Math.min((gridWidth - 10) / numTilesX, tileSize = (gridHeight - 10 ) / numTilesY);

        board = new Node[numTilesX][numTilesY];
        for(int i = 0; i < numTilesX; i++){
            for(int j = 0; j < numTilesY; j++) {
                board[i][j] = new Node(i, j );
            }
        }

        repaint();
    }

    // listener for spinner changes (dimensions)
    public void stateChanged(ChangeEvent e) {
        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        calculateBoard();
    }

    // start button action listener
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "StartButton" && startX != -1 && startY != -1 && endX != -1 && endY != -1){

            astar = new AStar(board, startX, startY, endX, endY, numTilesX, numTilesY);
            timer.start();
        }
    }

    // paint attributes for the grid
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGrid(g);

        g.setColor(Color.black);
        paintObstacles(g);

        paintStart(g);
        paintEnd(g);

        if (astar != null) {

            paintOpenSet(g);
            paintCloseSet(g);

            g.setColor(Color.decode("#883689"));
            paintPath(g);

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

    public int centerNode(int node_size) {
        return tileSize/2 - node_size/2;
    }

    // ************** Paint Component Helper Functions *****************
    public void drawGrid(Graphics g) {
        g.setColor(Color.lightGray);
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                g.drawRect(i * tileSize, j * tileSize, tileSize, tileSize);

            }
        }
    }

    public void paintObstacles(Graphics g) {
        for (int i = 0; i < obstacleCount; i++){
            g.fillRect(obstacles.get(i).x * tileSize + 1, obstacles.get(i).y * tileSize + 1,
                    tileSize - 1, tileSize - 1);
        }
    }

    public void paintStart(Graphics g) {
        if (startX != -1 && startY != -1) {
            g.setColor(Color.decode("#355C7D"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(startX), getCoord(startY), tileSize, tileSize);

    }

    public void paintEnd(Graphics g) {
        if (endX != -1 && endY != -1) {
            g.setColor(Color.decode("#6C5B7B"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(endX) + 1, getCoord(endY) + 1, tileSize - 1, tileSize - 1);

    }

    public void paintOpenSet(Graphics g) {
        for (int i = 0; i < astar.openSet.numItems; i++){
            Node open = astar.openSet.items[i];
            if ((open == astar.start) || (open == astar.end)){
                continue;
            }
            // if the detail index is < 3, the node is still expanding and changing color
            if (open.nodeDetailIndex < 5) {
                int thisNodeSize = nodeSize[open.nodeDetailIndex];
                String thisNodeColor = nodeColor[open.nodeDetailIndex];
                g.setColor(Color.decode(thisNodeColor));
                g.fillRect(open.x * tileSize + centerNode(thisNodeSize) + 1,
                        open.y * tileSize + centerNode(thisNodeSize) + 1,thisNodeSize - 1,thisNodeSize - 1);
                open.nodeDetailIndex++;
            }
            else if (!astar.pathExists){
                open.nodeDetailIndex = 0;
                g.setColor(Color.decode("#bee3b6"));
                g.fillRect(open.x * tileSize + 1, open.y * tileSize + 1, tileSize - 1, tileSize - 1);
            }
            else {
                g.setColor(Color.decode("#8fd4cb"));
                g.fillRect(open.x * tileSize + 1, open.y * tileSize + 1, tileSize - 1, tileSize - 1);
            }
        }
    }

    public void paintCloseSet(Graphics g) {
        for (Node c : astar.closeSet){
            if (c == astar.start || c == astar.end){
                continue;
            }
            // if the detail index is < 3, the node is still expanding and changing color
            if (c.nodeDetailIndex < 5) {
                int thisNodeSize = nodeSize[c.nodeDetailIndex];
                String thisNodeColor = nodeColor[c.nodeDetailIndex];
                g.setColor(Color.decode(thisNodeColor));
                g.fillRect(c.x * tileSize + centerNode(thisNodeSize) + 1,
                        c.y * tileSize + centerNode(thisNodeSize) + 1,thisNodeSize - 1,thisNodeSize - 1);
                c.nodeDetailIndex++;
            }

            else {
                g.setColor(Color.decode("#8fd4cb"));
                g.fillRect(c.x * tileSize + 1, c.y * tileSize + 1, tileSize - 1, tileSize - 1);
            }
        }
    }

    public void paintPath(Graphics g) {
        if (astar.pathExists && pathCounter < astar.path.size()) {
            // to animate path, start from end and add one node every timer iteration
            for (int i = astar.path.size() - 1; i >= astar.path.size() - pathCounter; i--) {
                int pathX = astar.path.get(i).x;
                int pathY = astar.path.get(i).y;
                if ((astar.path.get(i) == astar.start) || (astar.path.get(i) == astar.end)){
                    continue;
                }
                g.fillRect(pathX * tileSize + 1, pathY * tileSize + 1, tileSize - 1, tileSize - 1);
            }
            pathCounter++;
        }
    }

    // ************** Paint Component Helper Functions END *****************


    public void mouseMoved (MouseEvent e) { }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
