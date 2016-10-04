package com.retro.engine.system;

import com.jogamp.opengl.GL2;
import com.retro.engine.Messaging.Message;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.component.ComponentType;
import com.retro.engine.entity.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Michael on 7/16/2016.
 */
public final class SystemManager extends System {

    private static SystemManager m_instance = null;

    private HashMap<ComponentType, System> m_systemComponent;
    private HashMap<Integer, System> m_systemId;

    private ArrayList<System > m_systems;

    /**
     * Initialize anything involving the system manager.
     */
    private SystemManager(){
        m_systemComponent = new HashMap<>();
        m_systemId = new HashMap<>();
        m_systems = new ArrayList<>();

        m_instance = this;
    }

    /**
     * Get the instance of systemmanager currently being used.
     * @return An instance of systemmanager that is safe to use.
     */
    public static SystemManager getInstance(){
        if(m_instance == null)
            new SystemManager();
        return m_instance;
    }

    /**
     * Add a system that will be checked for when an entity is passed to update.
     * @param s System to be added to the systemmanager.
     */
    public void addSystem(System s){
        if(m_systemId.get(s.getID()) != null)
        {

        }else {
            m_systemComponent.put(s.getComponentType(), s);
            m_systemId.put(s.getID(), s);
            m_systems.add(s);
        }
    }

    /**
     * Get a system that is activated upon the componenttype c
     * TODO Throw error when a system does not exist or equals null.
     * @param c ComponentType that requested system acts upon
     * @return System that acts upon ComponentType c or null if nonexistent
     */
    public System getSystem(ComponentType c){
        System system = m_systemComponent.get(c);
        return system;
    }

    /**
     * Get a system that has the id
     * TODO Throw error when a system does not exist or equals null.
     * @param id Id to search for the system
     * @return System that has id or null if nonexistent
     */
    public System getSystem(int id){
        System system = m_systemId.get(id);
        return system;
    }


    @Override
    public void processEntity(Entity e){
        if(e == null)
            return;
        for(System s : m_systems){
            if(!s.isLogicSystem())
                continue;
            if(e.has(s.getComponentType().getIndex()))
                s.processEntity(e);
        }
    }

    public void renderEntity(Entity e, Graphics2D g){
        if(e == null)
            return;
        for(System s : m_systems){
            if(!s.isRenderSystem())
                continue;
            if(e.has(s.getComponentType().getIndex()))
                s.processEntity(e, g);
        }
    }
    public void renderEntity(Entity e, GL2 g){
        if(e == null)
            return;
        for(System s : m_systems){
            if(!s.isRenderSystem())
                continue;
            if(e.has(s.getComponentType().getIndex()))
                s.processEntity(e, g);
        }
    }

    @Override
    public void handleMessage(RetroMessage msg){
        if(msg.getID() == -1)
        {
            for(System s : m_systems)
                s.handleMessage(msg);
        }
        System s = m_systemId.get(msg.getID());
        if(s != null)
            s.handleMessage(msg);
        // TODO throw error if system does not exist to send message to.
    }

}
