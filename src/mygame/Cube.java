/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author Jeezy
 */
public class Cube extends Node {
    
    protected Material cubeMat;
    protected Geometry cubeGeo;
    protected Box cubeMesh;
    
    public Cube(String nodeName, Vector3f size, Vector3f location) {
        super(nodeName);
        
        cubeMesh = new Box(size.x, size.y, size.z);
        cubeGeo = new Geometry(nodeName, cubeMesh);
        
        cubeGeo.setLocalTranslation(location);
    }
    
    void init(AssetManager assetManager, String textureName) {
        cubeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cTex = assetManager.loadTexture("Textures/" + textureName);
        cubeMat.setTexture("ColorMap", cTex);
        cubeGeo.setMaterial(cubeMat);
    }
    
    Geometry getGeometry() {
        return this.cubeGeo;
    }
}
