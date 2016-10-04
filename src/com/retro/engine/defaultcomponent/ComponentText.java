package com.retro.engine.defaultcomponent;

import com.jogamp.opengl.GL2;
import com.retro.engine.component.Component;
import com.retro.engine.ui.Text;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentText extends Component {

    private String m_fontName;
    private String m_text;
    private float m_fontSize;

    private Text m_textObject;

    public ComponentText(){
        m_fontName = "Courier New";
        m_fontSize = 12;
        m_text = "";
    }
    public ComponentText(String text){
        this();
        m_text = text;
    }
    public ComponentText(GL2 gl, String text, String fontName, float fontSize){
        this(text);
        m_fontName = fontName;
        m_fontSize = fontSize;

        m_textObject = new Text(fontName, gl);
        m_textObject.setFontSize(fontSize);
        m_textObject.setText(text);
    }
    public ComponentText(String text, String fontName, float fontSize){
        this(text);
        m_fontName = fontName;
        m_fontSize = fontSize;

        m_textObject = new Text(fontName);
        m_textObject.setFontSize(fontSize);
        m_textObject.setText(text);
    }

    public Text getTextObject(){
        return m_textObject;
    }

    public void setFont(String fontname, float fontsize)
    {
        m_fontName = fontname;
        m_fontSize = fontsize;
    }

    public void setText(String text)
    {
        m_text = text;
    }
    public String getText()
    {
        return m_text;
    }
    public String getFontName()
    {
        return m_fontName;
    }
    public float getFontSize()
    {
        return m_fontSize;
    }

    @Override
    public void kill(){
        getTextObject().kill();
    }
}
