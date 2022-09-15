import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;


public class FlappyBird extends MouseAdapter implements ActionListener, KeyListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public Image img, img1;

    public ArrayList<Rectangle> columns;

    public Random rand = new Random();

    public int ticks, yMotion, score, heighScore;

    public boolean gameOver, started;

    private int cloudXPos = 0, cloudYPos = rand.nextInt(600);

    public FlappyBird() {
        renderer = new Renderer();

        Timer timer = new Timer(20, this);

        JFrame jFrame = new JFrame();

        jFrame.add(renderer);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.addMouseListener(this);
        jFrame.addKeyListener(this);
        jFrame.setResizable(false);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }

    /**
     * Methode that runs again and again and again
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            BufferedImage image = ImageIO.read(new File(new File("").getAbsolutePath() + "\\Files\\FlappyBird.png"));
            img = image;
            BufferedImage image1 = ImageIO.read(new File(new File("").getAbsolutePath() + "\\Files\\Wolken.png"));
            img1 = image1.getScaledInstance(150, 100, 0);
        } catch (IOException a) {
            a.printStackTrace();
        }

        try {
            setHeighScore();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ticks++;
        int speed = 10;

        if (started) {

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

                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    score++;
                }

                if (column.intersects(bird)) {
                    gameOver = true;
                    bird.x = column.x - bird.width;
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }

            if (gameOver) {
                bird.y = HEIGHT - 120 - bird.height;
                try {
                    setHeighScore();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        renderer.repaint();

    }

    /**
     * makes clouds and moves then
     *
     * @param g
     * @throws InterruptedException
     */
    public void clouds(Graphics g) throws InterruptedException {

        if (cloudXPos < -250) {
            cloudYPos = rand.nextInt(600);
            cloudXPos = 800;
        }
        cloudXPos -= 5;
        g.drawImage(img1, cloudXPos, cloudYPos, null);
    }

    /**
     * adds the coordinates of a rectangle (pipes) to a Rectangle array
     *
     * @param start
     */
    public void addColumn(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    /**
     * paints the pipes with the Rectangle coordinates of the array
     *
     * @param g
     * @param column
     */
    public void paintColumn(Graphics g, Rectangle column) {
        g.setColor(Color.GREEN.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    /**
     * paints the game
     *
     * @param g
     */
    public void repaint(Graphics g) {
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(66, 40, 14));
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(new Color(124, 252, 0));
        g.fillRect(0, HEIGHT - 120, WIDTH, 25);

        try {
            clouds(g);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Rectangle column : columns) {
            paintColumn(g, column);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 25));
        g.drawString("Heighscore:" + heighScore, 600, 30);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));


        if (!gameOver && !started) {
            g.drawString("Click to start!", 100, HEIGHT / 2 - 50);
        }

        if (gameOver) {   //bird.y + yMotion >= HEIGHT-120
            g.setColor(Color.RED);
            g.drawString("Game Over!", 75, HEIGHT / 2 - 75);
            g.setColor(Color.WHITE);
            g.drawString("Click to start!", 75, HEIGHT / 2 + 10);
        }

        g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        g.drawImage(img, bird.x, bird.y, null);
    }

    /**
     * makes that the bird jumps
     */
    public void jump() {
        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }

            yMotion -= 10;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    /**
     * sets the Highscore and reads and writs it in to a file
     *
     * @throws IOException
     */
    public void setHeighScore() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "\\Files\\heighscore.txt"));
        heighScore = Integer.parseInt(reader.readLine());
        reader.close();

        if (score > heighScore) {
            heighScore = score;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("").getAbsolutePath() + "\\Files\\heighscore.txt"));
            writer.write(Integer.toString(heighScore));
            writer.flush();
            writer.close();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            jump();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
