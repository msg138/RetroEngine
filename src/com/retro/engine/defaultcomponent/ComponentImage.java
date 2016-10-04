package com.retro.engine.defaultcomponent;

import com.jogamp.opengl.GL2;
import com.retro.engine.component.Component;
import com.retro.engine.ui.Image;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentImage extends Component {

    private Image m_image;

    public ComponentImage(){
        m_image = new Image();
    }
    public ComponentImage(String fname){
        m_image = new Image(fname);
    }
    public ComponentImage(String fname, GL2 gl){
        m_image = new Image(fname, gl);
    }

    public Image getImage(){
        return m_image;
    }

    @Override
    public void kill(){
        getImage().kill();
    }
}
