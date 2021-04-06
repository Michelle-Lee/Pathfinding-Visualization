package sample;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;


public class FrameV2 extends JPanel implements MouseInputListener, ActionListener, ChangeListener {

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
    JComboBox themeBox;
    Theme theme = new Theme("Plain");   // default


    Image img;

    AStar astar;
    Timer timer;
    Timer timer2;

    //CircularLinkedList colors = new CircularLinkedList();
    //CircularLinkedList colors2 = new CircularLinkedList();
    String myColor;
    String myColor2;


    public FrameV2() {

        // DIMENSIONS
        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        gridWidth = 890;
        gridHeight = 560;
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
        grid.getContentPane().validate();
        grid.setVisible(true);



        grid.addMouseListener(this);
        grid.addMouseMotionListener(this);


        // CONTROLS
        startButton.addActionListener(this);

        spinnerX.addChangeListener(this);
        spinnerY.addChangeListener(this);

        String[] themeList = {"Plain", "Synthwave Plain", "Synthwave",
                "Sunset", "Dragon Ball Z Pixel", "Dragon Ball Z"};
        themeBox = new JComboBox(themeList);
        themeBox.setSelectedIndex(0);
        themeBox.addActionListener(this);

        grid.add(startButton);
        grid.add(widthLabel);
        grid.add(spinnerX);
        grid.add(heightLabel);
        grid.add(spinnerY);
        grid.add(themeBox);

        myColor = theme.gradient.head.value;
        myColor2 = theme.flash.head.value;

        // ANIMATION & COLORS
        //hardCodeColors();

        // timer for color gradient
        timer2 = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(astar.pathExists) {
                    ((Timer)e.getSource()).stop();
                    startButton.setEnabled(false);
                }
                myColor = theme.gradient.getNext();
            }
        });

        // timer for path exploring animation: timer action runs off every 10 ms
        timer = new Timer(10, new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                if (astar != null) {
                    if(astar.pathExists) {
                        ((Timer)e.getSource()).stop();
                        startButton.setEnabled(false);
                    }
                    else {
                        myColor2 = theme.flash.getNext();
                        astar.findPath();
                    }
                    repaint();
                }
            }
        });

        revalidate();
    }

    // **** MAIN ****
    public static void main(String[] args){
        new FrameV2();
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

    // action listener for spinner changes (dimensions)
    public void stateChanged(ChangeEvent e) {
        numTilesX = (int) dimX.getNumber();
        numTilesY = (int) dimY.getNumber();
        calculateBoard();
    }

    // action listener for start button and theme dropdown
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startButton && startX != -1 && startY != -1 && endX != -1 && endY != -1){
            astar = new AStar(board, startX, startY, endX, endY, numTilesX, numTilesY);
            timer.start();
            timer2.start();
        }
        if(e.getSource() == themeBox) {
            JComboBox cb = (JComboBox)e.getSource();
            String selectedTheme = (String)cb.getSelectedItem();
            theme = new Theme(selectedTheme);

            if (selectedTheme != "Plain" && selectedTheme != "Synthwave Plain") {
                img = Toolkit.getDefaultToolkit().getImage(theme.background);
            }
            else {
                grid.getContentPane().setBackground(Color.decode(theme.background));
            }
            myColor = theme.gradient.head.value;
            myColor2 = theme.flash.head.value;
            repaint();
        }
    }

    // paint attributes for the grid
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(img, 0, 0, numTilesX*tileSize, numTilesY*tileSize,this);

        g.setColor(Color.decode(theme.drawColor));
        drawGrid(g);

        g.setColor(Color.decode(theme.obstacleColor));
        paintObstacles(g);

        paintStart(g);
        paintEnd(g);


        if (astar != null) {

            g.setColor(Color.decode(myColor2));
            paintOpenSet(g);

            paintCloseSet(g);

            g.setColor(Color.decode(myColor2));
            paintNeighbors(g);

            g.setColor(Color.decode(theme.pathColor));
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

    // ************** Paint Component Helper Functions *****************

    public void drawGrid(Graphics g) {
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                g.drawRect(i * tileSize, j * tileSize, tileSize, tileSize);

            }
        }
    }

    public void paintObstacles(Graphics g) {
        for (int i = 0; i < obstacleCount; i++){
            g.fillRect(obstacles.get(i).x * tileSize, obstacles.get(i).y * tileSize, tileSize, tileSize);
        }
    }

    public void paintStart(Graphics g) {
        if (startX != -1 && startY != -1) {
            g.setColor(Color.decode(theme.startColor));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(startX), getCoord(startY), tileSize, tileSize);
    }

    public void paintEnd(Graphics g) {
        if (endX != -1 && endY != -1) {
            g.setColor(Color.decode(theme.endColor));
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(getCoord(endX), getCoord(endY), tileSize, tileSize);
    }

    public void paintOpenSet(Graphics g) {
        for (int i = 0; i < astar.openSet.numItems; i++){
            Node openNode = astar.openSet.items[i];
            if ((openNode == astar.start) || (openNode == astar.end)){
                continue;
            }
            g.fillRect(openNode.x * tileSize, openNode.y * tileSize, tileSize, tileSize);
        }
    }

    public void paintCloseSet(Graphics g) {
        for (Node c : astar.closeSet){
            if (c == astar.start || c == astar.end){
                continue;
            }
            // if the node doesn't have a color assigned, assign it to myColor
            if (c.color.length() == 0) {
                c.color = myColor;
            }
            g.setColor(Color.decode(c.color));
            g.fillRect(c.x * tileSize, c.y * tileSize, tileSize, tileSize);
        }
    }

    public void paintNeighbors(Graphics g) {
        for (Node n : astar.currNeighbors) {
            if (n == astar.start || n == astar.end){
                continue;
            }
            g.fillRect(n.x * tileSize, n.y * tileSize, tileSize, tileSize);
        }
    }

    public void paintPath(Graphics g) {
        if (astar.pathExists) {
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

    // ************** Paint Component Helper Functions END *****************


/*    // Sunset theme
    public void hardCodeColors() {
        colors.add("#f8b195");
        colors.add("#f3a797");
        colors.add("#eb9d99");
        colors.add("#e2959b");
        colors.add("#d78d9e");
        colors.add("#cb879f");
        colors.add("#bd81a1");
        colors.add("#ae7ca1");
        colors.add("#9d77a0");
        colors.add("#8c739e");
        colors.add("#7b6e9a");
        colors.add("#696a95");
        colors.add("#58668e");
        colors.add("#466186");
        colors.add("#355c7d");
        colors.add("#466186");
        colors.add("#58668e");
        colors.add("#696a95");
        colors.add("#7b6e9a");
        colors.add("#8c739e");
        colors.add("#9d77a0");
        colors.add("#ae7ca1");
        colors.add("#bd81a1");
        colors.add("#cb879f");
        colors.add("#d78d9e");
        colors.add("#e2959b");
        colors.add("#eb9d99");

        colors2.add("#f8b195");
        colors2.add("#eb9d99");
        colors2.add("#d78d9e");
        colors2.add("#bd81a1");
        colors2.add("#9d77a0");
        colors2.add("#7b6e9a");
        colors2.add("#58668e");
        colors2.add("#355c7d");
    }*/

    public void mouseMoved (MouseEvent e) { }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    // ******* method to start pathfinder
}
// add labels to dimensions
