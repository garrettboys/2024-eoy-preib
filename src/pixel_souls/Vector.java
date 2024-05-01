package pixel_souls;

public class Vector { // simple vector class to handle thrown dynamite physics
	private float x, y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	public void scale(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
	}
}
