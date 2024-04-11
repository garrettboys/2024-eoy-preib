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
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		key =-1; 
		world = new World("assets/map.png");
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
	key = e.getKeyCode();

	}

	

	
}
