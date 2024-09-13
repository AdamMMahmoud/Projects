package core;

import java.util.*;

import tileengine.TETile;
import tileengine.Tileset;
import java.awt.Color;

public class World {
    private final TETile[][] world;
    private final Avatar[] avatars = new Avatar[2];
    public static ArrayList<int[]> flowerPinks = new ArrayList<>();
    public static ArrayList<int[]> flowerOranges = new ArrayList<>();
    public static ArrayList<int[]> flowerBlue = new ArrayList<>();
    public static ArrayList<int[]> allFlowers = new ArrayList<>();

    private static int[] portal1 = new int[2];
    private static int[] portal2 = new int[2];

    private static int numPlayers = 1;
    private final Random random;
    private final Color player1Color;
    private final Color player2Color;

    private static final int WORLD_WIDTH = 90;
    private static final int WORLD_HEIGHT = 47;

    private static final int MIN_ROOM_SIZE = 12;
    private static final int MAX_ROOM_SIZE = 25;

    private static final int BIG_MAX_ATTEMPTS = 100;
    private static final int SMALL_MAX_ATTEMPTS = 10;

    private final ArrayList<Room> rooms = new ArrayList<>();
    private UnionFind<Room> roomUnionFind = new UnionFind<>();
    public static ArrayList<int[]> hallwayFloors = new ArrayList<>();
    public static int numGrasses = 0;

    public World(long seed, int numPlayers, Color player1Color, Color player2Color) {
        World.numPlayers = numPlayers;
        this.player1Color = player1Color;
        this.player2Color = player2Color;
        this.random = new Random(seed);
        world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        fillWorldWithNothing();
    }

    public TETile[][] generateWorld() {
        if (Main.round != 1) {
            fillWorldWithNothing();
            rooms.clear();
            hallwayFloors.clear();
            roomUnionFind = new UnionFind<>();
            getAvatar(0).burrowCanMove = true;
            getAvatar(0).hasTeleporter = false;
            getAvatar(0).droppedTeleporter = false;

        }

        int numRooms = random.nextInt(5) + 7;
        for (int i = 0; i < numRooms; i++) {
            addRoom();
        }
        for (Room room : rooms) {
            roomUnionFind.add(room);
        }
        connectRooms();
        while (notAllRoomsConnected()) {
            ensureAllRoomsConnected();
        }
        turningHallway();

        Room startRoom = rooms.get(random.nextInt(rooms.size()));
        if (avatars[0] == null) {
            avatars[0] = new Avatar(startRoom.getCenterX(), startRoom.getCenterY(), world, player1Color, "Player 1");
        } else {
            avatars[0].setPosition(startRoom.getCenterX(), startRoom.getCenterY());
        }
        if (numPlayers == 2 && avatars[1] == null) {
            if (Main.round != 1) {
                getAvatar(1).burrowCanMove = true;
            }
            do {
                startRoom = rooms.get(random.nextInt(rooms.size()));
            } while (startRoom.getCenterX() == avatars[0].getX() && startRoom.getCenterY() == avatars[0].getY());
            avatars[1] = new Avatar(startRoom.getCenterX(), startRoom.getCenterY(), world, player2Color, "Player 2");
        } else if (numPlayers == 2) {
            if (Main.round != 1) {
                getAvatar(1).burrowCanMove = true;
                getAvatar(1).hasTeleporter = false;
                getAvatar(1).droppedTeleporter = false;
            }
            do {
                startRoom = rooms.get(random.nextInt(rooms.size()));
            } while (startRoom.getCenterX() == avatars[0].getX() && startRoom.getCenterY() == avatars[0].getY());
            avatars[1].setPosition(startRoom.getCenterX(), startRoom.getCenterY());
        }
        addFlowerPinks();
        addFlowerOranges();
        addFlowerBlue();
        setPortals();
        placeGrass();
        placeCarrot();
        placeTrap();
        placeBurrow();
        placeRareFlower();
        placeTeleporter();
        if (numPlayers == 2) {
            placeMindControl();
        }
        return world;
    }

