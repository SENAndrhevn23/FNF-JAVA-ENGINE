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
    // OPTIMIZATIONS (USED BY PLAYSTATE)
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
            "Disable PreHit Events",
            "Bulk Skipping",
            "Show Counters",
            "Disable Strum Animations",
            "Show Notes",
            "Remove Overlap System",
            "Disable GPU Catching",
            "Disable Multithread Catching",
            "Disable VSync",
            "Note Density"
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
    private Font fnfFont = new Font("Arial", Font.BOLD, 48);
    private Font hudFont = new Font("Arial", Font.PLAIN, 18);

    private BufferedImage bgMain, bgOptions, imgFreeplay, imgOptions;

    public MainMenu() {
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
            drawMenu(g2, mainMenu);
        } else {
            drawBG(g2, bgOptions);
            drawMenu(g2, getMenu());
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

    private void drawMenu(Graphics2D g2, String[] menu) {
        if (menu == null) return;

        g2.setFont(fnfFont);

        for (int i = 0; i < menu.length; i++) {
            g2.setColor(i == selected ? Color.YELLOW : Color.WHITE);

            String text = menu[i];

            if (state.equals("GAMEPLAY")) {
                text = "BotPlay: " + onOff(botPlay);
            }

            if (state.equals("OPTIMIZATIONS")) {
                text += ": " + getOptimizationValue(i);
            }

            if (state.equals("EXTRA_KEYS") && i == 0) {
                text = "Key Count: " + (infiniteKeys ? "INF" : extraKeysCount);
            }

            g2.drawString(text, 280, 240 + (i * 60));
        }
    }

    private String getOptimizationValue(int i) {
        return switch (i) {
            case 0 -> onOff(notePooling);
            case 1 -> onOff(noteSkipping);
            case 2 -> onOff(disableComboPopups);
            case 3 -> onOff(disableRgbNoteColors);
            case 4 -> onOff(disableGarbageCollectorLag);
            case 5 -> onOff(disablePreHitEvents);
            case 6 -> onOff(bulkSkipping);
            case 7 -> onOff(showCounters);
            case 8 -> onOff(disableStrumAnimations);
            case 9 -> onOff(showNotes);
            case 10 -> onOff(removeOverlappedSystem);
            case 11 -> onOff(disableGpuCatching);
            case 12 -> onOff(disableMultithreadingCatching);
            case 13 -> onOff(disableVsync);
            case 14 -> String.format("%.2f", noteDensityMs);
            default -> "";
        };
    }

    private String onOff(boolean b) {
        return b ? "ON" : "OFF";
    }

    private String[] getMenu() {
        return switch (state) {
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
        String[] menu = state.equals("MAIN") ? mainMenu : getMenu();

        if (key == KeyEvent.VK_DOWN) selected++;
        if (key == KeyEvent.VK_UP) selected--;

        if (menu != null) {
            if (selected < 0) selected = menu.length - 1;
            if (selected >= menu.length) selected = 0;
        }

        // ENTER
        if (key == KeyEvent.VK_ENTER) {
            handleEnter();
        }

        // BACK
        if (key == KeyEvent.VK_BACK_SPACE) {
            handleBack();
        }

        // LEFT/RIGHT for density
        if (state.equals("OPTIMIZATIONS") && selected == 14) {
            if (key == KeyEvent.VK_LEFT) {
                noteDensityMs = Math.max(0.1, noteDensityMs - 0.1);
            }
            if (key == KeyEvent.VK_RIGHT) {
                noteDensityMs = Math.min(5.0, noteDensityMs + 0.1);
            }
        }

        repaint();
    }

    private void handleEnter() {
        switch (state) {
            case "MAIN" -> {
                if (selected == 0) {
                    frame.dispose();
                    new FreeplayState();
                } else {
                    state = "OPTIONS";
                    selected = 0;
                }
            }
            case "OPTIONS" -> {
                state = switch (selected) {
                    case 0 -> "GAMEPLAY";
                    case 1 -> "OPTIMIZATIONS";
                    case 2 -> "CONTROLS";
                    case 3 -> "EXTRA_KEYS";
                    default -> state;
                };
                selected = 0;
            }
            case "GAMEPLAY" -> botPlay = !botPlay;

            case "OPTIMIZATIONS" -> toggleOptimization(selected);
        }
    }

    private void toggleOptimization(int i) {
        switch (i) {
            case 0 -> notePooling = !notePooling;
            case 1 -> noteSkipping = !noteSkipping;
            case 2 -> disableComboPopups = !disableComboPopups;
            case 3 -> disableRgbNoteColors = !disableRgbNoteColors;
            case 4 -> disableGarbageCollectorLag = !disableGarbageCollectorLag;
            case 5 -> disablePreHitEvents = !disablePreHitEvents;
            case 6 -> bulkSkipping = !bulkSkipping;
            case 7 -> showCounters = !showCounters;
            case 8 -> disableStrumAnimations = !disableStrumAnimations;
            case 9 -> showNotes = !showNotes;
            case 10 -> removeOverlappedSystem = !removeOverlappedSystem;
            case 11 -> disableGpuCatching = !disableGpuCatching;
            case 12 -> disableMultithreadingCatching = !disableMultithreadingCatching;
            case 13 -> disableVsync = !disableVsync;
        }
    }

    private void handleBack() {
        state = switch (state) {
            case "MAIN" -> "MAIN";
            case "OPTIONS" -> "MAIN";
            default -> "OPTIONS";
        };
        selected = 0;
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
