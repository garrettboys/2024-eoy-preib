package pixel_souls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import pixel_souls.Boss.AIStates;


@SuppressWarnings("serial")
public class Game  extends JPanel implements Runnable, KeyListener{ 
	
	private BufferedImage back; 
	private World world;
	private Player player;
	private Boss boss;
	private Set<Integer> pressedKeys;
	private boolean isAttacking;
	private SoundPlayer soundPlayer;

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
		soundPlayer = new SoundPlayer();
		isAttacking = false;
		soundPlayer.playBackgroundMusic("assets/pixel_souls_boss.wav");
		
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
		bossAICheck();
	}
	
	public void guiRender(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawRect(120, 50, 1000, 20);
		g.setColor(Color.RED);
		g.fillRect(120, 50, boss.getHealth()*2, 20);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("PROSPECTOR GOBLIN", 120, 110);
	}
	
	public int playerDistance() {
		return (int) Math.sqrt(Math.pow(player.getX() - boss.getX(), 2) + Math.pow(player.getY() - boss.getY(), 2));
	}
	

	    public void bossAICheck() {
	        // Constants for AI behavior
	        final int attackDistance = 100; // The distance within which the boss can throw
	        final int retreatDistance = 50; // The distance to maintain while retreating
	        final int chaseDistance = 300; // Distance under which the boss will start chasing
	        final double bossSpeed = 1.0; // Boss speed in pixels per frame

	        // Get current positions of the boss and the player
	        Point bossPosition = boss.getPosition();
	        Point playerPosition = player.getPosition();

	        // Calculate the distance between the boss and the player
	        double distance = Math.sqrt(Math.pow(bossPosition.x - playerPosition.x, 2) + Math.pow(bossPosition.y - playerPosition.y, 2));

	        // State transitions based on current state
	        switch (boss.getAiState()) {
	            case IDLING:
	                // Transition from IDLING to CHASING or THROWING
	                if (distance <= attackDistance && canAttack()) {
	                    boss.setAiState(AIStates.THROWING);
	                } else if (distance < chaseDistance) {
	                    boss.setAiState(AIStates.CHASING);
	                }
	                break;

	            case CHASING:
	                // Update position to chase the player
	                if (distance > attackDistance) {
	                    double dx = playerPosition.x - bossPosition.x;
	                    double dy = playerPosition.y - bossPosition.y;
	                    double norm = Math.sqrt(dx * dx + dy * dy);
	                    dx /= norm; // Normalize the difference
	                    dy /= norm; // Normalize the difference

	                    // Update boss position
	                    boss.setX((int)(bossPosition.x + dx * bossSpeed));
	                    boss.setY((int)(bossPosition.y + dy * bossSpeed));   
	                    
	                }

	                // Transition from CHASING to THROWING or RETREATING
	                if (distance <= attackDistance && canAttack()) {
	                    boss.setAiState(AIStates.THROWING);
	                } else if (distance > chaseDistance) {
	                    boss.setAiState(AIStates.IDLING);
	                }
	                break;

	            case THROWING:
	                // After throwing, transition to RETREATING
	                boss.setAiState(AIStates.RETREATING);
	                break;

	            case RETREATING:
	                // Logic for RETREATING state here (not detailed in this snippet)
	                break;

	            default:
	                // If state is undefined or unexpected, default to IDLING
	                boss.setAiState(AIStates.IDLING);
	                break;
	        }
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
