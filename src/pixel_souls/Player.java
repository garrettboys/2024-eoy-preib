package pixel_souls;

public class Player  {
	private int x;
	private int y;
	private int width;
	private int height;
	private int health = 100;
	
	public Player() {
		this.x = 640;
		this.y = 576;
		this.width = 32;
		this.height = 32;
	}
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		width = 32;
		height = 32;
	}
	
	public Player(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.health = 100;
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
	

}
