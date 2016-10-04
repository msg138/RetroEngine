package com.retro.engine.scene;

/**
 * Created by Michael on 7/19/2016.
 */
public enum SceneType {

    // Used for things pregame or program.
    SCENE_VISUALIZING(-3),// Used for when the window is waiting to appear.
    SCENE_LOADING(-1),
    SCENE_INITIALIZING(-2),

    SCENE000(0),
    SCENE001(1),
    SCENE002(2),
    SCENE003(3),
    SCENE004(4),
    SCENE005(5),
    SCENE006(6),
    SCENE007(7),
    SCENE008(8),
    SCENE009(9),
    SCENE010(10);

    private int m_sceneId;

    private SceneType(int id){
        m_sceneId = id;
    }

    public int getId(){
        return m_sceneId;
    }

    public int getSceneId(){
        return getId();
    }
}
