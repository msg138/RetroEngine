package com.retro.engine.system;

import com.jogamp.opengl.GL2;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.component.Component;
import com.retro.engine.component.ComponentType;
import com.retro.engine.entity.Entity;

import java.awt.*;

/**
 * Created by Michael on 7/15/2016.
 */
public abstract class System {

    private ComponentType m_component;

    private static int m_systemCount = 0;

    private int m_systemID;

    private boolean m_systemRender = false;// Just so we don't start executing with render systems automatically.
    private boolean m_systemLogic = false;

    /**
     * Initialize the system to work on said component
     * @param c Component Class  for the system to trigger on
     */
    protected System(Class<? extends Component> c){
        this(c, false);
    }

    protected System(Class<? extends Component> c, boolean render){
        if(render){
            renderSystem();
        }
        m_component = ComponentType.getComponentType(c);

        m_systemID = m_systemCount++;

        // TODO DOES THIS WORK
        if(c != null)
            SystemManager.getInstance().addSystem(this);
    }

    /**
     * Makes the system a render system and will be executed with the other render systems. Shouldn't contain any logic
     */
    protected void renderSystem(){
        m_systemRender = true;
    }

    /**
     * Makes the system a logic system, mostly contains logic and is executed with the others.
     */
    protected void logicSystem(){
        m_systemLogic = true;
    }

    /**
     * Get whether or not the system is called in render or logic loop.
     * @return
     */
    public boolean isRenderSystem(){
        return m_systemRender;
    }

    /**
     * Get whether or not the system is called in logic or render loop.
     * @return
     */
    public boolean isLogicSystem(){
        return(m_systemLogic == false && m_systemRender == false) || m_systemLogic;
    }

    /**
     * Create a system without a component type. Useful for system managers.
     */
    protected System(){
        this(null);
    }

    /**
     * Get the id of the system. This is used for messaging.
     * @return m_systemID as it pertains to the object.
     */
    public int getID(){
        return m_systemID;
    }

    /**
     * Get the component type that triggers the system.
     * @return Component type that triggers the system.
     */
    public ComponentType getComponentType(){
        return m_component;
    }

    /**
     * Do whatever the system does with the entity.
     * @param e Entity to work magic on.
     */
    public void processEntity(Entity e){ }

    public void processEntity(Entity e, Graphics2D g){
        processEntity(e);
    }
    public void processEntity(Entity e, GL2 g){
        processEntity(e);
    }

    public abstract void handleMessage(RetroMessage msg);
}
