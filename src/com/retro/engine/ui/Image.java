package com.retro.engine.ui;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.retro.engine.Framework;
import com.retro.engine.component.*;
import com.retro.engine.debug.Debug;
import com.retro.engine.defaultcomponent.ComponentColor;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.defaultcomponent.ComponentRotation;
import com.retro.engine.entity.Entity;
import com.retro.engine.model.Matrix;
import com.retro.engine.model.RawModel;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.repository.ImageRepository;
import com.retro.engine.util.entity.UtilEntity;
import sun.awt.image.PNGImageDecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static jogamp.opengl.util.av.EGLMediaPlayerImpl.TextureType.GL;

/**
 * Created by Michael on 7/30/2016.
 */
public class Image {
    protected BufferedImage m_image;
    protected String m_imageName;
    protected float m_width=0, m_height=0;
    protected boolean m_flippedH = false, m_flippedV = false;

    protected int m_texture;

    protected int m_curVertAmount = 0;

    protected String m_fname;

    protected boolean m_3dSpace = false;
    protected boolean m_lookAtCamera = false;
    protected boolean m_lookAtCameraWithoutX = false;

    protected boolean m_loaded = false;

    protected volatile IntBuffer m_buffers = null;
    protected volatile FloatBuffer m_vertexFB = null;
    protected volatile FloatBuffer m_texFB = null;
    protected volatile FloatBuffer m_colorFB = null;
    protected volatile IntBuffer m_vertexArray = null;
    protected float[] m_vertices;
    protected boolean m_goodBuffers = false;

    protected ComponentPosition m_spriteLoc;
    protected ComponentPosition m_rotationPoint;

    protected ComponentColor m_tintColor;

    public Image(){
        m_width = 0;
        m_height = 0;
        m_loaded = false;

        m_tintColor = new ComponentColor(255, 255, 255, 255);

        m_spriteLoc = new ComponentPosition(0,0, 1, 1);
        m_rotationPoint = new ComponentPosition(0,0, 0);
    }
    public Image(String fname)
    {
        this();
        loadImage(fname);
    }
    public Image(String fname, String aName)
    {
        this();
        loadImage(fname, aName);
    }
    public Image(String fname, String aName, GL2 gl)
    {
        this();
        loadImage(fname, aName);
        if(m_image != null)
            loadImageGL(m_image, gl);
    }
    public Image(String fname, GL2 gl){
        this();
        loadImage(fname, gl);
    }

    public void setColor(ComponentColor c){
        m_tintColor = c;
    }
    public ComponentColor getColor(){
        return m_tintColor;
    }

    public void setSpriteLocation(ComponentPosition sloc){
        m_spriteLoc = sloc;
        m_goodBuffers = false;
    }
    public void setSpriteLocationPixel(ComponentPosition sloc){
        m_spriteLoc = new ComponentPosition(sloc.getX() / m_image.getWidth(), sloc.getY() / m_image.getHeight(),
                    sloc.getWidth() / m_image.getWidth(), sloc.getHeight() / m_image.getHeight());
    }

    public void setRotationPoint(ComponentPosition pos){
        m_rotationPoint = pos;
    }

    public String getImageName(){
        return m_imageName;
    }

    public boolean goodBuffers(){
        return m_goodBuffers;
    }

    public boolean is3dSpace(){
        return m_3dSpace;
    }

    public void set3dSpace(boolean d){
        m_3dSpace = d;
    }
    public boolean isLookAtCamera(){
        return m_lookAtCamera;
    }
    public boolean isLookAtCameraWithoutX(){ return m_lookAtCameraWithoutX; }

    public void setLookAtCamera(boolean d){
        m_lookAtCamera = d;
    }
    public void setLookAtCameraWithoutX(boolean d){
        m_lookAtCameraWithoutX = d;
    }

    public BufferedImage getImage()
    {
        return m_image;
    }

    public float getWidth()
    {
        return m_width;
    }
    public float getHeight()
    {
        return m_height;
    }

    public void loadImage(String fname, GL2 gl){
        loadImage(fname);
        loadImageGL(m_image, gl);
    }

    public void loadImage(String fname){
        loadImage(fname, fname);
    }

