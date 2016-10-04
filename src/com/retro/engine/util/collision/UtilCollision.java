package com.retro.engine.util.collision;

import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.entity.Entity;
import com.retro.engine.event.EventHandler;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Michael on 8/4/2016.
 */
public class UtilCollision {

    private static HashMap<Entity, ActiveCollision> s_collisions = new HashMap<>();

    public static ActiveCollision getCollision(Entity e){
        return s_collisions.get(e);
    }
    public static void pushCollision(ActiveCollision coll){
        s_collisions.put(coll.getEntityOne(), coll); // Should not be a chance for the first entity to be null like a mouse.
    }
    public static void clearCollisions(){
        s_collisions.clear();
    }

    /**  2 Dimensional Collision Detection */
    public static boolean rectangleCollision(Rectangle r1, Rectangle r2){
        return !(r2.getX() > r1.getX() + r1.getWidth()
                || r2.getX() + r2.getWidth() < r1.getX()
                || r2.getY() > r1.getY() + r1.getHeight()
                || r2.getY() + r2.getHeight() < r1.getY());
    }
    public static boolean rectangleCollision(ComponentPosition r1, ComponentPosition r2){
        return rectangleCollision(new Rectangle((int)r1.getX(), (int)r1.getY(), (int)r1.getWidth(), (int)r1.getHeight()),
                new Rectangle((int)r2.getX(), (int)r2.getY(), (int)r2.getWidth(), (int)r2.getHeight()));
    }


    public static class ActiveCollision{

        private Entity m_entityOne;
        private Entity m_entityTwo;

        public ActiveCollision(Entity e1, Entity e2){
            m_entityOne = e1;
            m_entityTwo = e2;
        }

        public Entity getEntityOne(){
            return m_entityOne;
        }
        public Entity getEntityTwo(){
            return m_entityTwo;
        }

        public boolean isEntityMouse(Entity e){
            return e == null || e.getId() == EventHandler.getInstance().getMouseEntityId();
        }

        public boolean isEntityMouse(){
            return isEntityMouse(m_entityTwo);
        }
    }
}
