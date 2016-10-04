package com.retro.engine.defaultsystem;

import com.jogamp.opengl.GL2;
import com.retro.engine.Framework;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.defaultcomponent.*;
import com.retro.engine.entity.Entity;
import com.retro.engine.system.System;
import com.retro.engine.util.entity.UtilEntity;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Michael on 7/30/2016.
 */
public class SystemRenderRectangle extends System {

    public SystemRenderRectangle(){
        super(ComponentRectangle.class, true);
    }

    public void handleMessage(RetroMessage msg){

    }

    @Override
    public void processEntity(Entity e, Graphics2D gg){
    }

    @Override
    public void processEntity(Entity e, GL2 gl){
        ComponentRectangle rec = (ComponentRectangle)e.get(ComponentRectangle.class);
        rec.render(gl, e);
    }
}
