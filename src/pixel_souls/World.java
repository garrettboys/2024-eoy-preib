package pixel_souls;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class World {
    private BufferedImage map; // store the entire tilemap as one image

    /*
     * Constructor for the World class
     * Requires the path to the image file that contains the map
     * @param imagePath the path to the image file that contains the map
     */
    public World(String imagePath) {
        loadMap(imagePath); // load the map at initialization
    }

    private void loadMap(String path) {
        try {
            map = ImageIO.read(new File(path)); // load the map image
        } catch (IOException e) {
            e.printStackTrace();
            map = null; // if there's an error, set map to null
        }
    }

    public void mapRender(Graphics g) {
        if (map != null) {
            g.drawImage(map, 0, 0, null); // draw the map at the top-left corner
        }
    }
}
