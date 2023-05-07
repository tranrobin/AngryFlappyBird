package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The Sprite class represents a game object with an image that can be rendered on a graphics context. It
 * also has position, velocity and size coefficients, as well as methods for updating and checking for collisions
 * @author Robin Tran, Jennifer Pham
 *
 */
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";

    /**
     * Construct a new Sprite object with default position and velocity
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * Construct a new Sprite object with the specified position and image
     * 
     * @param pX x coordinate of the sprite's position
     * @param pY y coordinate of the sprite's position
     * @param image the image to use for the sprite
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /** 
     * Set the image for the sprite
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    /**
     * Set the position for the spirte
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
    
    /**
     * Get the x coordinate of the sprite's position
     * @return  x coordinate of the sprite's position
     */ 
    public double getPositionX() {
        return positionX;
    }
    
    /**
     * Get the y coordinate of the sprite's position
     * @return  y coordinate of the sprite's position
     */
    public double getPositionY() {
        return positionY;
    }
    
    /**
     * Set the velocity for the spirte
     * @param velocityX
     * @param velocityY
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /** 
     * Update the velocity of the spirte
     * @param x
     * @param y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }
    
    /**
     * Get the velocity in the horizontal direction
     * @return velocity in the horizontal direction
     */
    public double getVelocityX() {
        return velocityX;
    }
    
    /**
     * Get the velocity in the vertical direction
     * @return velocity in the vertical direction
     */
    public double getVelocityY() {
        return velocityY;
    }
    
    /**
     * Get the width of the sprite
     * @return the width of the sprite
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Render the image
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY, width, height);
    }
    
    /**
     * Get the boundary of the sprite
     * @return
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }
    
    /**
     * Check if sprites intersect each other
     * @param s
     * @return if intersection happens
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }
    
    /**
     * Updates the position of the sprite based on its velocity and the given time.
     * @param time the elapsed time since the last update in seconds
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
}
