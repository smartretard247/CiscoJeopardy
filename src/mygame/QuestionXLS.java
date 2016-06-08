/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mygame.Question.numQuestions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Jeezy
 */
public class QuestionXLS extends Question {
    public QuestionXLS(String nodeName, Vector3f size, Vector3f location) {
        super(nodeName, size, location);
    }
    
    public boolean loadQA(String fromFile, char qOrA, int round) {
        int sheetNum = 0;
        int totalQuestionsLoaded = 0, totalCategories = 0;
        int nCategoryRow = (round*13) + 1;
        int nQuestionRow = (round*13) + 1;
        
        if(qOrA == 'Q') {
            super.resetAll();
        } else {
            super.resetCategories();
            sheetNum = 1;
        }
        
        //initialize temporary string array
        String[] temp = new String[numQuestions];
        for(int i = 0; i < numQuestions; i++) {
            temp[i] = new String();
        }
        
        //load the question from XLS file
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(fromFile));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet firstSheet = workbook.getSheetAt(sheetNum);
            
            //get category names
            Row categoryRow = firstSheet.getRow(nCategoryRow);
            Iterator<Cell> cellIterator = categoryRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                this.category[totalCategories] = cell.getStringCellValue();
                totalCategories++;
                if(qOrA == 'Q') System.out.print(cell.getStringCellValue() + " - ");
            }
            System.out.println();
            
            //get questions (or answers)
            //Iterator<Row> iterator = firstSheet.iterator();
            Row questionRow;
            for(int i = 1; i <= 5; i++) { //iterate through all five levels ie 500, 400, 300, 200, 100
                nQuestionRow += 2;
                questionRow = firstSheet.getRow(nQuestionRow);
                cellIterator = questionRow.cellIterator();
                while (cellIterator.hasNext()) { //iterate through the row to get six categories worth of questions
                    Cell cell = cellIterator.next();
                    temp[totalQuestionsLoaded] = cell.getStringCellValue();
                    totalQuestionsLoaded++;
                }
            }
         
            workbook.close();
            inputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuestionXLS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QuestionXLS.class.getName()).log(Level.SEVERE, null, ex);
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
    
    @Override
    protected void reorderArray(String[] source, String[] dest) {
        for(int i = 0; i < source.length; i++) {
            dest[i] = source[i];
        }
    }
}
