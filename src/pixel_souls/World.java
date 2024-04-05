package pixel_souls;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
// above imports for sprite sheet parsing
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
// above imports for XML parsing
public class World {
    private int[][] map; // store tile IDs in a 2D array
    private Image[] tileset; // store tile images in an array, need to parse from spritesheet
    
    /**
     * constructs a new World object by loading a tile map from the specified file path.
     * im so cool making my own javadocs
     * @param path the filepath to the tilemap that should be loaded. has to be XML
     */
    public World(String tileMapPath, String spriteSheetPath) {
        loadMap(tileMapPath);
        this.tileset = loadTilesetFromSpriteSheet(spriteSheetPath, 64, 64, 10, 5); // hardcoded values :P
    }

    // its annyoing that loadMap is void and loadTilesetFromSpriteSheet is Image[] but whatever i dont wanna refactor
    
    private void loadMap(String path) {
        try {
        	
        	// creates a DocumentBuilder and parses the XML file given its path
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getClass().getResourceAsStream(path));
            doc.getDocumentElement().normalize(); // no idea what this does, apparently it's important
            
            // gets all the "layer" elements in the XML file in case I add more later, instead of hardcoding two
            NodeList layerList = doc.getElementsByTagName("layer");
            for (int i = 0; i < layerList.getLength(); i++) {
                Element layer = (Element) layerList.item(i);
                
                NodeList data = layer.getElementsByTagName("data"); // gets the tile data from the current layer
                String mapData = data.item(0).getTextContent().trim(); // gets the text content of the data element for parse
                
                // for now, assuming only use of one layer
                if (i == 0) {
                    String[] ids = mapData.split(","); // uses CSV: comma separated values
                    map = new int[25][28]; // hardcoded 64x64 tile map size
                    for (int row = 0; row < 25; row++) { // terrible time complexity, but it's a small map so whateveerrr
                        for (int col = 0; col < 28; col++) {
                            int id = Integer.parseInt(ids[row * 28 + col].trim()) - 1; // tileIDs start at 1, but array indices start at 0
                            map[row][col] = id; // populates the map with the tile IDs
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // print the error to the console, probably non XML file error
        }
    }

    private Image[] loadTilesetFromSpriteSheet(String spriteSheetPath, int tileWidth, int tileHeight, int columns, int rows) {
        try {
            Image spriteSheet = ImageIO.read(new File(spriteSheetPath));
            Image[] tilesetTemp = new Image[columns * rows];
            
            for (int i = 0; i < rows; i++) {
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
            return null; // i dont want to make error handling so this is ok
        }
    }

    
	public int[][] getMap() {
		return map;
	}
	
	public Image[] getTileset() {
		return tileset;
	}
}
