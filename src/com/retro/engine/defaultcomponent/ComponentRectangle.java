package com.retro.engine.defaultcomponent;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.retro.engine.component.Component;
import com.retro.engine.entity.Entity;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.util.entity.UtilEntity;
import com.sun.prism.ps.Shader;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by Michael on 7/30/2016.
 */
public class ComponentRectangle extends Component {

    private boolean m_filled;

    private IntBuffer m_buffers = IntBuffer.allocate(2);
    private FloatBuffer m_vertexFB;
    private FloatBuffer m_colorFB;
    private IntBuffer m_vertexArray = IntBuffer.allocate(1);
    private float[] m_vertices ;
    private boolean m_goodBuffers = false;


    public ComponentRectangle(){
        m_filled = true;
    }
    public ComponentRectangle(boolean fill){
        this();
        m_filled = fill;
    }

    public boolean isFilled(){
        return m_filled;
    }

    public void setFilled(boolean fill){
        m_filled = fill;
    }

    public boolean toggleFill(){
        setFilled(!isFilled());
        return isFilled();
    }

    public float[] getVertices(Entity owner){
        Rectangle rect = UtilEntity.getEntityBounds(owner);
        float[] verts = {
                rect.x, rect.y,
                rect.x + rect.width, rect.y,
                rect.x + rect.width, rect.y + rect.height,

                rect.x + rect.width, rect.y + rect.height,
                rect.x, rect.y + rect.height,
                rect.x, rect.y
        };

        return verts;
    }
    public float[] getColors(Entity owner){
        float[] colors = {
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,

                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
        };
        if(owner.has(ComponentColor.class)){
            ComponentColor cc = (ComponentColor)owner.get(ComponentColor.class);
            colors = new float[]{
                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),
                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),
                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),

                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),
                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),
                    cc.getRedf(), cc.getGreenf(), cc.getBluef(), cc.getAlphaf(),
            };
        }

        return colors;
    }

    public boolean goodBuffers(){
        return m_goodBuffers;
    }

    public void generateGLBuffers(GL2 gl, Entity owner){
        m_vertices = getVertices(owner);
        float[] m_colors = getColors(owner);
        m_vertexFB = FloatBuffer.wrap(m_vertices);
        m_colorFB = FloatBuffer.wrap(m_colors);

        gl.glGenBuffers(2, m_buffers);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* m_vertices.length, m_vertexFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* m_colors.length, m_colorFB, gl.GL_STATIC_DRAW);

        gl.glGenVertexArrays(1, m_vertexArray);
        gl.glBindVertexArray(m_vertexArray.get(0));

        // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);

        // VertexAttribArray 1 corresponds with location 1 in the vertex shader.
        gl.glEnableVertexAttribArray(1);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glVertexAttribPointer(1, 4, gl.GL_FLOAT, false, 0, 0);

        m_goodBuffers = true;
    }

    public void render(GL2 gl, Entity owner){
        ShaderLoader.defaultShaderColor2d.activate(gl);

        if(!goodBuffers())
            generateGLBuffers(gl, owner);
        gl.glBindVertexArray(m_vertexArray.get(0));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_vertices.length);

        ShaderLoader.getCurrentShader(gl).activate(gl);
    }
}
