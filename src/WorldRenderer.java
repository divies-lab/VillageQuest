import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class WorldRenderer {
    private BufferedImage grassV, grassO, monasteryImg, tealWater, treeImg;
    private BufferedImage wallH, wallVR, wallVL, cornerL, cornerR;
    private BufferedImage npcPortrait;
    private BufferedImage[] waterFoam;

    public WorldRenderer() {
        loadAssets();
    }

    private void loadAssets() {
        // Tiles
        BufferedImage tileMap = AssetManager.loadImage("/Tilemap_color1.png");
        if (tileMap != null) {
            grassV = tileMap.getSubimage(16, 16, 16, 16);
            grassO = tileMap.getSubimage(32, 16, 16, 16);
        }

        // Static images
        monasteryImg = AssetManager.loadImage("/Monastery.png");
        tealWater = AssetManager.loadImage("/Water Background color.png");
        npcPortrait = AssetManager.loadImage("/NPC_Portrait.png");

        // Trees
        BufferedImage trees = AssetManager.loadImage("/Trees.png");
        if (trees != null) treeImg = trees.getSubimage(0, 0, 48, 80);

        // Walls
        wallH = AssetManager.loadImage("/Castle_wall1.png");
        wallVR = AssetManager.loadImage("/Castle_wall2.png");
        wallVL = AssetManager.loadImage("/Castle_vertical3.png");
        cornerL = AssetManager.loadImage("/Castle_corner4.png");
        cornerR = AssetManager.loadImage("/Castle_corner1.png");

        // Water animation
        waterFoam = AssetManager.loadSpriteSheet("/Water_Foam.png", GameConstants.WATER_FOAM_FRAMES);
    }

    public void drawBackground(Graphics2D g2, int camX, int camY) {
        int tileSize = GameConstants.TILE_SIZE;
        int screenW = GameConstants.SCREEN_WIDTH;
        int screenH = GameConstants.SCREEN_HEIGHT;

        for (int x = (camX / tileSize) * tileSize; x < camX + screenW + tileSize; x += tileSize) {
            for (int y = (camY / tileSize) * tileSize; y < camY + screenH + tileSize; y += tileSize) {
                boolean inMonastery = x > GameConstants.MONASTERY_MIN_X && x < GameConstants.MONASTERY_MAX_X
                        && y > GameConstants.MONASTERY_MIN_Y && y < GameConstants.MONASTERY_MAX_Y;
                BufferedImage tile = inMonastery ? grassV : grassO;
                if (tile != null) {
                    g2.drawImage(tile, x - camX, y - camY, tileSize, tileSize, null);
                }
            }
        }
    }

    public void drawGameObjects(Graphics2D g2, List<GameObject> objects, int camX, int camY,
                                int foamFrame, BufferedImage[] bushFrames, int bushFrame,
                                BufferedImage[] duckFrames, int duckFrame) {
        for (GameObject obj : objects) {
            int screenX = obj.getX() - camX;
            int screenY = obj.getY() - camY;

            switch (obj.type) {
                case "water" -> {
                    if (tealWater != null) {
                        g2.drawImage(tealWater, screenX, screenY, obj.getWidth(), obj.getHeight(), null);
                    }
                    if (waterFoam != null && waterFoam.length > 0) {
                        g2.drawImage(waterFoam[foamFrame], screenX, screenY,
                                GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, null);
                    }
                }
                case "tree" -> {
                    if (treeImg != null) {
                        g2.drawImage(treeImg, screenX - 10, screenY - 60, 64, 96, null);
                    }
                }
                case "bush" -> {
                    if (bushFrames != null && bushFrame < bushFrames.length) {
                        g2.drawImage(bushFrames[bushFrame], screenX, screenY, 64, 64, null);
                    }
                }
                case "duck" -> {
                    if (duckFrames != null && duckFrame < duckFrames.length) {
                        g2.drawImage(duckFrames[duckFrame], screenX, screenY, 48, 48, null);
                    }
                }
                case "wall_h", "wall_v_l", "wall_v_r", "corner_L", "corner_R" -> drawWall(g2, obj, screenX, screenY);
            }
        }
    }

    private void drawWall(Graphics2D g2, GameObject obj, int screenX, int screenY) {
        BufferedImage wallImg = getWallImage(obj.type);
        if (wallImg != null) {
            for (int wx = 0; wx < obj.getWidth(); wx += GameConstants.TILE_SIZE) {
                for (int wy = 0; wy < obj.getHeight(); wy += GameConstants.TILE_SIZE) {
                    g2.drawImage(wallImg, screenX + wx, screenY + wy,
                            GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, null);
                }
            }
        }
    }

    private BufferedImage getWallImage(String type) {
        return switch (type) {
            case "wall_h" -> wallH;
            case "wall_v_l" -> wallVL;
            case "wall_v_r" -> wallVR;
            case "corner_L" -> cornerL;
            case "corner_R" -> cornerR;
            default -> null;
        };
    }

    public void drawMonastery(Graphics2D g2, int camX, int camY) {
        if (monasteryImg != null) {
            int screenX = GameConstants.MONASTERY_X - camX;
            int screenY = GameConstants.MONASTERY_Y - camY;
            g2.drawImage(monasteryImg, screenX, screenY,
                    GameConstants.MONASTERY_W, GameConstants.MONASTERY_H, null);
        }
    }

    public void drawNPC(Graphics2D g2, BufferedImage[] npcFrames, int npcFrame, int camX, int camY) {
        if (npcFrames != null && npcFrame < npcFrames.length) {
            int screenX = GameConstants.ELRIC_X - camX;
            int screenY = GameConstants.ELRIC_Y - camY;
            g2.drawImage(npcFrames[npcFrame], screenX, screenY,
                    GameConstants.ELRIC_WIDTH, GameConstants.ELRIC_HEIGHT, null);
        }
    }

    public void drawDialogueBox(Graphics2D g2, String npcName, String dialogueLine) {
        // Background
        g2.setColor(new Color(255, 255, 255, 240));
        g2.fillRoundRect(GameConstants.DIALOGUE_BOX_X, GameConstants.DIALOGUE_BOX_Y,
                GameConstants.DIALOGUE_BOX_WIDTH, GameConstants.DIALOGUE_BOX_HEIGHT, 20, 20);

        // Border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(GameConstants.DIALOGUE_BOX_X, GameConstants.DIALOGUE_BOX_Y,
                GameConstants.DIALOGUE_BOX_WIDTH, GameConstants.DIALOGUE_BOX_HEIGHT, 20, 20);

        // Portrait
        if (npcPortrait != null) {
            g2.drawImage(npcPortrait, GameConstants.DIALOGUE_PORTRAIT_X, GameConstants.DIALOGUE_PORTRAIT_Y,
                    GameConstants.DIALOGUE_PORTRAIT_SIZE, GameConstants.DIALOGUE_PORTRAIT_SIZE, null);
        }

        // NPC Name
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(npcName, GameConstants.DIALOGUE_TEXT_START_X, GameConstants.DIALOGUE_NAME_Y);

        // Dialogue text
        g2.setFont(new Font("Serif", Font.PLAIN, 20));
        g2.drawString(dialogueLine, GameConstants.DIALOGUE_TEXT_START_X, GameConstants.DIALOGUE_TEXT_Y);
    }
}
