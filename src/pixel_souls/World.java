package pixel_souls;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class World {
    private Tile[][] tiles;

    public World() {
        tiles = new Tile[20][20]; // Example size
        generateWorld();
    }

    private void generateWorld() {
        // Example generation
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                // Placeholder: all tiles walkable for now
                tiles[i][j] = new Tile(new ImageIcon("path/to/tile/image.png").getImage(), true);
            }
        }
    }

    public void draw(Graphics g) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                g.drawImage(tiles[i][j].getImage(), i * 32, j * 32, null);
            }
        }
    }
}
