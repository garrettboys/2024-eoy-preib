package pixel_souls;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Explosion {
    Point position;
    long startTime;
    BufferedImage image;

    public Explosion(Point position, long startTime) {
        this.position = position;
        this.startTime = startTime;
        try {
			image = ImageUtils.resizeImage(ImageIO.read(new File("assets/explosion.png")), 64, 64);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("idiot");
		}
    }

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
