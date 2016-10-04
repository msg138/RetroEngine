package com.retro.engine.model;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.debug.DebugMode;
import com.retro.engine.light.LightHandler;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.util.vector.Vector3;
import com.sun.prism.ps.Shader;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by Michael on 7/30/2016.
 */
public class RawModel {

    public static final int c_maxBufferSize = (int)Math.pow(2, 20);
    public static final int c_maxChunkBufferSize = (int)65536;

    private volatile IntBuffer m_buffers;

    private float[] m_vertices;
    private float[] m_colors;
    private float[] m_normals;
    private float[] m_surfaceNormals;

    private volatile FloatBuffer m_vertexFB;
    private volatile FloatBuffer m_colorFB;
    private volatile FloatBuffer m_normalFB;

    private static int m_biggestSizeYet = 0;

    private volatile IntBuffer m_vertexArray;

    private boolean m_goodBuffers = false;

    public RawModel(){

    }

    public RawModel(float[] vert, float[] color){
        setRawModel(vert, color);
    }

    public void setNormals(){

        // first we will set the surface normals.
        float[] normals = new float[m_vertices.length];
        int j = 0;// Keep track of normals location
        for(int i=0;i+8<m_vertices.length;i+=9){
            Vector3 v1 = new Vector3(m_vertices[i], m_vertices[i+1], m_vertices[i+2]);
            Vector3 v2 = new Vector3(m_vertices[i+3], m_vertices[i+4], m_vertices[i+5]);
            Vector3 v3 = new Vector3(m_vertices[i+6], m_vertices[i+7], m_vertices[i+8]);

            Vector3 d1 = new Vector3(v2.getX() - v1.getX(), v2.getY() - v1.getY(), v2.getZ() - v1.getZ());
            Vector3 d2 = new Vector3(v3.getX() - v2.getX(), v3.getY() - v2.getY(), v3.getZ() - v2.getZ());

            Vector3 cp = d1.crossProduct(d2);
            float dist = 1;
            normals[j] = cp.getX() / dist;
            normals[j+1] = cp.getY() / dist;
            normals[j+2] = cp.getZ() / dist;
            normals[j+3] = cp.getX() / dist;
            normals[j+4] = cp.getY() / dist;
            normals[j+5] = cp.getZ() / dist;
            normals[j+6] = cp.getX() / dist;
            normals[j+7] = cp.getY() / dist;
            normals[j+8] = cp.getZ() / dist;
            j+=9;
        }
        m_surfaceNormals = normals;
    }

    public void resetBuffers(){
        m_goodBuffers = false;
    }

    public boolean goodBuffers(){
        return m_goodBuffers;
    }

    public void setRawModel(float[] vert, float[] color){
        m_vertices = vert;
        m_colors = color;
        setNormals();
    }

    public void generateBuffers(GL2 gl){
        if(m_buffers != null) {
            kill();
            return;
        }
        if(m_colors == null || m_vertices == null || m_surfaceNormals == null)
        {
            Debug.out("Did not generate buffers." + m_vertices + " " + m_colors +" " + m_surfaceNormals);
            m_goodBuffers = true;
            return;
        }else if(m_colors.length<= 0 || m_vertices.length <= 0 || m_surfaceNormals.length <= 0)
        {
            Debug.out("Did not generate buffers."+ m_vertices.length + " " + m_colors.length +" " + m_surfaceNormals.length);
            m_goodBuffers = true;
            return;
        }
        m_buffers = Buffers.newDirectIntBuffer(3);
        gl.glGenBuffers(3, m_buffers);

        Debug.out("\t\tBuffer cap: " + m_buffers.capacity());

        m_vertexArray = Buffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, m_vertexArray);
        gl.glBindVertexArray(m_vertexArray.get(0));
        m_vertexFB = Buffers.newDirectFloatBuffer(m_vertices);
        m_colorFB = Buffers.newDirectFloatBuffer(m_colors);
        m_normalFB = Buffers.newDirectFloatBuffer(m_surfaceNormals);
        m_vertexFB.flip();
        m_colorFB.flip();
        m_normalFB.flip();

        //if(Buffers.SIZEOF_FLOAT * m_vertices.length > m_biggestSizeYet)
            m_biggestSizeYet = Buffers.SIZEOF_FLOAT * m_vertices.length;

        m_buffers.clear();

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, m_biggestSizeYet, m_vertexFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, m_biggestSizeYet, m_colorFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, m_biggestSizeYet, m_normalFB, gl.GL_STATIC_DRAW);

        // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 0, 0);

        // VertexAttribArray 1 corresponds with location 1 in the vertex shader.
        gl.glEnableVertexAttribArray(1);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glVertexAttribPointer(1, 3, gl.GL_FLOAT, false, 0, 0);

        // VertexAttribArray 1 corresponds with location 1 in the vertex shader.
        gl.glEnableVertexAttribArray(3);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glVertexAttribPointer(3, 3, gl.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);

        gl.glDeleteBuffers(3, m_buffers);


        m_goodBuffers = true;

        Debug.out("Generated buffers for a model." + m_vertices.length + " " + m_colors.length +" " + m_surfaceNormals.length + " ggl" +gl.GL_MAX_ELEMENTS_VERTICES, DebugMode.DEBUG_ALL);
    }

    public void render(GL2 gl, float x, float y, float z){
        Framework.getModelMatrix().pushMatrix();
        Framework.getModelMatrix().translatef(x, y, z);
        render(gl);
        Framework.getModelMatrix().popMatrix();
    }

    public void render(GL2 gl){
        if(!goodBuffers())
            generateBuffers(gl);
        LightHandler.getInstance().activateLights(gl, ShaderLoader.defaultShaderColor3d);

        if(m_vertexArray != null && goodBuffers()) {
            gl.glBindVertexArray(m_vertexArray.get(0));
            gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_vertices.length / 3);
        }

    }

    public void kill(){
        if(m_vertexArray != null)
            Framework.getInstance().addDeadVAO(m_vertexArray);
        //if(m_buffers != null)
            //Framework.getInstance().addDeadVBO(m_buffers);

        //m_vertexArray = null;
        //m_buffers = null;
    }


    @Override
    public void finalize() throws java.lang.Throwable {
        super.finalize();

        /*if(m_vertexArray != null)
            Framework.getInstance().addDeadVAO(m_vertexArray);
        if(m_buffers != null)
            Framework.getInstance().addDeadVBO(m_buffers);

        m_vertexArray = null;
        m_buffers = null;*/
    }
}
