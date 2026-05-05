import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
    private List<GameObject> objects;

    public GameWorld() {
        objects = new ArrayList<>();
        initializeWorld();
    }

    private void initializeWorld() {
        // Monastery walls and corners
        int vX = 500, vY = 500, vSize = 1000;

        // Top wall
        objects.add(new GameObject(vX + 64, vY, 400, 64, "wall_h"));
        objects.add(new GameObject(vX + 600, vY, 400, 64, "wall_h"));

        // Bottom wall
        objects.add(new GameObject(vX + 64, vY + vSize, vSize - 64, 64, "wall_h"));

        // Side walls
        objects.add(new GameObject(vX, vY + 64, 64, vSize - 64, "wall_v_l"));
        objects.add(new GameObject(vX + vSize, vY + 64, 64, vSize - 64, "wall_v_r"));

        // Corners
        objects.add(new GameObject(vX, vY, 64, 64, "corner_L"));
        objects.add(new GameObject(vX + vSize, vY, 64, 64, "corner_R"));
        objects.add(new GameObject(vX, vY + vSize, 64, 64, "corner_L"));
        objects.add(new GameObject(vX + vSize, vY + vSize, 64, 64, "corner_R"));

        // Water
        objects.add(new GameObject(GameConstants.WATER_X, GameConstants.WATER_Y,
                GameConstants.WATER_WIDTH, GameConstants.WATER_HEIGHT, "water"));

        // Interactive objects
        objects.add(new GameObject(GameConstants.DUCK_X, GameConstants.DUCK_Y,
                GameConstants.DUCK_WIDTH, GameConstants.DUCK_HEIGHT, "duck"));
        objects.add(new GameObject(GameConstants.BUSH_X, GameConstants.BUSH_Y,
                GameConstants.BUSH_WIDTH, GameConstants.BUSH_HEIGHT, "bush"));

        // Randomly placed trees
        Random random = new Random();
        for (int i = 0; i < GameConstants.TREE_SPAWN_COUNT; i++) {
            int tx = random.nextInt(GameConstants.WORLD_SIZE);
            int ty = random.nextInt(GameConstants.WORLD_SIZE);

            // Don't place trees inside monastery
            if (!(tx > GameConstants.MONASTERY_MIN_X && tx < GameConstants.MONASTERY_MAX_X &&
                  ty > GameConstants.MONASTERY_MIN_Y && ty < GameConstants.MONASTERY_MAX_Y)) {
                objects.add(new GameObject(tx, ty, 40, 20, "tree"));
            }
        }
    }

    public boolean checkCollision(Rectangle playerBounds) {
        for (GameObject obj : objects) {
            // Skip non-collidable objects
            if (obj.type.equals("bush") || obj.type.equals("duck") || obj.type.equals("water")) {
                continue;
            }

            if (playerBounds.intersects(obj.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public List<GameObject> getObjects() {
        return objects;
    }
}
