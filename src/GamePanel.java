import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {

    private BufferedImage background;
    private ArrayList<GameCharacter> characters = new ArrayList<>();
    private GameCharacter playerCharacter;
    private int currentMap = 1;

    

    private int cameraX = 0;
    private int cameraY = 0;

    

    private long lastPlayerHitTime = 0;
    private final int ENEMY_DAMAGE = 1;
    private final int DAMAGE_COOLDOWN = 1000; 



    public GamePanel() {
        loadMap(currentMap);
        setPreferredSize(new Dimension(1600, 1000));

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new java.awt.event.KeyAdapter() {


            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (playerCharacter == null) return;
                double speed = 10;

               
               
               
               
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_W -> {
                        playerCharacter.y -= speed;
                        playerCharacter.image = playerCharacter.upImg != null ? playerCharacter.upImg : playerCharacter.image;
                    }
                    case java.awt.event.KeyEvent.VK_S -> {
                        playerCharacter.y += speed;
                        playerCharacter.image = playerCharacter.downImg != null ? playerCharacter.downImg : playerCharacter.image;
                    }
                    case java.awt.event.KeyEvent.VK_A -> {
                        playerCharacter.x -= speed;
                        playerCharacter.image = playerCharacter.leftImg != null ? playerCharacter.leftImg : playerCharacter.image;
                    }
                    case java.awt.event.KeyEvent.VK_D -> {
                        playerCharacter.x += speed;
                        playerCharacter.image = playerCharacter.rightImg != null ? playerCharacter.rightImg : playerCharacter.image;
                    }
                    case java.awt.event.KeyEvent.VK_SPACE -> playerAttack();
                }

                
                
                
                if (playerCharacter.x < 0) playerCharacter.x = 0;
                if (playerCharacter.y < 0) playerCharacter.y = 0;
                if (background != null) {
                    if (playerCharacter.x + playerCharacter.image.getWidth() > background.getWidth())
                        playerCharacter.x = background.getWidth() - playerCharacter.image.getWidth();
                    if (playerCharacter.y + playerCharacter.image.getHeight() > background.getHeight())
                        playerCharacter.y = background.getHeight() - playerCharacter.image.getHeight();
                }

             
                checkNextMapTrigger();
                updateCamera();
                updateEnemies();
                repaint();
            }
        });
    }

    public void setCurrentMap(int map) { this.currentMap = map; }
    public int getCurrentMap() { return currentMap; }

    private void playerAttack() {
        if (playerCharacter == null) return;

        double attackRange = 50;
        ArrayList<GameCharacter> defeated = new ArrayList<>();

        for (GameCharacter enemy : characters) {
            if (enemy.isEnemy) {
                double dx = enemy.x - playerCharacter.x;
                double dy = enemy.y - playerCharacter.y;
                double distance = Math.sqrt(dx*dx + dy*dy);
                if (distance <= attackRange) {
                    enemy.health--;
                    if (enemy.health <= 0) defeated.add(enemy);
                }
            }
        }

        characters.removeAll(defeated);
    }

    private void updateEnemies() {
        if (playerCharacter == null) return;


        double speed = 2; 
        for (GameCharacter enemy : characters) {
            if (enemy.isEnemy) {
                


                double dx = playerCharacter.x - enemy.x;
                double dy = playerCharacter.y - enemy.y;
                double distance = Math.sqrt(dx*dx + dy*dy);

                if (distance > 1) {
                    enemy.x += (dx / distance) * speed;
                    enemy.y += (dy / distance) * speed;
                }

                

                if (dx > 0 && enemy.rightImg != null) enemy.image = enemy.rightImg;
                else if (dx < 0 && enemy.leftImg != null) enemy.image = enemy.leftImg;

                


                if (distance <= 50) {
                    long now = System.currentTimeMillis();
                    if (now - lastPlayerHitTime >= DAMAGE_COOLDOWN) {
                        playerCharacter.health -= ENEMY_DAMAGE;
                        lastPlayerHitTime = now;

                        if (playerCharacter.health <= 0) {
                            JOptionPane.showMessageDialog(this, "You died! Game Over.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    private void checkNextMapTrigger() {
        if (playerCharacter == null || background == null) return;

        if (playerCharacter.x + playerCharacter.image.getWidth() >= background.getWidth()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Go to the next map?",
                    "Next Map",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                nextMap();
            } else {
                playerCharacter.x = background.getWidth() - playerCharacter.image.getWidth() - 1;
            }
        }
    }

   
   
    private void updateCamera() {
        if (playerCharacter == null || background == null) return;

        cameraX = (int)playerCharacter.x - getWidth() / 2;
        cameraY = (int)playerCharacter.y - getHeight() / 2;

        cameraX = Math.max(0, Math.min(cameraX, background.getWidth() - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, background.getHeight() - getHeight()));
    }

   
   
    private void loadMap(int mapNumber) {
        try {
            characters.clear();
            switch (mapNumber) {
                case 1 -> loadMap1();
                case 2 -> loadMap2();
                case 3 -> loadMap3();
                default -> {
                    JOptionPane.showMessageDialog(this, "All maps cleared! You win!");
                    System.exit(0);
                }
            }
            updateCamera();
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading map " + mapNumber + ": " + e.getMessage());
            System.exit(1);
        }
    }

    
   
   
    private void loadMap1() throws IOException {
        background = scaleImage(loadImageSafe("res\\daan.png"), 3500, 1500);
        loadExtraSafe("res\\water.png", 610, 360, false);
        loadExtraSafe("res\\water.png", 1550, 940, false);
        loadExtraSafe("res\\Sprite-0007.png", 1450, 360, true);
        loadExtraWithCollisionSafe("res\\puno.png", 200, 1000);
        loadExtraWithCollisionSafe("res\\bato.png", 1600, 350);

       
       
        loadCharacters(); 
    }

   
    private void loadMap2() throws IOException {
        background = scaleImage(loadImageSafe("res\\daan.png2.png"), 1500, 1500);
        loadExtraSafe("res\\water.png", 610, 360, false);
        loadExtraSafe("res\\water.png", 1550, 940, false);
        loadExtraSafe("res\\puno.png", 1450, 360, true);
        loadExtraWithCollisionSafe("res\\puno.png", 200, 1000);
        loadExtraWithCollisionSafe("res\\bato.png", 1600, 350);

        loadCharacters();
    }

   
   
    private void loadMap3() throws IOException {
        background = scaleImage(loadImageSafe("res\\map3.png"), 2500, 1500);
        loadExtraSafe("res\\water.png", 1200, 700, false);
        loadExtraSafe("res\\water.png", 500, 400, true);
        loadExtraWithCollisionSafe("res\\puno.png", 700, 500);

        loadCharacters();
        loadCharactersMap3Boss();
    }

    
   
   
    private void loadCharacters() throws IOException {
        
        if (playerCharacter == null) {
            BufferedImage up = loadImageSafe("res\\likod_charter.png");
            BufferedImage down = loadImageSafe("res\\charter.png");
            BufferedImage left = loadImageSafe("res\\side1.png");
            BufferedImage right = loadImageSafe("res\\attack2.png");
            playerCharacter = new GameCharacter(900, 500, up, down, left, right, true);
            characters.add(playerCharacter);
        } else {
            characters.add(playerCharacter);
        }

        
      
        if (characters.stream().noneMatch(c -> c.isEnemy)) {
            BufferedImage enemy1 = loadImageSafe("res\\enemy1_blue.png");
            BufferedImage enemy2 = loadImageSafe("res\\yellow.png");
            characters.add(new GameCharacter(800, 400, enemy1, false, true));
            characters.add(new GameCharacter(700, 600, enemy2, false, true));
        }
    }

   
   
    private void loadCharactersMap3Boss() throws IOException {
        boolean bossExists = characters.stream().anyMatch(c -> "BigBoss".equals(c.name));
        if (!bossExists) {
            BufferedImage bossImg = loadImageSafe("res\\big_boss.png");
            GameCharacter bigBoss = new GameCharacter(1400, 600, bossImg, false, true);
            bigBoss.health = 10;
            bigBoss.maxHealth = 10;
            bigBoss.name = "BigBoss";
            characters.add(bigBoss);
        }

       
       
        BufferedImage minionImg = loadImageSafe("res\\big_boss.png");
        double[][] positions = {{1200, 500}, {1500, 500}};
        for (double[] pos : positions) {
            boolean exists = characters.stream().anyMatch(c -> c.x == pos[0] && c.y == pos[1]);
            if (!exists) {
                GameCharacter minion = new GameCharacter(pos[0], pos[1], minionImg, false, true);
                minion.health = 5;
                minion.maxHealth = 5;
                characters.add(minion);
            }
        }
    }

    
   
   
    private BufferedImage loadImageSafe(String resource) throws IOException {
        try {
            return makeWhiteTransparent(ImageIO.read(new File(resource)));
        } catch (IOException e) {
            System.err.println("Missing resource: " + resource);
            BufferedImage placeholder = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(Color.MAGENTA);
            g2.fillRect(0, 0, 50, 50);
            g2.dispose();
            return placeholder;
        }
    }

    private void loadExtraSafe(String resource, double x, double y, boolean interactive) {
        try {
            BufferedImage img = loadImageSafe(resource);
            GameCharacter obj = new GameCharacter(x, y, img, false); // obstacles are NOT player/enemy
            obj.collidable = interactive;
            characters.add(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExtraWithCollisionSafe(String resource, double x, double y) {
        try {
            BufferedImage img = loadImageSafe(resource);
            GameCharacter obj = new GameCharacter(x, y, img, false);
            obj.collidable = true;
            characters.add(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage makeWhiteTransparent(BufferedImage img) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                if ((rgba & 0x00FFFFFF) == 0xFFFFFF) rgba &= 0x00FFFFFF;
                newImg.setRGB(x, y, rgba);
            }
        }
        return newImg;
    }

    private BufferedImage scaleImage(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return scaled;
    }

   
   
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null)
            g.drawImage(background, -cameraX, -cameraY, null);

        for (GameCharacter c : characters) {
            g.drawImage(c.image, (int)c.x - cameraX, (int)c.y - cameraY, null);

            
           
           
            if (c.isPlayer || c.isEnemy) {
                int barWidth = 40;
                int barHeight = 6;
                int barX = (int) c.x - cameraX + c.image.getWidth()/2 - barWidth/2;
                int barY = (int) c.y - cameraY - 10;

                double healthPercent = (double) c.health / c.maxHealth;
                int greenWidth = (int) (barWidth * healthPercent);

                g.setColor(Color.RED);
                g.fillRect(barX, barY, barWidth, barHeight);

                g.setColor(Color.GREEN);
                g.fillRect(barX, barY, greenWidth, barHeight);

                g.setColor(Color.BLACK);
                g.drawRect(barX, barY, barWidth, barHeight);
            }
        }
    }

    
   
   
    class GameCharacter {
        double x, y;
        BufferedImage image;
        boolean isPlayer;
        boolean isEnemy;
        boolean collidable;
        String name;
        int health = 3;
        int maxHealth = 3;

        BufferedImage upImg, downImg, leftImg, rightImg;

       
      
        GameCharacter(double x, double y, BufferedImage up, BufferedImage down, BufferedImage left, BufferedImage right, boolean isPlayer) {
            this.x = x;
            this.y = y;
            this.upImg = up;
            this.downImg = down;
            this.leftImg = left;
            this.rightImg = right;
            this.image = down;
            this.isPlayer = isPlayer;
            this.maxHealth = 5;
            this.health = 5;
        }

        
        GameCharacter(double x, double y, BufferedImage img, boolean isPlayer) {
            this.x = x;
            this.y = y;
            this.image = img;
            this.isPlayer = isPlayer;
        }

        
        GameCharacter(double x, double y, BufferedImage img, boolean isPlayer, boolean isEnemy) {
            this(x, y, img, isPlayer);
            this.isEnemy = isEnemy;
            this.maxHealth = 3;
            this.health = 3;
        }
    }

    
    public void nextMap() {
        currentMap++;
        loadMap(currentMap);
        repaint();
    }

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("The Adventure of the Slime Wars");
        GamePanel panel = new GamePanel();
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
