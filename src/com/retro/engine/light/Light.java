package com.retro.engine.light;

import com.retro.engine.defaultcomponent.ComponentColor;
import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 8/7/2016.
 */
public class Light {

    private Vector3 m_position;
    private ComponentColor m_color;
    private Vector3 m_ambient;
    private Vector3 m_diffuse;
    private Vector3 m_specular;

    private boolean m_directional;

    public Light(){
        m_position = new Vector3();
        m_ambient = new Vector3();
        m_diffuse = new Vector3();
        m_specular = new Vector3();
        m_color = new ComponentColor(255,255,255);
        m_directional = false;
    }

    public Vector3 getAmbient(){
        return m_ambient;
    }
    public void setAmbient(Vector3 v){
        m_ambient = v;
    }

    public Vector3 getDiffuse(){
        return m_diffuse;
    }
    public void setDiffuse(Vector3 v){
        m_diffuse = v;
    }

    public Vector3 getSpecular(){
        return m_specular;
    }
    public void setSpecular(Vector3 v){
        m_specular = v;
    }

    public Vector3 getPosition(){
        return m_position;
    }
    public void setPosition(Vector3 pos){
        m_position = pos;
    }

    public boolean isDirectional(){
        return m_directional;
    }
    public void setDirectional(boolean d){
        m_directional = d;
    }

    public ComponentColor getColor(){
        return m_color;
    }
    public void setColor(ComponentColor color){
        m_color = color;
    }
}
