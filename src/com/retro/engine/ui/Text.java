package com.retro.engine.ui;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.defaultcomponent.ComponentRotation;
import com.retro.engine.entity.Entity;
import com.retro.engine.model.Matrix;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.util.entity.UtilEntity;
import com.retro.engine.util.list.UtilArray;
import com.retro.engine.util.string.UtilString;
import com.retro.engine.util.vector.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static java.awt.SystemColor.text;

/**
 * Created by Michael on 7/30/2016.
 */
public class Text extends Image{

    private String m_text = "";
    private float m_fontSize = 0.2f;// Scales the images by this.

    private int m_wrapText = -1;

    public Text(){
        m_width = 0;
        m_height = 0;
        m_loaded = false;

        m_spriteLoc = new ComponentPosition(0,0, 7, 7);
    }
    public Text(String fname)
    {
        this();
        loadImage(fname);
    }
    public Text(String fname, GL2 gl){
        this();
        loadImage(fname, gl);
    }

    public void setFontSize(float size){
        m_fontSize = size;
    }
    public float getFontSize(){
        return m_fontSize;
    }
    public void setWrapText(int i){
        m_wrapText = i;
    }
    public int getWrapText(){
        return m_wrapText;
    }

    @Override
    public float getHeight(){
        if(!is3dSpace())
            return m_fontSize*7f;
        return m_fontSize * 0.02f;
    }
    @Override
    public float getWidth(){
        if(!is3dSpace())
            return getText().length() * m_fontSize*7f;
        return getText().length() * m_fontSize * 0.02f;
    }

    public void setText(String t){
        m_text = t;
        m_goodBuffers = false;
    }

    public void setTextWithWrap(String text){
        setTextWithWrap(text, m_wrapText);
    }
    public void setTextWithWrap(String text, int wrap){
        setText(UtilString.getTextWithWrap(text, wrap));
    }

    public String getText(){
        return m_text;
    }

    public float[] getVertices(Entity owner){
        if(!is3dSpace()) {
            Rectangle rect = new Rectangle(0,0,0,0);
            char[] text = m_text.toCharArray();
            float[] ret = new float[(text.length - UtilString.countChars(m_text, '\n')) * 12];
            float width = 7f;
            float height = 7f;
            int line = 0;
            float spot = 0;
            for(int i=0;i<text.length;i++){
                if(text[i] == '\n') {
                    line++;
                    spot = -1;
                    continue;
                }else if(i != 0)
                    spot += 1;
                int j = (i-line)*12;

                ret[j+0]=rect.x + spot*width;
                ret[j+1]=rect.y + line*height + height;

                ret[j+2]= rect.x + spot*width + width;
                ret[j+3]= rect.y + line*height + height;

                ret[j+4]= rect.x + spot*width + width;
                ret[j+5]= rect.y + line*height;

                ret[j+6]= rect.x + spot*width + width;
                ret[j+7]= rect.y + line*height;

                ret[j+8]= rect.x + spot*width;
                ret[j+9]= rect.y + line*height;

                ret[j+10]  = rect.x + spot*width;
                ret[j+11]= rect.y + line*height + height;
            }
            return ret;/*new float[]{
                    rect.x, rect.y,
                    rect.x + rect.width, rect.y,
                    rect.x + rect.width, rect.y + rect.height,

                    rect.x + rect.width, rect.y + rect.height,
                    rect.x, rect.y + rect.height,
                    rect.x, rect.y
            };*/
        }
        char[] text = m_text.toCharArray();
        float[] ret = new float[(text.length - UtilString.countChars(m_text, '\n')) * 18];
        float width = 0.2f;
        float height = 0.2f;
        float sw = width;
        if(UtilString.countChars(m_text, '\n') != 0){
            int longest = 0;
            for(String s : m_text.split("\n"))
                if(s.length() > longest)
                    longest = s.length();
            sw *= longest;
        }else
            sw *= text.length - (ret.length - text.length);
        float sh = height;
        float x = -sw/2.0f, y = -sh/2.0f, z = 0;
        int spot = 0;
        int line = 0;
        for(int i=0;i<text.length;i++){
            if(text[i] == '\n') {
                line++;
                spot = -1;
                continue;
            }else if(i != 0)
                spot += 1;
            int j = (i-line)*18;
            ret[j]  = x + spot*width;
            ret[j+1]= y - line*height;
            ret[j+2]= z;

            ret[j+3]= x + spot*width + width;
            ret[j+4]= y - line*height;
            ret[j+5]= z;

            ret[j+6]= x + spot*width + width;
            ret[j+7]= y - line*height + height;
            ret[j+8]= z;

            ret[j+9]=  x + spot*width + width;
            ret[j+10]= y - line*height + height;
            ret[j+11]= z;

            ret[j+12]= x + spot*width;
            ret[j+13]= y - line*height + height;
            ret[j+14]= z;

            ret[j+15]=x + spot*width;
            ret[j+16]=y - line*height;
            ret[j+17]= z;
        }
        return ret;
    }

