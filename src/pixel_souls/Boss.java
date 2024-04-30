package pixel_souls;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Boss { // screw inheritance
    private float x;
    private float y;
    private int width;
    private int height;
    private int health;
    private int dx, dy;
    private Boolean lastMoveRight = true;
    private Map<States, BufferedImage[]> sprites = new HashMap<>();
    
    // hardcoded things because i dont want to write it twice:
    private int idleRightFrameCount = 1;
    private int idleRightAnimationCount = 0;
    private int idleLeftFrameCount = 1;
    private int idleLeftAnimationCount = 0;
    private int runRightFrameCount = 1;
    private int runRightAnimationCount = 0;
    private int runLeftFrameCount = 1;
    private int runLeftAnimationCount = 0;
    private int throwingRightFrameCount = 1;
    private int throwingRightAnimationCount = 0;
    private int throwingLeftFrameCount = 1;
    private int throwingLeftAnimationCount = 0;
    private int animationSpeed = 5; // 5 frames per animation frame
    private States state = States.IDLE_RIGHT; // default state

	// below for boss ai handling
    private AIStates aiState = AIStates.IDLING; // default ai state
    private double distanceToPlayer;
    private double throwingThreshold = 50; // distance(px) at which the boss starts throwing
    private double retreatThreshold = 30; // distance(px) at which the boss starts retreating due to low health or other conditions

    private long lastAttackTime = 0; 
    private static final long ATTACK_COOLDOWN = 3000; //cd in milliseconds (3 seconds)
    
    private Rectangle hitbox;
    
	public enum States {
		IDLE_RIGHT, IDLE_LEFT, THROWING_RIGHT, THROWING_LEFT, RUN_RIGHT, RUN_LEFT
	}
    
	public enum AIStates {
		IDLING, CHASING, THROWING, RETREATING
	}
	public Boss() {
		x = 640;
		y = 288;
		dx = 0; 
		dy = 0;
		width = 96;
		height = 96;
		health = 1000;
		setSprites(sprites);
		setHitbox(new Rectangle((int)x, (int)y, width, height));
	}
	

	    // Determine the state transitions
	    public void updateAI(int playerDistance, int playerHealth) {
	        switch (aiState) {
	            case IDLING:
	                // transition from IDLING to CHASING if player is detected within a certain range
	                if (playerDistance < 500) { // arbitrary distance for detection
	                    aiState = AIStates.CHASING;
	                }
	                break;
	            
	            case CHASING:
	                // transition to THROWING if within throwing range and can attack
	                if (playerDistance < 300 && canAttack()) { // closer distance for throwing
	                    aiState = AIStates.THROWING;
	                } else if (playerDistance >= 500) { // if player moves out of chase range
	                    aiState = AIStates.IDLING;
	                }
	                break;
	            
	            case THROWING:
	                // assume the attack is performed here, and then transition to RETREATING
	                if (canAttack()) {
	                    performThrow();
	                    aiState = AIStates.RETREATING;
	                } else {
	                    // if the boss can't attack, probably due to cooldown, keep chasing
	                    aiState = AIStates.CHASING;
	                }
	                break;
	            
	            case RETREATING:
	                // transition back to CHASING after increasing the distance
	                if (playerDistance > 400) { // increased distance before stopping retreat
	                    aiState = AIStates.CHASING;
	                }
	                break;
	        }
	    }
	    
	    public boolean canAttack() {
	        long currentTime = System.currentTimeMillis();
	        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
	            lastAttackTime = currentTime; // reset
	            return true;
	        }
	        return false;
	    }


	private void performIdle() {
		if (lastMoveRight) {
			state = States.IDLE_RIGHT;
		} else {
			state = States.IDLE_LEFT;
		}
	}
	
	private void performChase() {
		if (x < 640) {
			dx = 1;
			lastMoveRight = true;
			state = States.RUN_RIGHT;
		} else {
			dx = -1;
			lastMoveRight = false;
			state = States.RUN_LEFT;
		}
	}
	
	private void performThrow() {
		if (lastMoveRight) {
			state = States.THROWING_RIGHT;
		} else {
			state = States.THROWING_LEFT;
		}
	}
	
	private void performRetreat() {
		if (x < 640) {
			dx = -1;
			lastMoveRight = false;
			state = States.RUN_LEFT;
		} else {
			dx = 1;
			lastMoveRight = true;
			state = States.RUN_RIGHT;
		}
	}
	
	public void setSprites(Map<States, BufferedImage[]> sprites) {
		try {
			BufferedImage[] idleRight = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleRight[i-1] = ImageUtils
						.resizeImage(ImageIO.read(new File("assets/boss_sprites/idle"+i+".png")), 96, 96);
			}
			sprites.put(States.IDLE_RIGHT, idleRight);
			
			BufferedImage[] idleLeft = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleLeft[i - 1] = ImageUtils.flipImageHorizontally(idleRight[i - 1]);
			}
			sprites.put(States.IDLE_LEFT, idleLeft);
			
			BufferedImage[] throwingRight = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				throwingRight[i - 1] = ImageUtils
						.resizeImage(ImageIO.read(new File("assets/boss_sprites/throw" + i + ".png")), 96, 96);
			}
			sprites.put(States.THROWING_RIGHT, throwingRight);
			
			BufferedImage[] throwingLeft = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				throwingLeft[i - 1] = ImageUtils.flipImageHorizontally(throwingRight[i - 1]);
			}
			sprites.put(States.THROWING_LEFT, throwingLeft);
			
			BufferedImage[] runRight = new BufferedImage[6];
			for (int i = 1; i <= 6; i++) {
				runRight[i - 1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/boss_sprites/run" + i + ".png")),
						96, 96);
			}
			sprites.put(States.RUN_RIGHT, runRight);
			
			BufferedImage[] runLeft = new BufferedImage[6];
			for (int i = 1; i <= 6; i++) {
				runLeft[i - 1] = ImageUtils.flipImageHorizontally(runRight[i - 1]);
			}
			sprites.put(States.RUN_LEFT, runLeft);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getCurrentSprite() {
	    switch (state) {
	    case IDLE_RIGHT:  
	        if (++idleRightAnimationCount == animationSpeed) {
	            idleRightAnimationCount = 0;
	            idleRightFrameCount = (idleRightFrameCount % 7) + 1;
	        }
	        return getIdleRightSprite(idleRightFrameCount - 1);

	    case IDLE_LEFT: 
	        if (++idleLeftAnimationCount == animationSpeed) {
	            idleLeftAnimationCount = 0;
	            idleLeftFrameCount = (idleLeftFrameCount % 8) + 1;
	        }
	        return getIdleLeftSprite(idleLeftFrameCount - 1);
	        
	    case RUN_RIGHT:
	        if (++runRightAnimationCount == animationSpeed) {
	            runRightAnimationCount = 0;
	            runRightFrameCount = (runRightFrameCount % 7) + 1;
	        }
	        return getRunRightSprite(runRightFrameCount - 1);
	        
	    case RUN_LEFT:
	        if (++runLeftAnimationCount == animationSpeed) {
	            runLeftAnimationCount = 0;
	            runLeftFrameCount = (runLeftFrameCount % 7) + 1;
	        }
	        return getRunLeftSprite(runLeftFrameCount - 1);
	        
	    case THROWING_RIGHT:
	        if (++throwingRightAnimationCount == animationSpeed) {
	            throwingRightAnimationCount = 0;
	            throwingRightFrameCount = (throwingRightFrameCount % 5) + 1; // Assuming 5 frames for throwing animation
	            if (throwingRightFrameCount == 1) {
	                this.setState(States.IDLE_RIGHT);
	            }
	        }
	        return getThrowingRightSprite(throwingRightFrameCount - 1);
	        
	    case THROWING_LEFT:
	        if (++throwingLeftAnimationCount == animationSpeed) {
	            throwingLeftAnimationCount = 0;
	            throwingLeftFrameCount = (throwingLeftFrameCount % 5) + 1; // Assuming 5 frames for throwing animation
	            if (throwingLeftFrameCount == 1) {
	                this.setState(States.IDLE_LEFT);
	            }
	        }
	        return getThrowingLeftSprite(throwingLeftFrameCount - 1);

	    default:
	        return getIdleRightSprite(1);
	    }
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

	public BufferedImage getThrowingRightSprite(int frameCt) {
	    return sprites.get(States.THROWING_RIGHT)[frameCt];
	}

	public BufferedImage getThrowingLeftSprite(int frameCt) {
	    return sprites.get(States.THROWING_LEFT)[frameCt];
	}

    
    public int getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(int animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	public AIStates getAiState() {
		return aiState;
	}

	public void setAiState(AIStates aiState) {
		this.aiState = aiState;
	}

	public double getDistanceToPlayer() {
		return distanceToPlayer;
	}

	public void setDistanceToPlayer(double distanceToPlayer) {
		this.distanceToPlayer = distanceToPlayer;
	}

	
	public int getX() {
		return (int)(x+.5);
	}

	public void setX(float x) {
		this.x = x;
	}

	public int getY() {
		return (int)(y+.5);
	}

	public void setY(float y) {
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

	public States getState() {
		return state;
	}

	public void setState(States state) {
		this.state = state;
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

	public Boolean getLastMoveRight() {
		return lastMoveRight;
	}

	public void setLastMoveRight(Boolean lastMoveRight) {
		this.lastMoveRight = lastMoveRight;
	}


	public Rectangle getHitbox() {
		return hitbox;
	}


	public void setHitbox(Rectangle hitbox) {
		this.hitbox = hitbox;
	}
	

	
	
	

}
