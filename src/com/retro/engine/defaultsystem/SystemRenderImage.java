package com.retro.engine.defaultsystem;

import com.jogamp.opengl.GL2;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.defaultcomponent.ComponentImage;
import com.retro.engine.defaultcomponent.ComponentRectangle;
import com.retro.engine.entity.Entity;
import com.retro.engine.system.System;

import java.awt.*;

/**
 * Created by Michael on 7/30/2016.
 */
public class SystemRenderImage extends System {

    public SystemRenderImage(){
        super(ComponentImage.class, true);
    }

    public void handleMessage(RetroMessage msg){

    }

    @Override
    public void processEntity(Entity e, Graphics2D gg){
    }

    @Override
    public void processEntity(Entity e, GL2 gl){
        ComponentImage img = (ComponentImage)e.get(ComponentImage.class);
        if(img.getImage().is3dSpace()){
            img.getImage().render3D(gl, e);
        }else
            img.getImage().render2D(gl, e);
    }
}
