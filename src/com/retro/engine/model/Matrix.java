package com.retro.engine.model;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Matrix4;
import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.model.shader.ShaderProgram;
import com.retro.engine.util.vector.Vector3;

import java.util.ArrayList;

/**
 * Created by Michael on 7/31/2016.
 */
public class Matrix {
    private ArrayList<float[]> m_matrixStack = new ArrayList<>();

    private ShaderProgram m_shaderOwner = null;

    public Matrix(){
        getMatrix();
    }

    public void setShaderOwner(ShaderProgram s){
        m_shaderOwner = s;
    }

    public float[] getMatrixData(){
        return getMatrix();
    }

    public void pushMatrix(){
        float[] newArray = new float[16];
        System.arraycopy(getMatrix(), 0, newArray, 0, 16);
        m_matrixStack.add(newArray);
    }
    public void popMatrix(){
        m_matrixStack.remove(m_matrixStack.size()-1);
    }

    public void pushMatrixV(GL2 gl){
        pushMatrix();
        activateV(gl);
    }
    public void pushMatrixM(GL2 gl){
        pushMatrix();
        activateM(gl);
    }
    public void popMatrixV(GL2 gl){
        popMatrix();
        activateV(gl);
    }
    public void popMatrixM(GL2 gl){
        popMatrix();
        activateM(gl);
    }

    public float[] getMatrix(){
        if(m_matrixStack.size() <= 0) {

            float[] newmat = new float[]{
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            };
            Debug.out(printMatrix(newmat));
            m_matrixStack.add(newmat);
        }
        return m_matrixStack.get(m_matrixStack.size()-1);
    }

    public void setMatrix(float[] mat){
        m_matrixStack.set(m_matrixStack.size()-1, mat);
    }

    public void translatef(float dx, float dy, float dz){
        float[] translateMat = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                dx, dy, dz, 1
        };
        setMatrix(FloatUtil.multMatrix(getMatrix(), translateMat));
    }
    public void translatefV(GL2 gl, float dx, float dy, float dz){
        translatef(dx, dy, dz);
        activateV(gl);
    }
    public void translatefM(GL2 gl, float dx, float dy, float dz){
        translatef(dx, dy, dz);
        activateM(gl);
    }

    public void rotatef(float rad, float x, float y, float z){
        float[] rotX = new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
        float[] rotY = new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
        float[] rotZ = new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
        if(x > 0.0f)
            rotX = new float[]{
                    1, 0, 0, 0,
                    0, (float) Math.cos(rad), (float) Math.sin(rad), 0,
                    0, (float) -Math.sin(rad), (float) Math.cos(rad), 0,
                    0, 0, 0, 1
            };
        if(y > 0.0f)
            rotY = new float[]{
                    (float) Math.cos(rad), 0, (float) -Math.sin(rad), 0,
                    0, 1, 0, 0,
                    (float) Math.sin(rad), 0, (float) Math.cos(rad), 0,
                    0, 0, 0, 1
            };
        if(z > 0.0f)
            rotZ = new float[]{
                    (float) Math.cos(rad), (float) Math.sin(rad), 0, 0,
                    (float) -Math.sin(rad), (float) Math.cos(rad), 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            };
        float[] res = FloatUtil.multMatrix(FloatUtil.multMatrix(rotZ, rotY), rotX);
        setMatrix(FloatUtil.multMatrix(getMatrix(), res));
    }

    public void rotatefV(GL2 gl, float rad, float x, float y, float z){
        rotatef(rad, x, y, z);
        activateV(gl);
    }
    public void rotatefM(GL2 gl, float rad, float x, float y, float z){
        rotatef(rad, x, y, z);
        activateM(gl);
    }

    public void scalef(float sx, float sy, float sz){
        float[] mat = {
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0, 1,
        };
        setMatrix(FloatUtil.multMatrix(getMatrix(), mat));
    }

    public void scalefV(GL2 gl, float sx, float sy, float sz){
        scalef(sx, sy, sz);
        activateV(gl);
    }
    public void scalefM(GL2 gl, float sx, float sy, float sz){
        scalef(sx, sy, sz);
        activateM(gl);
    }

    public static String printMatrix(float[] mat){
        String ret = "MATRIX: \n";
        int sq = (int)Math.sqrt(mat.length);
        String row = "\t";
        for(int i=0; i<mat.length;i++)
        {
            if(i % sq == 0 && i != 0) {
                ret += row + "\n";
                row = "\t";
            }
            row += mat[i] + " ";
        }
        ret += row;
        return ret;
    }

    public void updateMatrix(float[] mat){
        setMatrix(mat);
    }

    public void activateV(GL2 gl){
        if(m_shaderOwner != null)
            ShaderLoader.updateViewMatrix(gl, getMatrix(), m_shaderOwner);
    }

    public void activateM(GL2 gl){
        if(m_shaderOwner != null)
            ShaderLoader.updateModelMatrix(gl, getMatrix(), m_shaderOwner);
    }

    public void activateV(GL2 gl, ShaderProgram program){
        ShaderLoader.updateViewMatrix(gl, getMatrix(), program);
    }
    public void activateM(GL2 gl, ShaderProgram program){
        ShaderLoader.updateModelMatrix(gl, getMatrix(), program);
    }

}
