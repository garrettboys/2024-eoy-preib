package pixel_souls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Game  extends JPanel implements Runnable, KeyListener{ 
	
	private BufferedImage back; 
	private World world;
	private Player player;
	private Boss boss;
	private Set<Integer> pressedKeys;
	private boolean isAttacking;
 

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
		pressedKeys = new HashSet<>();
		isAttacking = false;
		
		player.setAttackCompletionListener(() -> {
		    onAttackComplete();  // lambda function to call onAttackComplete in the Player class using listener interface
		    // this is called when the player's attack animation is complete so it is not interrupted
		});
		
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
		guiRender(g2d);
		// CODE ABOVE

		
		twoDgraph.drawImage(back, null, 0, 0);

	}
	
	public void entityRender(Graphics g) {
		player.update(deltaTime);
		g.drawImage(player.getCurrentSprite(), player.getX(), player.getY(), null);
		g.setColor(Color.RED);
		g.drawImage(boss.getCurrentSprite(), boss.getX(), boss.getY(), null); 
		g.setColor(Color.WHITE);
		boss.updateAI(playerDistance(), player.getHealth());
	}
	
	public void guiRender(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Health: " + player.getHealth(), 10, 20);
		g.drawString("Boss Health: " + boss.getHealth(), 10, 40);
	}
	
	public int playerDistance() {
		return (int) Math.sqrt(Math.pow(player.getX() - boss.getX(), 2) + Math.pow(player.getY() - boss.getY(), 2));
	}
	
	
	
	public void keyTyped(KeyEvent e) {
			
	}

	public void keyPressed(KeyEvent e) {
	    pressedKeys.add(e.getKeyCode());
	    updatePlayerState();
	}
	
	public void keyReleased(KeyEvent e) {
	    pressedKeys.remove(e.getKeyCode());
	    updatePlayerState();
	}

	private void updatePlayerState() {
	    // if currently attacking, ignore all other input until the attack is complete
	    if (isAttacking) return;

	    boolean hasHorizontalInput = pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_D);
	    boolean hasVerticalInput = pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_S);

	    // reset movement velocities
	    player.setDx(0);
	    player.setDy(0);

	    if (pressedKeys.contains(KeyEvent.VK_A)) {
	        player.setState(Player.States.RUN_LEFT);
	        player.setDx(-player.getSpeed());
	        player.setLastDirectionMoved(Player.Directions.WEST);
	        
	    } else if (pressedKeys.contains(KeyEvent.VK_D)) {
	        player.setState(Player.States.RUN_RIGHT);
	        player.setDx(player.getSpeed());
	        player.setLastDirectionMoved(Player.Directions.EAST);
	    }

	    if (pressedKeys.contains(KeyEvent.VK_W)) {
	        player.setDy(-player.getSpeed());
	        player.setLastDirectionMoved(Player.Directions.NORTH);
	        
	    } else if (pressedKeys.contains(KeyEvent.VK_S)) {
	        player.setDy(player.getSpeed());
	        player.setLastDirectionMoved(Player.Directions.SOUTH);
	    }
	    
	    if (!hasHorizontalInput && hasVerticalInput) {
	        player.setState(player.getLastDirectionMoved() == Player.Directions.WEST ? Player.States.RUN_LEFT : Player.States.RUN_RIGHT);
	    }

	    if (pressedKeys.contains(KeyEvent.VK_F) && canAttack()) {
	        player.setState(player.getAttackDirection());
	        System.out.println(player.attackCheck(boss));
	        isAttacking = true;
	    }

	    // reset to appropriate idle state based on last direction moved if no movement keys are pressed
	    if (!hasHorizontalInput && !hasVerticalInput && !isAttacking) {
	        if (player.getLastDirectionMoved() == Player.Directions.WEST) {
	            player.setState(Player.States.IDLE_LEFT);
	        } else {
	            player.setState(Player.States.IDLE_RIGHT);
	        }
	    }
	}

	private boolean canAttack() {
	    return !player.getState().name().startsWith("ATK");
	}



	
	public void onAttackComplete() {
	    isAttacking = false;
	    updatePlayerState();  // re-evaluate state based on current keys
	}


}
