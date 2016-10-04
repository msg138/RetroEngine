package com.retro.engine.util.entity;

import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.defaultcomponent.ComponentText;
import com.retro.engine.entity.Entity;

import java.awt.*;

/**
 * Created by Michael on 7/30/2016.
 */
public class UtilEntity {

    public static Rectangle getEntityBounds(Entity e){
        if(!e.has(ComponentPosition.class))
            return new Rectangle();
        ComponentPosition pos = (ComponentPosition)e.get(ComponentPosition.class);
        if(e.has(ComponentText.class)){
            ComponentText t = ((ComponentText)e.get(ComponentText.class));
            pos.setHeight(t.getTextObject().getHeight());
            pos.setWidth(t.getTextObject().getWidth());
        }
        return new Rectangle((int)pos.getX(), (int)pos.getY(), (int)pos.getWidth(), (int)pos.getHeight());
    }
}
