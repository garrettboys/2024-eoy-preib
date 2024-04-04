package pixel_souls;
import java.awt.*;
import javax.swing.*;

public class Entity {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int dx;
	protected int dy;
	protected boolean movedn, movert;
	protected Color c;
	protected ImageIcon pic;
	protected int getX() {
		return x;
	}
	protected void setX(int x) {
		this.x = x;
	}
	protected int getY() {
		return y;
	}
	protected void setY(int y) {
		this.y = y;
	}
	protected int getWidth() {
		return width;
	}
	protected void setWidth(int width) {
		this.width = width;
	}
	protected int getHeight() {
		return height;
	}
	protected void setHeight(int height) {
		this.height = height;
	}
	protected int getDx() {
		return dx;
	}
	protected void setDx(int dx) {
		this.dx = dx;
	}
	protected int getDy() {
		return dy;
	}
	protected void setDy(int dy) {
		this.dy = dy;
	}
	protected boolean isMovedn() {
		return movedn;
	}
	protected void setMovedn(boolean movedn) {
		this.movedn = movedn;
	}
	protected boolean isMovert() {
		return movert;
	}
	protected void setMovert(boolean movert) {
		this.movert = movert;
	}
	protected Color getC() {
		return c;
	}
	protected void setC(Color c) {
		this.c = c;
	}
	protected ImageIcon getPic() {
		return pic;
	}
	protected void setPic(ImageIcon pic) {
		this.pic = pic;
	}
	

	
}
