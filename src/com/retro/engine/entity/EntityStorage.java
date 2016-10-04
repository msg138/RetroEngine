package com.retro.engine.entity;

import com.retro.engine.component.Component;
import com.retro.engine.defaultcomponent.ComponentImage;
import com.retro.engine.defaultcomponent.ComponentText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 7/15/2016.
 */
public class EntityStorage {

    private final ArrayList<Entity> m_entities;

    public EntityStorage(){
        m_entities = new ArrayList<>();
    }

    public void addEntity(Entity e){
        if(e == null)
            return;
        m_entities.add(e);
    }

    public int getEntityAmount(){
        return m_entities.size();
    }

    public Entity getEntity(int index){
        try {
            return m_entities.get(index);
        }catch(Exception e){

        }
        return null;
    }

    public void clearEntities(){
        // TODO cleanup
        synchronized (m_entities) {
            for(int i=0;i<m_entities.size();i++){
                if (m_entities.get(i) == null)
                    continue;
                removeEntity(m_entities.get(i), true);
                i--;
            }
            m_entities.clear();
        }
    }

    public void removeEntity(Entity e, boolean killComponents){
        if(e == null)
            return;
        if(killComponents) {
            List<Component> components = e.getComponents();
            for (Component c : components)
                c.kill();
        }
        m_entities.remove(e);
    }

    public void bringEntityToFront(Entity e){
        removeEntity(e, false);
        addEntity(e);
    }
    public void bringEntityToBack(Entity e){
        removeEntity(e, false);
        m_entities.add(0, e);
    }

    public int getEntityIndex(Entity e){
        return m_entities.indexOf(e);
    }
}
