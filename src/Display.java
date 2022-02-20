import PathfindingScripts.AStar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Display extends JPanel implements Runnable{
    Thread thread;

    int[][] grid;
    int state = 0;

    final int tileSize = 5;
    int visibleMapSizeX;
    int visibleMapSizeY;

    int FPS = 30; // FPS
    int waitFrames = 10;
    int counter = 0;

    boolean mouseClicked = false;
    Point mousePos;

    int sX = -1;
    int sY = -1;
    int eX = -1;
    int eY = -1;

    boolean pathCreated = false;

    public Display(int width, int height){
        this.setPreferredSize(new Dimension(width, height));

        visibleMapSizeX = (int)(width/(double)tileSize);
        visibleMapSizeY = (int)(height/(double)tileSize);
        grid = new int[visibleMapSizeY][visibleMapSizeX];

        this.setBackground(Color.white);
        this.setDoubleBuffered(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                mouseClicked = true;
                mousePos = new Point(e.getX(), e.getY());
            }
        });
    }

    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("Running");

        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (thread != null){ // game loop
            counter = (counter + 1) % waitFrames;

            if (Keyboard.isKeyPressed(10) && counter == 0) state = (state + 1) % 3;

            if (mouseClicked){
                int tempMouseX = (int)(mousePos.x / (double)tileSize);
                int tempMouseY = (int)(mousePos.y / (double)tileSize);

                if (sX == -1){
                    grid[tempMouseY][tempMouseX] = 3;
                    sX = tempMouseX;
                    sY = tempMouseY;
                } else if (eX == -1) {
                    grid[tempMouseY][tempMouseX] = 4;
                    eX = tempMouseX;
                    eY = tempMouseY;
                } else {
                    if (grid[tempMouseY][tempMouseX] == 1){
                        grid[tempMouseY][tempMouseX] = 0;
                    } else if (grid[tempMouseY][tempMouseX] == 3){
                        grid[tempMouseY][tempMouseX] = 0;
                        sX = -1;
                        sY = -1;
                    } else if (grid[tempMouseY][tempMouseX] == 4){
                        grid[tempMouseY][tempMouseX] = 0;
                        eX = -1;
                        eY = -1;
                    } else{
                        grid[tempMouseY][tempMouseX] = 1;
                    }
                }

                mouseClicked = false;
            }

            switch (state) {
                case 0:
                    break;
                case 1:
                    if (!pathCreated) {
                        AStar aStar = new AStar(grid, sX, sY, eX, eY);
                        grid = aStar.grid;
                        pathCreated = true;
                    }
                    break;
                case 2:
                    pathCreated = false;
                    sX = -1;
                    sY = -1;
                    eX = -1;
                    eY = -1;
                    grid = new int[visibleMapSizeY][visibleMapSizeX];
                    state = 0;
                    break;
            }

            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = clamp0(remainingTime / 1000000);

                Thread.sleep((long)remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g){
        Graphics2D graphics = (Graphics2D) g;

        for (int y = 0; y < visibleMapSizeY; y++){
            for (int x = 0; x < visibleMapSizeX; x++){

                if (grid[y][x] == 1){
                    graphics.setColor(Color.BLACK);
                } else if (grid[y][x] == 2){
                    graphics.setColor(Color.BLUE);
                } else if (grid[y][x] == 3){
                    graphics.setColor(Color.GREEN);
                }else if (grid[y][x] == 4){
                    graphics.setColor(Color.RED);
                } else {
                    graphics.setColor(Color.white);
                }

                graphics.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        graphics.dispose();
    }

    private double clamp0(double value){
        if (value < 0){
            return 0;
        } else{
            return value;
        }
    }
}
