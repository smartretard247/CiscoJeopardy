/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Jeezy
 */
public class Question extends Cube {
    
    private static int numQuestions = 0;
    
    protected int bet;
    
    public Question(String nodeName, Vector3f size, Vector3f location) {
        super(nodeName + numQuestions, size, location);
        ++numQuestions;
        
        this.bet = 0;
    }
    
    public int getBet() {
        return this.bet;
    }
    
    public void setBet(int to) {
        this.bet = to;
    }
    
    public void destroy() {
        --numQuestions;
    }
}
