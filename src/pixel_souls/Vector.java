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
	
    public static Vector direction(Vector from, Vector to) {
        return new Vector(to.x - from.x, to.y - from.y);
    }
    
    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        if (length != 0) {
            x /= length;
            y /= length;
        }
    }
    
    public float getX() {
    	return x;
    }
    
    public float getY() {
    	return y;
    }

}