    private void addRoom() {
        boolean roomAdded = false;
        int attempts = 0;
        int width = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + (MIN_ROOM_SIZE * 3 / 2);
        int height = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + (MIN_ROOM_SIZE * 2 / 3);

        while (!roomAdded && attempts < BIG_MAX_ATTEMPTS) {
            int x = random.nextInt(2, WORLD_WIDTH - width - 2);
            int y = random.nextInt(2, WORLD_HEIGHT - height - 3);

            if (!doesOverlap(x, y, width, height)) {
                Room newRoom = new Room(x, y, width, height, world);
                rooms.add(newRoom);
                roomAdded = true;
            } else {
                boolean choose = random.nextBoolean();
                if (choose) {
                    width = Math.max(width - 1, MIN_ROOM_SIZE);
                } else {
                    height = Math.max(height - 1, MIN_ROOM_SIZE);
                }
            }
            attempts++;
        }
    }

    private void connectRooms() {
        for (Room room : rooms) {
            if (isIsolated(room)) {
                boolean connected = false;
                int attempts = 0;
                while (!connected && attempts < SMALL_MAX_ATTEMPTS) {
                    int[] start = canBuildHallway(room, "up");
                    if (start != null && random.nextBoolean()) {
                        Room targetRoom = buildHallway(start, "up");
                        if (targetRoom != null) {
                            roomUnionFind.union(room, targetRoom);
                            connected = true;
                        }
                    }

                    start = canBuildHallway(room, "left");
                    if (!connected && start != null && random.nextBoolean()) {
                        Room targetRoom = buildHallway(start, "left");
                        if (targetRoom != null) {
                            roomUnionFind.union(room, targetRoom);
                            connected = true;
                        }
                    }

                    start = canBuildHallway(room, "right");
                    if (!connected && start != null && random.nextBoolean()) {
                        Room targetRoom = buildHallway(start, "right");
                        if (targetRoom != null) {
                            roomUnionFind.union(room, targetRoom);
                            connected = true;
                        }
                    }

                    start = canBuildHallway(room, "down");
                    if (!connected && start != null && random.nextBoolean()) {
                        Room targetRoom = buildHallway(start, "down");
                        if (targetRoom != null) {
                            roomUnionFind.union(room, targetRoom);
                            connected = true;
                        }
                    }
                    attempts++;
                }
            }
        }
    }

