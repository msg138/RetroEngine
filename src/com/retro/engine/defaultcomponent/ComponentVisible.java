package com.retro.engine.defaultcomponent;

import com.retro.engine.component.Component;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentVisible extends Component {
    private boolean m_isVisible = true;

    public ComponentVisible(){
        this(true);
    }

    public ComponentVisible(boolean v)
    {
        m_isVisible = v;
    }

    public void setVisible(boolean v)
    {
        m_isVisible = v;
    }
    public boolean isVisible()
    {
        return m_isVisible;
    }
}
