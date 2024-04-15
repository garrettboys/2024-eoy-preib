package pixel_souls;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Player  {
	private int x;
	private int y;
	private int width;
	private int height;
	private int health;
	private int moveCooldownMs;
	private States state;
	private Map<States, BufferedImage[]> sprites = new HashMap<>();
	
	public enum States {
		IDLE, RUN, ATK_NORTH, ATK_EAST, ATK_SOUTH, ATK_WEST
	}
	
	public Player() {
		this.x = 640 + 2;
		this.y = 576; 
		this.width = 32 - 4;
		this.height = 32;
		this.health = 100;
		this.moveCooldownMs = 75;
		this.setState(States.IDLE);
		setSprites();
	}
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		this.width = 32;
		this.height = 32;
		this.health = 100;
		this.moveCooldownMs = 75;
		this.setState(States.IDLE);
		setSprites();
	}
	
	public Player(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.health = 100;
		this.moveCooldownMs = 75;
		this.setState(States.IDLE);
		setSprites();
	}

	public void setSprites() {
		try {
			BufferedImage[] idle = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				System.out.print("assets/player_sprites/idle"+i+".png");
				idle[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/idle"+i+".png")), 96, 96);
			}
			
			BufferedImage[] run = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				run[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/run"+i+".png")), 32, 32);
			
			BufferedImage[] northAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				northAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/northAtk"+i+".png")), 32, 32);
			
			BufferedImage[] eastAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				eastAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/eastAtk"+i+".png")), 32, 32);
			
			BufferedImage[] southAtk = new BufferedImage[6];	
			for (int i = 1; i <= 6; i++)
				southAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/southAtk"+i+".png")), 32, 32);
			
			BufferedImage[] westAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				westAtk[i - 1] = ImageUtils.flipImageHorizontally(ImageUtils.resizeImage(ImageIO.read
						(new File("assets/player_sprites/eastAtk"+i+".png")), 32, 32));
			
			sprites.put(States.IDLE, idle); 
			sprites.put(States.RUN, run);
			sprites.put(States.ATK_NORTH, northAtk);
			sprites.put(States.ATK_EAST, eastAtk);
			sprites.put(States.ATK_SOUTH, southAtk);
			sprites.put(States.ATK_WEST, westAtk);			
			
			
		}
		catch (Exception e) {
			e.printStackTrace(); // required for ImageIO 
			System.out.print("you done fucked up!!");
			
		}
		
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getTileX() {
		return x / 32;
	}
	
	public int getTileY() {
		return y / 32;
	}


	public int getMoveCooldownMs() {
		return moveCooldownMs;
	}

	public void setMoveCooldownMs(int moveDelayMs) {
		this.moveCooldownMs = moveDelayMs;
	}
	
	public States getState() {
		return state;
	}

	public void setState(States state) {
		this.state = state;
	}

	public BufferedImage getIdleSprite(int frameCt) {
		return sprites.get(States.IDLE)[frameCt];
	}
	
	public BufferedImage getRunSprite(int frameCt) {
		return sprites.get(States.RUN)[frameCt];
	}

	public BufferedImage getNorthAtkSprite(int frameCt) {
		return sprites.get(States.ATK_NORTH)[frameCt];
	}
	
	public BufferedImage getEastAtkSprite(int frameCt) {
		return sprites.get(States.ATK_EAST)[frameCt];
	}
	
	public BufferedImage getSouthAtkSprite(int frameCt) {
		return sprites.get(States.ATK_SOUTH)[frameCt];
	}
	
	public BufferedImage getWestAtkSprite(int frameCt) {
		return sprites.get(States.ATK_WEST)[frameCt];
	}
}
