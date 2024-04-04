package pixel_souls;
import java.awt.Image;

public class Tile {
    private Image image;
    private boolean walkable;

    public Tile(Image image, boolean walkable) {
        this.image = image;
        this.walkable = walkable;
    }

    public Image getImage() {
        return image;
    }

    public boolean isWalkable() {
        return walkable;
    }
}