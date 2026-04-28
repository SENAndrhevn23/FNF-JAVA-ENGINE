import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main extends JPanel implements KeyListener, Runnable {

    private JFrame frame;

    private int sickBeats = 0;
    private boolean closedState = false;

    private ArrayList<String> text = new ArrayList<>();

    private boolean ngVisible = false;

    // FPS SYSTEM
    private int fps = 0;
    private int frames = 0;
    private long lastTime = System.nanoTime();

    public Main() {
        frame = new JFrame("FNF Java Engine - Intro");

        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);

        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.requestFocus();

        // beat system
        Timer beatTimer = new Timer(500, e -> beatHit());
        beatTimer.start();

        // render loop (FPS)
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    // 🎮 GAME LOOP (FPS)
    @Override
    public void run() {
        while (true) {
            repaint();
            frames++;

            long now = System.nanoTime();
            if (now - lastTime >= 1_000_000_000) {
                fps = frames;
                frames = 0;
                lastTime = now;
            }

            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }

    public void beatHit() {
        if (closedState) return;

        sickBeats++;

        switch (sickBeats) {
            case 1: text.clear(); break;
            case 2: createCoolText("Java Engine by"); break;
            case 4: addMoreText("Andre Nicholas Jr"); break;
            case 5: deleteCoolText(); break;
            case 6:
                createCoolText("Not associated with");
                addMoreText("Friday Night Funkin");
                break;
            case 8:
                addMoreText("Newgrounds");
                ngVisible = true;
                break;
            case 9:
                deleteCoolText();
                ngVisible = false;
                break;
            case 17:
                skipIntro();
                break;
        }

        repaint();
    }

    public void createCoolText(String t) {
        text.clear();
        text.add(t);
    }

    public void addMoreText(String t) {
        text.add(t);
    }

    public void deleteCoolText() {
        text.clear();
    }

    public void skipIntro() {
        closedState = true;
        frame.dispose();

        // LOAD MAIN MENU (FIXED)
        new Source.MainMenu();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            skipIntro();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1280, 720);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        int y = 300;
        for (String s : text) {
            g.drawString(s, 420, y);
            y += 60;
        }

        // TOP LEFT DEBUG HUD
        g.setFont(new Font("Arial", Font.PLAIN, 20));

        long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;

        g.drawString("FPS: " + fps, 20, 30);
        g.drawString("MEM: " + usedMem + "MB", 20, 55);

        if (ngVisible) {
            g.drawString("NG LOGO", 20, 80);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}