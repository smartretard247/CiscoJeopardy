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
    
    private static int numQuestions = 30;
    private static int numCategories = 6;
    
    protected String[] category;
    protected String[] theQuestions;
    protected String[] theAnswers;
    
    public Question(String nodeName, Vector3f size, Vector3f location) {
        super(nodeName, size, location);
        
        this.category = new String[numCategories];
        for(int i = 0; i < numCategories; i++) {
            this.category[i] = "";
        }
        
        this.theQuestions = new String[numQuestions];
        this.theAnswers = new String[numQuestions];
        for(int i = 0; i < numQuestions; i++) {
            this.theQuestions[i] = new String();
            this.theQuestions[i] = "";
            
            this.theAnswers[i] = new String();
            this.theAnswers[i] = "";
        }
    }
    
    public String getCategory(int index) {
        if(index < numCategories) {
            return this.category[index];
        } else {
            return "Category index out of bounds.";
        }
    }
    
    public String getQuestion(int index) {
        if(index < numQuestions) {
            return this.theQuestions[index];
        } else {
            return "Question index out of bounds.";
        }
    }
    
    public String getAnswer(int index) {
        if(index < numQuestions) {
            return this.theAnswers[index];
        } else {
            return "Answer index out of bounds.";
        }
    }
    
    public boolean loadQA(String fromFile) {
        //test function for now...
        for(int i = 0; i < numQuestions; i++) {
            this.theQuestions[i] = "What layer handles logical addressing?" + i;
            this.theAnswers[i] = "Answer" + i;
        }
        
        for(int i = 0; i < numCategories; i++) {
            this.category[i] = "Category" + i;
        }
        
        return true;
    }
}
