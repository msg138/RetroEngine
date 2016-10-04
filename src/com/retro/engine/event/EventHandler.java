package com.retro.engine.event;

import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.debug.DebugMode;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.entity.Entity;
import com.retro.engine.util.vector.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Michael on 7/19/2016.
 */
public class EventHandler implements MouseListener, MouseMotionListener, KeyListener{

    private static EventHandler m_instance;

    private Vector2 m_mousePosition = new Vector2();
    private Vector2 m_dmousePosition = new Vector2();
    private Entity m_mouseEntity;
    private boolean m_mouselclick = false;
    private boolean m_mouserclick = false;
    private boolean m_mousemclick = false;

    private boolean m_takingInput = false;

    private String m_input = "";

    private boolean m_mouseLocked = false;

    private boolean m_key[] = new boolean[525];

    private EventHandler(){
        m_instance = this;
    }

    public static EventHandler getInstance(){
        if(m_instance == null)
            new EventHandler();
        return m_instance;
    }

    public static void initialize(java.awt.Component f){
        Debug.out("Initializing EventHandler.", DebugMode.DEBUG_NOTICE);
        Debug.out("\tSetting Event Listeners...", DebugMode.DEBUG_NOTICE);
        setListeners(f);
        Debug.out("\tListeners set successfully.", DebugMode.DEBUG_NOTICE);
        Debug.out("EventHandler successfully initialized.", DebugMode.DEBUG_NOTICE);
    }

    public static void setListeners(java.awt.Component f){
        f.addMouseListener(getInstance());
        f.addMouseMotionListener(getInstance());
        f.addKeyListener(getInstance());
    }

    public Entity getMouseEntity(){
        if(m_mouseEntity == null){
            m_mouseEntity = new Entity();
            m_mouseEntity.add(new ComponentPosition(0,0, 1, 1));
        }
        ((ComponentPosition)m_mouseEntity.get(ComponentPosition.class)).setX(m_mousePosition.getX() / Framework.getInstance().getWidthRatio());
        ((ComponentPosition)m_mouseEntity.get(ComponentPosition.class)).setY(m_mousePosition.getY() / Framework.getInstance().getHeightRatio());
        return m_mouseEntity;
    }
    public int getMouseEntityId(){
        return m_mouseEntity.getId();
    }

    public boolean isMouseLocked(){
        return m_mouseLocked;
    }
    public void setMouseLocked(boolean m){
        m_mouseLocked = m;
    }

    public boolean isMouseL(){
        return m_mouselclick;
    }
    public boolean isMouseR(){
        return m_mouserclick;
    }
    public boolean isMouseM(){
        return m_mousemclick;
    }

    public void releaseMouseL(){ m_mouselclick = false; }
    public void releaseMouseR(){ m_mouserclick = false; }
    public void releaseMouseM(){ m_mousemclick = false; }

    public boolean isKeyPressed(int kid){
        return m_key[kid];
    }

    public void takeInput(){
        m_takingInput = true;
    }
    public void stopTakingInput(){
        m_takingInput = false;
    }
    public boolean isTakingInput(){
        return m_takingInput;
    }
    public String getInput(){
        return m_input;
    }
    public void resetInput(){
        m_input = "";
    }

    public void setInput(String i){
        m_input = i;
    }

    public void releaseKey(int kid){ m_key[kid] = false; }

    public int getMouseX(){
        return (int)m_mousePosition.getX();
    }
    public int getMouseY(){
        return (int)m_mousePosition.getY();
    }
    public Vector2 getMousePosition(){
        return new Vector2(m_mousePosition.getX() / Framework.getInstance().getWidthRatio(),
                m_mousePosition.getY() / Framework.getInstance().getHeightRatio());
    }
    public Vector2 getDeltaMouse(){ Vector2 mouse = m_dmousePosition; m_dmousePosition = new Vector2(); return mouse; }


    // Events stuff
    @Override
    public void keyTyped(KeyEvent e) {

        if(m_takingInput){
            if(isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
                if(m_input.length() > 0)
                    m_input = m_input.substring(0, m_input.length() - 1);
            }else if(e.getKeyChar() >=32 && e.getKeyChar() <= 126)
                m_input += e.getKeyChar();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        m_key[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        m_key[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == e.BUTTON1)
            m_mouselclick = true;
        if(e.getButton() == e.BUTTON2)
            m_mousemclick = true;
        if(e.getButton() == e.BUTTON3)
            m_mouserclick = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == e.BUTTON1)
            m_mouselclick = false;
        if(e.getButton() == e.BUTTON2)
            m_mousemclick = false;
        if(e.getButton() == e.BUTTON3)
            m_mouserclick = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        m_dmousePosition.setX(m_mousePosition.getX() - e.getX());
        m_dmousePosition.setY(m_mousePosition.getY() - e.getY());
        m_mousePosition.setX(e.getX());
        m_mousePosition.setY(e.getY());

        int wl = Framework.getInstance().getGameWidth()/4;
        int wh = Framework.getInstance().getGameWidth() - wl;

        int hl = Framework.getInstance().getGameHeight()/4;
        int hh = Framework.getInstance().getGameHeight() - hl;
        if(m_mouseLocked && !isKeyPressed(KeyEvent.VK_ALT) && ((e.getX() > wh || e.getX() < wl) || (e.getY() > hh || e.getY() < hl)))
            try {
                Robot r = new Robot();
                r.mouseMove(Framework.getInstance().getProcess().getWindowHandle().getX() + Framework.getInstance().getGameWidth()/2, Framework.getInstance().getProcess().getWindowHandle().getY() + Framework.getInstance().getGameHeight()/2);
                // TODO make camera movement smoother. Right now it is choppy.
                PointerInfo pi = MouseInfo.getPointerInfo();
                Point p = pi.getLocation();
                SwingUtilities.convertPointFromScreen(p, Framework.getGlHandle());
                m_mousePosition.setX((float)p.getX());
                m_mousePosition.setY((float)p.getY());
            }catch(Exception ex){
                ex.printStackTrace();
            }
    }
}
