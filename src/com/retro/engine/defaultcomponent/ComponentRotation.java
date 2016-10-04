package com.retro.engine.defaultcomponent;

import com.retro.engine.component.Component;
import com.retro.engine.model.Matrix;

/**
 * Created by Michael on 8/2/2016.
 */
public class ComponentRotation extends Component {

    private float m_rotX;
    private float m_rotY;
    private float m_rotZ;

    public ComponentRotation(){
        m_rotX = 0f;
        m_rotY = 0f;
        m_rotZ = 0f;
    }

    public ComponentRotation(float x){
        this();
        m_rotX = x;
    }
    public ComponentRotation(float x, float y){
        this(x);
        m_rotY = y;
    }
    public ComponentRotation(float x, float y, float z){
        this(x, y);
        m_rotZ = z;
    }

    public float getRotationX(){
        return m_rotX;
    }
    public float getRotationY(){
        return m_rotY;
    }
    public float getRotationZ(){
        return m_rotZ;
    }

    public void setRotationX(float x){
        m_rotX = x;
    }
    public void setRotationY(float y){
        m_rotY = y;
    }
    public void setRotationZ(float z){
        m_rotZ = z;
    }

    public Matrix applyRotation(Matrix m){
        m.rotatef(m_rotZ, 0f, 0f, 1f);
        m.rotatef(m_rotX, 1f, 0f, 0f);
        m.rotatef(m_rotY, 0f, 1f, 0f);
        return m;
    }
}
