package com.retro.engine;

import com.jogamp.opengl.GL2;
import com.retro.engine.loading.LoadData;
import com.retro.engine.scene.SceneType;
import com.retro.engine.ui.Window;

/**
 * Created by Michael on 7/19/2016.
 */
public abstract class RetroProcess {

    private boolean m_done = false;

    private Window m_window;

    private int m_processWidth;
    private int m_processHeight;

    public RetroProcess(int w, int h){
        m_processHeight = h;
        m_processWidth = w;
    }

    public boolean isDone(){
        return m_done;
    }

    public int getWidth(){
        return m_processWidth;
    }

    public int getHeight(){
        return m_processHeight;
    }

    public void initializeGL(GL2 gl){ }

    public void setWindowHandle(Window w){ m_window = w; }
    public Window getWindowHandle(){ return m_window; }

    public abstract void scene(SceneType st);

    public abstract void initialize();

    public abstract void finish();

    /**
     * Get a loadData that contains information to be loaded by the engine.
     * @return
     */
    public abstract LoadData load(SceneType nextScene);
}
