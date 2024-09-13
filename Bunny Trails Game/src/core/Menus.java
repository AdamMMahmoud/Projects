package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

public class Menus {
    private static int width = 0;
    private static int height = 0;
    private static final int FONT_SIZE = 16;
    public static final int FONT_SIZE_BIG = 30;
    public static final int FONT_SIZE_BIGGER = 50;
    private final StringBuilder seedInput;
    private boolean isInstructionsMenu;
    private boolean isEnteringSeed;
    private boolean isChoosingPlayers;
    private boolean isChoosingColor;
    private boolean isChoosingColorPlayer2;
    private boolean gameStarted;
    private long seed;
    private int numPlayers;
    private Color player1Color;
    private Color player2Color;
    private JFrame titleFrame;
    public static Font dogica = new FontManager("Resources/Fonts/dogicapixelbold.otf", FONT_SIZE_BIG).getFont();
    public static Font dogicaBig = new FontManager("Resources/Fonts/dogicapixelbold.otf", FONT_SIZE_BIGGER).getFont();

    public Menus(int width, int height) {
        Menus.width = width;
        Menus.height = height;
        StdDraw.setCanvasSize(width * FONT_SIZE, height * FONT_SIZE);
        StdDraw.setFont(dogica);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.seedInput = new StringBuilder();
        this.isInstructionsMenu = false;
        this.isEnteringSeed = false;
        this.isChoosingPlayers = false;
        this.isChoosingColor = false;
        this.isChoosingColorPlayer2 = false;
        this.gameStarted = false;
    }

