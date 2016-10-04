package com.retro.engine.defaultsystem;

import com.retro.engine.Framework;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.debug.Debug;
import com.retro.engine.defaultcomponent.ComponentCollision;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.entity.Entity;
import com.retro.engine.event.EventHandler;
import com.retro.engine.system.System;
import com.retro.engine.util.collision.UtilCollision;
import com.retro.engine.util.entity.UtilEntity;
import com.retro.engine.util.vector.Vector2;
import com.retro.engine.util.vector.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 8/4/2016.
 */
public class SystemCollision extends System{

    public SystemCollision(){
        super(ComponentCollision.class);
    }

    @Override
    public void handleMessage(RetroMessage msg){
        if(msg.getRawData().equalsIgnoreCase("RESETQUADTREE")) {
            UtilCollision.clearCollisions();
            Framework.getInstance().getQuadtree().clear();
            // Insert the mouse for collision
            Vector3 mousePos = new Vector3((ComponentPosition)EventHandler.getInstance().getMouseEntity().get(ComponentPosition.class));
            Framework.getInstance().getQuadtree().insert(EventHandler.getInstance().getMouseEntity());
        }
    }

    @Override
    public void processEntity(Entity e){
        // Two dimensional collision detection.
        if(((ComponentCollision)e.get(ComponentCollision.class)).isTwoDimensional()){
            Framework.getInstance().getQuadtree().insert(e);

            List<Entity> possibleCollisionObjects = new ArrayList<>();
            Framework.getInstance().getQuadtree().retrieve(possibleCollisionObjects, e);
            for(int i=0;i<possibleCollisionObjects.size();i++)
            {
                if(possibleCollisionObjects.get(i).getId() == e.getId())
                    continue;
                Rectangle rect = UtilEntity.getEntityBounds(e);
                Rectangle otherRect = UtilEntity.getEntityBounds(possibleCollisionObjects.get(i));
                if(UtilCollision.rectangleCollision(otherRect, rect) && UtilCollision.getCollision(e) == null) {
                    UtilCollision.pushCollision(new UtilCollision.ActiveCollision(e, possibleCollisionObjects.get(i)));
                    UtilCollision.pushCollision(new UtilCollision.ActiveCollision(possibleCollisionObjects.get(i), e));
                }
            }

        }
    }
}
