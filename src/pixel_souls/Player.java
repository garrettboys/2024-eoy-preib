package pixel_souls;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Player  {
	private float x;
	private float y;
	private int dx; // note: unlike other projects, this is pixels/second not pixels/frame
	private int dy;
	private int width;
	private int height;
	private int health;
	private int moveCooldownMs;
	private int speed;
	private States state;
	private Directions lastDirectionMoved = Directions.EAST;
	private int attackRange;
	
	private Map<States, BufferedImage[]> sprites = new HashMap<>();
	// see getCurrentPlayerSprite() in Game for below variables
	private int idleRightFrameCount;
	private int idleRightAnimationCount;
	private int idleLeftFrameCount;
	private int idleLeftAnimationCount;
	private int runLeftFrameCount;
	private int runLeftAnimationCount;
	private int runRightFrameCount;
	private int runRightAnimationCount;
	private int northAtkFrameCount, northAnimationCount;
	private int eastAtkFrameCount, eastAnimationCount;
	private int southAtkFrameCount, southAnimationCount;
	private int westAtkFrameCount, westAnimationCount;
	private int animationSpeed;
	
	// below interface is used to notify the game that the player has finished attacking and can now move again
	
	public interface AttackCompletionListener {
	    void onAttackComplete();
	}
	
	private AttackCompletionListener attackCompletionListener;
	
	public void setAttackCompletionListener(AttackCompletionListener listener) {
	    this.attackCompletionListener = listener;
	}
	
	public void finishAttack() {
	    if (attackCompletionListener != null) {
	        attackCompletionListener.onAttackComplete();
	    }
	}
	
	public enum States {
		IDLE_RIGHT, IDLE_LEFT, RUN_RIGHT, RUN_LEFT, RUN_UP, RUN_DOWN, ATK_NORTH, ATK_EAST, ATK_SOUTH, ATK_WEST
	}
	
	public enum Directions {
		NORTH, EAST, SOUTH, WEST
	}
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		this.dx = 0;
		this.dy = 0;
		this.width = 32;
		this.height = 32;
		this.health = 100;
		this.moveCooldownMs = 75;
		this.setState(States.IDLE_RIGHT);
		this.setSpeed(90);
		this.idleRightFrameCount = 1;
		this.idleLeftFrameCount = 1;
		this.runRightFrameCount = 1;
		this.runLeftFrameCount = 1;
		this.northAtkFrameCount = 1;
		this.eastAtkFrameCount = 1;
		this.southAtkFrameCount = 1;
		this.westAtkFrameCount = 1;
		this.idleRightAnimationCount = 1;
		this.idleLeftAnimationCount = 1;
		this.runRightAnimationCount = 1;
		this.runLeftAnimationCount = 1;
		this.northAnimationCount = 1;
		this.eastAnimationCount = 1;
		this.southAnimationCount = 1;
		this.westAnimationCount = 1;
		this.animationSpeed = 5; // 5 real frames to 1 animation frame
		this.setAttackRange(32);
		setSprites();
	}
	
	public Player(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.dx = 0;
		this.dy = 0;
		this.width = width;
		this.height = height;
		this.health = 100;
		this.moveCooldownMs = 75;
		this.setState(States.IDLE_RIGHT);
		this.setSpeed(90);
		this.idleRightFrameCount = 1;
		this.idleLeftFrameCount = 1;
		this.runRightFrameCount = 1;
		this.runLeftFrameCount = 1;
		this.northAtkFrameCount = 1;
		this.eastAtkFrameCount = 1;
		this.southAtkFrameCount = 1;
		this.westAtkFrameCount = 1;
		this.idleRightAnimationCount = 1;
		this.runRightAnimationCount = 1;
		this.runLeftAnimationCount = 1;
		this.northAnimationCount = 1;
		this.eastAnimationCount = 1;
		this.southAnimationCount = 1;
		this.westAnimationCount = 1;
		this.animationSpeed = 5; // 5 real frames to 1 animation frame
		this.setAttackRange(32);
		setSprites();
		
	}

	public void setSprites() { // instantiate a map of sprites for each state, standard naming for sprites
		try { 
			BufferedImage[] idleRight = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleRight[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/idle"+i+".png")), 96, 96);
			}
			
			BufferedImage[] idleLeft = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleLeft[i - 1] = ImageUtils.flipImageHorizontally(ImageUtils
						.resizeImage(ImageIO.read(new File("assets/player_sprites/idle" + i + ".png")), 96, 96));
			}
			
			BufferedImage[] runRight = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				runRight[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/run"+i+".png")), 96, 96);
			
			BufferedImage[] runLeft = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				runLeft[i - 1] = ImageUtils.flipImageHorizontally(ImageUtils.resizeImage(ImageIO.read
						(new File("assets/player_sprites/run"+i+".png")), 96, 96));
			
			BufferedImage[] northAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				northAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/northAtk"+i+".png")), 96, 96);
			
			BufferedImage[] eastAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				eastAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/eastAtk"+i+".png")), 96, 96);
			
			BufferedImage[] southAtk = new BufferedImage[6];	
			for (int i = 1; i <= 6; i++)
				southAtk[i-1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/player_sprites/southAtk"+i+".png")), 96, 96);
			
			BufferedImage[] westAtk = new BufferedImage[6];
			for (int i = 1; i <= 6; i++)
				westAtk[i - 1] = ImageUtils.flipImageHorizontally(ImageUtils.resizeImage(ImageIO.read
						(new File("assets/player_sprites/eastAtk"+i+".png")), 96, 96));
			
			sprites.put(States.IDLE_RIGHT, idleRight); 
			sprites.put(States.IDLE_LEFT, idleLeft);
			sprites.put(States.RUN_RIGHT, runRight);
			sprites.put(States.RUN_LEFT, runLeft);
			sprites.put(States.ATK_NORTH, northAtk);
			sprites.put(States.ATK_EAST, eastAtk);
			sprites.put(States.ATK_SOUTH, southAtk);
			sprites.put(States.ATK_WEST, westAtk);		
			
			
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
	public boolean attackCheck(Boss boss) {
	    // uses rectangles because the collision formula is annoying and i want to use .intersects
	    Rectangle attackArea = new Rectangle(
	        this.getX() - this.getAttackRange(), 
	        this.getY() - this.getAttackRange(), 
	        this.getAttackRange() * 2, 
	        this.getAttackRange() * 2
	    );

	  
	    if (attackArea.intersects(boss.getHitbox())) {
	        boss.setHealth(boss.getHealth() - 10);
	        boss.setFlashingRed(true);
	        boss.setRedFlashFramesRemaining(12);
	        return true;
	    }
	    return false;
	}
	
	
	public States getAttackDirection() {
		switch (lastDirectionMoved) {
		case NORTH:
			return States.ATK_NORTH;
		case EAST:
			return States.ATK_EAST;
		case SOUTH:
			return States.ATK_SOUTH;
		case WEST:
			return States.ATK_WEST;
		default:
			return States.IDLE_RIGHT;
		}
	}
	
    public void update(double deltaTime) {
        x += (dx * (deltaTime / 1000));

        y += (dy * (deltaTime / 1000));
    }
    
	public BufferedImage getCurrentSprite() {
		/* for each state, we check if the animation count is equal to the animation speed, so the animationCount
	 	 * is how many actual frames run, and then every animationSpeed frames, we increment the frame count for that state
		 */
		switch (state) {
		case IDLE_RIGHT:  
			if (idleRightFrameCount == animationSpeed) {
				idleRightFrameCount++;
				idleRightAnimationCount = 1;
			}
			
			idleRightAnimationCount++;
				
			if (idleRightFrameCount == 8)
				idleRightFrameCount = 1;
			return getIdleRightSprite(idleRightFrameCount - 1);
			
		case IDLE_LEFT: 
			if (idleLeftAnimationCount == animationSpeed) {
				idleLeftFrameCount++;
				idleLeftAnimationCount = 1;
			}

			idleLeftAnimationCount++;

			if (idleLeftFrameCount == 8)
				idleLeftFrameCount = 1;
			return getIdleLeftSprite(idleLeftFrameCount - 1);
			
		case RUN_RIGHT:
			if (runRightAnimationCount == animationSpeed) {
				runRightFrameCount++;
				runRightAnimationCount = 1;
			}
			
			runRightAnimationCount++;
			
			if (runRightFrameCount == 7)
				runRightFrameCount = 1;
			return getRunRightSprite(runRightFrameCount - 1);
			
		case RUN_LEFT:
			if (runLeftAnimationCount == animationSpeed) {
				runLeftFrameCount++;
				runLeftAnimationCount = 1;
			}
			
			runLeftAnimationCount++;
			
			if (runLeftFrameCount == 7)
				runLeftFrameCount = 1;
			return getRunLeftSprite(runLeftFrameCount - 1);
			
		case ATK_NORTH:
			if (northAnimationCount == animationSpeed) {
			    northAtkFrameCount++;
				northAnimationCount = 1;
			}
			
			northAnimationCount++;
			
			if (northAtkFrameCount == 7) {
				northAtkFrameCount = 1;
				this.setState(States.IDLE_RIGHT);
				finishAttack();
				return getIdleRightSprite(1);

			}
			return getNorthAtkSprite(northAtkFrameCount - 1);
			
		case ATK_EAST:
			if (eastAnimationCount == animationSpeed) {
				eastAtkFrameCount++;
				eastAnimationCount = 1;
			}
			
			eastAnimationCount++;
			
			if (eastAtkFrameCount == 7) {
				eastAtkFrameCount = 1;
				this.setState(States.IDLE_RIGHT);
				finishAttack();
				return getIdleRightSprite(1);
			}
			return getEastAtkSprite(eastAtkFrameCount - 1);
			
		case ATK_SOUTH:
			if (southAnimationCount == animationSpeed) {
				southAtkFrameCount++;
				southAnimationCount = 1;
			}
			
			southAnimationCount++;

			if (southAtkFrameCount == 7) {
				this.setState(States.IDLE_RIGHT);
				southAtkFrameCount = 1;
				finishAttack();
				return getIdleRightSprite(1);

			}
			return getSouthAtkSprite(southAtkFrameCount - 1);
			
		case ATK_WEST:
			if (westAnimationCount == animationSpeed) {
				westAtkFrameCount++;
				westAnimationCount = 1;
			}
			
			westAnimationCount++;
			
			if (westAtkFrameCount == 7) {
				this.setState(States.IDLE_LEFT);
				westAtkFrameCount = 1;
				finishAttack();
				return getIdleLeftSprite(1);

			}
			return getWestAtkSprite(westAtkFrameCount - 1);
			
		default:
			return getIdleRightSprite(1);
		}
		
	}
	
	public Map<States, BufferedImage[]> getSprites() {
		return sprites;
	}
	
	public int getX() {
		return (int)x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return (int)y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	


	public int getMoveCooldownMs() {
		return moveCooldownMs;
	}

	public void setMoveCooldownMs(int moveDelayMs) {
		this.moveCooldownMs = moveDelayMs;
	}
	
	public States getState() {
		return state;
	}

	public void setState(States state) {
		this.state = state;
	}

	public BufferedImage getIdleRightSprite(int frameCt) {
		return sprites.get(States.IDLE_RIGHT)[frameCt];
	}
	
	public BufferedImage getIdleLeftSprite(int frameCt) {
		return sprites.get(States.IDLE_LEFT)[frameCt];
	}
	
	public BufferedImage getRunRightSprite(int frameCt) {
		return sprites.get(States.RUN_RIGHT)[frameCt];
	}
	
	public BufferedImage getRunLeftSprite(int frameCt) {
		return sprites.get(States.RUN_LEFT)[frameCt];
	}

	public BufferedImage getNorthAtkSprite(int frameCt) {
		return sprites.get(States.ATK_NORTH)[frameCt];
	}
	
	public BufferedImage getEastAtkSprite(int frameCt) {
		return sprites.get(States.ATK_EAST)[frameCt];
	}
	
	public BufferedImage getSouthAtkSprite(int frameCt) {
		return sprites.get(States.ATK_SOUTH)[frameCt];
	}
	
	public BufferedImage getWestAtkSprite(int frameCt) {
		return sprites.get(States.ATK_WEST)[frameCt];
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}
	
	public int getIdleRightFrameCount() {
		return idleRightFrameCount;
	}

	public int getIdleRightAnimationCount() {
		return idleRightAnimationCount;
	}

	public int getIdleLeftFrameCount() {
		return idleLeftFrameCount;
	}
	
	public int getIdleLeftAnimationCount() {
		return idleLeftAnimationCount;
	}
	
	public int getRunRightFrameCount() {
		return runRightFrameCount;
	}

	public int getRunAnimationCount() {
		return runRightAnimationCount;
	}

	public int getNorthAtkFrameCount() {
		return northAtkFrameCount;
	}

	public int getNorthAnimationCount() {
		return northAnimationCount;
	}

	public int getEastAtkFrameCount() {
		return eastAtkFrameCount;
	}

	public int getEastAnimationCount() {
		return eastAnimationCount;
	}

	public int getSouthAtkFrameCount() {
		return southAtkFrameCount;
	}

	public int getSouthAnimationCount() {
		return southAnimationCount;
	}

	public int getWestAtkFrameCount() {
		return westAtkFrameCount;
	}

	public int getWestAnimationCount() {
		return westAnimationCount;
	}

	public int getAnimationSpeed() {
		return animationSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Directions getLastDirectionMoved() {
		return lastDirectionMoved;
	}

	public void setLastDirectionMoved(Directions lastDirectionMoved) {
		this.lastDirectionMoved = lastDirectionMoved;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
	
	public Vector getPosition() {	
		return new Vector(x, y);
	}
	
}
