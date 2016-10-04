package com.retro.engine;

import com.retro.engine.ui.Window;

/**
 * Created by Michael on 7/30/2016.
 */
public class AppProcess {

    private Window m_window;


    public AppProcess(Window w)
    {
        m_window = w;
    }

    public Window getWindow(){
        return m_window;
    }
}
