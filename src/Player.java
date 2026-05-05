import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player {
    private int worldX, worldY;
    private int speed = GameConstants.PLAYER_SPEED;
    private String direction = "right";

    private boolean up, down, left, right;
    private AnimationController animator;

    public Player(AnimationController animator) {
        this.worldX = GameConstants.PLAYER_START_X;
        this.worldY = GameConstants.PLAYER_START_Y;
        this.animator = animator;
    }

    public void handleKeyInput(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_W) up = pressed;
        if (keyCode == KeyEvent.VK_S) down = pressed;
        if (keyCode == KeyEvent.VK_A) {
            left = pressed;
            if (pressed) direction = "left";
        }
        if (keyCode == KeyEvent.VK_D) {
            right = pressed;
            if (pressed) direction = "right";
        }
    }

    public void update() {
        animator.update();

        int nextX = worldX;
        int nextY = worldY;

        if (up) nextY -= speed;
        if (down) nextY += speed;
        if (left) nextX -= speed;
        if (right) nextX += speed;

        worldX = nextX;
        worldY = nextY;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int screenX = worldX - camX;
        int screenY = worldY - camY;

        BufferedImage frame = animator.getCurrentFrame();
        if (frame != null) {
            if (direction.equals("left")) {
                g2.drawImage(frame, screenX + GameConstants.PLAYER_WIDTH, screenY,
                        -GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT, null);
            } else {
                g2.drawImage(frame, screenX, screenY,
                        GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT, null);
            }
        }
    }

    public Rectangle getCollisionBox() {
        return new Rectangle(worldX + 32, worldY + 64, 32, 20);
    }

    public boolean isMoving() {
        return up || down || left || right;
    }

    // Getters
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }
    public String getDirection() { return direction; }

    // Setters
    public void setPosition(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }
}
