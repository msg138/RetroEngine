package com.retro.engine.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Michael on 7/19/2016.
 */
public abstract class Canvas extends JPanel {

    public Canvas(boolean useOGL){
        if(useOGL)
            return;
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.cyan);
    }

    public abstract void render(Graphics2D g2d);

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g2d);
        render(g2d);
    }

}
