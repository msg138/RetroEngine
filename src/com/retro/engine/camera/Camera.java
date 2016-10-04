package com.retro.engine.camera;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;
import com.retro.engine.Framework;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.model.Matrix;
import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 7/30/2016.
 */
public class Camera {

    private static Camera m_instance;

    private ComponentPosition m_position;
    private ComponentPosition m_tmpPosition;

    private boolean m_changedCamera = false;

    private float m_pitch;
    private float m_yaw;

    private float m_tmpPitch;
    private float m_tmpYaw;


    protected Camera(){
        m_position = new ComponentPosition();
        m_tmpPosition = new ComponentPosition();
        m_yaw = 0;
        m_pitch = 0;

        m_tmpPitch = 0;
        m_tmpYaw = 0;

        m_instance = this;
    }

    public Camera setUpCamera(Camera c){
        m_instance = c;
        m_changedCamera = true;
        return m_instance;
    }

    public void setLook(float y, float p){
        setLook(y, p, true);
    }

    public void setLook(float y, float p, boolean lockCamera){
        m_tmpPitch = p;
        m_tmpYaw = y;

        if(lockCamera)
            lockTmpCamera();
    }

    public boolean changedCamera(){
        return m_changedCamera;
    }

    public static Camera getInstance(){
        if(m_instance == null){
            new Camera();
        }
        return m_instance;
    }

    public ComponentPosition getPosition(){
        return m_tmpPosition;
    }

    public Vector3 getViewDirection(){
        float[] mat = getCameraLookMatrix();
        return new Vector3(mat[2], mat[6], mat[10]);
    }

    public float getRealX(){
        return m_position.getX();
    }
    public float getRealY(){
        return m_position.getY();
    }
    public float getRealZ(){
        return m_position.getZ();
    }

    private void setYaw(float y){
        m_yaw = y;
    }
    private void setPitch(float p){
        m_pitch = p;
    }

    public float getYaw(){
        return m_yaw;
    }
    public float getPitch(){
        return m_pitch;
    }

    public boolean canMove(Vector3 newPos, float dir){
        // This can be overridden to allow some sort of collision detection.
        return true;
    }

    public void onMove(Vector3 newPos, float dir){
        // To be overridden to get cam position right after it moves.
    }
    public void onRotate(float nyaw, float npitch){
        // to be overridden
    }

    protected void lockCamera(){
        if(getPitch() > Math.toRadians(90))
            setPitch((float)Math.toRadians(90));
        if(getPitch() < Math.toRadians(-90))
            setPitch(-(float)Math.toRadians(90));
        if(getYaw() < Math.toRadians(0))
            setYaw(getYaw() + (float)Math.toRadians(360));
        if(getYaw() > Math.toRadians(360))
            setYaw(getYaw() - (float)Math.toRadians(360));
    }
    protected void lockTmpCamera(){
        if(m_tmpPitch > Math.toRadians(90))
            m_tmpPitch = ((float)Math.toRadians(90));
        if(m_tmpPitch < Math.toRadians(-90))
            m_tmpPitch = (-(float)Math.toRadians(90));
        if(m_tmpYaw < Math.toRadians(0))
            m_tmpYaw = (m_tmpYaw + (float)Math.toRadians(360));
        if(m_tmpYaw > Math.toRadians(360))
            m_tmpYaw = (m_tmpYaw - (float)Math.toRadians(360));
    }

    public Vector3 getMovePosition(float distance, float direction, Vector3 pos){
        Vector3 newPos = new Vector3(pos.getX(), pos.getY(), pos.getZ());
        double rad = ((getYaw() + direction));//*Math.PI / 180.0f);
        newPos.modX((float)-Math.sin(rad)*distance);
        newPos.modZ((float)-Math.cos(rad)*distance);
        return newPos;
    }

    public Vector3 getMovePosition(float distance, float direction){
        return getMovePosition(distance, direction, m_tmpPosition.toVector3());
    }

