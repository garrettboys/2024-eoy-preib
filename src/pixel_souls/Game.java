package pixel_souls;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage; 
import java.awt.event.*;


@SuppressWarnings("serial")
public class Game  extends JPanel implements Runnable, KeyListener{
	
	private BufferedImage back; 
	private int key; 
	private World world;
	private Player player;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		key = -1; 
		world = new World("assets/map.png");
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
		world.mapRender(g2d);
		g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
		// CODE ABOVE
		twoDgraph.drawImage(back, null, 0, 0);

	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		key = e.getKeyCode();
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setX(player.getX() - 32);
                break;
            case KeyEvent.VK_D:
                player.setX(player.getX() + 32);
                break;
            case KeyEvent.VK_W:
                player.setY(player.getY() - 32);
                break;
            case KeyEvent.VK_S:
                player.setY(player.getY() + 32);
                break;
        }
	
	}

	

	
}
