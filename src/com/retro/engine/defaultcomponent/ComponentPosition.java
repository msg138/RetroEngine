package com.retro.engine.defaultcomponent;

import com.retro.engine.component.Component;
import com.retro.engine.model.Matrix;
import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentPosition extends Component {

    private float m_x;
    private float m_y;
    private float m_z;
    private float m_width;
    private float m_height;

    public ComponentPosition(){
        m_x = 0;
        m_y = 0;
        m_z = 0;
        m_width = 0;
        m_height = 0;
    }
    public ComponentPosition(float x, float y){
        this();
        m_x = x;
        m_y = y;
    }
    public ComponentPosition(float x, float y, float z){
        this(x, y);
        m_z = z;
    }
    public ComponentPosition(float x, float y, float w, float h){
        this(x, y);
        m_width = w;
        m_height = h;
    }
    public ComponentPosition(float x, float y, float z, float w, float h){
        this(x, y, w, h);
        m_z = z;
    }

    public float getX(){
        return m_x;
    }
    public float getY(){
        return m_y;
    }
    public float getZ(){
        return m_z;
    }
    public float getWidth()
    {
        return m_width;
    }
    public float getHeight()
    {
        return m_height;
    }

    public void modX(float dx){
        m_x += dx;
    }
    public void modY(float dy){
        m_y += dy;
    }
    public void modZ(float dz){
        m_z += dz;
    }
    public void setX(float x)
    {
        m_x = x;
    }
    public void setY(float y)
    {
        m_y = y;
    }
    public void setZ(float z)
    {
        m_z = z;
    }
    public void setWidth(float w)
    {
        m_width = w;
    }
    public void setHeight(float h)
    {
        m_height = h;
    }

    public Matrix applyTranslation(Matrix m){
        m.translatef(m_x, m_y, m_z);
        return m;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ComponentPosition) {
            ComponentPosition p = (ComponentPosition)o;
            return p.getX() == getX() && p.getY() == getY() && p.getZ() == getZ() && p.getWidth() == getWidth() && p.getHeight() == getHeight();
        }
        return false;
    }

    public Vector3 toVector3(){
        return new Vector3(getX(), getY(), getZ());
    }
}