    public void moveCamera(float distance, float direction){
        Vector3 newPos = getMovePosition(distance, direction);
        if(canMove(newPos, direction))
        {
            m_tmpPosition.setX(newPos.getX());
            m_tmpPosition.setY(newPos.getY());
            m_tmpPosition.setZ(newPos.getZ());
            onMove(newPos, direction);
        }
    }

    public boolean canMoveCameraUp(Vector3 newPos){
        return true;
    }

    public void moveCameraUp(float distance, float direction){
        double rad = ((getPitch() + direction)*Math.PI / 180.0f);
        m_tmpPosition.modY((float)-Math.sin(rad)*distance);
    }

    public void changeLook(float dy, float dp){
        setYaw(getYaw() + dy);
        setPitch(getPitch() + dp);
    }

    public Vector3 getLookAt(){
        return new Vector3();
    }

    public float[] getCameraLookMatrix(){
        float cosa, cosb, sina, sinb;
        float cosc = 1.0f;
        float sinc = 0.0f;
        float x =0, y=0, z=0;

        cosa = (float) Math.cos(-getPitch());
        sina = (float) Math.sin(-getPitch());

        cosb = (float) (Math.cos(-getYaw()));
        sinb = (float) (Math.sin(-getYaw()));

        float[] matrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        matrix[0] = cosc*cosb;
        matrix[1] = cosa*sinc*cosb + sina*sinb;
        matrix[2] = sina*sinc*cosb-cosa*sinb;
        matrix[3] =  0;
        matrix[4] = -sinc;
        matrix[5] =  cosa*cosc;
        matrix[6] =  sina*cosc;
        matrix[7] =  0;
        matrix[8] = cosc*sinb;
        matrix[9] = cosa*sinc*sinb - sina*cosb;
        matrix[10] = sina*sinc*sinb + cosa*cosb;
        matrix[11] =  0;
        matrix[12] = matrix[0] * x + matrix[4] * y + matrix[8] * z;
        matrix[13] = matrix[1] * x + matrix[5] * y + matrix[9] * z;
        matrix[14] = matrix[2] * x + matrix[6] * y + matrix[10] * z;
        matrix[15] =  1;

        float[] mat = new float[16];
        mat = FloatUtil.invertMatrix(matrix, mat);

        return mat;
    }
    public float[] getCameraLookMatrix(float x, float y, float z){
        float cosa, cosb, sina, sinb;
        float cosc = 1.0f;
        float sinc = 0.0f;

        cosa = (float) Math.cos(-getPitch());
        sina = (float) Math.sin(-getPitch());

        cosb = (float) (Math.cos(-getYaw()));
        sinb = (float) (Math.sin(-getYaw()));

        float[] matrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        matrix[0] = cosc*cosb;
        matrix[1] = cosa*sinc*cosb + sina*sinb;
        matrix[2] = sina*sinc*cosb-cosa*sinb;
        matrix[3] =  0;
        matrix[4] = -sinc;
        matrix[5] =  cosa*cosc;
        matrix[6] =  sina*cosc;
        matrix[7] =  0;
        matrix[8] = cosc*sinb;
        matrix[9] = cosa*sinc*sinb - sina*cosb;
        matrix[10] = sina*sinc*sinb + cosa*cosb;
        matrix[11] =  0;
        matrix[12] = matrix[0] * x + matrix[4] * y + matrix[8] * z;
        matrix[13] = matrix[1] * x + matrix[5] * y + matrix[9] * z;
        matrix[14] = matrix[2] * x + matrix[6] * y + matrix[10] * z;
        matrix[15] =  1;

        float[] mat = new float[16];
        mat = FloatUtil.invertMatrix(matrix, mat);

        return mat;
    }
    public float[] getCameraLookMatrixWithoutPitch(float x, float y, float z){
        float cosa, cosb, sina, sinb;
        float cosc = 1.0f;
        float sinc = 0.0f;

        cosa = (float) Math.cos(0);
        sina = (float) Math.sin(0);

        cosb = (float) (Math.cos(-getYaw()));
        sinb = (float) (Math.sin(-getYaw()));

        float[] matrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        matrix[0] = cosc*cosb;
        matrix[1] = cosa*sinc*cosb + sina*sinb;
        matrix[2] = sina*sinc*cosb-cosa*sinb;
        matrix[3] =  0;
        matrix[4] = -sinc;
        matrix[5] =  cosa*cosc;
        matrix[6] =  sina*cosc;
        matrix[7] =  0;
        matrix[8] = cosc*sinb;
        matrix[9] = cosa*sinc*sinb - sina*cosb;
        matrix[10] = sina*sinc*sinb + cosa*cosb;
        matrix[11] =  0;
        matrix[12] = matrix[0] * x + matrix[4] * y + matrix[8] * z;
        matrix[13] = matrix[1] * x + matrix[5] * y + matrix[9] * z;
        matrix[14] = matrix[2] * x + matrix[6] * y + matrix[10] * z;
        matrix[15] =  1;

        float[] mat = new float[16];
        mat = FloatUtil.invertMatrix(matrix, mat);

        return mat;
    }

