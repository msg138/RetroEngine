package com.retro.engine.entity;

import com.retro.engine.component.Component;
import com.retro.engine.component.ComponentStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 7/15/2016.
 */
public class Entity {

    // The id of the entity, used to keep track of individual entities.
    private int m_id;

    // Keeps count of how many entities have been created.
    private static int m_entityCount = 0;

    private final ComponentStorage m_componentStorage;

    /**
     * Create a new entity, optionally with a list of components.
     * @param components Components to add to entity when starting. Useful for factories.
     */
    public Entity(List<Component> components){
        m_componentStorage = new ComponentStorage();

        for(Component c : components)
            m_componentStorage.addComponent(c);

        m_id = m_entityCount++;
    }

    /**
     * Create a new barebones entity with no components to start.
     */
    public Entity(){
        this(new ArrayList<>());
    }

    public boolean has(Class<? extends Component> c){ return m_componentStorage.hasComponent(c); }
    public boolean has(int c){ return m_componentStorage.hasComponent(c); }

    public Component get(int c){
        return m_componentStorage.getComponent(c);
    }
    public Component get(Class<? extends Component> c){
        return m_componentStorage.getComponent(c);
    }

    public Component remove(int c){ return m_componentStorage.removeComponent(c); }
    public Component remove(Class<? extends Component> c){ return m_componentStorage.removeComponent(c); }

    public int getId(){
        return m_id;
    }

    public void add(Component c){
        m_componentStorage.addComponent(c);
    }

    public List<Component> getComponents(){
        List<Component> ret = new ArrayList<>();

        ret = m_componentStorage.getComponents();

        return ret;
    }
}
