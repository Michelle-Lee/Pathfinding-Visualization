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
    int startX, startY = -1;
    int endX, endY = -1;
    int obstacleCount = 0;

    AStar astar;

    // CONTROLS
    int initVal = 50;
    int min = 10;
    int max = 100;
    int step = 5;
    SpinnerNumberModel dimX = new SpinnerNumberModel(initVal, min, max, step);
    SpinnerNumberModel dimY = new SpinnerNumberModel(initVal, min, max, step);
    JSpinner spinnerX = new JSpinner(dimX);
    JSpinner spinnerY = new JSpinner(dimY);

    JButton startButton = new JButton("Start");

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
        //grid.getContentPane().repaint();

        // initialize all nodes in board for AStar object


        //astar = new AStar(board, startX, startY, endX, endY, numTilesX, numTilesY);
        spinnerX.addChangeListener(this);
        spinnerY.addChangeListener(this);



        //startButton.setBounds(25, gridHeight - 75, 75, 50);
        startButton.addActionListener(this);
        startButton.setActionCommand("StartButton");
        grid.add(startButton);

        //spinnerX.setBounds(gridWidth - 100, gridHeight - 75, 75, 20);
        grid.add(spinnerX);
        grid.add(spinnerY);

        timer = new Timer(1000, this); //new ActionListener()
/*        {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(astar.pathExists) {
                    ((Timer)e.getSource()).stop();
                    startButton.setEnabled(false);
                }
                else if (!astar.pathExists && astar.running){

                }
            }
        }*/
        timer.setInitialDelay(1);
        timer.start();

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

            //timer.start();
            astar = new AStar(board, this, startX, startY, endX, endY, numTilesX, numTilesY);

            astar.findPath();
            //timer.stop();

            //repaint();
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startX != -1 && startY != -1) {
            g.setColor(Color.decode("#FA6E4F"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(startX), getCoord(startY), tileSize, tileSize);

        if (endX != -1 && endY != -1) {
            g.setColor(Color.decode("#FB8E7E"));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(endX), getCoord(endY), tileSize, tileSize);

        // draw grid
        g.setColor(Color.gray);
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                g.drawRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }

        // draw obstacles
        g.setColor(Color.black);
        for (int i = 0; i < obstacleCount; i++){

            g.fillRect(obstacles.get(i).x * tileSize, obstacles.get(i).y * tileSize, tileSize, tileSize);
            System.out.println("tileX: " + obstacles.get(i).x +", tileY: " + obstacles.get(i).y);
        }

        // color open & closed set
        g.setColor(Color.decode("#C5D7C0"));

        if (astar != null) {
            for (int i = 0; i < astar.openSet.numItems; i++){
                Node openNode = astar.openSet.items[i];
                if ((openNode == astar.start) || (openNode == astar.end)){
                    continue;
                }
                g.fillRect(openNode.x * tileSize, openNode.y * tileSize, tileSize, tileSize);

            }

            g.setColor(Color.decode("#8EC9BB"));
            for (Node c : astar.closeSet){
                if (c == astar.start || c == astar.end){
                    continue;
                }
                g.fillRect(c.x * tileSize, c.y * tileSize, tileSize, tileSize);
            }


            if (astar.pathExists) {
                g.setColor(Color.decode("#F2CF59"));
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

    public boolean isValid(int x, int y) {

        return (x >= 0 && (x <= numTilesX) && y >= 0 && (y <= numTilesY));
    }


    public int getCoord(int coord) {
        return coord * tileSize;
    }


    public void mouseDragged (MouseEvent e){
        //if start or end node is null then...
        int mouseX = e.getX() / tileSize;
        int mouseY = e.getY() / tileSize;
        if (!isValid(mouseX, mouseY) || board[mouseX][mouseY].isObstacle) {
            return;
        }
        this.board[mouseX][mouseY].isObstacle = true;
        obstacles.add(this.board[mouseX][mouseY]);
        obstacleCount++;
        System.out.println("mouseX: " + mouseX +", mouseY: " + mouseY);
        System.out.println("******** num obstacles: " + obstacles.size());

        repaint();
    }


    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX() / tileSize;
        int mouseY = (e.getY() - 20) / tileSize;
        System.out.println("get x, get y: " + e.getX() + ", " + e.getY());
        System.out.println("mouse y, mouse y: " + mouseX + ", " + mouseY);
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

    public void mouseMoved (MouseEvent e) { }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
