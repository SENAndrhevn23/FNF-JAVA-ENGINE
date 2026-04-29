package Source;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainMenu extends JPanel implements KeyListener, Runnable {

    private JFrame frame;
    private boolean running = true;

    private String state = "MAIN";
    private int selected = 0;

    // =========================
    // GAME SETTINGS
    // =========================
    public static boolean botPlay = false;
    public static boolean numberPopups = true;
    public static boolean commaOnThirdDigits = true;
    public static boolean renderMode = false;
    public static int targetFPS = 60;

    public static int extraKeysCount = 4;
    public static boolean infiniteKeys = false;

    // =========================
    // OPTIMIZATIONS (FROM PLAYSTATE)
    // =========================
    public static boolean noteSkipping = true;
    public static boolean notePooling = true;
    public static boolean disableComboPopups = false;
    public static boolean disableRgbNoteColors = false;
    public static boolean disableGarbageCollectorLag = false;
    public static boolean disablePreHitEvents = false;
    public static boolean bulkSkipping = true;
    public static boolean showCounters = true;
    public static boolean disableStrumAnimations = false;
    public static boolean showNotes = true;
    public static int maxShownNotes = 0;
    public static boolean removeOverlappedSystem = true;
    public static boolean disableGpuCatching = false;
    public static boolean disableMultithreadingCatching = false;
    public static boolean disableVsync = false;
    public static double noteDensityMs = 1.0;

    // =========================
    // MENUS
    // =========================
    private final String[] mainMenu = {"Freeplay", "Options"};
    private final String[] optionsMenu = {"Gameplay", "Optimizations", "Controls", "Extra Keys"};

    private final String[] gameplayMenu = {"BotPlay"};

    private final String[] optimizationsMenu = {
            "Note Pooling",
            "Note Skipping",
            "Disable Combo Popups",
            "Disable RGB Notes",
            "Disable GC Lag",
            "Bulk Skipping",
            "Show Counters",
            "Disable Strum Animations",
            "Show Notes"
    };

    private final String[] extraKeysMenu = {"Key Count", "Back"};

    // =========================
    // INPUT BINDS
    // =========================
    private final int[] bindCodes = {
            KeyEvent.VK_D, KeyEvent.VK_F, KeyEvent.VK_J, KeyEvent.VK_K
    };
    private final String[] bindLabels = {"Left", "Down", "Up", "Right"};

    // =========================
    // FPS
    // =========================
    private int fps = 0;
    private int frames = 0;
    private long lastTime = System.nanoTime();

    // =========================
    // GRAPHICS
    // =========================
    private Font fnfFont;
    private Font hudFont;

    private BufferedImage bgMain, bgOptions, imgFreeplay, imgOptions;

    public MainMenu() {
        loadFont();
        loadImages();

        setPreferredSize(new Dimension(1280, 720));
        setFocusable(true);
        addKeyListener(this);

        frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        requestFocusInWindow();
        new Thread(this).start();
    }

    private void loadFont() {
        fnfFont = new Font("Arial", Font.BOLD, 48);
        hudFont = new Font("Arial", Font.PLAIN, 18);
    }

    private BufferedImage loadImg(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadImages() {
        String base = "Assets/Images/";
        bgMain = loadImg(base + "menuBG.png");
        bgOptions = loadImg(base + "menuBGMagenta.png");
        imgFreeplay = loadImg(base + "Freeplay.png");
        imgOptions = loadImg(base + "Option.png");
    }

    @Override
    public void run() {
        while (running) {
            repaint();

            frames++;
            long now = System.nanoTime();
            if (now - lastTime >= 1_000_000_000L) {
                fps = frames;
                frames = 0;
                lastTime = now;
            }

            try {
                Thread.sleep(1000 / Math.max(1, targetFPS));
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (state.equals("MAIN")) {
            drawBG(g2, bgMain);
            drawMainMenu(g2);
        } else {
            drawBG(g2, bgOptions);
            drawOptionsMenu(g2);
        }

        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("FPS: " + fps, 20, 30);
    }

    private void drawBG(Graphics2D g2, BufferedImage img) {
        if (img != null) g2.drawImage(img, 0, 0, 1280, 720, null);
        else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, 1280, 720);
        }
    }

    private void drawMainMenu(Graphics2D g2) {
        g2.setFont(fnfFont);

        for (int i = 0; i < mainMenu.length; i++) {
            g2.setColor(i == selected ? Color.YELLOW : Color.WHITE);
            g2.drawString(mainMenu[i], 500, 300 + (i * 80));
        }
    }

    private void drawOptionsMenu(Graphics2D g2) {
        String[] menu = getMenu();
        if (menu == null) return;

        g2.setFont(fnfFont);

        for (int i = 0; i < menu.length; i++) {
            g2.setColor(i == selected ? Color.YELLOW : Color.WHITE);
            String text = menu[i];

            if (state.equals("GAMEPLAY")) {
                text = "BotPlay: " + (botPlay ? "ON" : "OFF");
            }

            if (state.equals("OPTIMIZATIONS")) {
                switch (i) {
                    case 0 -> text += ": " + onOff(notePooling);
                    case 1 -> text += ": " + onOff(noteSkipping);
                    case 2 -> text += ": " + onOff(disableComboPopups);
                    case 3 -> text += ": " + onOff(disableRgbNoteColors);
                    case 4 -> text += ": " + onOff(disableGarbageCollectorLag);
                    case 5 -> text += ": " + onOff(bulkSkipping);
                    case 6 -> text += ": " + onOff(showCounters);
                    case 7 -> text += ": " + onOff(disableStrumAnimations);
                    case 8 -> text += ": " + onOff(showNotes);
                }
            }

            g2.drawString(text, 300, 250 + (i * 70));
        }
    }

    private String onOff(boolean b) {
        return b ? "ON" : "OFF";
    }

    private String[] getMenu() {
        return switch (state) {
            case "MAIN" -> mainMenu;
            case "OPTIONS" -> optionsMenu;
            case "GAMEPLAY" -> gameplayMenu;
            case "OPTIMIZATIONS" -> optimizationsMenu;
            case "CONTROLS" -> bindLabels;
            case "EXTRA_KEYS" -> extraKeysMenu;
            default -> null;
        };
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        String[] menu = getMenu();

        if (key == KeyEvent.VK_DOWN) selected++;
        if (key == KeyEvent.VK_UP) selected--;

        if (menu != null) {
            if (selected < 0) selected = menu.length - 1;
            if (selected >= menu.length) selected = 0;
        }

        if (key == KeyEvent.VK_ENTER) {
            if (state.equals("MAIN")) {
                if (selected == 0) {
                    frame.dispose();
                    new FreeplayState();
                } else {
                    state = "OPTIONS";
                    selected = 0;
                }
            }
            else if (state.equals("OPTIONS")) {
                if (selected == 0) state = "GAMEPLAY";
                if (selected == 1) state = "OPTIMIZATIONS";
                if (selected == 2) state = "CONTROLS";
                if (selected == 3) state = "EXTRA_KEYS";
                selected = 0;
            }
            else if (state.equals("GAMEPLAY")) {
                botPlay = !botPlay;
            }
            else if (state.equals("OPTIMIZATIONS")) {
                switch (selected) {
                    case 0 -> notePooling = !notePooling;
                    case 1 -> noteSkipping = !noteSkipping;
                    case 2 -> disableComboPopups = !disableComboPopups;
                    case 3 -> disableRgbNoteColors = !disableRgbNoteColors;
                    case 4 -> disableGarbageCollectorLag = !disableGarbageCollectorLag;
                    case 5 -> bulkSkipping = !bulkSkipping;
                    case 6 -> showCounters = !showCounters;
                    case 7 -> disableStrumAnimations = !disableStrumAnimations;
                    case 8 -> showNotes = !showNotes;
                }
            }
        }

        if (key == KeyEvent.VK_BACK_SPACE) {
            state = state.equals("MAIN") ? "MAIN" : "OPTIONS";
            selected = 0;
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
