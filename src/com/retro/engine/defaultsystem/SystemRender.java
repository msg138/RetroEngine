package com.retro.engine.defaultsystem;

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
public class SystemRender extends System {

    public SystemRender(){
        super(ComponentVisible.class, true);
    }

    public void handleMessage(RetroMessage msg){

    }

    @Override
    public void processEntity(Entity e, Graphics2D gg){
        Rectangle position = UtilEntity.getEntityBounds(e);

        BufferedImage buffer = new BufferedImage(Framework.getInstance().getWidth(), Framework.getInstance().getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buffer.createGraphics();
        if(e.has(ComponentColor.class))
        {
            g.setColor(new Color(((ComponentColor)e.get(ComponentColor.class)).getRed(),
                    ((ComponentColor)e.get(ComponentColor.class)).getGreen(),
                    ((ComponentColor)e.get(ComponentColor.class)).getBlue(),
                    ((ComponentColor)e.get(ComponentColor.class)).getAlpha()));
        }

        if(e.has(ComponentRectangle.class))
        {
            if(((ComponentRectangle)e.get(ComponentRectangle.class)).isFilled())
            {
                g.fillRect((int)position.getX(), (int)position.getY(), (int)position.getWidth(), (int)position.getHeight());
            }else
                g.drawRect((int)position.getX(), (int)position.getY(), (int)position.getWidth(), (int)position.getHeight());
        }

        if(e.has(ComponentImage.class))
        {
            g.drawImage(((ComponentImage)e.get(ComponentImage.class)).getImage().getImage(),
                    (int) position.getX(), (int) position.getY(),
                    (int) position.getWidth(), (int) position.getHeight(), null);
            // TODO add ability for spritesheets to be drawn.
        }

        if(e.has(ComponentText.class))
        {
            //g.setFont(new Font(((ComponentText)e.get(ComponentText.class)).getFontName(), Font.PLAIN, ((ComponentText)e.get(ComponentText.class)).getFontSize()));
            g.drawString(((ComponentText)e.get(ComponentText.class)).getText(), (int)position.getX(), (int)position.getY());
        }

        gg.drawImage(buffer, 0, 0, null);
    }
}
