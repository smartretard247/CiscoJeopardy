/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.font.Rectangle;

/**
 *
 * @author Jeezy
 */
public class ScreenAdjustment {
    private Rectangle categoryBox;
    private Rectangle questionBox;
    private int catMultiplier, catXOffset, catYOffset, qXOffset, qYOffset;
    private float qScale, catScale;
    
    public ScreenAdjustment(int widthOfScreen, int heightOfScreen) {
        categoryBox = new Rectangle(0,0,85,50);
        questionBox = new Rectangle(0,0,300,250);
        
        switch (heightOfScreen) {
            case 768: //standard screen height
                //adjust category text size
                catMultiplier = 162;
                catXOffset = 44;
                catYOffset = -30;
                catScale = 1.5f;
                
                //adjust question text size
                qXOffset = 75;
                qYOffset = 0;
                qScale = 3.0f;
                break;
            case 480: //smaller screen height
                //adjust category text size
                catMultiplier = 101;
                catXOffset = 26;
                catYOffset = -18;
                catScale = 1.0f;
                
                //adjust question text size
                qXOffset = 75;
                qYOffset = 10;
                qScale = 2.5f;
                break;
            default:
        }
    }
    
    public int getQuestionXOffset() {
        return this.qXOffset;
    }
    
    public int getQuestionYOffset() {
        return this.qYOffset;
    }
    
    public float getQuestionScale() {
        return this.qScale;
    }
    
    public float getCategoryScale() {
        return this.catScale;
    }
    
    public int getCatMultiplier() {
        return this.catMultiplier;
    }
    
    public int getCatXOffset() {
        return this.catXOffset;
    }
    
    public int getCatYOffset() {
        return this.catYOffset;
    }
    
    public Rectangle getCategoryBox() {
        return this.categoryBox;
    }
    
    public Rectangle getQuestionBox() {
        return this.questionBox;
    }
}
