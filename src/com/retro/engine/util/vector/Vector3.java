package com.retro.engine.util.vector;

import com.retro.engine.defaultcomponent.ComponentPosition;

/**
 * Created by Michael on 7/31/2016.
 */
public class Vector3 extends Vector2{

    private float m_z;

    public Vector3(){
        this(0,0,0);
    }
    public Vector3(float x, float y, float z){
        super(x, y);
        m_z = z;
    }

    public Vector3(ComponentPosition pos){
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public void modZ(float dz) {
        m_z += dz;
    }

    public float getZ(){
        return m_z;
    }
    public void setZ(float z){
        m_z = z;
    }

    public String toString(){
        return super.toString() + " . Z: " + getZ();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Vector3))
            return false;
        Vector3 vec = (Vector3)obj;
        if(vec.getX() != this.getX())
            return false;
        if(vec.getY() != this.getY())
            return false;
        if(vec.getZ() != this.getZ())
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        int x, y, z;
        x = (int)(getX() * 100);
        y = (int)(getY() * 100);
        z = (int)(getZ() * 100);
        return (int)(x%2147483647 * 2 + y%2147483647 / 2f + Math.pow(z%2147483647, 2));
    }

    public float distanceTo(Vector3 v){
        float math = (float) Math.sqrt( Math.pow(v.getX() - getX(), 2) + Math.pow(v.getY() - getY(), 2) + Math.pow(v.getZ() - getZ(), 2) );
        return math;
    }

    public float length(){
        return (float)Math.sqrt((getX() * getX()) + (getY() * getY()) + (getZ() * getZ()));
    }

    public Vector3 normalize(){
        float length = length();
        Vector3 ret = new Vector3();
        ret.setX(getX() / length);
        ret.setY(getY() / length);
        ret.setZ(getZ() / length);

        return ret;
    }

    public Vector3 multiplyWith(Vector3 v){
        v.setX(v.getX() * getX());
        v.setY(v.getY() * getY());
        v.setZ(v.getZ() * getZ());
        return v;
    }

    public Vector3 addTo(Vector3 v){
        v.modX(getX());
        v.modY(getY());
        v.modZ(getZ());
        return v;
    }

    public Vector3 subtractFrom(Vector3 v){
        v.modX(-getX());
        v.modY(-getY());
        v.modZ(-getZ());
        return v;
    }

    public float dotProduct(Vector3 v){
        multiplyWith(v);
        return v.getX() + v.getY() + v.getZ();
    }

    public Vector3 crossProduct(Vector3 v){
        return new Vector3(getY()*v.getZ() - getZ()*v.getY(), getZ()*v.getX() - getX()*v.getZ(), getX()*v.getY() - getY()*v.getX());
    }

    public Vector3 clone(){
        return new Vector3(getX(), getY(), getZ());
    }

    public float[] toFloatArray(){
        return new float[]{ getX(), getY(), getZ()};
    }
}
