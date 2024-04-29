package pixel_souls;

public class Projectile {
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
