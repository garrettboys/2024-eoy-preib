package pixel_souls;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// above imports for sprite sheet parsing
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
// above imports for XML parsing
public class World {
    private int[][][] map; // store tile IDs in a 3D array [layer][row][col]
    private List<Image[]> tilesets; // store tilesets in a list of arrays 
    private List<Integer> firstGids; 
    public World() {
        loadMap("/4_8_24map.xml");
        loadTilesets();
    }
    
    
    private void loadMap(String path) {
        try {
        	
        	// creates a DocumentBuilder and parses the XML file given its path
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getClass().getResourceAsStream(path));
            doc.getDocumentElement().normalize(); // no idea what this does, apparently it's important
            
            // gets all the "layer" elements in the XML file
            NodeList layerList = doc.getElementsByTagName("layer");
            for (int layerIndex = 0; layerIndex < layerList.getLength(); layerIndex++) {
                Element layer = (Element) layerList.item(layerIndex);
                
                NodeList data = layer.getElementsByTagName("data"); // gets the tile data from the current layer
                String mapData = data.item(0).getTextContent().trim(); // gets the text content of the data element for parse
                
                // only happens once for the first layer, since the map is the same for all layers
                if (layerIndex == 0)    
                    map = new int[layerList.getLength()][22][41]; // hardcoded 32x32 tile map size
                String[] ids = mapData.split(","); // uses CSV: comma separated values
                	for (int row = 0; row < 22; row++) { // terrible time complexity, but it's a small map so whateveerrr
                		for (int col = 0; col < 41; col++) {
                			int id = Integer.parseInt(ids[row * 41 + col].trim()); // tileIDs start at 1, but array indices start at 0
                			map[layerIndex][row][col] = id; // populates the map with the tile IDs
                		}
                    }
            	}
        	}
        catch (Exception e) {
            e.printStackTrace(); // error handling is for chumps
            }
    }

    private void loadTilesets() {
    	tilesets = new ArrayList<>();
    	tilesets.add(loadTilesetFromSpriteSheet("assets/Props.png", 32, 32, 16, 16));
    	tilesets.add(loadTilesetFromSpriteSheet("assets/Grass.png", 32, 32, 8, 8));
    	tilesets.add(loadTilesetFromSpriteSheet("assets/Shadow.png", 32, 32, 16, 16));
    	tilesets.add(loadTilesetFromSpriteSheet("assets/Struct.png", 32, 32, 16, 16));
    	
    	firstGids = new ArrayList<>();
    	firstGids.add(1);
    	firstGids.add(65);
    	firstGids.add(321);
    	firstGids.add(577);
    }
    
    private Image[] loadTilesetFromSpriteSheet(String spriteSheetPath, int tileWidth, int tileHeight, int columns, int rows) {
        Image[] exception = new Image[255]; // return a blank array if an exception occurs
        try {
            Image spriteSheet = ImageIO.read(new File(spriteSheetPath)); // reads the sprite sheet from the file path
            Image[] tilesetTemp = new Image[columns * rows]; // creates an array to store the tile images
            
            for (int i = 0; i < rows; i++) { // loops through the rows and columns of the sprite sheet
                for (int j = 0; j < columns; j++) {
                    BufferedImage tile = ((BufferedImage) spriteSheet).getSubimage(
                        j * tileWidth,
                        i * tileHeight,
                        tileWidth,
                        tileHeight
                    );
                    tilesetTemp[i * columns + j] = tile;
                }
            }
            return tilesetTemp;
        } catch (IOException e) {
            e.printStackTrace();
            return exception; // i dont want to make error handling so this is ok
        }
    }

	public void render(Graphics g2d) {
	    for (int layer = 0; layer < map.length; layer++) {
	        for (int row = 0; row < map[layer].length; row++) {
	            for (int col = 0; col < map[layer][row].length; col++) {
	                int globalTileId = map[layer][row][col];
	                
	                if (globalTileId > 0) {
	                    // Determine the correct tileset and tile ID
	                    Image tileImage = findTileImageByGlobalId(globalTileId);
	                    if (tileImage != null) {
	                        g2d.drawImage(tileImage, col * 32, row * 32, null);
	                    }
	                }
	            }
	        }
	    }
	}
	
	private Image findTileImageByGlobalId(int globalTileId) {
	    for (int i = 0; i < tilesets.size(); i++) {
	        int firstGid = firstGids.get(i);
	        int lastGid = i < tilesets.size() - 1 ? firstGids.get(i + 1) - 1 : Integer.MAX_VALUE; 
		        // x ? y : z is a shorthand if statement, if x is true, then y is returned, if not, z (ternary operator)
	        if (globalTileId >= firstGid && globalTileId < lastGid) {
	            int localTileId = globalTileId - firstGid;
	            Image[] tileset = tilesets.get(i);
	            System.out.println("Global ID: " + globalTileId + ", First GID: " + firstGid + ", Local Tile ID: " + localTileId);  //debug
	            if (localTileId < tileset.length) {
	                return tileset[localTileId];
	            }
	        }
	    }
		    return null; // Tile not found or error
	}
	
    
	public int[][][] getMap() {
		return map;
	}
	
	public List<Image[]> getTilesets() {
		return tilesets;
	}
}