    public void loadImage(String fname, String aName)
    {
        m_fname = aName;
        Debug.out("image loading" + fname +" , " + aName);
        try{
            if(ImageRepository.getInstance().imageExists(aName)) {
                m_image = ImageRepository.getInstance().getExistingImage(aName);
                Debug.out("image already exists");
            }else {
                m_image = ImageIO.read(new File(fname));
                ImageRepository.getInstance().addImage(aName, m_image);
                Debug.out("image created");
            }
            m_width = m_image.getWidth();
            m_height = m_image.getHeight();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        m_imageName = aName;
    }

    public int getTexture(){
        return m_texture;
    }

    public void resetBuffers(){
        m_goodBuffers = false;
    }

    public void loadImageGL(BufferedImage img, GL2 gl){
        if(ImageRepository.getInstance().imageExistsId(m_fname))
        {
            m_texture = ImageRepository.getInstance().getExistingImageId(m_fname);
            m_fname = "";
            return;
        }
        Debug.out(m_fname);
        //int[] pixels = new int[img.getWidth() * img.getHeight()];
        //img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

        ByteBuffer buffer = ByteBuffer.allocate(img.getWidth() * img.getHeight() * 4);
        Color c;

        for(int y=0;y<img.getHeight();y++)
        {
            for(int x=0;x<img.getWidth();x++)
            {
                c = new Color(img.getRGB(x, y), true);
                buffer.put((byte) c.getRed());
                buffer.put((byte) c.getGreen());
                buffer.put((byte) c.getBlue());
                buffer.put((byte) c.getAlpha());
            }
        }
        buffer.flip();

        IntBuffer tmp = IntBuffer.allocate(1);
        gl.glGenTextures(1, tmp);
        gl.glBindTexture(gl.GL_TEXTURE_2D, tmp.get(0));

        //Setup wrap mode
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER,gl.GL_NEAREST);

        gl.glTexEnvi(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_BLEND);

        //Send texel data to OpenGL
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGBA, img.getWidth(), img.getHeight(), 0, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, buffer);

        m_texture = tmp.get(0);