    public void displayMenu() {

        titleFrame = new JFrame("Bunny Trails");
        titleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titleFrame.setSize(width * 16, height * 16 + 48);
        titleFrame.setLocationRelativeTo(null);
        titleFrame.setUndecorated(true);

        ImageIcon gif = new ImageIcon("Resources/Images/Screens/MainMenu.gif");
        JLabel label = new JLabel(gif);

        label.setBorder(BorderFactory.createEmptyBorder(48, 0, 0, 0));
        titleFrame.add(label);
        titleFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_N) {
                    isChoosingPlayers = true;
                    closeGIF();
                    promptForPlayers();
                } else if (key == KeyEvent.VK_Q) {
                    System.exit(0);
                } else if (key == KeyEvent.VK_I) {
                    isInstructionsMenu = true;
                    closeGIF();
                    displayInstructionMenu();
                }
            }
        });

        titleFrame.setVisible(true);
        titleFrame.requestFocusInWindow();
    }

    public void handleUserInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                handleUserInput(input);
                if (isGameStarted()) {
                    closeGIF();
                    break;
                }
            }
        }
    }

    public void handleUserInput(char input) {
        if (isInstructionsMenu && !isEnteringSeed && !gameStarted && !isChoosingPlayers && !isChoosingColor && !isChoosingColorPlayer2) {
            if (input == 'i' || input == 'I') {
                isInstructionsMenu = false;
                StdDraw.clear();
                StdDraw.show();
                displayMenu();
            }
        }
        if (!isEnteringSeed && !gameStarted && !isChoosingPlayers && !isChoosingColor && !isChoosingColorPlayer2 && !isInstructionsMenu) {
            if (input == 'n' || input == 'N') {
                this.isChoosingPlayers = true;
                promptForPlayers();
            } else if (input == 'q' || input == 'Q') {
                System.exit(0);
            }
            else if (input == 'i' || input == 'I') {
                isInstructionsMenu = true;
                displayInstructionMenu();
            }
        } else if (!isChoosingColor && !isChoosingColorPlayer2 && isChoosingPlayers) {
            if (input == '1') {
                numPlayers = 1;
                isChoosingColor = true;
                isChoosingPlayers = false;
                promptForColor();
            } else if (input == '2') {
                numPlayers = 2;
                isChoosingColor = true;
                isChoosingPlayers = false;
                promptForColor();
            }
        } else if (isChoosingColor) {
            player1Color = getColorFromInput(input);
            if (player1Color != null) {
                isChoosingColor = false;
                if (numPlayers == 2) {
                    isChoosingColorPlayer2 = true;
                    promptForColor();
                } else {
                    isEnteringSeed = true;
                    promptForSeed();
                }
            }
        } else if (isChoosingColorPlayer2) {
            player2Color = getColorFromInput(input);
            if (player2Color != null) {
                isChoosingColorPlayer2 = false;
                isEnteringSeed = true;
                promptForSeed();
            }
        } else if (isEnteringSeed) {
            if (Character.isDigit(input)) {
                if (seedInput.length() < 18) {
                    seedInput.append(input);
                    promptForSeed();
                }
            } else if (input == '\b' && !seedInput.isEmpty()) {
                seedInput.deleteCharAt(seedInput.length() - 1);
                promptForSeed();
            } else if (input == 'S' || input == 's') {
                if (!seedInput.isEmpty()) {
                    seed = Long.parseLong(seedInput.toString());
                    isEnteringSeed = false;
                    gameStarted = true;
                }
            }
        }
    }

    public void displayInstructionMenu() {
        StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/InstructionsMenu.png");
        StdDraw.show();
    }

    public void promptForSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/SeedMenu.png");
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new FontManager("Resources/Fonts/dogicapixelbold.otf", 44).getFont());
        StdDraw.text(width / 2.0, 9.8, seedInput.toString());
        StdDraw.show();
    }

    public void promptForPlayers() {
        StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/PlayerSelection.png");
        StdDraw.show();
    }

    public void promptForColor() {
        if (isChoosingColor && !isChoosingColorPlayer2) {
            StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/ColorSelectP1.png");
            StdDraw.show();
        } else if (!isChoosingColor && isChoosingColorPlayer2) {
            StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/ColorSelectP2.png");
            StdDraw.show();
        }

        StdDraw.setPenColor(new Color(68, 198, 215));
        StdDraw.text(width / 2.0, height / 2.0 + 7, "Blue (b)");

        StdDraw.setPenColor(new Color(198, 72, 43));
        StdDraw.text(width / 2.0, height / 2.0 + 3, "Red (r)");

        StdDraw.setPenColor(new Color(110, 68, 243));
        StdDraw.text(width / 2.0, height / 2.0 - 1, "Purple (p)");

        StdDraw.setPenColor(new Color(215, 69, 217));
        StdDraw.text(width / 2.0, height / 2.0 - 5, "Magenta (m)");

        StdDraw.setPenColor(Color.white);
        StdDraw.text(width / 2.0, height / 2.0 - 9, "White (w)");

        StdDraw.setPenColor(new Color(254, 167, 62));
        StdDraw.text(width / 2.0, height / 2.0 - 13, "Orange (o)");

        StdDraw.show();
    }

    private Color getColorFromInput(char input) {
        return switch (Character.toUpperCase(input)) {
            case 'B' -> new Color(68, 198, 215);
            case 'R' -> new Color(198, 72, 43);
            case 'P' -> new Color(110, 68, 243);
            case 'M' -> new Color(215, 69, 217);
            case 'W' -> Color.WHITE;
            case 'O' -> new Color(254, 167, 62);
            default -> null;
        };
    }

    public static void displayHUD(TETile[][] world, World worldGenerator, String customMessage) {
        if (Main.overrideMode) {
            StdDraw.setPenColor(new Color(81, 129, 63));
            StdDraw.filledRectangle(width / 2.0, height - 0.5, width / 2.0 + 1, .6);
            StdDraw.setPenColor(Color.BLACK);
            Font dogicaSmall = new FontManager("Resources/Fonts/dogicapixelbold.otf", 12).getFont();
            StdDraw.setFont(dogicaSmall);

            String overrideMessage = "TILE  OVERRIDE  IN  PROGRESS:  Click  any  tile  to  turn  it  into  a  FLOOR  tile";
            StdDraw.text(width / 2.0, height - 1, overrideMessage);

            StdDraw.show();
            return;
        }

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        Avatar player1 = worldGenerator.getAvatar(0);
        Avatar player2 = World.getNumPlayers() > 1 ? worldGenerator.getAvatar(1) : null;

        StdDraw.setPenColor(new Color(81, 129, 63));
        StdDraw.filledRectangle(width / 2.0, height - 0.5, width / 2.0 + 1, .6);
        StdDraw.setPenColor(Color.WHITE);
        Font dogicaSmall = new FontManager("Resources/Fonts/dogicapixelbold.otf", 12).getFont();
        StdDraw.setFont(dogicaSmall);

        String centerText = customMessage != null && !customMessage.isEmpty() ? customMessage : "";

        if (player1.hasMindControl()) {
            StdDraw.setPenColor(Color.BLACK);
            Font dogicaBig = new FontManager("Resources/Fonts/dogicapixelbold.otf", 16).getFont();
            StdDraw.setFont(dogicaBig);
            double timeLeft = (player1.getMindControlEndTime() - System.currentTimeMillis()) / 1000.0;
            centerText = String.format("PLAYER 2 IS MIND CONTROLLED! %.1fs LEFT", timeLeft);
        } else if (player2 != null && player2.hasMindControl()) {
            StdDraw.setPenColor(Color.BLACK);
            Font dogicaBig = new FontManager("Resources/Fonts/dogicapixelbold.otf", 16).getFont();
            StdDraw.setFont(dogicaBig);
            double timeLeft = (player2.getMindControlEndTime() - System.currentTimeMillis()) / 1000.0;
            centerText = String.format("PLAYER 1 IS MIND CONTROLLED! %.1fs LEFT", timeLeft);
        } else {
            if (customMessage == null || customMessage.isEmpty()) {
                String tileDescription = "";
                if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
                    tileDescription = "Tile: " + world[mouseX][mouseY].description();
                }
                String roundText = "Round " + Main.round;
                centerText = roundText + "  |  " + tileDescription;
            }
        }
        StdDraw.text(width / 2.0, height - 1, centerText);

        StdDraw.setPenColor(player1.getColor());
        StdDraw.setFont(dogicaSmall);
        StringBuilder player1Info = new StringBuilder("Player 1: " + player1.getPoints());
        if (player1.hasCarrotBoost()) {
            double timeLeft = (player1.getCarrotBoostEndTime() - System.currentTimeMillis()) / 1000.0;
            player1Info.append(String.format("   |   Carrot Boost: %.1fs", timeLeft));
        }
        if (player1.isTrapped()) {
            double timeLeft = (player1.getTrapEndTime() - System.currentTimeMillis()) / 1000.0;
            player1Info.append(String.format("   |   Trap: %.1fs", timeLeft));
        }
        StdDraw.textLeft(.5, height - 1, player1Info.toString());

        if (World.getNumPlayers() > 1) {
            assert player2 != null;
            StdDraw.setPenColor(player2.getColor());
            StringBuilder player2Info = new StringBuilder();
            if (player2.isTrapped()) {
                double timeLeft = (player2.getTrapEndTime() - System.currentTimeMillis()) / 1000.0;
                player2Info.append(String.format("Trap: %.1fs   |   ", timeLeft));
            }
            if (player2.hasCarrotBoost()) {
                double timeLeft = (player2.getCarrotBoostEndTime() - System.currentTimeMillis()) / 1000.0;
                player2Info.append(String.format("Carrot Boost: %.1fs   |   ", timeLeft));
            }
            player2Info.append("Player 2: ").append(player2.getPoints());
            StdDraw.textRight(width - .5, height - 1, player2Info.toString());
        } else {
            HighScoreManager highScoreManager = new HighScoreManager();
            int highScore = highScoreManager.getHighScore();

            long currentTimeMillis = System.currentTimeMillis();
            double currentTimeSeconds = (currentTimeMillis - Main.startTime) / 1000.0;

            String highScoreText = highScoreManager.hasHighScore()
                    ? String.format("High Score: %.2fs", highScore / 1000.0)
                    : "No high score set yet";

            String rightSideText = String.format("Current Time: %.1fs   |   %s", currentTimeSeconds, highScoreText);
            StdDraw.textRight(width - .5, height - 1, rightSideText);        }
        StdDraw.show();
    }

    public void displayInstructionsScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/InstructionsScreen.png");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    public void displayRoundScreen(int round) {
        titleFrame = new JFrame("Round " + round);
        titleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titleFrame.setSize(width * 16, height * 16 + 48);
        titleFrame.setLocationRelativeTo(null);
        titleFrame.setUndecorated(true);

        ImageIcon gif = new ImageIcon("Resources/Images/Screens/Round" + round + ".gif");
        JLabel label = new JLabel(gif);

        label.setBorder(BorderFactory.createEmptyBorder(48, 0, 0, 0));
        titleFrame.add(label);
        titleFrame.setVisible(true);
        titleFrame.requestFocusInWindow();

        try {
            Thread.sleep(2200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            closeGIF();
        }
    }

    public void displayStandingsScreen(World worldGenerator) {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(width / 2.0, height / 2.0, "Resources/Images/Screens/StandingsScreen.png");
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(dogica);

        int player1Points = worldGenerator.getAvatar(0).getPoints();
        int player2Points = World.getNumPlayers() > 1 ? worldGenerator.getAvatar(1).getPoints() : 0;

        if (World.getNumPlayers() == 1) {
            double currentTime = (System.currentTimeMillis() - Main.startTime) / 1000.0;
            String formattedTime = String.format("%.1f", currentTime);
            StdDraw.text(width / 2.0, height / 2.0 + 2, "Current Time: " + formattedTime + "s");
            String highScoreText = Main.highScoreTime == Integer.MAX_VALUE
                    ? "No high score set yet"
                    : String.format("High Score Time: %.1fs", Main.highScoreTime / 1.0);
            StdDraw.text(width / 2.0, height / 2.0 - 3, highScoreText);
        } else {
            StdDraw.text(width / 2.0, height / 2.0 + 3, "Current Standings");
            StdDraw.text(width / 2.0, height / 2.0 - 1, "Player 1: " + player1Points + " pts.");
            StdDraw.text(width / 2.0, height / 2.0 - 5, "Player 2: " + player2Points + " pts.");
        }
        StdDraw.show();
        StdDraw.pause(3000);
        StdDraw.clear();
        StdDraw.show();
    }

    public void displayWinnerScreen(World worldGenerator) {
        int player1Points = worldGenerator.getAvatar(0).getPoints();
        int player2Points = World.getNumPlayers() > 1 ? worldGenerator.getAvatar(1).getPoints() : 0;

        String winnerText;
        Color winnerColor;
        if (player1Points > player2Points) {
            winnerText = "Player 1 wins!";
            winnerColor = player1Color;
        } else {
            winnerText = "Player 2 wins!";
            winnerColor = player2Color;
        }

        String bunnyColor;
        if (winnerColor.equals(new Color(68, 198, 215))) {
            bunnyColor = "Blue";
        } else if (winnerColor.equals(new Color(198, 72, 43))) {
            bunnyColor = "Red";
        } else if (winnerColor.equals(new Color(110, 68, 243))) {
            bunnyColor = "Purple";
        } else if (winnerColor.equals(new Color(215, 69, 217))) {
            bunnyColor = "Magenta";
        } else if (winnerColor.equals(Color.WHITE)) {
            bunnyColor = "White";
        } else if (winnerColor.equals(new Color(254, 167, 62))) {
            bunnyColor = "Orange";
        } else {
            bunnyColor = "White";
        }

        String gifPath = "Resources/Images/Screens/WinScreen" + bunnyColor + ".gif";
        String player1Score = "Player 1: " + player1Points + " pts.";
        String player2Score = World.getNumPlayers() > 1 ? "Player 2: " + player2Points + " pts." : "";

        titleFrame = new JFrame("Winner Screen");
        titleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titleFrame.setSize(width * 16, height * 16 + 48);
        titleFrame.setLocationRelativeTo(null);
        titleFrame.setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon gif = new ImageIcon(gifPath);
                gif.paintIcon(this, g, 0, 48);

                g.setColor(Color.BLACK);
                g.setFont(dogicaBig);
                g.drawString(winnerText, getWidth() / 2 - g.getFontMetrics().stringWidth(winnerText) / 2,  250);

                g.setFont(dogica);
                g.drawString(player1Score, getWidth() / 2 - g.getFontMetrics().stringWidth(player1Score) / 2, getHeight() - 175);
                if (!player2Score.isEmpty()) {
                    g.drawString(player2Score, getWidth() / 2 - g.getFontMetrics().stringWidth(player2Score) / 2, getHeight() - 125);
                }
                g.drawString("Quit (q)", getWidth() / 2 - g.getFontMetrics().stringWidth("Quit (q)") / 2, getHeight() - 50);
            }
        };

        titleFrame.add(panel);

        titleFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_Q) {
                    System.exit(0);
                }
            }
        });
        titleFrame.setVisible(true);
        titleFrame.requestFocusInWindow();

        handleEndGameInput();
    }

    private void handleEndGameInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'q' || input == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    public void displayHighScoreScreen(long time, boolean isNewHighScore) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(dogica);

        if (isNewHighScore) {
            StdDraw.text(width / 2.0, height / 2.0 + 6, "You beat your high score!");
            StdDraw.text(width / 2.0, height / 2.0 + 2, "New High Score: " + (time / 1000.0) + " seconds");
        } else {
            StdDraw.text(width / 2.0, height / 2.0 + 6, "Game Over!");
            StdDraw.text(width / 2.0, height / 2.0 + 2, "Time: " + String.format("%.2f", time / 1000.0) + "s");
        }
        HighScoreManager highScoreManager = new HighScoreManager();
        int highScore = highScoreManager.getHighScore();
        StdDraw.text(width / 2.0, height / 2.0 - 2, "Previous Best: " + String.format("%.2fs", highScore / 1000.0));
        StdDraw.text(width / 2.0, height / 2.0 - 6, "Press [q] to quit");

        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'Q' || input == 'q') {
                    System.exit(0);
                }
            }
        }
    }

    public void closeGIF() {
        if (titleFrame != null) {
            titleFrame.dispose();
            titleFrame = null;
        }
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public long getSeed() {
        return seed;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public Color getPlayer1Color() {
        return player1Color;
    }

    public Color getPlayer2Color() {
        return player2Color;
    }
}
