package pixel_souls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Game  extends JPanel implements Runnable, KeyListener{
	
	private BufferedImage back; 
	private World world;
	private Player player;
	private Boss boss;
 

	// controlling frametiming below
	final double TARGET_FRAME_TIME = 1000.0 / 60.0;  // 60 FPS
	private double deltaTime;
	long lastTime = System.nanoTime(); 
	final double NANO_TO_MILLI = 1000000.0;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		world = new World();
		player = new Player(640, 576, 64, 64);
		boss = new Boss();
		deltaTime = 0.0167f;

	}
	
	public void run()
	   {
	   	try
	   	{
	   		while(true)
	   		{ // designed to maintain a standard 60 fps for rendering
	   		    long now = System.nanoTime();
	   		    deltaTime = (now - lastTime) / NANO_TO_MILLI;  // Calculate deltaTime in milliseconds
	   		    lastTime = now;

	   		    repaint();  // Perform game updates and rendering
	   		    // Calculate how much time to sleep to maintain 60 FPS
	   		    double timeTaken = (System.nanoTime() - now) / NANO_TO_MILLI;
	   		    double timeToSleep = TARGET_FRAME_TIME - timeTaken;  // Time remaining to reach 16.67 milliseconds

	   		    if (timeToSleep > 0) 
	   		            Thread.sleep((long)timeToSleep);  // Sleep to maintain the frame rate
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
		player.update(deltaTime);
		g.drawImage(player.getCurrentSprite(), player.getX(), player.getY(), null);
		g.setColor(Color.RED);
		g.fillRect(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
		g.setColor(Color.WHITE);
	}
	

	
	
	
	
	@Override
	public void keyTyped(KeyEvent e) {
	
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
	    case KeyEvent.VK_A: 
	    	player.setState(Player.States.RUN_LEFT);
            player.setDx(-player.getSpeed());  
            player.setLastDirectionMoved(Player.Directions.WEST);
            break;
	    case KeyEvent.VK_D:
	    	player.setState(Player.States.RUN_RIGHT);
            player.setDx(player.getSpeed());  
            player.setLastDirectionMoved(Player.Directions.EAST);
            break;
	    case KeyEvent.VK_W:
	        player.setDy(-player.getSpeed());  
	        player.setLastDirectionMoved(Player.Directions.NORTH);
	        break;
	    case KeyEvent.VK_S:
	        player.setDy(player.getSpeed()); 
	        player.setLastDirectionMoved(Player.Directions.SOUTH);
	        break;
	    case KeyEvent.VK_F:
	    	player.setState(player.getAttackDirection());
	    	break;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
        case KeyEvent.VK_A: 
        	player.setState(Player.States.IDLE);
            player.setDx(0);
            break;
        case KeyEvent.VK_D:
        	player.setState(Player.States.IDLE);
            player.setDx(0); 
            break;
        case KeyEvent.VK_W:
        	player.setState(Player.States.IDLE);
            player.setDy(0);  
            break;
        case KeyEvent.VK_S:
        	player.setState(Player.States.IDLE);
            player.setDy(0);  
            break;
		}      
		            
	}


}
