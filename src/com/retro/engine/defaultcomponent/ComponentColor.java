package com.retro.engine.defaultcomponent;

import com.jogamp.opengl.GL2;
import com.retro.engine.component.Component;

import java.awt.*;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentColor extends Component {

    private int m_red;
    private int m_green;
    private int m_blue;
    private int m_alpha;

    public ComponentColor(){
        m_red = 0;
        m_green = 0;
        m_blue = 0;
        m_alpha = 255;
    }

    public ComponentColor(int r, int g, int b){
        this();
        m_red = r;
        m_green = g;
        m_blue = b;
    }

    public ComponentColor(int r, int g, int b, int a){
        this(r, g, b);
        m_alpha = a;
    }

    public int getRed(){
        return m_red;
    }
    public int getBlue(){
        return m_blue;
    }
    public int getGreen(){
        return m_green;
    }
    public int getAlpha(){
        return m_alpha;
    }

    public float getRedf(){
        return (float) m_red / 255;
    }
    public float getBluef(){
        return (float) m_blue / 255;
    }
    public float getGreenf(){
        return (float) m_green / 255;
    }
    public float getAlphaf(){
        return (float) m_alpha / 255;
    }

    public void setRed(int r){
        m_red = r;
    }
    public void setGreen(int g){
        m_green = g;
    }
    public void setBlue(int b){
        m_blue = b;
    }
    public void setAlpha(int a){
        m_alpha = a;
    }

    public void setColor(int r, int g, int b)
    {
        m_red = r;
        m_green = g;
        m_blue = b;
    }
    public void setColor(int r, int g, int b, int a)
    {
        m_red = r;
        m_green = g;
        m_blue = b;
        m_alpha = a;
    }

    public void activateColor(GL2 gl){
        gl.glColor4f(getRedf(), getGreenf(), getBluef(), getAlphaf());
    }

    public ComponentColor clone(){
        return new ComponentColor(getRed(), getGreen(), getBlue(), getAlpha());
    }

    public Color toColor(){
        return new Color(getRed(), getGreen(), getBlue(), getAlpha());
    }

    @Override
    public String toString(){
        return "R: " + getRed() +" G: " + getGreen() + " B: " + getBlue() +" A: " + getAlpha();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ComponentColor){
            ComponentColor color = (ComponentColor)o;
            if(color.getRed() == getRed() && color.getGreen() == getGreen() && color.getBlue() == getBlue() && getAlpha() == color.getAlpha())
                return true;
        }
        return false;
    }
}
