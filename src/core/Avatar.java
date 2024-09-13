package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Avatar implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private transient TETile[][] world;
    private final Color color;
    private final String description;
    private int flowerPinksCollected;
    private int flowerOrangeCollected;
    private int flowerBlueCollected;
    private int rareFlowerCollected;
    public boolean hasCarrotBoost = false;
    public long carrotBoostEndTime = 0;
    private long trapEndTime = 0;
    public boolean burrowCanMove = true;
    ArrayList<TETile> greeneryTileList = new ArrayList<>();
    public boolean hasMindControl = false;
    public long mindControlEndTime = 0;
    public boolean hasTeleporter = false;
    public boolean droppedTeleporter = false;
    public int[] droppedTeleporterCoord;

    public Avatar(int startX, int startY, TETile[][] world, Color color, String description) {
        this.x = startX;
        this.y = startY;
        this.world = world;
        this.color = color;
        this.description = description;
        this.flowerPinksCollected = 0;
        this.flowerOrangeCollected = 0;
        this.flowerBlueCollected = 0;
        this.rareFlowerCollected = 0;
        greeneryTileList.add(Tileset.NOTHING);
        greeneryTileList.add(Tileset.SHRUB);
        greeneryTileList.add(Tileset.WEED);
        greeneryTileList.add(Tileset.GRASS);

        this.world[x][y] = Tileset.avatar(color, description);
    }

    public void move(char direction) {
        if (isTrapped()) {
            return;
        }
        int dx = 0, dy = 0;
        if (System.currentTimeMillis() > carrotBoostEndTime) {
            hasCarrotBoost = false;
        }

        switch (direction) {
            case 'W', 'I': dy = 1;
                break;
            case 'A', 'J': dx = -1;
                break;
            case 'S', 'K': dy = -1;
                break;
            case 'D', 'L': dx = 1;
                break;
            default:
                return;
        }

        int newX = x + dx;
        int newY = y + dy;

        if (canMoveTo(newX, newY)) {
            if (world[newX][newY] == Tileset.FLOWERPINK
                    || world[newX][newY] == Tileset.FLOWERORANGE
                    || world[newX][newY] == Tileset.FLOWERBLUE
                    || world[newX][newY] == Tileset.RAREFLOWER
                    || world[newX][newY] == Tileset.FLOOR) {
                if (world[newX][newY] == Tileset.FLOWERPINK) {
                    flowerPinksCollected++;
                    removeFlowerPinks(newX, newY);
                } else if (world[newX][newY] == Tileset.FLOWERORANGE) {
                    flowerOrangeCollected++;
                    removeFlowerOranges(newX, newY);
                } else if (world[newX][newY] == Tileset.FLOWERBLUE) {
                    flowerBlueCollected++;
                    removeFlowerBlue(newX, newY);
                } else if (world[newX][newY] == Tileset.RAREFLOWER) {
                    rareFlowerCollected++;
                    world[x][y] = Tileset.FLOOR;
                    x = newX;
                    y = newY;
                    world[x][y] = Tileset.avatar(color, description);
                    ArrayList<int[]> coords = squareAroundContaining(new int[]{x, y}, 1, greeneryTileList);
                    drawTiles(coords, Tileset.BURROW);
                }
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);
            }

            else if (world[newX][newY] == Tileset.PORTAL) {
                int[] destination;
                if (World.getPortal1()[0] == newX && World.getPortal1()[1] == newY) {
                    destination = World.getPortal2();
                } else {
                    destination = World.getPortal1();
                }
                int destinationX = destination[0] + dx;
                int destinationY = destination[1] + dy;

                if (canMoveTo(destinationX, destinationY)) {
                    if (world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDRED
                            || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDBLUE
                            || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDORANGE
                            || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDMAGENTA
                            || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDPURPLE
                            || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDWHITE) {
                        TETile tile1 = world[destinationX][destinationY];
                        if (canMoveTo(destinationX + dx, destinationY + dy)) {
                            if (world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDRED
                                    || world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDBLUE
                                    || world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDORANGE
                                    || world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDMAGENTA
                                    || world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDPURPLE
                                    || world[destinationX + dx][destinationY + dy] == Tileset.TELEPORTERDROPPEDWHITE) {
                                TETile tile2 = world[destinationX + dx][destinationY + dy];
                                if (canMoveTo(destinationX + 2 * dx, destinationY + 2 * dy)) {
                                    world[x][y] = Tileset.FLOOR;
                                    x = destinationX + dx;
                                    y = destinationY + dy;
                                    move(direction);
                                    world[destinationX + dx][destinationY + dy] = tile2;
                                    return;
                                } else {
                                    return;
                                }
                            }
                            world[x][y] = Tileset.FLOOR;
                            x = destinationX;
                            y = destinationY;
                            move(direction);
                            world[destinationX][destinationY] = tile1;
                            return;
                        } else {
                            return;
                        }
                    }
                    world[x][y] = Tileset.FLOOR;
                    x = destination[0];
                    y = destination[1];
                    move(direction);
                    world[destination[0]][destination[1]] = Tileset.PORTAL;
                }
            }

            else if (world[newX][newY] == Tileset.CARROT) {
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);

                hasCarrotBoost = true;
                long currentTime = System.currentTimeMillis();
                carrotBoostEndTime = Math.max(carrotBoostEndTime, currentTime) + 3000;
            }

            else if (world[newX][newY] == Tileset.TRAP) {
                trapEndTime = System.currentTimeMillis() + 3000;
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);
            }

            else if (world[newX][newY] == Tileset.BURROW
                    && newX + dx >= 0 && newX + dx < World.getWorldWidth()
                    && newY + dy >= 0 && newY + dy < World.getWorldHeight() - 1
                    && burrowCanMove) {
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);
                if (world[x + dx][y + dy] == Tileset.WALL) {
                    world[x + dx][y + dy] = Tileset.FLOOR;
                    burrowCanMove = false;
                } else if (world[x + dx][y + dy] == Tileset.FLOOR) {
                    world[x + dx][y + dy] = Tileset.FLOOR;
                } else if (world[x + dx][y + dy] != Tileset.RAREFLOWER) {
                    world[x + dx][y + dy] = Tileset.BURROW;
                }
                ArrayList<int[]> coords = squareAroundContaining(new int[]{x, y}, 1, greeneryTileList);
                drawTiles(coords, Tileset.BURROW);
            }

            else if (world[newX][newY] == Tileset.MINDCONTROL) {
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);
                hasMindControl = true;
                mindControlEndTime = System.currentTimeMillis() + 5000;
            }

            else if (world[newX][newY] == Tileset.TELEPORTERPICKUP && !hasTeleporter && !droppedTeleporter) {
                world[x][y] = Tileset.FLOOR;
                x = newX;
                y = newY;
                world[x][y] = Tileset.avatar(color, description);
                hasTeleporter = true;
            }

            else if (world[newX][newY] == Tileset.TELEPORTERDROPPEDRED
                    || world[newX][newY] == Tileset.TELEPORTERDROPPEDBLUE
                    || world[newX][newY] == Tileset.TELEPORTERDROPPEDORANGE
                    || world[newX][newY] == Tileset.TELEPORTERDROPPEDMAGENTA
                    || world[newX][newY] == Tileset.TELEPORTERDROPPEDPURPLE
                    || world[newX][newY] == Tileset.TELEPORTERDROPPEDWHITE) {
                TETile tile = world[newX][newY];
                if (canMoveTo(newX + dx, newY + dy)) {
                    if (world[newX + dx][newY + dy] == Tileset.PORTAL) {
                        int[] destination;
                        if (World.getPortal1()[0] == newX && World.getPortal1()[1] == newY) {
                            destination = World.getPortal2();
                        } else {
                            destination = World.getPortal1();
                        }
                        int destinationX = destination[0] + dx;
                        int destinationY = destination[1] + dy;
                        if (canMoveTo(destinationX, destinationY)) {
                            if (world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDRED
                                    || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDBLUE
                                    || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDORANGE
                                    || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDMAGENTA
                                    || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDPURPLE
                                    || world[destinationX][destinationY] == Tileset.TELEPORTERDROPPEDWHITE) {
                                TETile tile1 = world[destinationX][destinationY];
                                if (canMoveTo(destinationX + dx, destinationY + dy)) {
                                    world[x][y] = Tileset.FLOOR;
                                    x = destinationX;
                                    y = destinationY;
                                    move(direction);
                                    world[destinationX][destinationY] = tile1;
                                    return;
                                } else {
                                    return;
                                }
                            }
                        } else {
                            return;
                        }
                    } else if (world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDRED
                            || world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDBLUE
                            || world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDORANGE
                            || world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDMAGENTA
                            || world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDPURPLE
                            || world[newX + dx][newY + dy] == Tileset.TELEPORTERDROPPEDWHITE) {
                        TETile tile2 = world[newX + dx][newY + dy];
                        if (canMoveTo(newX + 2 * dx, newY + 2 * dy)) {
                            if (world[newX + 2 * dx][newY + 2 * dy] == Tileset.PORTAL) {
                                int[] destination;
                                if (World.getPortal1()[0] == newX && World.getPortal1()[1] == newY) {
                                    destination = World.getPortal2();
                                } else {
                                    destination = World.getPortal1();
                                }
                                int destinationX = destination[0] + dx;
                                int destinationY = destination[1] + dy;

                                if (canMoveTo(destinationX, destinationY)) {
                                    world[x][y] = Tileset.FLOOR;
                                    x = destination[0];
                                    y = destination[1];
                                    move(direction);
                                    world[destination[0]][destination[1]] = Tileset.PORTAL;
                                } else {
                                    return;
                                }
                            }
                            world[x][y] = Tileset.FLOOR;
                            x = newX + dx;
                            y = newY + dy;
                            move(direction);
                            world[newX + dx][newY + dy] = tile2;
                        } else {
                            return;
                        }
                    }
                    world[x][y] = Tileset.FLOOR;
                    x = newX;
                    y = newY;
                    move(direction);
                    world[newX][newY] = tile;
                }
            }
        }
    }

    private boolean canMoveTo(int newX, int newY) {
        return world[newX][newY] == Tileset.FLOOR
                || world[newX][newY] == Tileset.FLOWERPINK
                || world[newX][newY] == Tileset.FLOWERORANGE
                || world[newX][newY] == Tileset.FLOWERBLUE
                || world[newX][newY] == Tileset.PORTAL
                || world[newX][newY] == Tileset.CARROT
                || world[newX][newY] == Tileset.TRAP
                || world[newX][newY] == Tileset.BURROW
                || world[newX][newY] == Tileset.RAREFLOWER
                || world[newX][newY] == Tileset.MINDCONTROL
                || world[newX][newY] == Tileset.TELEPORTERPICKUP
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDRED
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDBLUE
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDORANGE
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDMAGENTA
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDPURPLE
                || world[newX][newY] == Tileset.TELEPORTERDROPPEDWHITE;
    }

    public int getPoints() {
        return flowerPinksCollected + (2 * flowerOrangeCollected) + (5 * flowerBlueCollected) + (15 * rareFlowerCollected);
    }

    private void removeFlowerPinks(int x, int y) {
        for (Iterator<int[]> it = World.flowerPinks.iterator(); it.hasNext(); ) {
            int[] flowerPink = it.next();
            if (flowerPink[0] == x && flowerPink[1] == y) {
                it.remove();
                removeFromAllFlowers(x, y);
                break;
            }
        }
    }

    private void removeFlowerOranges(int x, int y) {
        for (Iterator<int[]> it = World.flowerOranges.iterator(); it.hasNext(); ) {
            int[] flowerOrange = it.next();
            if (flowerOrange[0] == x && flowerOrange[1] == y) {
                it.remove();
                removeFromAllFlowers(x, y);
                break;
            }
        }
    }

    private void removeFlowerBlue(int x, int y) {
        World.flowerBlue.clear();
        removeFromAllFlowers(x, y);
    }

    private void removeFromAllFlowers(int x, int y) {
        for (Iterator<int[]> it = World.allFlowers.iterator(); it.hasNext(); ) {
            int[] flower = it.next();
            if (flower[0] == x && flower[1] == y) {
                it.remove();
                break;
            }
        }
    }

    public ArrayList<int[]> squareAroundContaining(int[] coord, int radius, ArrayList<TETile> tiles) {
        ArrayList<int[]> coordinates = new ArrayList<>();
        int xCenter = coord[0];
        int yCenter = coord[1];

        int xStart = Math.max(0, xCenter - radius);
        int xEnd = Math.min(World.getWorldWidth() - 1, xCenter + radius);
        int yStart = Math.max(0, yCenter - radius);
        int yEnd = Math.min(World.getWorldHeight() - 1, yCenter + radius);

        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
                for (TETile tile : tiles) {
                    if (world[x][y] == tile) {
                        coordinates.add(new int[]{x, y});
                    }
                }
            }
        }
        return coordinates;
    }

    public void drawTiles(ArrayList<int[]> coords, TETile tile) {
        for (int[] coord : coords) {
            world[coord[0]][coord[1]] = tile;
        }
    }

    public void setPosition(int x, int y) {
        TETile tile = world[this.x][this.y];
        world[this.x][this.y] = tile;
        this.x = x;
        this.y = y;
        world[x][y] = Tileset.avatar(color, description);
    }

    public boolean hasCarrotBoost() {
        return hasCarrotBoost && System.currentTimeMillis() < carrotBoostEndTime;
    }

    public boolean hasMindControl() {
        return hasMindControl && System.currentTimeMillis() < mindControlEndTime;
    }

    public boolean isTrapped() {
        return System.currentTimeMillis() < trapEndTime;
    }

    public long getCarrotBoostEndTime() {
        return carrotBoostEndTime;
    }

    public long getTrapEndTime() {
        return trapEndTime;
    }

    public Color getColor() {
        return color;
    }

    public long getMindControlEndTime() {
        return mindControlEndTime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[] getLocation() {
        return new int[]{getX(), getY()};
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.world = new TETile[World.getWorldWidth()][World.getWorldHeight()];
    }
}
