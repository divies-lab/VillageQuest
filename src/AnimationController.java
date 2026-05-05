import java.awt.image.BufferedImage;

public class AnimationController {
    private BufferedImage[] frames;
    private int frameDelay;
    private int counter;
    private int currentFrameIndex;

    public AnimationController(BufferedImage[] frames, int frameDelay) {
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.counter = 0;
        this.currentFrameIndex = 0;
    }

    public void update() {
        if (frames == null || frames.length == 0) return;

        counter++;
        if (counter > frameDelay) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.length;
            counter = 0;
        }
    }

    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.length == 0) return null;
        return frames[currentFrameIndex];
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    public void reset() {
        counter = 0;
        currentFrameIndex = 0;
    }
}
