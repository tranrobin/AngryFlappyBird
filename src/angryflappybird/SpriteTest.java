/**
 * 
 */
package angryflappybird;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Jennifer Pham, Robin Tran
 *
 */
class SpriteTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {
    }

    /**
     * Test constructor
     */
    @Test
    void testSprite() {
        Sprite s = new Sprite();
        assertEquals(0, s.getPositionX(), 0);
        assertEquals(0, s.getPositionY(), 0);
        assertEquals(0, s.getVelocityX(), 0);
        assertEquals(0, s.getVelocityY(), 0);
    }

    /**
     * Test method setPositionXY
     */
    @Test
    void testSetPositionXY() {
        Sprite s = new Sprite();
        s.setPositionXY(5, 10);
        assertEquals(5, s.getPositionX(), 0);
        assertEquals(10, s.getPositionY(), 0);
    }

    /**
     * Test method getPositionXY
     */
    @Test
    void testGetVelocityY() {
        Sprite s = new Sprite();
        s.setVelocity(5, -2);
        s.addVelocity(1, 1);
        assertEquals(6, s.getVelocityX(), 0);
        assertEquals(-1, s.getVelocityY(), 0);
    }

    /**
     * Test method update
     */
    @Test
    void testUpdate() {
        Sprite s = new Sprite();
        s.setVelocity(2, 3);
        s.update(1);
        assertEquals(2, s.getPositionX(), 0);
        assertEquals(3, s.getPositionY(), 0);
        s.update(0.5);
        assertEquals(3, s.getPositionX(), 0);
        assertEquals(4.5, s.getPositionY(), 0);
    }

}
