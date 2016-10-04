package com.retro.engine.component;

import com.retro.engine.util.list.UOList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Michael on 7/15/2016.
 */
public class ComponentStorage {

    // An ArrayList that contains the components in this storage.
    private UOList<Component> m_components;

    /**
     * Creates a new ComponentStorage and performs basic initialization.
     */
    public ComponentStorage(){
        m_components = new UOList<>();
    }

    /**
     * Add a component to the current storage object.
     * @param c Component to be added to the ComponentStorage object.
     */
    public void addComponent(Component c){
        m_components.set(ComponentType.getIndex(c.getClass()), c);
    }

    /**
     * Get whether the ComponentStorage has a Component of ComponentType
     * @param componentType Type of component to check against
     * @return
     */
    public boolean hasComponent(Class<? extends Component> componentType){
        return getComponent(componentType) != null;
    }
    /**
     * Get whether the ComponentStorage has a Component of ComponentType
     * @param componentType Type of component to check against
     * @return
     */
    public boolean hasComponent(int componentType){
        return getComponent(componentType) != null;
    }

    /**
     * Get the data for a component
     * @param componentType Component type to search for.
     * @return Component data.
     */
    public Component getComponent(Class<? extends Component> componentType){
        return m_components.get(ComponentType.getIndex(componentType));
    }
    /**
     * Get the data for a component
     * @param componentType Component type to search for.
     * @return Component data.
     */
    public Component getComponent(int componentType){
        return m_components.get(componentType);
    }

    public Component removeComponent(int c){
        Component cc = m_components.get(c);
        m_components.set(c, null);
        return cc;
    }

    public Component removeComponent(Class<? extends Component> ct){
        return removeComponent(ComponentType.getIndex(ct));
    }

    public List<Component> getComponents(){
        List<Component> ret = new ArrayList<>();

        for(int i=0;i<m_components.getMaxCapacity();i++)
        {
            if(m_components.get(i) != null)
                ret.add(m_components.get(i));
        }

        return ret;
    }
}
