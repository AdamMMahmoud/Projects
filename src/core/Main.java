package core;

import tileengine.TERenderer;
import tileengine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.Tileset;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static final int W = 90;
    static final int H = 47;
    private static final int FONT_SIZE_BIG = 32;
    private static final int TOTAL_ROUNDS = 3;
    public static int round = 1;
    public static Font dogica = new FontManager("Resources/Fonts/dogicapixelbold.otf", FONT_SIZE_BIG).getFont();

    public static final Map<Character, Long> keyPressStartTime = new HashMap<>();
    public static final Map<Character, Boolean> isContinuousMovement = new HashMap<>();
    private static final long CONTINUOUS_MOVEMENT_THRESHOLD = 200;
    private static final long INITIAL_DELAY = 220;
    private static final long REPEAT_INTERVAL = 130;
    public static boolean overrideMode = false;

    public static long startTime;
    public static long totalTime;
    private static HighScoreManager highScoreManager;
    public static int highScoreTime;
    private static AudioManager audioManager = new AudioManager();

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(W, H);
        Menus mainMenu = new Menus(W, H);
        mainMenu.displayMenu();
        audioManager.playMusic("Resources/Music/MainMenu.wav", true);
        mainMenu.handleUserInput();
        World worldGenerator;

        long seed = mainMenu.getSeed();
        int numPlayers = mainMenu.getNumPlayers();
        Color player1Color = mainMenu.getPlayer1Color();
        Color player2Color = mainMenu.getPlayer2Color();
        mainMenu.displayInstructionsScreen();
        worldGenerator = new World(seed, numPlayers, player1Color, player2Color);

        highScoreManager = new HighScoreManager();
        highScoreTime = highScoreManager.getHighScore();
        if (highScoreTime != Integer.MAX_VALUE) {
            highScoreTime /= 1000;
        }

        if (numPlayers == 1) {
            startTime = System.currentTimeMillis();
        }

        TETile[][] world = null;
        while (round <= TOTAL_ROUNDS) {
            if (round == 1) {
                audioManager.stopMusic();
                audioManager.playMusic("Resources/Music/RoundOne.wav", true);
                StdDraw.clear(new Color(81, 129, 63));
                StdDraw.show();
                mainMenu.displayRoundScreen(round);
                world = worldGenerator.generateWorld();
            } else if (round == 2) {
                audioManager.stopMusic();
                audioManager.playMusic("Resources/Music/RoundTwo.wav", true);
                mainMenu.displayStandingsScreen(worldGenerator);
                mainMenu.displayRoundScreen(round);
                ter.grassRotations.clear();
                world = worldGenerator.generateWorld();
            } else if (round == 3) {
                audioManager.stopMusic();
                audioManager.playMusic("Resources/Music/RoundThree.wav", true);
                mainMenu.displayStandingsScreen(worldGenerator);
                mainMenu.displayRoundScreen(round);
                ter.grassRotations.clear();
                world = worldGenerator.generateWorld();
            }
            resetStdDrawSettings();
            ter.renderFrame(world);
            displayCountdown(worldGenerator, ter, world);

            boolean colonPressed = false;
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    char input = StdDraw.nextKeyTyped();
                    if (input == 'O') {
                        overrideMode = !overrideMode;
                    }

                    if (input == '2' && worldGenerator.getAvatar(0).hasCarrotBoost) {
                        worldGenerator.getAvatar(0).hasCarrotBoost = false;
                        worldGenerator.getAvatar(0).carrotBoostEndTime = 0;
                    } else if (numPlayers == 2 && input == '8' && worldGenerator.getAvatar(1).hasCarrotBoost) {
                        worldGenerator.getAvatar(1).hasCarrotBoost = false;
                        worldGenerator.getAvatar(1).carrotBoostEndTime = 0;
                    }

                    if ((input == 'z' || input == 'Z') && worldGenerator.getAvatar(0).hasTeleporter) {
                        worldGenerator.getAvatar(0).hasTeleporter = false;
                        worldGenerator.getAvatar(0).droppedTeleporter = true;
                        worldGenerator.dropTeleporter(0);
                        ter.renderFrame(world);
                    } else if ((input == 'z' || input == 'Z') && worldGenerator.getAvatar(0).droppedTeleporter && !worldGenerator.getAvatar(0).isTrapped()) {
                        worldGenerator.teleport(0);
                        ter.renderFrame(world);
                    }
                    if (numPlayers == 2 && (input == 'm' || input == 'M') && worldGenerator.getAvatar(1).hasTeleporter) {
                        worldGenerator.getAvatar(1).hasTeleporter = false;
                        worldGenerator.getAvatar(1).droppedTeleporter = true;
                        worldGenerator.dropTeleporter(1);
                        ter.renderFrame(world);
                    } else if (numPlayers == 2 && (input == 'm' || input == 'M') && worldGenerator.getAvatar(1).droppedTeleporter && !worldGenerator.getAvatar(1).isTrapped()) {
                        worldGenerator.teleport(1);
                        ter.renderFrame(world);
                    }

                    if (colonPressed) {
                        if (input == 'Q' || input == 'q') {
                            System.exit(0);
                        } else {
                            colonPressed = false;
                        }
                    } else if (input == ':') {
                        colonPressed = true;
                    }
                }

                if (overrideMode) {
                    handleOverrideMode(world, ter);
                } else {
                    boolean moved = handleNormalMode(worldGenerator, ter, world);
                    if (moved) {
                        StdDraw.pause(10);
                    }
                }

                Menus.displayHUD(world, worldGenerator, "");

                if (!overrideMode && worldGenerator.allFlowersCollected()) {
                    round++;
                    if (round > TOTAL_ROUNDS) {
                        audioManager.stopMusic();
                        audioManager.playMusic("Resources/Music/WinnerScreen.wav", true);
                        if (numPlayers == 1) {
                            long endTime = System.currentTimeMillis();
                            totalTime = endTime - startTime;

                            if (highScoreManager.isNewHighScore((int) totalTime)) {
                                highScoreManager.saveHighScore((int) totalTime);
                                mainMenu.displayHighScoreScreen(totalTime, true);
                            } else {
                                mainMenu.displayHighScoreScreen(totalTime, false);
                            }
                        } else {
                            mainMenu.displayWinnerScreen(worldGenerator);
                        }
                        return;
                    }
                    break;
                }
            }
        }
    }

    private static void handleOverrideMode(TETile[][] world, TERenderer ter) {
        if (StdDraw.isMousePressed()) {
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();

            if (x >= 0 && x < W && y >= 0 && y < H) {
                if (world[x][y] == Tileset.GRASS) {
                    ter.grassRotations.remove(0);
                    World.numGrasses--;
                }
                world[x][y] = Tileset.FLOOR;
                ter.renderFrame(world);
            }
        }
    }

    private static boolean handleNormalMode(World worldGenerator, TERenderer ter, TETile[][] world) {
        boolean moved = false;
        long currentTime = System.currentTimeMillis();

        moved |= handleMovement(worldGenerator, ter, world, 'W', 0, KeyEvent.VK_W, currentTime);
        moved |= handleMovement(worldGenerator, ter, world, 'A', 0, KeyEvent.VK_A, currentTime);
        moved |= handleMovement(worldGenerator, ter, world, 'S', 0, KeyEvent.VK_S, currentTime);
        moved |= handleMovement(worldGenerator, ter, world, 'D', 0, KeyEvent.VK_D, currentTime);

        if (worldGenerator.getAvatar(1) != null) {
            moved |= handleMovement(worldGenerator, ter, world, 'I', 1, KeyEvent.VK_I, currentTime);
            moved |= handleMovement(worldGenerator, ter, world, 'J', 1, KeyEvent.VK_J, currentTime);
            moved |= handleMovement(worldGenerator, ter, world, 'K', 1, KeyEvent.VK_K, currentTime);
            moved |= handleMovement(worldGenerator, ter, world, 'L', 1, KeyEvent.VK_L, currentTime);
        }

        return moved;
    }

    private static boolean handleMovement(World worldGenerator, TERenderer ter, TETile[][] world, char direction, int playerIndex, int keyCode, long currentTime) {
        Avatar player = worldGenerator.getAvatar(playerIndex);
        Avatar otherPlayer = worldGenerator.getAvatar(1 - playerIndex);

        if (otherPlayer != null && otherPlayer.hasMindControl() && !player.hasMindControl()) {
            return false;
        }

        if (StdDraw.isKeyPressed(keyCode)) {
            Long pressStartTime = keyPressStartTime.get(direction);

            if (pressStartTime == null) {
                moveAvatarAndRender(worldGenerator, ter, world, direction, playerIndex);
                keyPressStartTime.put(direction, currentTime);
                isContinuousMovement.put(direction, false);
                return true;
            }

            long elapsedTime = currentTime - pressStartTime;
            if (!isContinuousMovement.get(direction)) {
                if (elapsedTime >= CONTINUOUS_MOVEMENT_THRESHOLD) {
                    isContinuousMovement.put(direction, true);
                    keyPressStartTime.put(direction, currentTime);
                }
            } else {
                if (elapsedTime >= INITIAL_DELAY) {
                    moveAvatarAndRender(worldGenerator, ter, world, direction, playerIndex);
                    keyPressStartTime.put(direction, currentTime - (INITIAL_DELAY - REPEAT_INTERVAL));
                    return true;
                }
            }
        } else {
            keyPressStartTime.remove(direction);
            isContinuousMovement.remove(direction);
        }
        return false;
    }


    private static void moveAvatarAndRender(World worldGenerator, TERenderer ter, TETile[][] world, char direction, int playerIndex) {
        Avatar player = worldGenerator.getAvatar(playerIndex);
        Avatar otherPlayer = worldGenerator.getAvatar(1 - playerIndex);

        if (player.hasMindControl()) {
            worldGenerator.moveAvatar(1 - playerIndex, direction);
        } else if (otherPlayer != null && otherPlayer.hasMindControl()) {
            worldGenerator.moveAvatar(playerIndex, direction);
        } else {
            worldGenerator.moveAvatar(playerIndex, direction);
        }
        ter.renderFrame(world);
    }

    private static void displayCountdown(World worldGenerator, TERenderer ter, TETile[][] world) {
        for (int i = 10; i > 0; i--) {
            ter.renderFrame(world);
            Menus.displayHUD(world, worldGenerator, "Game starts in: " + i + " seconds");
            StdDraw.pause(1000);
        }
        Menus.displayHUD(world, worldGenerator, "");
    }

    private static void resetStdDrawSettings() {
        StdDraw.clear();
        StdDraw.setPenColor();
        StdDraw.enableDoubleBuffering();
        StdDraw.setFont(dogica);
    }
}
