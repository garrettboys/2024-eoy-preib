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
	private int[][] map;
	private Image[] tileset;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		key =-1; 
		world = new World("/initmap.xml", "assets/tileset.png");
		map = world.getMap();
		tileset = world.getTileset();
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
		if(back==null)
			back=(BufferedImage)( (createImage(getWidth(), getHeight()))); 
		

		Graphics g2d = back.createGraphics();
	
		g2d.clearRect(0,0,getSize().width, getSize().height);
		// CODE BELOW
		render(g2d, tileset);
		// CODE ABOVE
		twoDgraph.drawImage(back, null, 0, 0);

	}

	public void render(Graphics g2d, Image[] tileset) { 
	    for (int row = 0; row < map.length; row++) {
	        for (int col = 0; col < map[row].length; col++) {
	            int tileId = map[row][col];
	            if (tileId >= 0) { // -1 is empty tile, but I don't have any right now
	                g2d.drawImage(tileset[tileId], col * 64, row * 64, null);
	            }
	        }
	    }
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
