package pixel_souls;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class World {
    private BufferedImage[] map; // store the entire tilemap as one image
    private boolean[][] collisionMap; // store the walkable status of each tile

    /*
     * Constructor for the World class
     * Requires the path to the image file that contains the map
     * @param imagePath the path to the image file that contains the map
     */
    public World() {
        loadMap("assets/decorUnderPlayer.png", "assets/decorOverPlayer.png"); // load the map at initialization
        createCollisionMap(); // create the collision map at initialization
    }

    private void loadMap(String pathUnder, String pathOver) {
        try {
        	map = new BufferedImage[2];
            map[0] = ImageIO.read(new File(pathUnder)); // load the map image under the 
            map[1] = ImageIO.read(new File(pathOver)); // load the map image
        } catch (IOException e) {
            e.printStackTrace();
            map = null; 
        }
    }

    
    private void createCollisionMap() { //hardcoded 
    	collisionMap = new boolean[40][21]; // [column][row]];
    	
    	int[][] collisionTiles = { // [column][row] tiles that are collide enabled
            {11, 9}, // hardcoded map of collision enabled tiles
            {6, 3}, {7, 3}, {6, 4}, {7, 4},
            {12, 2}, {13, 2}, {12, 3}, {13, 3},
            {17, 1}, {29, 9}, {28, 2},
            {36, 2}, {37, 2}, {36, 3}, {37, 3},
            {34, 10}, {35, 10}, {34, 11}, {35, 11}, {29, 4}, {30, 4},
            {32, 16}, {33, 16}, {32, 15}, {33, 15},
            {26, 15}, {26, 18}, {29, 5}, {30, 5},
            {14, 15}, {14, 18},
            {6, 13}, {7, 13}, {6, 14}, {7, 14},
            {2, 10}, {2, 11}, {3, 11}, {3, 10}
        };

        // Loop over the collisionTiles array and set the corresponding tiles on the collisionMap
        for (int[] tile : collisionTiles) 
            collisionMap[tile[0]][tile[1]] = true;
    }
    
    public boolean isCollision(int row, int column) {
    	try {
    		if (row >= 0 && row < collisionMap.length && column >= 0 && column < collisionMap[0].length)
            return collisionMap[row][column];
    		else
    			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
    }
    
    
    
    public void mapRenderUnder(Graphics g) {
        if (map != null) {
            g.drawImage(map[0], 0, 0, null); // draw the first layer of the map at the top-left corner
        }
    }
    
	public void mapRenderOver(Graphics g) {
		if (map != null) {
			g.drawImage(map[1], 0, 0, null); // draw the second layer of the map at the top-left corner
		}
	}
}