        ImageRepository.getInstance().addImageId(m_fname, m_texture);
        m_fname = "";
    }

    public void flipImageVert()
    {
        AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
        tx.translate(0, -m_height);
        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        m_image = op.filter(m_image,null);
        m_flippedV = !m_flippedV;
    }
    public void flipImageHorz()
    {
        AffineTransform tx = AffineTransform.getScaleInstance(-1,1);
        tx.translate(0, -m_height);
        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        m_image = op.filter(m_image,null);
        m_flippedH = !m_flippedH;
    }

    public float[] getVertices(Entity owner){
        Rectangle rec = UtilEntity.getEntityBounds(owner);
        Rectangle rect = new Rectangle(0, 0, (int)rec.getWidth(), (int)rec.getHeight());
        if(!is3dSpace())
            return new float[]{
                    rect.x, rect.y + rect.height,
                    rect.x + rect.width, rect.y + rect.height,
                    rect.x + rect.width, rect.y,

                    rect.x + rect.width, rect.y,
                    rect.x, rect.y,
                    rect.x, rect.y + rect.height
            };

        ComponentPosition pos;
        if(owner.has(ComponentPosition.class))
            pos = (ComponentPosition)owner.get(ComponentPosition.class);
        else
            pos = new ComponentPosition(rect.x, rect.y, rect.width, rect.height);
        float sw = pos.getWidth();
        float sh = pos.getHeight();
        float x = -sw/2.0f, y = -sh/2.0f, z = 0;
        return new float[]{
               x, y, z,
                x + sw, y, z,
                x + sw, y + sh, z,

                x + sw, y + sh, z,
                x, y + sh, z,
                x, y, z,
        };
    }

    public float[] getTexCoords(){
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
        };
    }

    public float[] getColorCoords(){
        return new float[]{
                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),

                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
                m_tintColor.getRedf(), m_tintColor.getGreenf(), m_tintColor.getBluef(), m_tintColor.getAlphaf(),
        };
    }

    public void updateBufferData(GL2 gl, Entity owner){

        /**
        m_vertexFB.clear();
        m_vertexFB.compact();
        m_vertexFB.put(new float[m_vertexFB.capacity()]);
        m_texFB.clear();
        m_texFB.compact();
        m_texFB.put(new float[m_texFB.capacity()]);
        */
        float[] vert = getVertices(owner);
        float[] tex = getTexCoords();
        float[] colors = getColorCoords();
        m_curVertAmount = vert.length;
        m_vertexFB = FloatBuffer.wrap(vert);
        m_texFB = FloatBuffer.wrap(tex);

        m_colorFB = FloatBuffer.wrap(colors);

        m_buffers.clear();

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* m_curVertAmount, m_vertexFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* tex.length, m_texFB, gl.GL_STATIC_DRAW);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* colors.length, m_colorFB, gl.GL_STATIC_DRAW);

        gl.glBindVertexArray(m_vertexArray.get(0));
        m_buffers.clear();

        // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(0));
        if(is3dSpace())
            gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 0, 0);
        else
            gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);

        gl.glEnableVertexAttribArray(2);// corresponds to 2 in the shader
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glVertexAttribPointer(2, 2, gl.GL_FLOAT, false, 0, 0);

        gl.glEnableVertexAttribArray(1);// corresponds to 1 in the shader
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glVertexAttribPointer(1, 4, gl.GL_FLOAT, false, 0, 0);

        // Clear the bound array.
        gl.glBindVertexArray(0);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
    }

    public void generateGLBuffers(GL2 gl, Entity owner){
        /*if(m_vertexArray != null && m_vertexArray.hasArray()){
            gl.glDeleteVertexArrays(1, m_vertexArray);
        }*/
        //m_vertices = getVertices(owner);
        //deleteBuffersIfNeeded(gl);
        if(m_buffers != null){
            updateBufferData(gl, owner);
            return;
        }

        float[] vert = getVertices(owner);
        float[] tex = getTexCoords();
        float[] colors = getColorCoords();
        m_curVertAmount = vert.length;
        m_vertexFB = Buffers.newDirectFloatBuffer(vert);
        m_texFB = Buffers.newDirectFloatBuffer(tex);
        m_colorFB = FloatBuffer.wrap(colors);

        m_buffers = Buffers.newDirectIntBuffer(3);
        gl.glGenBuffers(3, m_buffers);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(0));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* vert.length, m_vertexFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* tex.length, m_texFB, gl.GL_STATIC_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, Buffers.SIZEOF_FLOAT* colors.length, m_colorFB, gl.GL_STATIC_DRAW);

        m_vertexArray = IntBuffer.allocate(1);
        gl.glGenVertexArrays(1, m_vertexArray);
        gl.glBindVertexArray(m_vertexArray.get(0));

        // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(0));
        if(is3dSpace())
            gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 0, 0);
        else
            gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);

        gl.glEnableVertexAttribArray(2);// corresponds to 2 in the shader
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(1));
        gl.glVertexAttribPointer(2, 2, gl.GL_FLOAT, false, 0, 0);

        gl.glEnableVertexAttribArray(1);// corresponds to 1 in the shader
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, m_buffers.get(2));
        gl.glVertexAttribPointer(1, 4, gl.GL_FLOAT, false, 0, 0);

        // Clear the bound array.
        gl.glBindVertexArray(0);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);

        m_goodBuffers = true;
    }
    public void render2D(GL2 gl, Entity owner){
        if(!m_fname.isEmpty())
            loadImageGL(m_image, gl);

        ShaderLoader.defaultShaderImage2d.activate(gl);
        ComponentPosition pos = ((ComponentPosition)owner.get(ComponentPosition.class));
        if(pos == null)
            pos = new ComponentPosition();
        Framework.getModelMatrix().pushMatrix();
        Framework.getModelMatrix().translatef(-m_rotationPoint.getX()/2 - pos.getX()/2,-m_rotationPoint.getY()/2 - pos.getY()/2,-m_rotationPoint.getZ()/2 - pos.getZ()/2);
        if(owner.has(ComponentRotation.class)){
            ((ComponentRotation)owner.get(ComponentRotation.class)).applyRotation(Framework.getModelMatrix());
        }
        Framework.getModelMatrix().translatef(m_rotationPoint.getX()/2 + pos.getX()/2,m_rotationPoint.getY()/2 + pos.getY()/2,m_rotationPoint.getZ()/2 + pos.getZ()/2);
        if(owner.has(ComponentPosition.class)){
            ((ComponentPosition)owner.get(ComponentPosition.class)).applyTranslation(Framework.getModelMatrix());
        }
        Framework.getModelMatrix().activateM(gl, ShaderLoader.defaultShaderImage2d);

        if(!goodBuffers())
            generateGLBuffers(gl, owner);

        gl.glDisable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_TEXTURE_2D);

        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, getTexture());
        ShaderLoader.defaultShaderImage2d.setUniformInt(gl, "tex", 0);

        gl.glBindVertexArray(m_vertexArray.get(0));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_curVertAmount/2);

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
        Framework.getModelMatrix().translatef(-m_rotationPoint.getX()/2,-m_rotationPoint.getY()/2,-m_rotationPoint.getZ()/2);
        if(owner.has(ComponentRotation.class)){
            ((ComponentRotation)owner.get(ComponentRotation.class)).applyRotation(Framework.getModelMatrix());
        }
        Framework.getModelMatrix().translatef(m_rotationPoint.getX()/2,m_rotationPoint.getY()/2,m_rotationPoint.getZ()/2);
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
        Framework.getModelMatrix().activateM(gl, ShaderLoader.defaultShaderImage3d);

        if(!goodBuffers())
            generateGLBuffers(gl, owner);

        gl.glEnable(gl.GL_TEXTURE_2D);

        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, getTexture());
        ShaderLoader.defaultShaderImage3d.setUniformInt(gl, "tex", 0);

        gl.glBindVertexArray(m_vertexArray.get(0));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, m_curVertAmount / 3);

        // unbind everything.
        gl.glBindVertexArray(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);

        gl.glDisable(gl.GL_TEXTURE_2D);

        ShaderLoader.getCurrentShader(gl).activate(gl);
        Framework.getModelMatrix().popMatrix();
    }

    public void kill(){
        if(m_vertexArray != null)
            Framework.getInstance().addDeadVAO(m_vertexArray);
        if(m_buffers != null)
            Framework.getInstance().addDeadVBO(m_buffers);
    }

    @Override
    protected void finalize() throws java.lang.Throwable{
        super.finalize();/*
        if(m_vertexArray != null)
            Framework.getInstance().addDeadVAO(m_vertexArray);
        if(m_buffers != null)
            Framework.getInstance().addDeadVBO(m_buffers);*/
    }
}
