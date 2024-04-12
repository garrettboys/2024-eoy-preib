package pixel_souls;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage; 
import java.awt.event.*;


@SuppressWarnings("serial")
public class Game  extends JPanel implements Runnable, KeyListener{
	
	private BufferedImage back; 
	private World world;
	private Player player;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		world = new World();
		player = new Player();
	}
	
	public void run()
	   {
	   	try
	   	{
	   		while(true)
	   		{
	   		   Thread.currentThread();
			Thread.sleep(3);
	           repaint();
	         }
	      }
	   		catch(Exception e)
	      {
	      }
	  	}
	
	public void paint(Graphics g){
		
		Graphics2D twoDgraph = (Graphics2D) g; 
		if (back == null) {
		    back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		

		Graphics g2d = back.createGraphics();

		// CODE BELOW
		world.mapRenderUnder(g2d);
		g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
		world.mapRenderOver(g2d);
		// CODE ABOVE
		twoDgraph.drawImage(back, null, 0, 0);

	}
	
	public Boolean canMoveUp() {
		return !world.isCollision(player.getTileX(), player.getTileY() - 1);
	}

	public Boolean canMoveDown() {
		return !world.isCollision(player.getTileX(), player.getTileY() + 1);
	}
	
	public Boolean canMoveLeft() {
		return !world.isCollision(player.getTileX() - 1, player.getTileY());
	}
	
	public Boolean canMoveRight() {
		return !world.isCollision(player.getTileX() + 1, player.getTileY());
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_A:
        	if (canMoveLeft())
            player.setX(player.getX() - 32);
            break;
        case KeyEvent.VK_D:
        	if (canMoveRight())
            player.setX(player.getX() + 32);
            break;
        case KeyEvent.VK_W:
        	if (canMoveUp())
            player.setY(player.getY() - 32);
            break;
        case KeyEvent.VK_S:
        	if (canMoveDown())
            player.setY(player.getY() + 32);
            break;
    }
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
	
	}



	
}
