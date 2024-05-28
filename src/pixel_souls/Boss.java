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
    private Map<AnimationStates, BufferedImage[]> sprites = new HashMap<>();
    
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
    private AnimationStates animState = AnimationStates.IDLE_RIGHT; // default state

	// below for boss ai handling
    private AIStates aiState = AIStates.IDLING; // default ai state
    private double distanceToPlayer;

    private AttackStates attackState = AttackStates.IDLE;
    
    private long lastAttackTime = 0; 
    private long attackDuration = 0;
    private static final long ATTACK_COOLDOWN = 3000; //cd in milliseconds (3 seconds)
    
    private boolean isFlashingRed = false;
    private int redFlashFramesRemaining = 0;
    
    private Rectangle hitbox;
    
    private attackInitListener attackInitListener;
    
    
    public interface attackInitListener {
    	void attack();
    }
    
    public void setAttackInitListener(attackInitListener listener) {
    	this.attackInitListener = listener;
    }
    
	public void startAttack() {
	    if (attackInitListener != null) {
	        attackInitListener.attack();
	    }
	}
    
	public enum AnimationStates {
		IDLE_RIGHT, IDLE_LEFT, THROWING_RIGHT, THROWING_LEFT, RUN_RIGHT, RUN_LEFT
	}
    
	public enum AIStates {
		IDLING, CHASING, THROWING, RETREATING
	}
	
	public enum AttackStates {
		IDLE,
		TAP, // like the boss is 'tapping' the attack key 5 times
		BURST, // three, three round bursts of dynamite
		ARMAGEDDON, // spams as much dynamite as possible for 5 seconds
		BLOOM //  sends 360 things of dynamite for every degree outward from the boss
	}
	
	public Boss() {
		x = 610;
		y = 250;
		dx = 0; 
		dy = 0;
		width = 96;
		height = 96;
		health = 500;
		setSprites(sprites);
		setHitbox(new Rectangle((int)x, (int)y, width, height));
	}

	

	public boolean canAttack() {
	        long currentTime = System.currentTimeMillis();
	        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
	            lastAttackTime = currentTime; // reset
	            return true;
	        }
	        return false;
	    }

	
	public void setSprites(Map<AnimationStates, BufferedImage[]> sprites) {
		try {
			BufferedImage[] idleRight = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleRight[i-1] = ImageUtils
						.resizeImage(ImageIO.read(new File("assets/boss_sprites/idle"+i+".png")), 96, 96);
			}
			sprites.put(AnimationStates.IDLE_RIGHT, idleRight);
			
			BufferedImage[] idleLeft = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				idleLeft[i - 1] = ImageUtils.flipImageHorizontally(idleRight[i - 1]);
			}
			sprites.put(AnimationStates.IDLE_LEFT, idleLeft);
			
			BufferedImage[] throwingRight = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				throwingRight[i - 1] = ImageUtils
						.resizeImage(ImageIO.read(new File("assets/boss_sprites/throw" + i + ".png")), 96, 96);
			}
			sprites.put(AnimationStates.THROWING_RIGHT, throwingRight);
			
			BufferedImage[] throwingLeft = new BufferedImage[7];
			for (int i = 1; i <= 7; i++) {
				throwingLeft[i - 1] = ImageUtils.flipImageHorizontally(throwingRight[i - 1]);
			}
			sprites.put(AnimationStates.THROWING_LEFT, throwingLeft);
			
			BufferedImage[] runRight = new BufferedImage[6];
			for (int i = 1; i <= 6; i++) {
				runRight[i - 1] = ImageUtils.resizeImage(ImageIO.read(new File("assets/boss_sprites/run" + i + ".png")),
						96, 96);
			}
			sprites.put(AnimationStates.RUN_RIGHT, runRight);
			
			BufferedImage[] runLeft = new BufferedImage[6];
			for (int i = 1; i <= 6; i++) {
				runLeft[i - 1] = ImageUtils.flipImageHorizontally(runRight[i - 1]);
			}
			sprites.put(AnimationStates.RUN_LEFT, runLeft);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getCurrentSprite() { // attack logic contained in here
		if (isFlashingRed && redFlashFramesRemaining == 0) 
            isFlashingRed = false;
	    switch (animState) {
	    case IDLE_RIGHT:  
	        if (++idleRightAnimationCount == animationSpeed) {
	            idleRightAnimationCount = 0;
	            idleRightFrameCount = (idleRightFrameCount % 7) + 1;
	        }
	        return getIdleRightSprite(idleRightFrameCount - 1);

	    case IDLE_LEFT: 
	        if (++idleLeftAnimationCount == animationSpeed) {
	            idleLeftAnimationCount = 0;
	            idleLeftFrameCount = (idleLeftFrameCount % 7) + 1;
	        }
	        return getIdleLeftSprite(idleLeftFrameCount - 1);
	        
	    case RUN_RIGHT:
	        if (++runRightAnimationCount == animationSpeed) {
	            runRightAnimationCount = 0;
	            runRightFrameCount = (runRightFrameCount % 6) + 1;
	        }
	        return getRunRightSprite(runRightFrameCount - 1);
	        
	    case RUN_LEFT:
	        if (++runLeftAnimationCount == animationSpeed) {
	            runLeftAnimationCount = 0;
	            runLeftFrameCount = (runLeftFrameCount % 6) + 1;
	        }
	        return getRunLeftSprite(runLeftFrameCount - 1);
	        
	    case THROWING_RIGHT:
	        if (++throwingRightAnimationCount == animationSpeed) {
	            throwingRightAnimationCount = 0;
	            throwingRightFrameCount = (throwingRightFrameCount % 7) + 1; 
	            if (throwingRightFrameCount == 1) {
	                this.setState(AnimationStates.IDLE_RIGHT);
	            }
	        }
			if (throwingRightFrameCount == 5 && canAttack()) {
				startAttack();
			}
	        return getThrowingRightSprite(throwingRightFrameCount - 1);
	        
	    case THROWING_LEFT:
	        if (++throwingLeftAnimationCount == animationSpeed) {
	            throwingLeftAnimationCount = 0;
	            throwingLeftFrameCount = (throwingLeftFrameCount % 7) + 1; 
	            if (throwingLeftFrameCount == 1) {
	                this.setState(AnimationStates.IDLE_LEFT);
	            }
	        }
	        if (throwingLeftFrameCount == 5 && canAttack()) {
                startAttack();
	        }
	        return getThrowingLeftSprite(throwingLeftFrameCount - 1);

	    default:
	        return getIdleRightSprite(1);
	    }
	}
	

	public BufferedImage getIdleRightSprite(int frameCt) {
		if (!isFlashingRed) 
	    return sprites.get(AnimationStates.IDLE_RIGHT)[frameCt];
	    
		else {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.IDLE_RIGHT)[frameCt]);
		}
			
	}

	public BufferedImage getIdleLeftSprite(int frameCt) {
		if (!isFlashingRed)
	    return sprites.get(AnimationStates.IDLE_LEFT)[frameCt];
		
		else  {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.IDLE_LEFT)[frameCt]);
		}
	}

	public BufferedImage getRunRightSprite(int frameCt) {
		if (!isFlashingRed)
	    return sprites.get(AnimationStates.RUN_RIGHT)[frameCt];
		
		else {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.RUN_RIGHT)[frameCt]);
		}
	}

	public BufferedImage getRunLeftSprite(int frameCt) {
		if (!isFlashingRed)
	    return sprites.get(AnimationStates.RUN_LEFT)[frameCt];
		
		else {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.RUN_LEFT)[frameCt]);
		}
	}

	public BufferedImage getThrowingRightSprite(int frameCt) {
		if (!isFlashingRed)
	    return sprites.get(AnimationStates.THROWING_RIGHT)[frameCt];
		
		else {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.THROWING_RIGHT)[frameCt]);
		}
	}

	public BufferedImage getThrowingLeftSprite(int frameCt) {
		if (!isFlashingRed)
	    return sprites.get(AnimationStates.THROWING_LEFT)[frameCt];
		
		else {
			redFlashFramesRemaining--;
			return ImageUtils.tintRed(sprites.get(AnimationStates.THROWING_LEFT)[frameCt]);
		}
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

	public AnimationStates getState() {
		return animState;
	}

	public void setState(AnimationStates state) {
		this.animState = state;
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


	public boolean isFlashingRed() {
		return isFlashingRed;
	}


	public void setFlashingRed(boolean isFlashingRed) {
		this.isFlashingRed = isFlashingRed;
	}


	public int getRedFlashFramesRemaining() {
		return redFlashFramesRemaining;
	}


	public void setRedFlashFramesRemaining(int redFlashFramesRemaining) {
		this.redFlashFramesRemaining = redFlashFramesRemaining;
	}


	public Vector getPosition() {	
		return new Vector(x, y);
	}

    
    public AttackStates getAttackState() {
		return attackState;
	}

	public void setAttackState(AttackStates attackState) {
		this.attackState = attackState;
	}

	public boolean isCooldown() {
		if (System.currentTimeMillis() - lastAttackTime < ATTACK_COOLDOWN)
			return true;
		else
			return false;
	}

	public long getAttackDuration() {
		return attackDuration;
	}

	public void setAttackDuration(long attackDuration) {
		this.attackDuration = attackDuration;
	}

	public void setLastAttackTime(long lastAttackTime) {
		this.lastAttackTime = lastAttackTime;
	}
}
