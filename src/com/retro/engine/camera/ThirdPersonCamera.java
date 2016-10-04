package com.retro.engine.camera;

import com.retro.engine.debug.Debug;
import com.retro.engine.event.EventHandler;
import com.retro.engine.util.vector.Vector;
import com.retro.engine.util.vector.Vector2;
import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 8/29/2016.
 */
public class ThirdPersonCamera extends FirstPersonCamera {

    private Vector3 m_lookatPos;

    private float m_lookYaw;
    private float m_lookPitch;

    private boolean m_moveTarget;

    private float m_lookDistance;

    public ThirdPersonCamera(){
        m_lookatPos = new Vector3();

        m_moveTarget = false;

        m_lookYaw = 0;
        m_lookPitch = 0;
        m_lookDistance = 0;
    }

    public void setLookat(Vector3 v){
        m_lookatPos = v;
    }
    public Vector3 getLookat(){
        return m_lookatPos;
    }

    public void setLookYaw(float y){
        m_lookYaw = y;
    }
    public float getLookYaw(){
        return m_lookYaw;
    }
    public void setLookPitch(float p){
        m_lookPitch = p;
    }
    public float getLookPitch(){
        return m_lookPitch;
    }

    public boolean getMoveTarget() {
        return m_moveTarget;
    }

    public void setMoveTarget(boolean m_moveTarget) {
        this.m_moveTarget = m_moveTarget;
    }

    public void setLookDistance(float d){
        m_lookDistance = d;
    }
    public float getLookDistance(){
        return m_lookDistance;
    }

    private void lookat(Vector3 po){
        Vector3 pos = po.clone();
        Vector3 camPos = getPosition().toVector3();

        Vector3 delta = pos.subtractFrom(camPos).normalize();

        float distance = (float)Math.sqrt(delta.getZ() * delta.getZ() + delta.getX() * delta.getX());
        float p = -(float)Math.asin(delta.getY());
        float y = (float)Math.atan2(delta.getX(), delta.getZ());

        setLook(y, p, true);
    }

    private void updateLookPosition(float y, float p){

        Vector3 newPos = getLookPosition(y, p, getLookDistance(), m_lookatPos);

        getPosition().setX(newPos.getX());
        getPosition().setY(newPos.getY());
        getPosition().setZ(newPos.getZ());
    }

    public static Vector3 getLookPosition(float y, float p,float d,  Vector3 target){
        Vector3 newPos = new Vector3();
        newPos.setX(target.getX() + (float) (Math.sin(y) * Math.cos(p) * d));
        newPos.setY(target.getY() + (float) (Math.sin(p) * d));
        newPos.setZ(target.getZ() + (float) (Math.cos(y) * Math.cos(p) * d));
        return newPos;
    }

    private void moveLookPosition(float distance, float direction){
        m_lookatPos = getMovePosition(distance, direction, m_lookatPos);
    }

    private void lockLook(){
        if(getLookPitch() > Math.toRadians(89))
            setLookPitch((float)Math.toRadians(89));
        if(getLookPitch() < Math.toRadians(-90))
            setLookPitch((float)Math.toRadians(-90));

        if(getLookYaw() > Math.toRadians(360))
            setLookYaw(getLookYaw() - (float)Math.toRadians(360));
        if(getLookYaw() < 0)
            setLookYaw(getLookYaw() + (float)Math.toRadians(360));
    }

    @Override
    public void controlCamera(){
        setCanRotate(false);
        if(!getMoveTarget() && canMove()) {
            if (EventHandler.getInstance().isKeyPressed(getKeyMoveForward())){
                if(getLookPitch() < 1.0f);
                    setLookPitch(getLookPitch() + getMoveVelocity());
                // moveCameraUp(getMoveVelocity(), 0.0f);
            }if (EventHandler.getInstance().isKeyPressed(getKeyMoveBackward())){
                if(getLookPitch() > .06f)
                    setLookPitch(getLookPitch() - getMoveVelocity());
                //moveCameraUp(getMoveVelocity(), 180.0f);
            }if(EventHandler.getInstance().isKeyPressed(getKeyMoveLeft())) {
                setLookYaw(getLookYaw() - getMoveVelocity());
                //moveCameraUp(getMoveVelocity(), 90.0f);
            }if(EventHandler.getInstance().isKeyPressed(getKeyMoveRight())) {
                    setLookYaw(getLookYaw() + getMoveVelocity());
                //moveCameraUp(getMoveVelocity(), 270.0f);
            }
        }else if(getMoveTarget() && canMove()) {
            if (EventHandler.getInstance().isKeyPressed(getKeyMoveForward())) {
                moveLookPosition(getMoveVelocity(), (float) Math.toRadians(0));
            }
            if (EventHandler.getInstance().isKeyPressed(getKeyMoveBackward())) {
                moveLookPosition(getMoveVelocity(), (float) Math.toRadians(180.0f));
            }
            if (EventHandler.getInstance().isKeyPressed(getKeyMoveLeft())) {
                moveLookPosition(getMoveVelocity(), (float) Math.toRadians(90.0f));
            }
            if (EventHandler.getInstance().isKeyPressed(getKeyMoveRight())) {
                moveLookPosition(getMoveVelocity(), (float) Math.toRadians(270.0f));
            }
        }
        lockLook();
        updateLookPosition(getLookYaw(), getLookPitch());
        lookat(m_lookatPos);
        lockCamera();
    }
}