    private int[] canBuildHallway(Room room, String direction) {
        ArrayList<int[]> walls;
        int dx = 0, dy = 0;

        switch (direction) {
            case "left":
                walls = room.getMergeableLeftWalls();
                dx = -1;
                break;
            case "right":
                walls = room.getMergeableRightWalls();
                dx = 1;
                break;
            case "up":
                walls = room.getMergeableTopWalls();
                dy = 1;
                break;
            case "down":
                walls = room.getMergeableBottomWalls();
                dy = -1;
                break;
            default:
                return null;
        }
        Collections.shuffle(walls, random);
        for (int[] start : walls) {
            int x = start[0] + dx;
            int y = start[1] + dy;
            while (x >= 0 && x < WORLD_WIDTH && y >= 0 && y < WORLD_HEIGHT) {
                for (Room roomCheck : rooms) {
                    if (direction.equals("left") || direction.equals("right")) {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y + 1)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y - 1)) {
                            return start;
                        }
                    } else {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x + 1, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x - 1, y)) {
                            return start;
                        }
                    }
                }
                x += dx;
                y += dy;
            }
        }
        return null;
    }

    private Room buildHallway(int[] start, String direction) {
        int x = start[0];
        int y = start[1];
        int dx = 0, dy = 0;
        for (Room room : rooms) {
            if (containsCoordinate(room.getMergeableWalls(), x, y)) {
                room.getMergeableWalls().remove(new int[]{x, y});
                break;
            }
        }
        switch (direction) {
            case "left":
                dx = -1;
                break;
            case "right":
                dx = 1;
                break;
            case "up":
                dy = 1;
                break;
            case "down":
                dy = -1;
                break;
            default:
                return null;
        }
        Room targetRoom = null;

        while (x >= 0 && x < WORLD_WIDTH && y >= 0 && y < WORLD_HEIGHT && world[x][y] != Tileset.FLOOR) {
            if (direction.equals("left") || direction.equals("right")) {
                world[x][y] = Tileset.FLOOR;
                world[x][y - 1] = Tileset.WALL;
                world[x][y + 1] = Tileset.WALL;
            } else {
                world[x][y] = Tileset.FLOOR;
                world[x - 1][y] = Tileset.WALL;
                world[x + 1][y] = Tileset.WALL;
            }
            hallwayFloors.add(new int[]{x, y});
            x += dx;
            y += dy;

            for (Room room : rooms) {
                if (containsCoordinate(room.getMergeableWalls(), x, y)) {
                    targetRoom = room;
                    break;
                }
            }
        }
        world[start[0]][start[1]] = Tileset.FLOOR;
        return targetRoom;
    }

    private boolean notAllRoomsConnected() {
        for (Room room : rooms) {
            if (roomUnionFind.notConnected(rooms.get(0), room)) {
                return true;
            }
        }
        return false;
    }

    private void ensureAllRoomsConnected() {
        ArrayList<Room> roomsCopy = new ArrayList<>(rooms);
        for (Room room : roomsCopy) {
            if (roomUnionFind.notConnected(rooms.get(0), room)) {
                connectRoom(room);
            }
        }
    }

    private void connectRoom(Room room) {
        boolean connected = false;
        int attempts = 0;
        String[] directions = {"up", "down", "left", "right"};

        while (!connected && attempts < SMALL_MAX_ATTEMPTS) {
            Collections.shuffle(Arrays.asList(directions), random);
            Room targetRoom;

            for (String direction : directions) {
                int[] start = canBuildHallway(room, direction);
                if (start != null && random.nextBoolean()) {
                    targetRoom = buildHallway(start, direction);
                    if (targetRoom != null) {
                        roomUnionFind.union(room, targetRoom);
                        connected = true;
                        break;
                    }
                }
            }
            attempts++;
        }
        if (!connected) {
            rooms.remove(room);
        }
    }

    private void turningHallway() {
        boolean built = false;
        int x, y;
        int attempts = 0;
        while (!built && attempts < BIG_MAX_ATTEMPTS) {
            int[] point = randomVoid();
            x = point[0];
            y = point[1];
            if (canBuildHallwayFromPoint(x, y, "down")
                    && canBuildHallwayFromPoint(x, y, "right")
                    && random.nextBoolean()) {
                buildHallway(new int[]{x, y - 1}, "down");
                buildHallway(new int[]{x + 1, y}, "right");
                world[x][y] = Tileset.FLOOR;
                world[x - 1][y] = Tileset.WALL;
                world[x - 1][y + 1] = Tileset.WALL;
                world[x][y + 1] = Tileset.WALL;
                built = true;
            } else if (canBuildHallwayFromPoint(x, y, "down")
                    && canBuildHallwayFromPoint(x, y, "left")
                    && random.nextBoolean()) {
                buildHallway(new int[]{x, y - 1}, "down");
                buildHallway(new int[]{x - 1, y}, "left");
                world[x][y] = Tileset.FLOOR;
                world[x + 1][y] = Tileset.WALL;
                world[x + 1][y + 1] = Tileset.WALL;
                world[x][y + 1] = Tileset.WALL;
                built = true;
            } else if (canBuildHallwayFromPoint(x, y, "up")
                    && canBuildHallwayFromPoint(x, y, "right")
                    && random.nextBoolean()) {
                buildHallway(new int[]{x, y + 1}, "up");
                buildHallway(new int[]{x + 1, y}, "right");
                world[x][y] = Tileset.FLOOR;
                world[x - 1][y] = Tileset.WALL;
                world[x - 1][y - 1] = Tileset.WALL;
                world[x][y - 1] = Tileset.WALL;
                built = true;
            } else if (canBuildHallwayFromPoint(x, y, "up")
                    && canBuildHallwayFromPoint(x, y, "left")
                    && random.nextBoolean()) {
                buildHallway(new int[]{x, y + 1}, "up");
                buildHallway(new int[]{x - 1, y}, "left");
                world[x][y] = Tileset.FLOOR;
                world[x + 1][y] = Tileset.WALL;
                world[x + 1][y - 1] = Tileset.WALL;
                world[x][y - 1] = Tileset.WALL;
                built = true;
            }
            attempts++;
        }
        if (!built) {
            System.out.println("Failed to build turning hallway after " + attempts + " attempts.");
        }
    }

    public boolean canBuildHallwayFromPoint(int x, int y, String direction) {
        int dx, dy;

        if (hasNonNothingTilesAround(x, y)) {
            return false;
        }

        switch (direction) {
            case "left":
                dx = -1;
                x += dx;
                while (x >= 0) {
                    for (Room roomCheck : rooms) {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y + 1)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y - 1)) {
                            return true;
                        }
                    }
                    x += dx;
                }
                break;
            case "right":
                dx = 1;
                x += dx;
                while (x < WORLD_WIDTH) {
                    for (Room roomCheck : rooms) {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y + 1)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x, y - 1)) {
                            return true;
                        }
                    }
                    x += dx;
                }
                break;
            case "up":
                dy = 1;
                y += dy;
                while (y < WORLD_HEIGHT) {
                    for (Room roomCheck : rooms) {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x + 1, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x - 1, y)) {
                            return true;
                        }
                    }
                    y += dy;
                }
                break;
            case "down":
                dy = -1;
                y += dy;
                while (y >= 0) {
                    for (Room roomCheck : rooms) {
                        if (containsCoordinate(roomCheck.getMergeableWalls(), x, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x + 1, y)
                                && containsCoordinate(roomCheck.getMergeableWalls(), x - 1, y)) {
                            return true;
                        }
                    }
                    y += dy;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private void fillWorldWithNothing() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public int[] randomVoid() {
        int x = random.nextInt(WORLD_WIDTH);
        int y = random.nextInt(WORLD_HEIGHT - 1);
        while (world[x][y] != Tileset.NOTHING) {
            x = random.nextInt(WORLD_WIDTH);
            y = random.nextInt(WORLD_HEIGHT - 1);
        }
        return new int[]{x, y};
    }

    public int[] randomFloor() {
        int x = random.nextInt(WORLD_WIDTH);
        int y = random.nextInt(WORLD_HEIGHT - 1);
        while (world[x][y] != Tileset.FLOOR) {
            x = random.nextInt(WORLD_WIDTH);
            y = random.nextInt(WORLD_HEIGHT - 1);
        }
        return new int[]{x, y};
    }

//    public int[] randomFloorAdjacentToWall() {
//        while (true) {
//            int[] coord = randomFloor();
//            if (world[coord[0] + 1][coord[1]] == Tileset.WALL) {
//                return new int[]{coord[0] + 1, coord[1]};
//            } else if (world[coord[0] - 1][coord[1]] == Tileset.WALL) {
//                return new int[]{coord[0] - 1, coord[1]};
//            } else if (world[coord[0]][coord[1] + 1] == Tileset.WALL) {
//                return new int[]{coord[0] - 1, coord[1] + 1};
//            } else if (world[coord[0]][coord[1] - 1] == Tileset.WALL) {
//                return new int[]{coord[0] - 1, coord[1] - 1};
//            }
//        }
//    }

    public int[] randomWall() {
        Room room = rooms.get(random.nextInt(rooms.size()));
        int[] coord = room.getMergeableWalls().get(random.nextInt(room.getMergeableWalls().size()));
        int x = coord[0];
        int y = coord[1];
        if ((x > 1 && x < WORLD_WIDTH - 2) && (y > 1 && y < WORLD_HEIGHT - 2) && world[x][y] == Tileset.WALL) {
            return coord;
        }
        return randomWall();
    }

    private boolean hasNonNothingTilesAround(int x, int y) {
        int startX = Math.max(0, x - 2);
        int endX = Math.min(WORLD_WIDTH - 1, x + 2);
        int startY = Math.max(0, y - 2);
        int endY = Math.min(WORLD_HEIGHT - 1, y + 2);

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                if (world[i][j] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean doesOverlap(int x, int y, int width, int height) {
        for (Room room : rooms) {
            int x2 = room.getCoordinate()[0];
            int y2 = room.getCoordinate()[1];

            boolean overlap = x < x2 + room.getWidth()
                    && x + width > x2
                    && y < y2 + room.getHeight()
                    && y + height > y2;

            if (overlap) {
                return true;
            }
        }
        return false;
    }

    private boolean isIsolated(Room room) {
        for (int[] wall : room.getAllWalls()) {
            if (world[wall[0]][wall[1]] == Tileset.FLOOR) {
                return false;
            }
        }
        return true;
    }

    public void moveAvatar(int playerIndex, char direction) {
        if (playerIndex >= 0 && playerIndex < numPlayers) {
            if (avatars[playerIndex].hasCarrotBoost) {
                avatars[playerIndex].move(direction);
                if (!avatars[playerIndex].hasCarrotBoost()) {
                    return;  // Exit if boost expired after first move
                }
            }
            avatars[playerIndex].move(direction);

        }
    }

    private boolean containsCoordinate(ArrayList<int[]> walls, int x, int y) {
        for (int[] wall : walls) {
            if (wall[0] == x && wall[1] == y) {
                return true;
            }
        }
        return false;
    }

    private void addFlowerPinks() {
        int numFlowerPinks = random.nextInt(7) * 2 + 15;

        for (int i = 0; i < numFlowerPinks; i++) {
            int[] coord = randomFloor();
            world[coord[0]][coord[1]] = Tileset.FLOWERPINK;
            flowerPinks.add(coord);
            allFlowers.add(coord);
        }
    }

    private void addFlowerOranges() {
        int numFlowerOranges = random.nextInt(3, 7);

        for (int i = 0; i < numFlowerOranges; i++) {
            int[] coord = randomFloor();
            world[coord[0]][coord[1]] = Tileset.FLOWERORANGE;
            flowerOranges.add(coord);
            allFlowers.add(coord);
        }
    }

    private void addFlowerBlue() {
        int[] coord = randomFloor();
        world[coord[0]][coord[1]] = Tileset.FLOWERBLUE;
        flowerBlue.add(coord);
        allFlowers.add(coord);
    }

    public boolean allFlowersCollected() {
        return allFlowers.isEmpty();
    }

    public void setPortals() {
        ArrayList<Room> roomList = new ArrayList<>(rooms);
        int rand = random.nextInt(roomList.size());
        Room room1 = roomList.get(rand);
        do {
            portal1 = room1.getAllFloors().get(random.nextInt(room1.getAllFloors().size()));
        } while (world[portal1[0]][portal1[1]] != Tileset.FLOOR
                || world[portal1[0] + 1][portal1[1]] == Tileset.WALL
                || world[portal1[0] - 1][portal1[1]] == Tileset.WALL
                || world[portal1[0]][portal1[1] - 1] == Tileset.WALL
                || world[portal1[0]][portal1[1] + 1] == Tileset.WALL
                || world[portal1[0] + 2][portal1[1]] == Tileset.WALL
                || world[portal1[0] - 2][portal1[1]] == Tileset.WALL
                || world[portal1[0]][portal1[1] - 2] == Tileset.WALL
                || world[portal1[0]][portal1[1] + 2] == Tileset.WALL
                || world[portal1[0] + 1][portal1[1] + 1] == Tileset.WALL
                || world[portal1[0] - 1][portal1[1] + 1] == Tileset.WALL
                || world[portal1[0] + 1][portal1[1] - 1] == Tileset.WALL
                || world[portal1[0] - 1][portal1[1] - 1] == Tileset.WALL);
        roomList.remove(room1);
        rand = random.nextInt(roomList.size());
        Room room2 = roomList.get(rand);
        do {
            portal2 = room2.getAllFloors().get(random.nextInt(room2.getAllFloors().size()));
        } while (world[portal2[0]][portal2[1]] != Tileset.FLOOR
                || world[portal2[0] + 1][portal2[1]] == Tileset.WALL
                || world[portal2[0] - 1][portal2[1]] == Tileset.WALL
                || world[portal2[0]][portal2[1] - 1] == Tileset.WALL
                || world[portal2[0]][portal2[1] + 1] == Tileset.WALL
                || world[portal2[0] + 2][portal2[1]] == Tileset.WALL
                || world[portal2[0] - 2][portal2[1]] == Tileset.WALL
                || world[portal2[0]][portal2[1] - 2] == Tileset.WALL
                || world[portal2[0]][portal2[1] + 2] == Tileset.WALL
                || world[portal2[0] + 1][portal2[1] + 1] == Tileset.WALL
                || world[portal2[0] - 1][portal2[1] + 1] == Tileset.WALL
                || world[portal2[0] + 1][portal2[1] - 1] == Tileset.WALL
                || world[portal2[0] - 1][portal2[1] - 1] == Tileset.WALL);
        world[portal1[0]][portal1[1]] = Tileset.PORTAL;
        world[portal2[0]][portal2[1]] = Tileset.PORTAL;
    }

    public void placeGrass() {
        numGrasses = random.nextInt(80, 100);
        for (int i = 0; i < numGrasses; i++) {
            int[] coord = randomVoid();
            world[coord[0]][coord[1]] = Tileset.GRASS;

        }
        int num = random.nextInt(15, 20);
        for (int i = 0; i < num; i++) {
            int[] coord = randomVoid();
            world[coord[0]][coord[1]] = Tileset.WEED;
        }
    }

    public void placeCarrot() {
        for (int i = 0; i < random.nextInt(4, 7); i++) {
            int[] coord = hallwayFloors.get(random.nextInt(hallwayFloors.size()));
            if (world[coord[0]][coord[1]] != Tileset.FLOOR) {
                i--;
                continue;
            }
            world[coord[0]][coord[1]] = Tileset.CARROT;
        }
    }

    public void placeTrap() {
        for (int i = 0; i < random.nextInt(15, 30); i++) {
            Room room = rooms.get(random.nextInt(rooms.size()));
            int[] coord = room.getAllFloors().get(random.nextInt(room.getAllFloors().size()));
            if (world[coord[0]][coord[1]] != Tileset.FLOOR) {
                i--;
                continue;
            }
            world[coord[0]][coord[1]] = Tileset.TRAP;
        }
    }

    public void placeRareFlower() {
        int[] coord;
        boolean isValid;

        do {
            coord = randomVoid();
            int x = coord[0];
            int y = coord[1];
            boolean isOnEdge = (x == 0 || x == World.getWorldWidth() - 1 || y == 0 || y == World.getWorldHeight() - 1);
            boolean isAdjacentToWall = x > 0 && world[x - 1][y] == Tileset.WALL;
            if (x < World.getWorldWidth() - 1 && world[x + 1][y] == Tileset.WALL) isAdjacentToWall = true;
            if (y > 0 && world[x][y - 1] == Tileset.WALL) isAdjacentToWall = true;
            if (y < World.getWorldHeight() - 1 && world[x][y + 1] == Tileset.WALL) isAdjacentToWall = true;
            isValid = !isOnEdge && !isAdjacentToWall;
        } while (!isValid);
        world[coord[0]][coord[1]] = Tileset.RAREFLOWER;
    }

    public void placeBurrow() {
        for (int i = 0; i < 2; i++){
            int[] coord;
            boolean adjacentToNothing;
            do {
                coord = randomWall();
                adjacentToNothing = false;

                int x = coord[0];
                int y = coord[1];

                if (x > 0 && world[x - 1][y] == Tileset.NOTHING) adjacentToNothing = true;
                if (x < World.getWorldWidth() - 1 && world[x + 1][y] == Tileset.NOTHING) adjacentToNothing = true;
                if (y > 0 && world[x][y - 1] == Tileset.NOTHING) adjacentToNothing = true;
                if (y < World.getWorldHeight() - 1 && world[x][y + 1] == Tileset.NOTHING) adjacentToNothing = true;

            } while (!adjacentToNothing);
            world[coord[0]][coord[1]] = Tileset.BURROW;
        }
    }

    public void placeMindControl() {
        int[] coord = randomFloor();
        world[coord[0]][coord[1]] = Tileset.MINDCONTROL;
    }

    public void placeTeleporter() {
        for (int i = 0; i < numPlayers; i++) {
            int[] coord = randomFloor();
            world[coord[0]][coord[1]] = Tileset.TELEPORTERPICKUP;
        }
    }

    public void dropTeleporter(int avatarIndex) {
        int[] coord = findNearestFloorTile(getAvatar(avatarIndex).getLocation());
        if (coord == null){
            return;
        }
        Color bunnycolor = getAvatar(avatarIndex).getColor();
        if (bunnycolor.equals(new Color(68, 198, 215))) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDBLUE;
        } else if (bunnycolor.equals(new Color(198, 72, 43))) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDRED;
        } else if (bunnycolor.equals(new Color(110, 68, 243))) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDPURPLE;
        } else if (bunnycolor.equals(new Color(215, 69, 217))) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDMAGENTA;
        } else if (bunnycolor.equals(Color.WHITE)) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDWHITE;
        } else if (bunnycolor.equals(new Color(254, 167, 62))) {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDORANGE;
        } else {
            world[coord[0]][coord[1]] = Tileset.TELEPORTERDROPPEDWHITE;
        }
        getAvatar(avatarIndex).droppedTeleporterCoord = coord;
    }

    public void teleport(int avatarIndex) {
        world[getAvatar(avatarIndex).getX()][getAvatar(avatarIndex).getY()] = Tileset.FLOOR;
        int[] coord = findNearestFloorTile(getAvatar(avatarIndex).droppedTeleporterCoord);
        getAvatar(avatarIndex).setPosition(coord[0], coord[1]);
    }

    public int[] findNearestFloorTile(int[] coord) {
        int startX = coord[0];
        int startY = coord[1];
        int[][] directions = {
                {0, 1},   // Above
                {0, -1},  // Below
                {-1, 0},  // Left
                {1, 0}    // Right
        };
        for (int[] direction : directions) {
            int newX = startX + direction[0];
            int newY = startY + direction[1];
            if (newX >= 0 && newX < WORLD_WIDTH && newY >= 0 && newY < WORLD_HEIGHT) {
                if (world[newX][newY] == Tileset.FLOOR) {
                    return new int[] {newX, newY};
                }
            }
        }
        return null;
    }

    public Avatar getAvatar(int index) {
        if (index >= 0 && index < avatars.length) {
            return avatars[index];
        }
        return null;
    }

    public static int getNumPlayers() {
        return numPlayers;
    }

    public static int getWorldWidth() {
        return WORLD_WIDTH;
    }

    public static int getWorldHeight() {
        return WORLD_HEIGHT;
    }

    public static int[] getPortal1() {
        return portal1;
    }

    public static int[] getPortal2() {
        return portal2;
    }
}
