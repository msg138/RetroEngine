package com.retro.engine.util.vector;

/**
 * Created by Michael on 8/3/2016.
 */
public class Vector4 extends Vector3 {

    private float m_w;

    public Vector4(){
        this(0,0,0,0);
    }
    public Vector4(float x, float y, float z, float w){
        super(x, y, z);
        m_w = w;
    }

    public Vector4(Vector3 v, float w){
        this(v.getX(), v.getY(), v.getZ(), w);
    }

    public void modW(float dw){
        m_w += dw;
    }

    public void setW(float w){
        m_w = w;
    }
    public float getW(){
        return m_w;
    }

    public Vector3 toVector3(){
        return new Vector3(getX(), getY(), getZ());
    }
    public String toString(){
        return super.toString() + " . W: " + getW();
    }

    @Override
    public int hashCode(){
        return (int)(super.hashCode() * this.getW());
    }

    public float length(){
        return (float)Math.sqrt((getX() * getX()) + (getY() * getY()) + (getZ() * getZ()) + (getW() * getW()));
    }

    public Vector4 normalize(){
        float length = length();
        Vector4 ret = new Vector4();
        ret.setX(getX() / length);
        ret.setY(getY() / length);
        ret.setZ(getZ() / length);
        ret.setW(getW() / length);

        return ret;
    }

    public Vector4 multiplyWith(Vector4 v){
        v.setX(v.getX() * getX());
        v.setY(v.getY() * getY());
        v.setZ(v.getZ() * getZ());
        v.setW(v.getZ() * getW());
        return v;
    }

    public Vector4 addTo(Vector4 v){
        v.modX(getX());
        v.modY(getY());
        v.modZ(getZ());
        v.modW(getW());
        return v;
    }

    public Vector4 subtractFrom(Vector4 v){
        v.modX(-getX());
        v.modY(-getY());
        v.modZ(-getZ());
        v.modW(-getW());
        return v;
    }

    public float dotProduct(Vector4 v){
        multiplyWith(v);
        return v.getX() + v.getY() + v.getZ() + v.getW();
    }

    public float[] toFloatArray(){
        return new float[]{ getX(), getY(), getZ(), getW()};
    }
}
