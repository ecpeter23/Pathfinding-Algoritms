package PathfindingScripts;

import java.util.*;

public class AStar {
    public int[][] grid;
    public Node targetNode;
    public List<Node> path = new ArrayList<>();

    public AStar(int[][] grid, int startX, int startY, int endX, int endY){
        this.grid = grid;
        targetNode = new Node(endX, endY, 0);


        List<Node> open = new ArrayList<>(); // set of nodes to be evaluated
        List<Node> closed = new ArrayList<>(); // set of nodes already evaluated

        // STEP 1: adds the starting node to open
        open.add(new Node(startX, startY, 0));

        while (true) { // loop

            // STEP 2: create a temp variable called node which is equal to the node in the open list with the lowest fCost
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            Node current = open.stream()
                    .min(Comparator.comparingInt(Node::getFCost))
                    .get();

            // STEP 3: Remove the current node from the open list and add it to the closed list as it is the node we are evaluating
            open.remove(current);
            closed.add(current);

            // STEP 4: If the current node is the target node (end) then we have found the path and can break
            if (current.x == targetNode.x && current.y == targetNode.y) {

                // STEP 6: Now that we have the final node we can just go back through all the parents until we reach the starting node and that is our path
                createPath(current);
                break;
            }

            // STEP 5: Loop for each neighbor of the node, if the node is in closed or not traversable skip else continue
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int baseX = current.x + j;
                    int baseY = current.y + i;

                    // This checks if the cords are off the grid or if the node is in cords or if it isn't traversable
                    if (baseX < 0 || baseY < 0 || baseX >= grid[0].length || baseY >= grid.length || grid[baseY][baseX] == 1 || closed.stream().anyMatch(o -> o.getCords().equals(baseX + " " + baseY))) {
                        continue;
                    }

                    // creates a new neighbor node and calculates its fCost
                    Node tempNode = new Node(baseX, baseY, 0, 0, current);
                    int fCost = getFCost(tempNode);

                    // checks if the node is already present in the open list and if it is checks if the fCost is less then the old node
                    Optional<Node> openNode = open.stream().filter(o -> o.getCords().equals(baseX + " " + baseY)).findFirst();
                    if (openNode.isPresent()) {
                        if (openNode.get().fCost > fCost){ // if the fCost is fewer removes the old node and adds the new node
                            open.remove(openNode.get());
                            open.add(tempNode);
                        }
                    } else {
                        open.add(tempNode);
                    }
                }
            }
        }


        for (int i = 0; i < path.size(); i++){
            if (i == 0){
                grid[path.get(i).y][path.get(i).x] = 4;
            } else if (i == path.size() - 1){
                grid[path.get(i).y][path.get(i).x] = 3;
            } else {
                grid[path.get(i).y][path.get(i).x] = 2;
            }
        }
    }

    private int getFCost(Node node){
        if (node.parent == null) return 0;

        int gCost;
        if (node.x - node.parent.x == 0 || node.y - node.parent.y == 0){ // if this is true the node is in line with the parent
            gCost = node.parent.gCost + 10;
        } else {
            gCost = node.parent.gCost + 14;
        }
        node.setGCost(gCost);


        int hCost = 0;
        int tempX = node.x;
        int tempY = node.y;
        while (tempX != targetNode.x || tempY != targetNode.y){
            int moveX = (targetNode.x - tempX);
            int moveY = (targetNode.y - tempY);

            if (moveX == 0){
                hCost += 10;
                tempY += (moveY > 0)? 1 : -1;
            } else if (moveY == 1){
                hCost += 10;
                tempX += (moveX > 0)? 1 : -1;
            } else {
                hCost += 14;
                tempX += (moveX > 0)? 1 : -1;
                tempY += (moveY > 0)? 1 : -1;
            }
        }
        node.setFCost(gCost + hCost);

        return gCost + hCost;
    }

    private void createPath(Node node){ // IDEA: Could do this with recursion if necessary (loop back for each node until the parent equals null when it will end)
        path.add(node);
        if (node.parent != null){
            createPath(node.parent);
        }
    }

    public static class Node {

        public final int x;
        public final int y;
        public int fCost;
        public int gCost;
        public final Node parent;

        Node(int x, int y, int fCost){
            this.x = x;
            this.y = y;
            this.fCost = fCost;
            gCost = 0;
            parent = null;
        }

        Node(int x, int y, int fCost, int gCost, Node parent){
            this.x = x;
            this.y = y;
            this.fCost = fCost;
            this.gCost = gCost;
            this.parent = parent;
        }

        void setGCost(int value){
            gCost = value;
        }

        void setFCost(int value){
            fCost = value;
        }

        int getFCost(){
            return fCost;
        }

        String getCords(){
            return x + " " + y;
        }
    }
}
