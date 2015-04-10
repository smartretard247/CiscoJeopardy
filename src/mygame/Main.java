package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
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
    private boolean isRunning = true;
    
    protected Cube main;
    protected Node questionNode;
    protected Question[][] question = new Question[NUM_ROWS][NUM_COLUMNS];
    private Geometry mark;
    protected BitmapText screenText;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false); //disable movement of camera
        inputManager.setCursorVisible(true);
        
        cam.setLocation(new Vector3f(8.0f, 6.0f, 15.410577f));
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        initKeys();
        initMark();
        
        main = new Cube("Board", new Vector3f(8.0f, 6.0f, 1.0f), new Vector3f(8.0f, 6.0f, 0.0f));
        rootNode.attachChild(createCube(main, "BlankJeopardy.png"));
        
        questionNode = new Node("The questions");
        rootNode.attachChild(questionNode);
        
        Vector3f screenOffset = new Vector3f(1.7f, 1.3f, 0.0f);
        for(int j = 0, k = NUM_ROWS-1; j < NUM_ROWS; j++, k--) {
            for(int i = 0; i < NUM_COLUMNS; i++) {
                question[j][i] = new Question("Question", new Vector3f(0.8f, 0.8f, 1.0f),
                        new Vector3f((float)(i*2.5)+screenOffset.x, (float)(j*1.91)+screenOffset.y, 0.1f));
                int value = ((k+1)*200);
                questionNode.attachChild(createCube(question[j][i], "Cost_" + value + ".png"));
                question[j][i].setBet(value);
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
        createLineOfText(screenText, 1);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(isRunning) {
            //screenText.setText("Test text");
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
        // Add the names to the action listener.
        inputManager.addListener(combinedListener,"Pause");
        inputManager.addListener(combinedListener,"Select");
        inputManager.addListener(combinedListener, new String[]{ "MouseRight", "MouseLeft", "MouseUp", "MouseDown"});
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
            if(isRunning) {
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
                    questionNode.collideWith(ray, results);
                    for (int i = 0; i < results.size(); i++) {
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        String hit = results.getCollision(i).getGeometry().getName();
                        screenText.setText("Question picked: " + hit);
                    }
                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0) {
                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();
                        // Let's interact - we mark the hit with a red dot.
                        mark.setLocalTranslation(closest.getContactPoint());
                        rootNode.attachChild(mark);
                    } else {
                        // No hits? Then remove the red mark.
                        rootNode.detachChild(mark);
                    }
                }
            }
        }
        
    }
    
    public Geometry createCube(Cube theCube, String textureFileName) {
        theCube.init(assetManager, textureFileName);
        return theCube.getGeometry();
    }
    
    public void createLineOfText(BitmapText theText, int lineNumber) {
        theText.setSize(guiFont.getCharSet().getRenderedSize());
        theText.setText("");
        theText.setLocalTranslation(400, theText.getLineHeight()*lineNumber, 0);
        guiNode.attachChild(theText);
    }
    
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
  }
}
