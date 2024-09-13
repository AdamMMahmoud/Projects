package core;

import java.util.ArrayList;
import tileengine.TETile;
import tileengine.Tileset;

public class Room {

    private final int[] coordinate;
    private final int width;
    private final int height;
    private final ArrayList<int[]> leftWalls = new ArrayList<>();
    private final ArrayList<int[]> rightWalls = new ArrayList<>();
    private final ArrayList<int[]> topWalls = new ArrayList<>();
    private final ArrayList<int[]> bottomWalls = new ArrayList<>();
    private final ArrayList<int[]> allWalls = new ArrayList<>();
    private final ArrayList<int[]> corners = new ArrayList<>();
    private final ArrayList<int[]> allFloors = new ArrayList<>();
    private final ArrayList<int[]> allTiles = new ArrayList<>();

    private ArrayList<int[]> mergeableLeftWalls = new ArrayList<>();
    private ArrayList<int[]> mergeableRightWalls = new ArrayList<>();
    private ArrayList<int[]> mergeableTopWalls = new ArrayList<>();
    private ArrayList<int[]> mergeableBottomWalls = new ArrayList<>();
    private final ArrayList<int[]> mergeableWalls = new ArrayList<>();

    public Room(int x, int y, int width, int height, TETile[][] world) {
        coordinate = new int[]{x, y};
        this.width = width;
        this.height = height;

        for (int dx = 1; dx < width - 1; dx++) {
            for (int dy = 1; dy < height - 1; dy++) {
                world[x + dx][y + dy] = Tileset.FLOOR;
                allFloors.add(new int[]{x + dx, y + dy});
                allTiles.add(new int[]{x + dx, y + dy});
            }
        }

        for (int dx = 0; dx < width; dx++) {
            world[x + dx][y] = Tileset.WALL;
            bottomWalls.add(new int[]{x + dx, y});
            allWalls.add(new int[]{x + dx, y});
            allTiles.add(new int[]{x + dx, y});
            world[x + dx][y + height - 1] = Tileset.WALL;
            topWalls.add(new int[]{x + dx, y + height - 1});
            allWalls.add(new int[]{x + dx, y + height - 1});
            allTiles.add(new int[]{x + dx, y + height - 1});
        }

        for (int dy = 0; dy < height; dy++) {
            world[x][y + dy] = Tileset.WALL;
            leftWalls.add(new int[]{x, y + dy});
            allWalls.add(new int[]{x, y + dy});
            allTiles.add(new int[]{x, y + dy});
            world[x + width - 1][y + dy] = Tileset.WALL;
            rightWalls.add(new int[]{x + width - 1, y + dy});
            allWalls.add(new int[]{x + width - 1, y + dy});
            allTiles.add(new int[]{x + width - 1, y + dy});
        }

        corners.add(new int[]{x, y});
        corners.add(new int[]{x, y + height - 1});
        corners.add(new int[]{x + width - 1, y});
        corners.add(new int[]{x + width - 1, y + height - 1});
        allTiles.add(new int[]{x, y});
        allTiles.add(new int[]{x, y + height - 1});
        allTiles.add(new int[]{x + width - 1, y});
        allTiles.add(new int[]{x + width - 1, y + height - 1});

        populateMergeableWalls();
    }

    private void populateMergeableWalls() {
        mergeableLeftWalls = createMergeableList(leftWalls);
        mergeableRightWalls = createMergeableList(rightWalls);
        mergeableTopWalls = createMergeableList(topWalls);
        mergeableBottomWalls = createMergeableList(bottomWalls);
        mergeableWalls.addAll(mergeableLeftWalls);
        mergeableWalls.addAll(mergeableRightWalls);
        mergeableWalls.addAll(mergeableTopWalls);
        mergeableWalls.addAll(mergeableBottomWalls);
    }

    private ArrayList<int[]> createMergeableList(ArrayList<int[]> walls) {
        ArrayList<int[]> mergeable = new ArrayList<>();
        if (walls.size() > 4) {
            for (int i = 1; i < walls.size() - 1; i++) {
                mergeable.add(walls.get(i));
            }
        }
        return mergeable;
    }

    public int[] getCoordinate() {
        return coordinate;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public ArrayList<int[]> getAllWalls() {
        return allWalls;
    }

    public ArrayList<int[]> getAllFloors() {
        return allFloors;
    }

    public ArrayList<int[]> getMergeableLeftWalls() {
        return mergeableLeftWalls;
    }
    public ArrayList<int[]> getMergeableRightWalls() {
        return mergeableRightWalls;
    }
    public ArrayList<int[]> getMergeableTopWalls() {
        return mergeableTopWalls;
    }
    public ArrayList<int[]> getMergeableBottomWalls() {
        return mergeableBottomWalls;
    }
    public ArrayList<int[]> getMergeableWalls() {
        return mergeableWalls;
    }

    public int getCenterX() {
        return coordinate[0] + width / 2;
    }
    public int getCenterY() {
        return coordinate[1] + height / 2;
    }
}
