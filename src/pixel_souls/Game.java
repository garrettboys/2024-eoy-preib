package pixel_souls;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage; 
import java.awt.event.*; 
import java.util.*;


public class Game  extends JPanel implements Runnable, KeyListener{
	
	private BufferedImage back; 
	private int key; 
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		key =-1; 
	}
	
	public void run()
	   {
	   	try
	   	{
	   		while(true)
	   		{
	   		   Thread.currentThread().sleep(3);
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
		
		// CODE ABOVE
		twoDgraph.drawImage(back, null, 0, 0);

	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		

	}


	@Override
	public void keyReleased(KeyEvent e) {
		

	}
	
	
	

	
}
