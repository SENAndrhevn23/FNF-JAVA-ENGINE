package Source;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FreeplayState extends JPanel implements KeyListener {

    private JFrame frame;

    private File songsDir = new File("Assets/Songs");
    private File[] songs;

    private int selected = 0;

    public FreeplayState() {

        // 🔥 SAFE LOADING
        if (!songsDir.exists()) {
            System.out.println("Songs folder not found: " + songsDir.getAbsolutePath());
            songs = new File[0];
        } else {
            songs = songsDir.listFiles(File::isDirectory);

            if (songs == null) {
                songs = new File[0];
            }
        }

        System.out.println("Loaded songs: " + songs.length);

        frame = new JFrame("Freeplay");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);
        frame.setVisible(true);

        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.requestFocus();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0,0,1280,720);

        g.setFont(new Font("Arial", Font.BOLD, 40));

        if (songs.length == 0) {
            g.setColor(Color.RED);
            g.drawString("NO SONGS FOUND", 450, 350);
            return;
        }

        for (int i = 0; i < songs.length; i++) {
            if (i == selected) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);

            g.drawString(songs[i].getName(), 500, 200 + (i * 60));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (songs.length == 0) return; // 🔥 prevents crash

        if (e.getKeyCode() == KeyEvent.VK_DOWN) selected++;
        if (e.getKeyCode() == KeyEvent.VK_UP) selected--;

        if (selected < 0) selected = songs.length - 1;
        if (selected >= songs.length) selected = 0;

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            frame.dispose();
            new PlayState(songs[selected].getName());
        }

        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            frame.dispose();
            new MainMenu();
        }

        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}