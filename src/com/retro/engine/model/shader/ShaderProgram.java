package com.retro.engine.model.shader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.FloatUtil;
import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.util.vector.Vector2;
import com.retro.engine.util.vector.Vector3;
import com.retro.engine.util.vector.Vector4;

/**
 * Created by Michael on 8/2/2016.
 */
public class ShaderProgram {

    private int m_program;
    private int m_projectionMatrix;
    private int m_viewMatrix;
    private int m_modelMatrix;
    private int m_normalMatrix;

    private int m_vertexShader;
    private int m_fragmentShader;

    private float[] m_vmatrix;
    private float[] m_mmatrix;
    private float[] m_pmatrix;

    public ShaderProgram(){
        m_program = -1;
        m_projectionMatrix = -1;
        m_viewMatrix = -1;
        m_modelMatrix = -1;
        m_normalMatrix = -1;

        m_vertexShader = -1;
        m_fragmentShader = -1;
    }

    public ShaderProgram(String vertexShader, String fragmentShader, GL2 gl){
        this();
        createShaderProgram(vertexShader, fragmentShader, gl);
    }

    public int getProgram(){
        return m_program;
    }

    public void createShaderProgram(String vertexShader, String fragmentShader, GL2 gl){
        m_program = gl.glCreateProgram();

        m_vertexShader = gl.glCreateShader(gl.GL_VERTEX_SHADER);
        String[] vertexSource = { vertexShader };
        gl.glShaderSource(m_vertexShader, 1, vertexSource, null);
        gl.glCompileShader(m_vertexShader);

        m_fragmentShader = gl.glCreateShader(gl.GL_FRAGMENT_SHADER);
        String[] fragmentSource = { fragmentShader };
        gl.glShaderSource(m_fragmentShader, 1, fragmentSource, null);
        gl.glCompileShader(m_fragmentShader);

        gl.glAttachShader(m_program, m_vertexShader);
        gl.glAttachShader(m_program, m_fragmentShader);
        gl.glLinkProgram(m_program);

        gl.glUseProgram(m_program);

        m_projectionMatrix = gl.glGetUniformLocation(m_program, "projection");

        m_viewMatrix = gl.glGetUniformLocation(m_program, "view");

        m_modelMatrix = gl.glGetUniformLocation(m_program, "model");

        m_normalMatrix =  gl.glGetUniformLocation(m_program, "normalMatrix");
        int error = gl.glGetError();
        if(error != gl.GL_NO_ERROR)
            m_normalMatrix = -1;
    }

    public float[] getViewMatrix(){
        return m_vmatrix;
    }
    public float[] getModelMatrix(){
        return m_mmatrix;
    }
    public float[] getProjectionMatrix(){
        return m_pmatrix;
    }

    public void setUniformInt(GL2 gl, String uniformName, int data){
        gl.glUseProgram(m_program);
        gl.glUniform1i(gl.glGetUniformLocation(m_program, uniformName), data);
    }
    public void setUniformFloat(GL2 gl, String uniformName, float data){
        gl.glUseProgram(m_program);
        gl.glUniform1f(gl.glGetUniformLocation(m_program, uniformName), data);
    }
    public void setUniformVec3(GL2 gl, String uniformName, Vector3 data){
        gl.glUseProgram(m_program);
        gl.glUniform3f(gl.glGetUniformLocation(m_program, uniformName), data.getX(), data.getY(), data.getZ());
    }
    public void setUniformBoolean(GL2 gl, String uniformName, boolean data){
        if(data)
            setUniformInt(gl, uniformName, 1);
        else
            setUniformInt(gl, uniformName, 0);
    }

    public void updateProjectionMatrix(GL2 gl, float fov, float width, float height, float zNear, float zFar){
        m_pmatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        FloatUtil.makePerspective(m_pmatrix, 0, false, (float)Math.PI/180.0f * fov, width / height, zNear, zFar);
        gl.glUniformMatrix4fv(m_projectionMatrix, 1, false, m_pmatrix, 0);
    }

    public void updateProjectionMatrixOrtho(GL2 gl, float width, float height){
        m_pmatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        FloatUtil.makeOrtho(m_pmatrix, 0, false, 0, width, height, 0, -1, 1);
        gl.glUniformMatrix4fv(m_projectionMatrix, 1, false, m_pmatrix, 0);
    }

    public static Vector3 unProject(float[] proj, float[] view, Vector3 screen){
        Vector4 vec = new Vector4();
        float[] pos = new float[4], npos = new float[4], nnpos = new float[4];
        // Clip Coords
        vec.setX((2f*screen.getX()) / Framework.getInstance().getGameWidth()-1f);
        vec.setY(1f-((2f*screen.getY())/Framework.getInstance().getGameHeight()));
        vec.setZ(-1f);
        vec.setW(1f);

        // Eye Coords
        float[] invp = new float[16];
        FloatUtil.invertMatrix(proj, invp);
        FloatUtil.multMatrixVec(invp, vec.toFloatArray(), pos);
        npos = new float[]{ pos[0], pos[1], -1f, 0f };

        // World Coords
        float[] invv = new float[16];
        FloatUtil.invertMatrix(view, invv);
        FloatUtil.multMatrixVec(invv, npos, nnpos);

        return (new Vector3(nnpos[0], nnpos[1], nnpos[2])).normalize();
    }

    public void updateViewMatrix(GL2 gl, float[] mat){
        gl.glUniformMatrix4fv(m_viewMatrix, 1, false, mat, 0);
        m_vmatrix = mat;
        if(hasNormalMatrix()){
            updateNormalMatrix(gl, Framework.getModelMatrix().getMatrix(), Framework.getViewMatrix().getMatrix());
        }
    }

    public void updateModelMatrix(GL2 gl, float[] mat){
        gl.glUniformMatrix4fv(m_modelMatrix, 1, false, mat, 0);
        m_mmatrix = mat;
        if(hasNormalMatrix()){
            updateNormalMatrix(gl, Framework.getModelMatrix().getMatrix(), Framework.getViewMatrix().getMatrix());
        }
    }

    public void updateNormalMatrix(GL2 gl, float[] model,  float[] view){
        float[] normMatrix = FloatUtil.multMatrix(view, model);
        normMatrix = FloatUtil.invertMatrix(normMatrix, normMatrix);
        normMatrix = FloatUtil.transposeMatrix(normMatrix, normMatrix);
        gl.glUniformMatrix4fv(m_normalMatrix, 1, false, normMatrix, 0);
    }

    public boolean hasNormalMatrix(){
        return m_normalMatrix != -1;
    }

    public void activate(GL2 gl){
        gl.glUseProgram(getProgram());
    }
}
