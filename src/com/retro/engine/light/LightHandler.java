package com.retro.engine.light;

import com.jogamp.opengl.GL2;
import com.retro.engine.camera.Camera;
import com.retro.engine.model.shader.ShaderProgram;
import com.retro.engine.util.vector.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 8/7/2016.
 */
public class LightHandler {
    public static final int c_maxLights = 64;// Make it high, shader will handle maxes and stuff.

    private ArrayList<Light> m_lights;

    private boolean m_changed;

    private static LightHandler m_instance;

    private LightHandler(){
        m_lights = new ArrayList<>();

        m_changed = true;

        m_instance = this;
    }

    public static LightHandler getInstance(){
        if(m_instance == null)
            new LightHandler();
        return m_instance;
    }

    public int getAmountOfLights(){
        return m_lights.size();
    }

    public Light getLight(int l){
        if(l > c_maxLights)
            return null;
        m_changed = true;
        return m_lights.get(l);
    }

    public void addLight(Light l){
        if(m_lights.size() >= c_maxLights)
            return;
        m_lights.add(l);
        m_changed = true;
    }

    public void removeLight(int index){
        if(m_lights.size() > index)
            m_lights.remove(index);
    }

    public boolean isChanged(){
        return m_changed;
    }

    public void activateLights(GL2 gl, ShaderProgram program){
        program.setUniformVec3(gl, "viewPos", Camera.getInstance().getPosition().toVector3());//Camera.getInstance().getViewDirection());
        if(!isChanged())
            return;
        program.setUniformInt(gl, "amountOfLights", m_lights.size());
        for(int i=0;i<m_lights.size();i++) {
            program.setUniformVec3(gl, "pLights[" + (i) + "].position", m_lights.get(i).getPosition());
            program.setUniformVec3(gl, "pLights[" + (i) + "].ambient", m_lights.get(i).getAmbient());
            program.setUniformVec3(gl, "pLights[" + (i) + "].diffuse", m_lights.get(i).getDiffuse());
            program.setUniformVec3(gl, "pLights[" + (i) + "].specular", m_lights.get(i).getSpecular());
            program.setUniformBoolean(gl, "pLights["+(i) + "].directional", m_lights.get(i).isDirectional());
            program.setUniformFloat(gl, "pLights[" + (i) + "].constant", 1.0f);
            program.setUniformFloat(gl, "pLights[" + (i) + "].linear", 0.09f);
            program.setUniformFloat(gl, "pLights[" + (i) + "].quadratic", 0.032f);
        }
        m_changed = false;
    }
}
