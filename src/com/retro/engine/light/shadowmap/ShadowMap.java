package com.retro.engine.light.shadowmap;

import com.jogamp.opengl.GL2;
import com.retro.engine.model.shader.ShaderProgram;

import java.nio.IntBuffer;

/**
 * Created by Michael on 8/19/2016.
 */
public class ShadowMap {

    private static ShadowMap m_instance;

    private IntBuffer m_frameBuffer;
    private ShaderProgram m_shadowProgram;

    private int m_depthTexture;

    private ShadowMap(){

        m_instance = this;
    }

    public static ShadowMap getInstance(){
        if(m_instance == null)
            new ShadowMap();
        return m_instance;
    }

    public void generateShadowShaders(GL2 gl){
        m_shadowProgram = new ShaderProgram("#version 330\n" +
                "layout(location=0) in vec3 vertex\n" +
                "uniform mat4 depthMVP;\n" +
                "void main(){\n" +
                "gl_Position = depthMVP * vec4(vertex, 1.0);\n" +
                "}\n",
                "#version 330 \n" +
                        "layout(location=0) out float fragmentdepth;\n" +
                        "void main(){\n" +
                        "fragmentdepth = gl_FragCoord.z;\n" +
                        "}\n", gl);

    }

    public void generateFrameBuffer(GL2 gl){
        gl.glGenFramebuffers(1, m_frameBuffer);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, m_frameBuffer.get(0));

        IntBuffer dt = IntBuffer.allocate(1);
        gl.glGenTextures(1, dt);
        m_depthTexture = dt.get(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, m_depthTexture);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_DEPTH_COMPONENT16, 1024, 1024, 0, gl.GL_DEPTH_COMPONENT, gl.GL_FLOAT, 0);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);

        gl.glFramebufferTexture2D(gl.GL_FRAMEBUFFER, gl.GL_DEPTH_ATTACHMENT, gl.GL_TEXTURE_2D, m_depthTexture, 0);
        gl.glDrawBuffer(gl.GL_NONE);
        gl.glReadBuffer(gl.GL_NONE);

        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
    }


}
