package pixel_souls;

public class Projectile { // for dynamite projectile physics, moves in 360 deg instead of 8 directions
	private Vector position;
	private Vector velocity;
	
	public Projectile(float startX, float startY, float velX, float velY) {
		position = new Vector(startX, startY);
		velocity = new Vector(velX, velY);
	}

	public Vector getPosition() {
		return position;
	}
	
	public void update() {
		position.add(velocity);
	}
}
