package tileengine;

import java.awt.*;

public class Tileset {

    public static TETile avatar(Color color, String description) {
        String filepath = "";
        if (color.equals(new Color(68, 198, 215))) {
            filepath = "Resources/Images/Bunnies/BunnyBlue.png";
        } else if (color.equals(new Color(198, 72, 43))) {
            filepath = "Resources/Images/Bunnies/BunnyRed.png";
        } else if (color.equals(new Color(254, 167, 62))) {
            filepath = "Resources/Images/Bunnies/BunnyOrange.png";
        } else if (color.equals(new Color(215, 69, 217))) {
            filepath = "Resources/Images/Bunnies/BunnyPink.png";
        } else if (color.equals(new Color(110, 68, 243))) {
            filepath = "Resources/Images/Bunnies/BunnyPurple.png";
        } else if (color.equals(Color.WHITE)) {
            filepath = "Resources/Images/Bunnies/BunnyWhite.png";
        }
        return new TETile('@', Color.black, Color.black, description, filepath, 1);
    }

    public static final TETile WALL = new TETile('≈', new Color(55, 93, 41), new Color(12, 61, 6),"Hedge", "Resources/Images/Plants/Hedge.png", 2);
    public static final TETile FLOOR = new TETile('·', new Color(72, 133, 49), new Color(175, 229, 155), "Grass", 3);
    public static final TETile NOTHING = new TETile(' ', Color.black, new Color(81, 129, 63), "Rough", 4);
    public static final TETile GRASS = new TETile('"', Color.black, Color.black, "Blade", "Resources/Images/Plants/Grass.png", 5);
    public static final TETile WEED = new TETile('^', Color.black, Color.black, "Weed", "Resources/Images/Plants/Weed.png", 6);
    public static final TETile SHRUB = new TETile('*', Color.black, Color.black, "Shrub", "Resources/Images/Plants/Shrub.png", 7);
    public static final TETile FLOWERPINK = new TETile('$', Color.orange, Color.black, "Pink Flower (1pt.)", "Resources/Images/Flowers/FlowerPink.png", 8);
    public static final TETile FLOWERORANGE = new TETile('$', Color.orange, Color.black, "Orange Flower (2pts.)", "Resources/Images/Flowers/FlowerOrange.png", 9);
    public static final TETile FLOWERBLUE = new TETile('$', Color.orange, Color.black, "Blue Flower (5pts.)", "Resources/Images/Flowers/FlowerBlue.png", 10);
    public static final TETile PORTAL = new TETile('p', Color.white, Color.black, "Hole", "Resources/Images/Items/Hole.png" ,11);
    public static final TETile CARROT = new TETile('c', Color.white, Color.black, "Carrot", "Resources/Images/Items/Carrot.png" ,12);
    public static final TETile TRAP = new TETile('t', Color.white, Color.black, "Trap", "Resources/Images/Items/Trap.png", 13);
    public static final TETile BURROW = new TETile('b', Color.white, Color.black, "Burrow", "Resources/Images/Items/Burrow.png", 14);
    public static final TETile RAREFLOWER = new TETile('r', Color.orange, Color.black, "Rare Flower (15pts.)", "Resources/Images/Flowers/RareFlower.png", 15);
    public static final TETile MINDCONTROL = new TETile('m', Color.orange, Color.black, "Mind Control Mushroom", "Resources/Images/Items/MindControl.png", 16);
    public static final TETile TELEPORTERPICKUP = new TETile('t', Color.blue, Color.black, "Teleporter - Pickup", "Resources/Images/Items/TeleporterPickup.png", 17);
    public static final TETile TELEPORTERDROPPEDBLUE = new TETile('T', Color.yellow, Color.black, "Teleporter - Placed (Blue)", "Resources/Images/Items/TeleporterDroppedBlue.png",18);
    public static final TETile TELEPORTERDROPPEDRED = new TETile('T', Color.yellow, Color.black, "Teleporter - Placed (Red)", "Resources/Images/Items/TeleporterDroppedRed.png",19);
    public static final TETile TELEPORTERDROPPEDPURPLE = new TETile('w', Color.yellow, Color.black, "Teleporter - Placed (Purple)", "Resources/Images/Items/TeleporterDroppedPurple.png",20);
    public static final TETile TELEPORTERDROPPEDMAGENTA = new TETile('T', Color.yellow, Color.black, "Teleporter - Placed (Pink)", "Resources/Images/Items/TeleporterDroppedPink.png",21);
    public static final TETile TELEPORTERDROPPEDWHITE = new TETile('w', Color.yellow, Color.black, "Teleporter - Placed (White)", "Resources/Images/Items/TeleporterDroppedWhite.png",22);
    public static final TETile TELEPORTERDROPPEDORANGE = new TETile('w', Color.yellow, Color.black, "Teleporter - Placed (Orange)", "Resources/Images/Items/TeleporterDroppedOrange.png",23);
}
