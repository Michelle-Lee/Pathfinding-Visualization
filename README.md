# Pathfinding-Visualization
This is an A* Pathfinding algorithm implementation and visualization using Java and Swing. I created this personal project for fun, but I also wanted to practice building a creative GUI and implementing pathfinding algorithms and data structures. Though this project is finished for the most part, I will be adding more features and making improvements such as updating the controls GUI, adding more algorithms, trying different heuristics, and more.

A* is an optimal and complete algorithm that utilizes heuristic functions to guide its search, making it more efficient in comparison to Dijkstra's algorithm and others.

Currently, there are two GUI files: Frame.java and FrameV2.java
Frame.java is a simpler GUI implementation, whereas in FrameV2, I took some creative liberties to have fun with different themes in the visualization.


## Controls
* The first click on a tile sets a starting point
* The second click on a tile sets an end point
* Dragging the mouse creates walls/obstacles
* Press the **Start** button to begin the pathfinding.
* NOTE: the animation speed can be sped up, but was reduced for demonstration purposes

![main-demo](demo/AStar_Demo.gif)

<br><br>
### FrameV2
FrameV2.java contains fun themes I've added to my project. I experimented with using the animation timers to interact with the circular linked lists (containing color palettes) to create different visual effects with color patterns, gradients, and light flashes.
<br><br>
**Sunrise** <br><br>
![sunrise-demo](demo/AStar_Demo_Sunrise.gif)
<br><br>
**Dragon Ball Z** <br><br>
![dbz-demo](demo/AStar_Demo_DBZ.gif)
