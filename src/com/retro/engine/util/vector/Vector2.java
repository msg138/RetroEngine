package com.retro.engine.util.vector;

/**
 * Created by Michael on 7/29/2016.
 */
public class Vector2 extends Vector {

    private float m_x;
    private float m_y;

    public Vector2(float x, float y){
        m_x = x;
        m_y = y;
    }
    public Vector2(){
        this(0,0);
    }

    public float getX(){
        return m_x;
    }
    public void setX(float x){
        m_x = x;
    }

    public float getY(){
        return m_y;
    }
    public void setY(float y){
        m_y = y;
    }

    public void modX(float dx){
        m_x += dx;
    }
    public void modY(float dy){
        m_y += dy;
    }
    // TODO make a VectorUtil with vector operations, operate off of Vector superclass.

    public String toString(){
        return "X: " + getX() + " . Y: " + getY();
    }

    public float length(){
        return (float)Math.sqrt((getX() * getX()) + (getY() * getY()));
    }

    public Vector2 normalize(){
        float length = length();
        Vector2 ret = new Vector2();
        ret.setX(getX() / length);
        ret.setY(getY() / length);

        return ret;
    }

    public float distanceTo(Vector2 v){
        float math = (float) Math.sqrt( Math.pow(v.getX() - getX(), 2) + Math.pow(v.getY() - getY(), 2));
        return math;
    }

    public Vector2 multiplyWith(Vector2 v){
        v.setX(v.getX() * getX());
        v.setY(v.getY() * getY());
        return v;
    }

    public Vector2 addTo(Vector2 v){
        v.modX(getX());
        v.modY(getY());
        return v;
    }

    public Vector2 subtractFrom(Vector2 v){
        v.modX(-getX());
        v.modY(-getY());
        return v;
    }

    public float dotProduct(Vector2 v){
        multiplyWith(v);
        return v.getX() + v.getY();
    }

    @Override
    public int hashCode(){
        int x, y;
        x = (int)(getX() * 100);
        y = (int)(getY() * 100);
        return (int)(x%2147483647 * 2 + y%2147483647 / 2f);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Vector2)
        {
            Vector2 v = (Vector2)o;
            return v.getX() == getX() && v.getY() == getY();
        }
        return false;
    }

    public float[] toFloatArray(){
        return new float[]{ getX(), getY()};
    }
}
