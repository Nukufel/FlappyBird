import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;


public class FlappyBird  implements ActionListener {

    public static  FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public ArrayList<Rectangle> columns;

    public Random rand;

    public int ticks, yMotion;

    public boolean gameOver, started = true;

    public FlappyBird(){
        rand = new Random();

        renderer = new Renderer();

        Timer timer = new Timer(20, this);

        JFrame jFrame = new JFrame();

        jFrame.add(renderer);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setVisible(true);

        columns = new ArrayList<Rectangle>();
        bird = new Rectangle(WIDTH/2-10, HEIGHT /2-100, 20, 20);

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ticks++;
        int speed = 10;

        if(started) {

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column);
                    if (column.y == 0) {
                        addColumn(false);
                    }

                }
            }

            bird.y += yMotion;

            for (Rectangle column : columns) {
                if (column.intersects(bird)) {
                    gameOver = true;
                    bird.x = column.x - bird.width;
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }

            if(gameOver){
                bird.y = HEIGHT -120 -bird.height;
            }
        }

        renderer.repaint();

    }

    public void addColumn(boolean start){
        int space = 300;
        int width = 100;
        int hight = 50 + rand.nextInt(300);

        if(start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - hight - 120, width, hight));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - hight - space));
        }else{
            columns.add(new Rectangle(columns.get(columns.size()-1).x +600 + width + columns.size() * 300, HEIGHT - hight - 120, width, hight));
            columns.add(new Rectangle(columns.get(columns.size()-1).x + width + (columns.size() - 1) * 300, 0, width, HEIGHT - hight - space));
        }
    }


    public void paintColumn(Graphics g, Rectangle column){
        g.setColor(Color.GREEN.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void repaint(Graphics g) {
        g.setColor(Color.cyan);
        g.fillRect(0,0,WIDTH, HEIGHT);

        g.setColor(new Color(66,40,14));
        g.fillRect(0, HEIGHT -120, WIDTH, 120);

        g.setColor(new Color(124, 252, 0));
        g.fillRect(0, HEIGHT -120, WIDTH, 25);

        g.setColor(Color.RED );
        g.fillRect(bird.x ,bird.y, bird.width, bird.height);

        for (Rectangle column : columns){
            paintColumn(g, column);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));
        if (gameOver) {
            g.drawString("Game Over!", 100, HEIGHT /2-50);
        }
    }

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }



}
