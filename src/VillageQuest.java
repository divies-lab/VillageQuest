import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class VillageQuest extends JPanel implements Runnable, KeyListener, MouseListener {

    private GameWorld gameWorld;
    private Player player;
    private WorldRenderer renderer;
    private DialogueManager dialogue;

    // Animation controllers
    private AnimationController playerWalkAnimator;
    private AnimationController elricIdleAnimator;
    private AnimationController waterFoamAnimator;
    private AnimationController bushIdleAnimator;
    private AnimationController duckIdleAnimator;

    // Sprite frames
    private BufferedImage[] elricIdle, bushIdle, duckIdle;

    // Camera
    private int camX, camY;

    // Thread
    private Thread gameThread;
    private Clip musicClip;

    public VillageQuest() {
        this.setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);

        loadAllAssets();
        initializeGame();
        setCustomCursor();
    }

    private void loadAllAssets() {
        try {
            // Player animation
            BufferedImage[] playerWalk = AssetManager.loadSpriteSheet("/Warrior_Run.png", GameConstants.PLAYER_WALK_FRAMES);
            playerWalkAnimator = new AnimationController(playerWalk, GameConstants.PLAYER_WALK_DELAY);

            // NPC animation
            elricIdle = AssetManager.loadSpriteSheet("/Elric_Idle.png", GameConstants.ELRIC_IDLE_FRAMES);
            elricIdleAnimator = new AnimationController(elricIdle, GameConstants.ELRIC_IDLE_DELAY);

            // Water animation
            BufferedImage[] waterFoam = AssetManager.loadSpriteSheet("/Water_Foam.png", GameConstants.WATER_FOAM_FRAMES);
            waterFoamAnimator = new AnimationController(waterFoam, GameConstants.WATER_FOAM_DELAY);

            // Bush animation
            bushIdle = AssetManager.loadSpriteSheet("/Bushes.png", GameConstants.BUSH_IDLE_FRAMES);
            bushIdleAnimator = new AnimationController(bushIdle, GameConstants.BUSH_IDLE_DELAY);

            // Duck animation
            duckIdle = AssetManager.loadSpriteSheet("/Duck.png", GameConstants.DUCK_IDLE_FRAMES);
            duckIdleAnimator = new AnimationController(duckIdle, GameConstants.DUCK_IDLE_DELAY);

        } catch (Exception e) {
            System.out.println("Asset loading error: " + e.getMessage());
        }
    }

    private void initializeGame() {
        gameWorld = new GameWorld();
        player = new Player(playerWalkAnimator);
        renderer = new WorldRenderer();
        dialogue = new DialogueManager();
    }

    private void playBackgroundMusic(String filename) {
        try {
            URL url = this.getClass().getResource("/" + filename);
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.out.println("Music loading error: " + e.getMessage());
        }
    }

    private void setCustomCursor() {
        try {
            BufferedImage cursorImg = AssetManager.loadImage(GameConstants.CURSOR_FILE);
            if (cursorImg != null) {
                Cursor customCursor = Toolkit.getDefaultToolkit()
                        .createCustomCursor(cursorImg, new Point(0, 0), "Custom");
                this.setCursor(customCursor);
            }
        } catch (Exception e) {
            System.out.println("Cursor loading error: " + e.getMessage());
        }
    }

    public void update() {
        if (dialogue.isActive()) {
            return; // Don't update game state during dialogue
        }

        // Update player
        player.update();

        // Check collisions
        if (gameWorld.checkCollision(player.getCollisionBox())) {
            // Revert position (simple collision response)
            player.setPosition(player.getWorldX(), player.getWorldY());
        }

        // Update animations
        elricIdleAnimator.update();
        waterFoamAnimator.update();
        bushIdleAnimator.update();
        duckIdleAnimator.update();

        // Update camera to follow player
        camX = player.getWorldX() - (GameConstants.SCREEN_WIDTH / 2);
        camY = player.getWorldY() - (GameConstants.SCREEN_HEIGHT / 2);

        // Check dialogue trigger distance
        double distToElric = Math.sqrt(
                Math.pow(player.getWorldX() - GameConstants.ELRIC_X, 2) +
                Math.pow(player.getWorldY() - GameConstants.ELRIC_Y, 2)
        );

        if (distToElric < GameConstants.ELRIC_DIALOGUE_DISTANCE && !dialogue.isFinished()) {
            dialogue.startDialogue();
        } else if (distToElric > GameConstants.ELRIC_DIALOGUE_RESET_DISTANCE) {
            dialogue.resetDialogue();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw world layers
        renderer.drawBackground(g2, camX, camY);
        renderer.drawGameObjects(g2, gameWorld.getObjects(), camX, camY,
                waterFoamAnimator.getCurrentFrameIndex(), bushIdle, bushIdleAnimator.getCurrentFrameIndex(),
                duckIdle, duckIdleAnimator.getCurrentFrameIndex());
        renderer.drawMonastery(g2, camX, camY);
        renderer.drawNPC(g2, elricIdle, elricIdleAnimator.getCurrentFrameIndex(), camX, camY);

        // Draw player on top
        player.draw(g2, camX, camY);

        // Draw dialogue if active
        if (dialogue.isActive()) {
            renderer.drawDialogueBox(g2, dialogue.getNPCName(), dialogue.getCurrentLine());
        }

        g2.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle dialogue advancement with SPACE or ENTER
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) && dialogue.isActive()) {
            dialogue.advanceLine();
            return;
        }

        // Handle player movement
        player.handleKeyInput(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.handleKeyInput(e.getKeyCode(), false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dialogue.isActive()) {
            dialogue.advanceLine();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        playBackgroundMusic(GameConstants.MUSIC_FILE);
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Village Quest");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new VillageQuest());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
