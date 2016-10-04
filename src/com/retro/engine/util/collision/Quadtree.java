package com.retro.engine.util.collision;

import com.retro.engine.entity.Entity;
import com.retro.engine.util.entity.UtilEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 8/4/2016.
 */
public class Quadtree {
    /**
     * Used for detecting likely collisions in 2 dimensional space.
     */

    private int m_maxObjects = 10;
    private int m_maxLevels = 0;

    private int m_level;
    private List<Entity> m_objects;
    private Rectangle m_bounds;
    private Quadtree[] m_nodes;

    public Quadtree(int pLevel, Rectangle pBounds){
        m_level = pLevel;
        m_bounds = pBounds;

        m_objects = new ArrayList();
        m_nodes = new Quadtree[4];
    }

    public void clear(){
        m_objects.clear();

        for(int i=0;i<m_nodes.length;i++){
            if(m_nodes[i] != null)
            {
                m_nodes[i].clear();
                m_nodes[i] = null;
            }
        }
    }

    private void split(){
        int subWidth = (int)(m_bounds.getWidth() / 2);
        int subHeight = (int)(m_bounds.getHeight() / 2);
        int x = (int)m_bounds.getX();
        int y = (int)m_bounds.getY();

        m_nodes[0] = new Quadtree(m_level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        m_nodes[1] = new Quadtree(m_level+1, new Rectangle(x, y, subWidth, subHeight));
        m_nodes[2] = new Quadtree(m_level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        m_nodes[3] = new Quadtree(m_level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Entity p) {
        Rectangle pRect = UtilEntity.getEntityBounds(p);
        int index = -1;
        double verticalMidpoint = m_bounds.getX() + (m_bounds.getWidth() / 2);
        double horizontalMidpoint = m_bounds.getY() + (m_bounds.getHeight() / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }
    public void insert(Entity pRect) {
        if (m_nodes[0] != null) {
            int index = getIndex(pRect);

            if (index != -1) {
                m_nodes[index].insert(pRect);

                return;
            }
        }

        m_objects.add(pRect);

        if (m_objects.size() > m_maxObjects && m_level < m_maxLevels) {
            if (m_nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < m_objects.size()) {
                int index = getIndex(m_objects.get(i));
                if (index != -1) {
                    m_nodes[index].insert(m_objects.remove(i));
                }
                else {
                    i++;
                }
            }
        }
    }
    public List retrieve(List returnObjects, Entity pRect) {
        int index = getIndex(pRect);
        if (index != -1 && m_nodes[0] != null) {
            m_nodes[index].retrieve(returnObjects, pRect);
        }

        returnObjects.addAll(m_objects);

        return returnObjects;
    }
}
