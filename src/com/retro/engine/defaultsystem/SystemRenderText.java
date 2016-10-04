package com.retro.engine.defaultsystem;

import com.jogamp.opengl.GL2;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.defaultcomponent.ComponentImage;
import com.retro.engine.defaultcomponent.ComponentText;
import com.retro.engine.entity.Entity;
import com.retro.engine.system.System;

import java.awt.*;

/**
 * Created by Michael on 7/30/2016.
 */
public class SystemRenderText extends System {

    public SystemRenderText(){
        super(ComponentText.class, true);
    }

    public void handleMessage(RetroMessage msg){

    }

    @Override
    public void processEntity(Entity e, Graphics2D gg){
    }

    @Override
    public void processEntity(Entity e, GL2 gl){
        ComponentText img = (ComponentText)e.get(ComponentText.class);
        if(img.getTextObject().is3dSpace()){
            img.getTextObject().render3D(gl, e);
        }else
            img.getTextObject().render2D(gl, e);
    }
}
