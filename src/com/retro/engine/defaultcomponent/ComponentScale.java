package com.retro.engine.defaultcomponent;

import com.retro.engine.component.Component;
import com.retro.engine.model.Matrix;

/**
 * Created by Michael on 8/2/2016.
 */
public class ComponentScale extends Component {

    private float m_scalex;
    private float m_scaley;
    private float m_scalez;

    public ComponentScale(){
        // Stick 1 as default to not scale any
        m_scalex = 1f;
        m_scaley = 1f;
        m_scalez = 1f;
    }

    public ComponentScale(float x){
        this();
        m_scalex = x;
    }
    public ComponentScale(float x, float y){
        this(x);
        m_scaley = y;
    }
    public ComponentScale(float x, float y, float z){
        this(x, y);
        m_scalez = z;
    }

    public float getScaleX(){
        return m_scalex;
    }
    public float getScaleY(){
        return m_scaley;
    }
    public float getScaleZ(){
        return m_scalez;
    }

    public void setScale(float s){
        m_scalex = s;
        m_scaley = s;
        m_scalez = s;
    }

    public void setScaleX(float x){
        m_scalex = x;
    }
    public void setScaleY(float y){
        m_scaley = y;
    }
    public void setScaleZ(float z){
        m_scalez = z;
    }

    public Matrix applyScale(Matrix m){
        m.scalef(m_scalex, m_scaley, m_scalez);
        return m;
    }

    public String toString(){
        return "X: "+getScaleX() +" Y: " +getScaleY()+" Z: "+getScaleZ();
    }
}
