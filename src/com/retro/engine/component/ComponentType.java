package com.retro.engine.component;

import com.retro.engine.util.list.UOList;

import java.util.HashMap;

/**
 * Created by Michael on 7/15/2016.
 */
public final class ComponentType {

    private static HashMap<Class<? extends Component>, ComponentType> m_components = new HashMap<>();

    private static int m_amountComponentType = 0;

    private final int m_index;

    /**
     * Initialize a new component type.
     */
    private ComponentType(){
        m_index = m_amountComponentType++;
    }

    /**
     * Get the index of the componentType, this should pertain to arrays.
     * @return m_index
     */
    public int getIndex(){
        return m_index;
    }

    /**
     * Initialize a new component Type
     * @param component Component to initialize for
     * @return
     */
    public static ComponentType initializeComponent(Class<? extends Component> component){
        ComponentType ct = m_components.get(component);
        if(ct != null)
            return ct;
        ct = new ComponentType();
        m_components.put(component, ct);
        return ct;
    }

    /**
     * Get the componentType object for x component
     * @param component Component to retrieve for
     * @return ComponentType of the Component.
     */
    public static ComponentType getComponentType(Class<? extends Component> component){
        ComponentType ct = m_components.get(component);

        if(ct == null)
        {
            ct = initializeComponent(component);
        }

        return ct;
    }

    /**
     * Get the index of a component without creating a new object for it.
     * @param component Component to search for.
     * @return index that should pertain to the component
     */
    public static int getIndex(Class<? extends Component> component){
        return getComponentType(component).getIndex();
    }
}
