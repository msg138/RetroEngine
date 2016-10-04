package com.retro.engine.camera;

import com.retro.engine.Framework;
import com.retro.engine.event.EventHandler;
import com.retro.engine.util.vector.Vector2;
import com.retro.engine.util.vector.Vector3;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Michael on 7/30/2016.
 */
public class FirstPersonCamera extends Camera {

    private float m_sensitivity;
    private float m_moveVelocity;

    private boolean m_canMove;
    private boolean m_canRotate;

    private int m_keyMoveForward;
    private int m_keyMoveBackward;
    private int m_keyMoveLeft;
    private int m_keyMoveRight;
    private int m_keyStopCam;

    public FirstPersonCamera(){
        super();

        m_sensitivity = 0.1f;
        m_moveVelocity = 0.2f;
        m_canMove = true;
        m_canRotate = true;

        m_keyMoveBackward = KeyEvent.VK_S;
        m_keyMoveForward = KeyEvent.VK_W;
        m_keyMoveLeft = KeyEvent.VK_A;
        m_keyMoveRight = KeyEvent.VK_D;
        m_keyStopCam = KeyEvent.VK_CONTROL;
    }

    public void setKeyMoveBackward(int k){
        m_keyMoveBackward = k;
    }
    public void setKeyMoveForward(int k){
        m_keyMoveForward = k;
    }
    public void setKeyMoveLeft(int k){
        m_keyMoveLeft = k;
    }
    public void setKeyMoveRight(int k){
        m_keyMoveRight = k;
    }
    public void setKeyStopCam(int k){
        m_keyStopCam = k;
    }

    public int getKeyMoveForward(){
        return m_keyMoveForward;
    }
    public int getKeyMoveBackward(){
        return m_keyMoveBackward;
    }
    public int getKeyMoveLeft(){
        return m_keyMoveLeft;
    }
    public int getKeyMoveRight(){
        return m_keyMoveRight;
    }
    public int getKeyStopCam(){
        return m_keyStopCam;
    }

    public float getSensitivity(){
        return m_sensitivity;
    }
    public float getMoveVelocity(){
        return m_moveVelocity;
    }
    public boolean canMove(){
        return m_canMove;
    }
    public boolean canRotate(){
        return m_canRotate;
    }

    public void setSensitivity(float s){
        m_sensitivity = s;
    }
    public void setMoveVelocity(float m){
        m_moveVelocity = m;
    }
    public void setCanMove(boolean a){
        m_canMove = a;
    }
    public void setCanRotate(boolean a){
        m_canRotate = a;
    }

    @Override
    public void controlCamera(){
        // Handle rotation
        if(canRotate() && !EventHandler.getInstance().isKeyPressed(m_keyStopCam)){
            Vector2 dm = EventHandler.getInstance().getDeltaMouse();
            if(!EventHandler.getInstance().isMouseLocked()) {
                EventHandler.getInstance().setMouseLocked(true);
                // Transparent 16 x 16 pixel cursor image.
                BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                        cursorImg, new Point(0, 0), "blank cursor");
                Framework.getInstance().getProcess().getWindowHandle().setCursor(blankCursor);
            }
            setLook(getYaw() + getSensitivity() * dm.getX(), getPitch() + getSensitivity() * dm.getY());

            lockCamera();
        }else {
            if(EventHandler.getInstance().isMouseLocked()) {
                EventHandler.getInstance().setMouseLocked(false);
                Framework.getInstance().getProcess().getWindowHandle().setCursor(Cursor.getDefaultCursor());
            }
        }
        // Handle Camera movement.
        if(canMove()) {
            if (EventHandler.getInstance().isKeyPressed(m_keyMoveForward)){
                moveCamera(getMoveVelocity(), (float) Math.toRadians(0));
               // moveCameraUp(getMoveVelocity(), 0.0f);
            }if (EventHandler.getInstance().isKeyPressed(m_keyMoveBackward)){
                moveCamera(getMoveVelocity(), (float) Math.toRadians(180.0f));
                //moveCameraUp(getMoveVelocity(), 180.0f);
            }if(EventHandler.getInstance().isKeyPressed(m_keyMoveLeft)) {
                moveCamera(getMoveVelocity(), (float) Math.toRadians(90.0f));
                //moveCameraUp(getMoveVelocity(), 90.0f);
            }if(EventHandler.getInstance().isKeyPressed(m_keyMoveRight)) {
                moveCamera(getMoveVelocity(), (float) Math.toRadians(270.0f));
                //moveCameraUp(getMoveVelocity(), 270.0f);
            }
        }
    }
}