    public float[] getCameraLookMatrixWithPosition(float ya, float p){

        float cosa, cosb, sina, sinb;
        float cosc = 1.0f;
        float sinc = 0.0f;
        float x = -getPosition().getX();
        float y = -getPosition().getY();
        float z = -getPosition().getZ();

        cosa = (float) Math.cos(p);
        sina = (float) Math.sin(p);

        cosb = (float) (Math.cos(ya));
        sinb = (float) (Math.sin(ya));

        float[] matrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        matrix[0] = cosc*cosb;
        matrix[1] = cosa*sinc*cosb + sina*sinb;
        matrix[2] = sina*sinc*cosb-cosa*sinb;
        matrix[3] =  0;
        matrix[4] = -sinc;
        matrix[5] =  cosa*cosc;
        matrix[6] =  sina*cosc;
        matrix[7] =  0;
        matrix[8] = cosc*sinb;
        matrix[9] = cosa*sinc*sinb - sina*cosb;
        matrix[10] = sina*sinc*sinb + cosa*cosb;
        matrix[11] =  0;
        matrix[12] = matrix[0] * x + matrix[4] * y + matrix[8] * z;
        matrix[13] = matrix[1] * x + matrix[5] * y + matrix[9] * z;
        matrix[14] = matrix[2] * x + matrix[6] * y + matrix[10] * z;
        matrix[15] =  1;
        return matrix;
    }

    public void updateCamera(GL2 gl){
        /*
        gl.glRotatef(-getPitch(), 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-getYaw(), 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(-m_position.getX(), -m_position.getY(), -m_position.getZ());

        Framework.getViewMatrix().rotatef(-(float)Math.toRadians(getPitch()), 1.0f, 0.0f, 0.0f);
        Framework.getViewMatrix().rotatef(-(float)Math.toRadians(getYaw()), 0.0f, 1.0f, 0.0f);
        Framework.getViewMatrix().translatefV(gl, -m_position.getX(), -m_position.getY(), -m_position.getZ());
        */

        if(!m_tmpPosition.equals(m_position))
        {
            m_position.setX(m_tmpPosition.getX());
            m_position.setY(m_tmpPosition.getY());
            m_position.setZ(m_tmpPosition.getZ());
        }

        if(m_tmpPitch != m_pitch || m_tmpYaw != m_yaw){
            setPitch(m_tmpPitch);
            setYaw(m_tmpYaw);
            onRotate(getYaw(), getPitch());
        }else
            onMove(new Vector3(), 0);

        Framework.getViewMatrix().setMatrix(getCameraLookMatrixWithPosition(-getYaw(), -getPitch()));

    }

    public void controlCamera(){}
}
