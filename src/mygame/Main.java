package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.font.Rectangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 * test
 * @author Jesse Young
 */
public class Main extends SimpleApplication {

    private static final int NUM_COLUMNS = 6;
    private static final int NUM_ROWS = 5;
    private boolean isRunning = true, awaitingAnswer = false;
    
    private int[] teamScores = new int[2];
    private char teamAnswering = 0;
    
    protected Cube main;
    protected Node boardCubeNode, categoryNode;
    protected Cube[][] boardCube = new Cube[NUM_ROWS][NUM_COLUMNS];
    protected Question question = new Question("TheQuestion", new Vector3f(4, 3, 1), new Vector3f(8.0f, 6.0f, 7.2f));
    private Geometry boardCubePicked, mark;
    protected BitmapText screenText, questionText, categoryText[] = new BitmapText[6], teamScoreText;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false); //disable movement of camera
        inputManager.setCursorVisible(true);
        
        setDisplayFps(false);       // to hide the FPS
        setDisplayStatView(false);  // to hide the statistics 
        
        teamScores[0] = 0;
        teamScores[1] = 0;
        
        cam.setLocation(new Vector3f(8.0f, 6.0f, 15.410577f));
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        initKeys();
        initMark();
        
        main = new Cube("Board", new Vector3f(8.0f, 6.0f, 1.0f), new Vector3f(8.0f, 6.0f, 0.0f));
        rootNode.attachChild(createCube(main, "BlankJeopardy.png"));
        
        boardCubeNode = new Node("Category Amount");
        rootNode.attachChild(boardCubeNode);
        
        Vector3f screenOffset = new Vector3f(1.7f, 1.3f, 0.0f);
        for(int j = 0, k = NUM_ROWS-1, index = 0; j < NUM_ROWS; j++, k--) {
            for(int i = 0; i < NUM_COLUMNS; i++) {
                boardCube[j][i] = new Cube("AmountBox", new Vector3f(0.8f, 0.8f, 1.0f),
                        new Vector3f((float)(i*2.5)+screenOffset.x, (float)(j*1.91)+screenOffset.y, 0.1f));
                int value = ((k+1)*200);
                boardCubeNode.attachChild(createCube(boardCube[j][i], "Cost_" + value + ".png"));
                boardCube[j][i].getGeometry().setUserData("Bet", value);
                boardCube[j][i].getGeometry().setUserData("Index", index);
                
                index++;
            }
        }
        
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        // Display a line of text with a default font
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        screenText = new BitmapText(guiFont, false);
        createLineOfText(screenText, 1, 1);
        
        teamScoreText = new BitmapText(guiFont, false);
        createLineOfText(teamScoreText, 37, 2);
        teamScoreText.setColor(ColorRGBA.Black);
        
        questionText = new BitmapText(guiFont, false);
        createQuestionText();
        
        categoryNode = new Node("Category Titles");
        guiNode.attachChild(categoryNode);
        
        initQuestions("IN_Jeopardy_Questions.txt"); //prep questions
        initCategories();//set categories, after initQuestions!
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(isRunning) {
            teamScoreText.setText("Team A Score: " + teamScores[0] + "\t\t\tTeam B Score: " + teamScores[1] + "");
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy() {
        //destroy code here...
    }
    
    /** Custom Keybinding: Map named actions to inputs. */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("MouseRight",  new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseLeft",  new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseUp",  new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouseDown",  new MouseAxisTrigger(MouseInput.AXIS_Y, false));

        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        inputManager.addMapping("CorrectAnswer", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("WrongAnswer", new KeyTrigger(KeyInput.KEY_BACK));
        // Add the names to the action listener.
        inputManager.addListener(combinedListener,"Pause");
        inputManager.addListener(combinedListener,"Select");
        inputManager.addListener(combinedListener, new String[]{ "MouseRight", "MouseLeft", "MouseUp", "MouseDown"});
        inputManager.addListener(combinedListener, new String[]{ "CorrectAnswer", "WrongAnswer"});
    }
    
    //add listener for keystrokes/mouse input
    private MyCombinedListener combinedListener = new MyCombinedListener();
    private class MyCombinedListener implements AnalogListener, ActionListener {

        public void onAnalog(String name, float value, float tpf) {
            if(isRunning) {
                
            }
        }

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Pause") && !isPressed) {
                isRunning = !isRunning;
                if (isRunning) {
                    screenText.setText("");
                } else {
                    screenText.setText("--Game Paused--");
                }
            }
            if(isRunning && !awaitingAnswer) {
                if (name.equals("Select") && !isPressed) {
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();
                    
                    // 2. Aim the ray from cam loc to cam direction.
                    Vector2f click2d = inputManager.getCursorPosition();
                    Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    // Aim the ray from the clicked spot forwards.
                    Ray ray = new Ray(click3d, dir);
                    
                    // 3. Collect intersections between Ray and Shootables in results list.
                    boardCubeNode.collideWith(ray, results);
                    String hit = "";
                    for (int i = 0; i < results.size(); i++) {
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        boardCubePicked = results.getCollision(i).getGeometry();
                        hit = boardCubePicked.getName();
                        
                        if(hit.equals("TheQuestion")) {
                            if(click2d.x < settings.getWidth()/2) {
                                teamAnswering = 'A';//team A answering
                            } else {
                                teamAnswering = 'B';//team A answering
                            }
                            awaitingAnswer = !awaitingAnswer; //pauses until question answered
                        } else {
                            question.setUserData("QuestionWorth", boardCubePicked.getUserData("Bet"));
                            question.getGeometry().setLocalTranslation(new Vector3f(8.0f, 6.0f, 7.2f));
                            
                            boardCubeNode.detachChild(boardCubePicked);
                            guiNode.detachChild(categoryNode);
                            guiNode.detachChild(teamScoreText);
                            
                            int index = boardCubePicked.getUserData("Index");
                            questionText.setText(question.getQuestion(index));
                            guiNode.attachChild(questionText);
                            
                            screenText.setText("");
                        }
                        break;
                    }
                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0) {
                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();
                        // Let's interact - we mark the hit with a red dot.
                        mark.setLocalTranslation(closest.getContactPoint());
                        
                        if(hit.equals("TheQuestion")) {
                            //figure out what team clicked
                        } else {
                            rootNode.attachChild(mark);
                        }
                    } else {
                        // No hits? Then remove the red mark.
                        rootNode.detachChild(mark);
                    }
                }
                
            }
            
            if(isRunning) {
                if (name.equals("CorrectAnswer") && !isPressed) {
                    int points = question.getUserData("QuestionWorth");
                    awardPoints(teamAnswering,points);
                    awaitingAnswer = !awaitingAnswer;
                }
                if (name.equals("WrongAnswer") && !isPressed) {
                    int points = question.getUserData("QuestionWorth");
                    awardPoints(teamAnswering,-points);
                    awaitingAnswer = !awaitingAnswer;
                }
            }
        }
    }
    
    public void awardPoints(char toTeam, int points) {
        if(toTeam == 'A') {
            //team A answered
            screenText.setText("TEAM A scored " + points + " points.");
            teamScores[0] += points;
        } else if(toTeam == 'B') {
            //team B answered
            screenText.setText("TEAM B scored " + points + " points.");
            teamScores[1] += points;
        }
        question.getGeometry().setLocalTranslation(new Vector3f(8.0f, 16.0f, 7.2f));
        //wait 5 seconds for player response
        rootNode.detachChild(mark);
        guiNode.detachChild(questionText);
        guiNode.attachChild(categoryNode);
        guiNode.attachChild(teamScoreText);
    }
    
    public Geometry createCube(Cube theCube, String textureFileName) {
        theCube.init(assetManager, textureFileName);
        return theCube.getGeometry();
    }
    
    public void createLineOfText(BitmapText theText, int lineNumber, float scale) {
        theText.setSize(guiFont.getCharSet().getRenderedSize());
        theText.scale(scale);
        theText.setText("");
        theText.setLocalTranslation(0, theText.getLineHeight()*lineNumber, 0);
        theText.setBox(new Rectangle(0,0,settings.getWidth()/scale,guiFont.getCharSet().getRenderedSize()));
        theText.setAlignment(BitmapFont.Align.Center);
        guiNode.attachChild(theText);
    }
    
    public void createQuestionText() {
        questionText.setSize(guiFont.getCharSet().getRenderedSize());
        questionText.scale(4);
        questionText.setText("");
        questionText.setLocalTranslation(110,settings.getHeight(), 0);
        
        questionText.setBox(new Rectangle(0,0,200,190));
        questionText.setLineWrapMode(LineWrapMode.Word);
        questionText.setColor(ColorRGBA.Yellow);
        questionText.setAlignment(BitmapFont.Align.Center);
        questionText.setVerticalAlignment(BitmapFont.VAlign.Center);
        guiNode.attachChild(questionText);
    }
    
    public void createCategoryText(int index) {
        int multiplier = 162, xoffset = 44, yoffset = -30;
        
        categoryText[index].setSize(guiFont.getCharSet().getRenderedSize());
        categoryText[index].scale(1.5f);
        categoryText[index].setText("");
        categoryText[index].setLocalTranslation(index*multiplier+xoffset,settings.getHeight()+yoffset, 0);
        
        categoryText[index].setBox(new Rectangle(0,0,85,50));
        categoryText[index].setLineWrapMode(LineWrapMode.Word);
        categoryText[index].setColor(ColorRGBA.Yellow);
        categoryText[index].setAlignment(BitmapFont.Align.Center);
        categoryText[index].setVerticalAlignment(BitmapFont.VAlign.Center);
        
        categoryNode.attachChild(categoryText[index]);
    }
    
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
  }
    
    protected void initQuestions(String fileName) { //initQuestions("IN_Jeopardy_Questions.txt");
        question.loadQA(fileName);
        question.getGeometry().setLocalTranslation(new Vector3f(8.0f, 16.0f, 7.2f));
        boardCubeNode.attachChild(createCube(question, "QuestionBack.png"));
    }
    
    protected void initCategories() {
        for(int i = 0; i < categoryText.length; i++) {
            categoryText[i] = new BitmapText(guiFont, false);
            createCategoryText(i);
            categoryText[i].setText(question.getCategory(i));
        }
    }
}
