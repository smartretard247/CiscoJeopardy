/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        this.theQuestions = new String[numQuestions];
        this.theAnswers = new String[numQuestions];
        
        for(int i = 0; i < numQuestions; i++) {
            this.theQuestions[i] = new String();
            this.theAnswers[i] = new String();
        }
        
        resetAll();
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
    
    private void resetQuestions() {
        for(int i = 0; i < numQuestions; i++) {
            this.theQuestions[i] = "";
        }
    }
    
    private void resetAnswers() {
        for(int i = 0; i < numQuestions; i++) {
            this.theAnswers[i] = "";
        }
    }
    
    private void resetAll() {
        resetQuestions();
        resetAnswers();
        resetCategories();
    }
    
    private void resetCategories() {
        for(int i = 0; i < numCategories; i++) {
            this.category[i] = "";
        }
    }
    
    public boolean loadQA(String fromFile, char qOrA) {
        if(qOrA == 'Q') {
            resetAll();
        } else {
            resetCategories();
        }
        
        //initialize temporary string array
        String[] temp = new String[numQuestions];
        for(int i = 0; i < numQuestions; i++) {
            temp[i] = new String();
        }
        
        FileReader theFileReader = null;
        //try creation of filereader so we can input questions
        try {
            theFileReader = new FileReader(".\\" + fromFile);
            
            int theCharacterRead = 0, totalQuestionsLoaded = 0, totalCategories = 0;
            while((theCharacterRead = theFileReader.read()) != -1) {
                if(theCharacterRead == (char)'@') {
                    while((theCharacterRead = theFileReader.read()) != (char)'@') {
                        this.category[totalCategories] += (char)theCharacterRead;
                    }
                    ++totalCategories;
                } else if(theCharacterRead == (char)'<'){
                    while((theCharacterRead = theFileReader.read()) != (char)'>') {
                        temp[totalQuestionsLoaded] += (char)theCharacterRead;
                    }
                    ++totalQuestionsLoaded;
                }
            }
            
            if(totalQuestionsLoaded < 30) {
                throw new IOException("You must have 30 questions/answers to play. You currently have " + totalQuestionsLoaded);
            }

            if(totalCategories < 6) {
                throw new IOException("You must have 6 categories to play. You currently have " + totalCategories);
            }
            
            theFileReader.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        //correct order of questions
        if(qOrA == 'Q') {
            reorderArray(temp, this.theQuestions);
        } else if(qOrA == 'A') {
            reorderArray(temp, this.theAnswers);
        } else {
            return false;
        }
        
        return true;
    }
    
    private void reorderArray(String[] source, String[] dest) {
        for(int j = 0; j < 5; j++) {
            for(int i = 0; i < 6; i++) {
                dest[j*6+i] = source[i*5+j];
            }
        }
    }
}
