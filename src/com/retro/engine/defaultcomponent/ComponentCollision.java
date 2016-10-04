package com.retro.engine.defaultcomponent;

import com.retro.engine.component.Component;

/**
 * Created by Michael on 8/4/2016.
 */
public class ComponentCollision extends Component {

    private boolean m_twoDimensional;

    public ComponentCollision(){
        this(true);
    }
    public ComponentCollision(boolean twoD){
        m_twoDimensional = twoD;
    }

    public boolean isTwoDimensional(){
        return m_twoDimensional;
    }
}
