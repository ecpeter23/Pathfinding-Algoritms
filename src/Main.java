import javax.swing.*;

public class Main extends JFrame {
    final int size = 500;
    public Main(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(size, size);
        this.setTitle("Pathfinding Demonstration");

        Display display = new Display(size, size);

        this.add(display);
        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);

        display.start();
    }

    public static void main(String[] args)  {
        new Main();
    }
}
