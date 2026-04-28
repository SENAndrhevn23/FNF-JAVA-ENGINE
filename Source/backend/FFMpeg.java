package backend;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class FFMpeg {

    private Process ffmpegProcess;
    private OutputStream ffmpegInput;

    private int width;
    private int height;
    private String outputPath;

    public void init(int width, int height, String name) {
        this.width = width;
        this.height = height;

        outputPath = "render_" + name + "_" + System.currentTimeMillis() + ".mp4";
    }

    public void setup(boolean audio) throws IOException {

        String[] cmd = {
                "ffmpeg",
                "-y",
                "-f", "rawvideo",
                "-vcodec", "rawvideo",
                "-pix_fmt", "argb",
                "-s", width + "x" + height,
                "-r", "60",
                "-i", "-",
                "-an",
                "-vcodec", "libx264",
                "-pix_fmt", "yuv420p",
                outputPath
        };

        System.out.println("Starting FFmpeg: " + Arrays.toString(cmd));

        ffmpegProcess = new ProcessBuilder(cmd)
                .redirectErrorStream(true)
                .start();

        ffmpegInput = new BufferedOutputStream(ffmpegProcess.getOutputStream());
    }

    // Send frame to ffmpeg
    public void pipeFrame(java.awt.image.BufferedImage img) {
        try {
            if (ffmpegInput == null) return;

            int[] pixels = new int[img.getWidth() * img.getHeight()];
            img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

            for (int p : pixels) {
                ffmpegInput.write((p) & 0xFF);
                ffmpegInput.write((p >> 8) & 0xFF);
                ffmpegInput.write((p >> 16) & 0xFF);
                ffmpegInput.write((p >> 24) & 0xFF);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finish rendering
    public String finish() {
        try {
            if (ffmpegInput != null) {
                ffmpegInput.flush();
                ffmpegInput.close();
            }

            if (ffmpegProcess != null) {
                ffmpegProcess.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Render saved: " + outputPath);
        return outputPath;
    }
}