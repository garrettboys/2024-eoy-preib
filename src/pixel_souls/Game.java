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
	private Boss boss;
	private long curtime;  // player movement cooldown control
	private int idleFrameCount;
	private int runFrameCount;
	private int northAtkFrameCount;
	private int eastAtkFrameCount;
	private int southAtkFrameCount;
	private int westAtkFrameCount;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		world = new World();
		player = new Player();
		boss = new Boss();
		curtime = 0;
		idleFrameCount = 1;
		runFrameCount = 1;
		northAtkFrameCount = 1;
		eastAtkFrameCount = 1;
		southAtkFrameCount = 1;
		westAtkFrameCount = 1;

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
		entityRender(g2d);
		world.mapRenderOver(g2d);
		// CODE ABOVE
		twoDgraph.drawImage(back, null, 0, 0);

	}
	
	public void entityRender(Graphics g) {
		g.drawImage(getCurrentPlayerSprite(), player.getX(), player.getY(), null);
		g.setColor(Color.RED);
		g.fillRect(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
		g.setColor(Color.WHITE);
	}
	
	public BufferedImage getCurrentPlayerSprite() {
		switch (player.getState()) {
		case IDLE:
			idleFrameCount++;
			if (idleFrameCount == 8)
				idleFrameCount = 1;
			return player.getIdleSprite(idleFrameCount);
		case RUN:
			runFrameCount++;
			if (runFrameCount == 7)
				runFrameCount = 1;
			return player.getRunSprite(runFrameCount);
		case ATK_NORTH:
			northAtkFrameCount++;
			if (northAtkFrameCount == 7)
				northAtkFrameCount = 1;
			return player.getNorthAtkSprite(northAtkFrameCount);
		case ATK_EAST:
			eastAtkFrameCount++;
			if (eastAtkFrameCount == 7)
				eastAtkFrameCount = 1;
			return player.getEastAtkSprite(eastAtkFrameCount);
		case ATK_SOUTH:
			southAtkFrameCount++;
			if (southAtkFrameCount == 7)
				southAtkFrameCount = 1;
			return player.getSouthAtkSprite(southAtkFrameCount);
		case ATK_WEST:
			westAtkFrameCount++;
			if (westAtkFrameCount == 7)
				westAtkFrameCount = 1;
			return player.getWestAtkSprite(westAtkFrameCount);
		default:
			return player.getIdleSprite(1);
		}
		
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
		
		if (System.currentTimeMillis() - curtime < player.getMoveCooldownMs()) 
			return;
	    	switch (e.getKeyCode()) {
	        case KeyEvent.VK_A:
	        	if (canMoveLeft())
	            player.setX(player.getX() - 32);
	        	curtime = System.currentTimeMillis();
	            break;
	        case KeyEvent.VK_D:
	        	if (canMoveRight())
	            player.setX(player.getX() + 32);
	        	curtime = System.currentTimeMillis();
	            break;
	        case KeyEvent.VK_W:
	        	if (canMoveUp())
	            player.setY(player.getY() - 32);
	        	curtime = System.currentTimeMillis();
	            break;
	        case KeyEvent.VK_S:
	        	if (canMoveDown())
	            player.setY(player.getY() + 32);
	        	curtime = System.currentTimeMillis();
	            break;
	    }
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
	
	}



	
}
