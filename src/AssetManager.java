import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final Map<String, BufferedImage[]> spriteSheetCache = new HashMap<>();

    /**
     * Load a single image with caching
     */
    public static BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            URL url = AssetManager.class.getResource(path);
            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                imageCache.put(path, img);
                return img;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Load and slice a sprite sheet into frames
     */
    public static BufferedImage[] loadSpriteSheet(String path, int frames) {
        if (spriteSheetCache.containsKey(path)) {
            return spriteSheetCache.get(path);
        }

        BufferedImage sheet = loadImage(path);
        if (sheet == null) return null;

        int frameWidth = sheet.getWidth() / frames;
        int frameHeight = sheet.getHeight();
        BufferedImage[] spriteFrames = new BufferedImage[frames];

        for (int i = 0; i < frames; i++) {
            spriteFrames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }

        spriteSheetCache.put(path, spriteFrames);
        return spriteFrames;
    }

    /**
     * Clear all cached assets
     */
    public static void clear() {
        imageCache.clear();
        spriteSheetCache.clear();
    }
}
