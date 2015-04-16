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
    
    public boolean loadQA(String fromFile) {
        resetAll();
        
        FileReader theFileReader = null;
        //try creation of filereader so we can input questions
        try {
            theFileReader = new FileReader(".\\" + fromFile);
            
            int theCharacterRead = 0, totalQuestionsLoaded = 1, totalCategories = 1;
            for(int i = 0; i < numQuestions; i++) {
                theCharacterRead = theFileReader.read(); //read the first character
                
                while(theCharacterRead != -1) {
                    if(theCharacterRead != (char)'\n') {
                        if(theCharacterRead == (char)'@') {
                            --i; //make sure to restart on the same question number
                            while((theCharacterRead = theFileReader.read()) != (char)'\n') {
                                this.category[totalCategories-1] += (char)theCharacterRead;
                            }
                            ++totalCategories;
                        } else {
                            this.theQuestions[i] += (char)theCharacterRead;
                            theCharacterRead = theFileReader.read();
                        }
                    } else {
                        ++totalQuestionsLoaded;
                        break;
                    }
                }
            }
            
            if(totalQuestionsLoaded < 30) {
                throw new IOException("You must have 30 questions to play. You currently have " + totalQuestionsLoaded);
            }
            
            if(totalCategories < 6) {
                throw new IOException("You must have 6 categories to play. You currently have " + totalCategories);
            }
            
            theFileReader.close();
        } catch (IOException ex) {
            Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        //copy to temporary string array
        String[] temp = this.theQuestions.clone();
        
        //correct order of questions
        for(int j = 0; j < 5; j++) {
            for(int i = 0; i < 6; i++) {
                this.theQuestions[j*6+i] = temp[i*5+j];
            }
        }
        
        return true;
    }
}
