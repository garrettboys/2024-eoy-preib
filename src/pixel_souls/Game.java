package pixel_souls;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class Game extends JPanel implements Runnable, KeyListener, MouseListener{ 
	
	private BufferedImage back; 
	private World world;
	private Player player;
	private Boss boss;
	private Set<Integer> pressedKeys;
	private boolean isAttacking;
	private SoundPlayer soundPlayer;
	private ArrayList<Projectile> projectiles;
    private List<Explosion> explosions;
    public static String gameState;
    private ImageIcon titleScreen = new ImageIcon("assets/title_assets/bg.png");
    private ImageIcon logo = new ImageIcon("assets/title_assets/logo.png");
    private ImageIcon tab = new ImageIcon("assets/title_assets/tab.png");
	private ImageIcon gameOver = new ImageIcon("assets/over_assets/gameover.png");
	private ImageIcon win = new ImageIcon("assets/win.png");
    private Boolean drawHitboxes = false;
	private Boolean gameOverDebounce = false;

    private Font font;

	private ArrayList<Point> waypointList = new ArrayList<>();

	// controlling frametiming below
	final double TARGET_FRAME_TIME = 1000.0 / 60.0;  // 60 FPS
	private double deltaTime;
	long lastTime = System.nanoTime(); 
	final double NANO_TO_MILLI = 1000000.0;
	
	public void loadFont() {
        try {
            InputStream is = new FileInputStream("assets/title_assets/alagard.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FileNotFoundException e) {
            System.out.println("The font file was not found.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the font file.");
        } catch (FontFormatException e) {
            System.out.println("The font file format is not supported.");
        } 
    }
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		this.addMouseListener(this);
		world = new World();
		player = new Player(640, 576, 64, 64);
		boss = new Boss();
		deltaTime = 0.0167f;
		pressedKeys = new HashSet<>();
		soundPlayer = new SoundPlayer();
		isAttacking = false;
		soundPlayer.playMusic("assets/title_assets/titlebgm.wav");
		projectiles = new ArrayList<>();
		explosions = new ArrayList<>();
		gameState = "START";
		loadFont();
		
		player.setAttackCompletionListener(() -> {
		    onAttackComplete();  
		});
		
		boss.setAttackInitListener(() -> {
			bossAttack();
		});
		
		
		
	}
	
	public void run()
	   {
	   	try
	   	{
	   		while(true)
	   		{ // designed to maintain a standard 60 fps for rendering
	   		    long now = System.nanoTime();
	   		    deltaTime = (now - lastTime) / NANO_TO_MILLI;  
	   		    lastTime = now;

	   		    repaint(); 
	   		    // calculate how much time to sleep to maintain 60 FPS
	   		    double timeTaken = (System.nanoTime() - now) / NANO_TO_MILLI;
	   		    double timeToSleep = TARGET_FRAME_TIME - timeTaken;  // time remaining to reach 16.67 milliseconds (1000 ms / 60 fps)

	   		    if (timeToSleep > 0) 
	   		            Thread.sleep((long)timeToSleep);  // sleep to maintain the frame rate
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
		switch (gameState) {
		case "START": {
			g2d.drawImage(titleScreen.getImage(), 0, 0, null);
			g2d.drawImage(logo.getImage(), 300, 50, null);
			g2d.drawImage(tab.getImage(), 425, 400, null);
			g2d.setFont(font.deriveFont(Font.PLAIN, 30));
			g2d.drawString("Press Space to Start", 520, 435);
			g2d.drawString("WASD to move, F to attack", 500, 350);
			break;
			
		}
		case "GAME": {
			boss.patrol(); 
			player.updateInvincibility();
			updateExplosions();
			world.mapRenderUnder(g2d);
			entityRender(g2d);
			projectileRender(g2d);
			hitboxUpdate();
			world.mapRenderOver(g2d);
			boomCheck();
			drawExplosions(g2d);
			guiRender(g2d);
			if (drawHitboxes) 
				drawHitboxes(g2d);
			bossAttackLogic();
	        break;	
			}
		case "WIN" :  {
			soundPlayer.stopMusic();
			g2d.drawImage(win.getImage(), 0, 0, null);
			g2d.setColor(Color.GREEN);
			g2d.setFont(font.deriveFont(Font.PLAIN, 30));
			g2d.drawString("good job! you killed the goblin. why would you do this", 300, 300);
			if (!gameOverDebounce) {
				soundPlayer.playSoundEffect("assets/electronic_win.wav");
				soundPlayer.playSoundEffect("assets/marimba_win.wav");
				soundPlayer.playSoundEffect("assets/violin_win.wav");
			    gameOverDebounce = true;
			}
			break;
		}

		case "OVER" : {
			soundPlayer.stopMusic();
			g2d.drawImage(gameOver.getImage(), 0, 0, null);
			if (!gameOverDebounce) {
			   soundPlayer.playSoundEffect("assets/baby_cry.wav");
			   soundPlayer.playSoundEffect("assets/laugh.wav");
			   soundPlayer.playSoundEffect("assets/sad_trombone.wav");
			   gameOverDebounce = true;
			}
			g2d.setColor(Color.RED);
			g2d.setFont(font.deriveFont(Font.PLAIN, 30));
			g2d.drawString("there is no restart button. quit the game", 500, 500);
			}
			break;
		}
		

		// CODE ABOVE

		
		twoDgraph.drawImage(back, null, 0, 0);

	}
	
	public void drawHitboxes(Graphics g2d) {
		Rectangle playerHitbox = player.getHitbox();
		Rectangle bossHitbox = boss.getHitbox();
		g2d.drawRect((int)bossHitbox.getX(), (int)bossHitbox.getY(), (int)bossHitbox.getWidth(), (int)bossHitbox.getHeight());
		g2d.drawRect((int)playerHitbox.getX(), (int)playerHitbox.getY(), (int)playerHitbox.getWidth(), (int)playerHitbox.getHeight());
		for (Projectile projectile : projectiles) {
			Rectangle projectileHitbox = projectile.getHitbox();
			g2d.drawRect((int) projectileHitbox.getX(), (int) projectileHitbox.getY(),
					(int) projectileHitbox.getWidth(), (int) projectileHitbox.getHeight());
		}
		// g2d.setColor(Color.blue);
		// g2d.drawRect(
		// (int)player.getHitbox().getX() - player.getAttackRange(), 
		// (int)player.getHitbox().getY() - player.getAttackRange(), 
		// player.getAttackRange() * 2, 
		// player.getAttackRange() * 2
	    // );
		// g2d.setColor(Color.red);

	}
	
	public void hitboxUpdate() {
		//constants added to fix x/y, width/height
		player.setHitbox(new Rectangle(player.getX()+30, player.getY()+30, player.getWidth()-25, player.getHeight()-18));
		boss.setHitbox(new Rectangle(boss.getX()+30, boss.getY()+30, boss.getWidth()-55, boss.getHeight()-55));
		for (Projectile projectile : projectiles) {
			projectile.setHitbox(new Rectangle((int) projectile.getPosition().getX()+40,
					(int) projectile.getPosition().getY()+40, projectile.getWidth(), projectile.getHeight()));
		}
	}
	
	public void boomCheck() {
		List<Projectile> toRemove = new ArrayList<>();
		for (Projectile projectile : projectiles) {
			if (projectile.getHitbox().intersects(player.getHitbox())) {
				toRemove.add(projectile);
				if (player.isInvincible() == false) {
					player.setHealth(player.getHealth() - 10);
					player.startInvincibility();
					addExplosion(new Point((int)projectile.getPosition().getX()+25, (int)projectile.getPosition().getY()+25));

				}
				if (player.getHealth() <= 0) {
					gameState = "OVER";
				}

				}
			}
			projectiles.removeAll(toRemove);
		}
	
	public void entityRender(Graphics g) {
		player.update(deltaTime);
		g.drawImage(player.getCurrentSprite(), player.getX(), player.getY(), null);
		g.setColor(Color.RED);
		g.drawImage(boss.getCurrentSprite(), boss.getX(), boss.getY(), null); 
		boss.setHitbox(new Rectangle(boss.getX(), boss.getY(), 64, 64));
		g.setColor(Color.WHITE);
		//bossAICheck();
	}
	
	public void projectileRender(Graphics g) {
		for (Projectile projectile : projectiles) {
			projectile.update();
			g.drawImage(projectile.getCurrentSprite(), (int)projectile.getPosition().getX(), (int)projectile.getPosition().getY(), null);
		}
	}
	
	public void guiRender(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawRect(120, 50, 1000, 20);
		g.drawRect(30, 625, 500, 20);
		g.setColor(Color.RED);
		g.fillRect(120, 50, boss.getHealth()*2, 20);
		g.fillRect(30, 625, player.getHealth()*5, 20);
		g.setFont(font.deriveFont(Font.PLAIN, 20));
		g.drawString("PROSPECTOR GOBLIN", 120, 110);
		g.drawString(""+player.getHealth(), 30, 600);
	}
	
	public int playerDistance() {
		return (int) Math.sqrt(Math.pow(player.getX() - boss.getX(), 2) + Math.pow(player.getY() - boss.getY(), 2));
	}
	// unimplemented: using set patrol path instead
    /* public void bossAICheck() { 
	        // constants for AI behavior

			int retreatDistance = 50; 
	        int chaseDistance = 300; 
	        double bossSpeed = 3.0; 

	        long stateTime = 0;
	        
	        Point bossPosition = new Point (boss.getX(), boss.getY());
	        Point playerPosition = new Point (player.getX(), player.getY());

	        double distance = Math.sqrt(Math.pow(bossPosition.x - playerPosition.x, 2) + Math.pow(bossPosition.y - playerPosition.y, 2));

	        switch (boss.getAiState()) {
	            case IDLING:
	            	
	            	if (stateTime > 3500) // ms
	            		
	            	break;
	            case CHASING:
	            	
	            	
	            	
	            	break;
	            case THROWING:

	            	break;
	            case RETREATING:

	                break;
	            default:
	                // If state is undefined or unexpected, default to IDLING
	                boss.setAiState(Boss.AIStates.IDLING);
	                break;
	        }
	        		
        }
	*/    
    public void bossAttackLogic() {
        switch (boss.getAttackState()) {
        case IDLE: 
        	if (!boss.isCooldown() && Math.random() > .90)
        		bossAttackSwitcheroo();
        	break;
		case ARMAGEDDON:
			bossArmageddon();
			break;
		case BLOOM:
			bossBloom();
			break;
		case TAP:
		bossTap();
			break;
		default:
			break;
        }
    }
    
    public void bossBloom() {
    	 if (boss.getAttackDuration() == 0) {
    		 boss.setAttackDuration(System.currentTimeMillis());
    	 }
    	 long attackDuration = System.currentTimeMillis() - boss.getAttackDuration();
	    	// spawn a projectile for every 5 degrees around the boss
    	 if (Math.random() < .1)
			for (int i = 0; i < 360; i += 20) {
				Vector bloomVector = new Vector((float) Math.cos(Math.toRadians(i)), (float) Math.sin(Math.toRadians(i)));
				projectiles.add(new Projectile(boss.getX(), boss.getY(), bloomVector, 3.0f));
			}
	    if (attackDuration > 1000) {
	        boss.setAttackState(Boss.AttackStates.IDLE);
	        boss.setAttackDuration(0); 
			boss.setLastAttackTime(System.currentTimeMillis());
	    }
    } 
    
    public void bossArmageddon() {
        if (boss.getAttackDuration() == 0) {
            boss.setAttackDuration(System.currentTimeMillis());
        }
        long attackDuration = System.currentTimeMillis() - boss.getAttackDuration();
            
            Vector attackVector = getAttackVector();
            projectiles.add(new Projectile(boss.getX(), boss.getY(), attackVector, 3.0f));

        if (attackDuration > 2000) {
            boss.setAttackState(Boss.AttackStates.IDLE);
            boss.setAttackDuration(0); 
			boss.setLastAttackTime(System.currentTimeMillis());
        }
    }

	public void bossTap() {
		if (boss.getAttackDuration() == 0) {
			boss.setAttackDuration(System.currentTimeMillis());
		}
		long attackDuration = System.currentTimeMillis() - boss.getAttackDuration();
	
		if (16 >= (attackDuration % 1000) && (attackDuration % 1000) >= 0) { 
			Vector tapVector = getAttackVector();
			projectiles.add(new Projectile(boss.getX(), boss.getY(), tapVector, 3.0f));
		}
	
		if (attackDuration > 6000) { 
			boss.setAttackState(Boss.AttackStates.IDLE);
			boss.setAttackDuration(0); 
			boss.setLastAttackTime(System.currentTimeMillis());
		}
	}

    public void bossAttackSwitcheroo() {
    		if (Math.random() < .5)
    			boss.setAttackState(Boss.AttackStates.TAP);
    		
			else {
			if (Math.random() < .5)
				boss.setAttackState(Boss.AttackStates.ARMAGEDDON);
		    else
		    	boss.setAttackState(Boss.AttackStates.BLOOM);
		}
    }
    
    public void addExplosion(Point position) {
        explosions.add(new Explosion(position, System.currentTimeMillis()));
    }

    public void updateExplosions() {
        long currentTime = System.currentTimeMillis();
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion exp = it.next();
            if ((currentTime - exp.startTime) > 500) {
                it.remove(); 
            }
        }
        
    }
 
    public void drawExplosions(Graphics g2d) {
        for (Explosion exp : explosions) {
            g2d.drawImage(exp.getImage(), exp.position.x, exp.position.y, null);
        }
    }

	public void bossAttack() {
		projectiles.add(
		new Projectile(boss.getX(), boss.getY(), getAttackVector(), 5.0f)
				);
	}
	
	public Vector getAttackVector() {
		Vector AtkVector = Vector.direction(boss.getPosition(), player.getPosition());
		AtkVector.normalize();
		return AtkVector;
	}

	public void keyTyped(KeyEvent e) {
			
	}

	public void keyPressed(KeyEvent e) {
	    pressedKeys.add(e.getKeyCode());
	    updatePlayerState();
	    
		if (e.getKeyCode() == KeyEvent.VK_T) { // boss attack test
			bossAttack();
		}
		
		if (e.getKeyChar() == 'h')
		    drawHitboxes = !drawHitboxes;
		
		if (e.getKeyChar() == ' ') {
			if (gameState.equals("START")) {
				gameState = "GAME";
				soundPlayer.stopMusic();
				soundPlayer.playMusic("assets/pixel_souls_boss.wav");
			}
		}
		
		if (e.getKeyChar() == '1') 
			boss.setAttackState(Boss.AttackStates.BLOOM);
		if (e.getKeyChar() == '2')
			boss.setAttackState(Boss.AttackStates.ARMAGEDDON);
		if (e.getKeyChar() == '3')
			boss.setAttackState(Boss.AttackStates.TAP);
		if (e.getKeyChar() == ']')
			gameState = "WIN";
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
			player.attackCheck(boss);
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

	@Override
	public void mouseClicked(MouseEvent e) {
		waypointList.add(new Point(e.getX(), e.getY()));
		System.out.println(waypointList);
	}

	@Override
	public void mousePressed(MouseEvent e) {	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