    public float[] getTexCoords(){
        char[] text = m_text.toCharArray();
        float[] ret = new float[(text.length - UtilString.countChars(m_text, '\n')) * 12];
        int curNewLines = 0;
        // Assuming there is
        float width = (float)m_spriteLoc.getWidth() / (float)super.getWidth();
        for(int i=0;i<text.length;i++)
        {
            Vector2 loc = UtilString.getCharacterLocation(text[i]);
            int j = (i-curNewLines)*12;
            if(text[i] == '\n')
            {
                curNewLines++;
                continue;
            }
            ret[j+ 0] = loc.getX();
            ret[j+ 1] = loc.getY() + 1.0f;

            ret[j+ 2] = loc.getX() + width;
            ret[j+ 3] = loc.getY() + 1.0f;

            ret[j+ 4] = loc.getX() + width;
            ret[j+ 5] = loc.getY();

            ret[j+ 6] = loc.getX() + width;
            ret[j+ 7] = loc.getY();

            ret[j+ 8] = loc.getX();
            ret[j+ 9] = loc.getY();

            ret[j+10] = loc.getX();
            ret[j+11] = loc.getY() + 1.0f;
        }
        return ret;
        /*
        float sx = m_spriteLoc.getX();
        float sy = m_spriteLoc.getY();
        float sw = m_spriteLoc.getWidth();
        float sh = m_spriteLoc.getHeight();
        return new float[]{
                sx, sy + sh,
                sx + sw, sy + sh,
                sx + sw, sy,

                sx + sw, sy,
                sx, sy,
                sx, sy + sh,
        };*/
    }
    @Override
    public float[] getColorCoords(){
        float[] ret = new float[0];

        for(int i=0;i<(m_text.length() - UtilString.countChars(m_text, '\n'));i++)
            ret = UtilArray.concat(ret, new float[]{
                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),

                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                    m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
            });
        return ret;
    }

    public void render2D(GL2 gl, Entity owner){
        if(!m_fname.isEmpty())
            loadImageGL(m_image, gl);
        ShaderLoader.defaultShaderImage2d.activate(gl);

        if(!goodBuffers())
            generateGLBuffers(gl, owner);

        Framework.getModelMatrix().pushMatrix();
        if(owner.has(ComponentPosition.class)){
            ((ComponentPosition)owner.get(ComponentPosition.class)).applyTranslation(Framework.getModelMatrix());
        }
        Framework.getModelMatrix().scalef(m_fontSize, m_fontSize, 1);
        Framework.getModelMatrix().activateM(gl, ShaderLoader.defaultShaderImage2d);

        gl.glDisable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_TEXTURE_2D);

        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, getTexture());
        ShaderLoader.defaultShaderImage2d.setUniformInt(gl, "tex", 0);

        gl.glBindVertexArray(m_vertexArray.get(0));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_curVertAmount / 2);

        // unbind everything.
        gl.glBindVertexArray(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);

        gl.glDisable(gl.GL_TEXTURE_2D);
        gl.glEnable(gl.GL_DEPTH_TEST);

        Framework.getModelMatrix().popMatrix();
        ShaderLoader.getCurrentShader(gl).activate(gl);
    }
    public void render3D(GL2 gl, Entity owner){
        if(!m_fname.isEmpty())
            loadImageGL(m_image, gl);
        ShaderLoader.defaultShaderImage3d.activate(gl);

        // Set the camera and model view from default.
        Framework.getViewMatrix().activateV(gl, ShaderLoader.defaultShaderImage3d);

        float x =0, y=0, z=0;
        Framework.getModelMatrix().pushMatrix();
        if(owner.has(ComponentRotation.class)){
            ((ComponentRotation)owner.get(ComponentRotation.class)).applyRotation(Framework.getModelMatrix());
        }
        if(owner.has(ComponentPosition.class)){
            ((ComponentPosition)owner.get(ComponentPosition.class)).applyTranslation(Framework.getModelMatrix());
            ComponentPosition pos = ((ComponentPosition)owner.get(ComponentPosition.class));
            x = -pos.getX();
            y = -pos.getY();
            z = -pos.getZ();
        }
        if(isLookAtCamera()) {
            Framework.getModelMatrix().setMatrix(Framework.getInstance().getCamera().getCameraLookMatrix(x, y, z));
        }
        if(isLookAtCameraWithoutX()) {
            Framework.getModelMatrix().setMatrix(Framework.getInstance().getCamera().getCameraLookMatrixWithoutPitch(x, y, z));
        }
        Framework.getModelMatrix().scalef(m_fontSize, m_fontSize, m_fontSize);
        Framework.getModelMatrix().activateM(gl, ShaderLoader.defaultShaderImage3d);

        if(!goodBuffers())
            generateGLBuffers(gl, owner);

        gl.glEnable(gl.GL_TEXTURE_2D);

        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, getTexture());
        ShaderLoader.defaultShaderImage3d.setUniformInt(gl, "tex", 0);

        gl.glBindVertexArray(m_vertexArray.get(0));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_curVertAmount/ 3);

        // unbind everything.
        gl.glBindVertexArray(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);

        gl.glDisable(gl.GL_TEXTURE_2D);

        ShaderLoader.getCurrentShader(gl).activate(gl);
        Framework.getModelMatrix().popMatrix();
    }
}
