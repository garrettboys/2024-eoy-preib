package pixel_souls;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Projectile { // for dynamite projectile physics, moves in 360 deg instead of 8 directions
	private Vector position;
	private Vector velocity;
	private static List<BufferedImage> sprites = new ArrayList<BufferedImage>();
	
	static {
		try {
			for (int i = 1; i <= 7; i++) {
                sprites.add(ImageIO.read(new File("assets/boss_sprities/dynamite_" + i + ".png")));
            }
		} 
		catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	
	public Projectile(float startX, float startY, Vector direction, float speed) {
	    position = new Vector(startX, startY);
	    direction.normalize();
	    direction.scale(speed); 
	    velocity = direction; 
	}

	
	public Vector getPosition() {
		return position;
	}
	
	
	public void update() {
		position.add(velocity);
	}
}
