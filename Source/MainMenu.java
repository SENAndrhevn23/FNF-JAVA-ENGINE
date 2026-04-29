package Source;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

public class MainMenu extends JPanel implements KeyListener, Runnable {
    private JFrame frame;
    private String state = "MAIN";
    private int selected = 0;

    public static boolean botPlay = false;
    public static boolean numberPopups = true;
    public static boolean commaOnThirdDigits = true;
    public static boolean renderMode = false;
    public static int targetFPS = 60;

    public static int extraKeysCount = 4;
    public static boolean infiniteKeys = false;

    // Optimizations
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
    public static int maxShownNotes = 0; // 0 = unlimited
    public static boolean removeOverlappedSystem = true;
    public static boolean disableGpuCatching = false;
    public static boolean disableMultithreadingCatching = false;
    public static boolean disableVsync = false;
    public static double noteDensityMs = 1.00; // higher = looser density window
    public static boolean shareableSettingsSystem = false;

    private static final int[] KEY_PRESETS = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 40, 50, 60, 70, 80, 90, 100, 105, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 9999
    };
    private static int extraKeysPresetIndex = 3;

    private final int[] bindCodes = {
            KeyEvent.VK_D,
            KeyEvent.VK_F,
            KeyEvent.VK_J,
            KeyEvent.VK_K
    };
    private final String[] bindLabels = {"Left", "Down", "Up", "Right"};
}
