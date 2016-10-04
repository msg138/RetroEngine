package com.retro.engine.model;

import com.jogamp.opengl.GL2;
import com.retro.engine.Framework;
import com.retro.engine.debug.Debug;
import com.retro.engine.defaultcomponent.ComponentPosition;
import com.retro.engine.defaultcomponent.ComponentRotation;
import com.retro.engine.defaultcomponent.ComponentScale;
import javafx.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 8/16/2016.
 */
public class AnimatedRawModel extends RawModel {

    public static final int c_animationLoop = 1;
    public static final int c_animationOnce = 2;

    // These only used when the ANimation is automated.
    private float m_frameSpeed;// Allow a float for short or fast speeds.
    private float m_animationFrame;// Same as currentframe but allows decimal.
    private int m_animationMode;
    private int m_animationLoops;// keep track of how many times animation loops. Useful for stopping after time.

    private int m_maxFrames;
    private int m_currentFrame;

    private boolean m_interpolate;

    private List<AnimationFrame> m_frames;
    private List<RawModel> m_models;

    private List<String> m_modelNames;

    public AnimatedRawModel(int maxFrames){
        m_maxFrames = maxFrames;
        m_frameSpeed = 1;
        m_animationLoops = -1;
        m_animationFrame = 0;
        m_animationMode = c_animationOnce;
        m_currentFrame = 0;
        m_interpolate = false;
        m_frames = new ArrayList<>();
        m_models = new ArrayList<>();
        m_modelNames = new ArrayList<>();
        for(int i=0;i<maxFrames;i++)
        {
            addFrame();
        }
    }

    public void play(){
        m_animationLoops = 0;
        m_animationFrame = 0;
    }
    public void stop(){
        m_animationLoops = -1;
    }

    public void setAnimationMode(int mode){
        m_animationMode = mode;
    }
    public void setAnimationSpeed(float sp){
        m_frameSpeed = sp;
    }
    public int getAnimationMode(){
        return m_animationMode;
    }
    public float getAnimationSpeed(){
        return m_frameSpeed;
    }
    public int getAnimationLoops(){
        return m_animationLoops;
    }

    public boolean isInterpolated(){
        return m_interpolate;
    }
    public void setInterpolated(boolean i){
        m_interpolate = i;
    }

    public int getModelCount(){
        return m_models.size();
    }
    public int getMaxFrames(){
        if(m_interpolate)
            return m_maxFrames;
        return m_frames.size();
    }
    public void setMaxFrames(int f){
        m_maxFrames = f;
    }

    public void setCurrentFrame(int f){
        if(m_interpolate && f < m_maxFrames)
            m_currentFrame = f;
        if(f < m_frames.size())
            m_currentFrame = f;
    }
    public int getCurrentFrame(){
        return m_currentFrame;
    }

    public List<String > getModelNames(){
        return m_modelNames;
    }

    public boolean isAnimationPlaying(){
        return m_animationLoops != -1;
    }

    public void addFrame(){
        AnimationFrame frame = new AnimationFrame(m_models.size());
        m_frames.add(frame);
    }

    public void cloneFrame(int fnum){
        AnimationFrame clone = new AnimationFrame(m_models.size());
        AnimationFrame frame = getFrame(fnum);
        for(int i=0;i<m_models.size();i++)
        {
            ComponentPosition pos = frame.getPosition(i);
            ComponentRotation rot = frame.getRotation(i);
            ComponentScale sca = frame.getScale(i);

            ComponentPosition pos2 = clone.getPosition(i);
            ComponentRotation rot2 = clone.getRotation(i);
            ComponentScale sca2 = clone.getScale(i);

            pos2.setX(pos.getX());
            pos2.setY(pos.getY());
            pos2.setZ(pos.getZ());
            rot2.setRotationX(rot.getRotationX());
            rot2.setRotationY(rot.getRotationY());
            rot2.setRotationZ(rot.getRotationZ());
            sca2.setScaleX(sca.getScaleX());
            sca2.setScaleY(sca.getScaleY());
            sca2.setScaleZ(sca.getScaleZ());
        }
        m_frames.add(clone);
    }

    public void removeFrame(int f){
        if(f < m_frames.size())
            m_frames.remove(f);
    }

    public AnimationFrame getFrame(int f){
        if(f >= m_frames.size())
            return null;
        return m_frames.get(f);
    }
    public RawModel getModel(int m){
        if(m >= m_models.size())
            return null;
        return m_models.get(m);
    }

    public void addModel(RawModel model, String modelName){
        m_models.add(model);
        for(AnimationFrame frame : m_frames){
            frame.addModel();
        }
        m_modelNames.add(modelName);
    }

    public void setModel(int id, RawModel model){
        m_models.set(id, model);
    }

    @Override
    public void render(GL2 gl){
        Matrix m = Framework.getModelMatrix();
        m.pushMatrix();

        // Do animation things if we need to.
        if(isAnimationPlaying()){
            m_animationFrame += m_frameSpeed;
            if(Math.floor(m_animationFrame) >= getMaxFrames())
            {
                m_animationFrame = 0;
                m_animationLoops ++;
            }

            m_currentFrame = (int)Math.floor(m_animationFrame);

            if(m_animationLoops > 0 && m_animationMode == c_animationOnce)
                stop();
        }

        if(m_currentFrame >= 0 && m_models.size() > 0){
            if(m_interpolate){

                for(int i=0;i<m_models.size();i++){
                    m.pushMatrix();
                    interpolate(i, gl, m);
                    m_models.get(i).render(gl);
                    m.popMatrix();
                }

            }else if(m_currentFrame < m_frames.size())
                for(int i=0;i<m_models.size();i++){
                    m.pushMatrix();
                    m_frames.get(m_currentFrame).activateTransformation(i, gl, m);
                    m_models.get(i).render(gl);
                    m.popMatrix();
                }
        }

        m.popMatrixM(gl);
    }

    private void interpolate(int m, GL2 gl, Matrix mat){
        float frameTime = (float)m_maxFrames / (float)m_frames.size();
        float cf = (float)m_currentFrame / frameTime;
        int frame = (int)Math.floor(cf);
        // get percentage of what has passed ; cf
        cf = cf % 1;
        // Get first frame
        AnimationFrame f1 = getFrame(frame);
        // Get second frame
        AnimationFrame f2 = f1;
        if(frame + 1 < m_frames.size())
            f2 = getFrame(frame+1);
        else {
            m_currentFrame = m_maxFrames - 1;
            m_animationFrame = m_maxFrames;
        }
        // Get difference in frames
        float dx = f1.getPosition(m).getX() - f2.getPosition(m).getX();
        float dy = f1.getPosition(m).getY() - f2.getPosition(m).getY();
        float dz = f1.getPosition(m).getZ() - f2.getPosition(m).getZ();
        float drx = f1.getRotation(m).getRotationX() - f2.getRotation(m).getRotationX();
        float dry = f1.getRotation(m).getRotationY() - f2.getRotation(m).getRotationY();
        float drz = f1.getRotation(m).getRotationZ() - f2.getRotation(m).getRotationZ();
        float dsx = f1.getScale(m).getScaleX() - f2.getScale(m).getScaleX();
        float dsy = f1.getScale(m).getScaleY() - f2.getScale(m).getScaleY();
        float dsz = f1.getScale(m).getScaleZ() - f2.getScale(m).getScaleZ();

        // Go the shortest route to complete the tween.
        float rx = -drx * cf;
        if(rx > Math.toRadians(180))
            rx = (float)-(2*Math.PI - rx);
        float ry = -dry * cf;
        if(ry > Math.toRadians(180))
            ry = (float)-(2*Math.PI - ry);
        float rz = -drz * cf;
        if(rz > Math.toRadians(180))
            rz = (float)-(2*Math.PI - rz);

        // Multiply by ratio to get the end result.
        ComponentRotation newRot = new ComponentRotation(f1.getRotation(m).getRotationX() + rx,
                f1.getRotation(m).getRotationY() + ry,
                f1.getRotation(m).getRotationZ() + rz);
        ComponentPosition newPos = new ComponentPosition(f1.getPosition(m).getX() - dx * cf,
                f1.getPosition(m).getY() - dy * cf,
                f1.getPosition(m).getZ() - dz * cf);
        ComponentScale newSca = new ComponentScale(f1.getScale(m).getScaleX() - dsx * cf,
                f1.getScale(m).getScaleY() - dsy * cf,
                f1.getScale(m).getScaleZ() - dsz * cf);

        newPos.applyTranslation(mat);
        newRot.applyRotation(mat);
        newSca.applyScale(mat);
        mat.activateM(gl);
    }

    public static class AnimationFrame{

        private List<ComponentRotation> m_rotations;
        private List<ComponentPosition> m_positions;
        private List<ComponentScale> m_scales;

        public AnimationFrame(List<ComponentRotation> rots, List<ComponentPosition> poss, List<ComponentScale> scas){
            m_rotations = rots;
            m_positions = poss;
            m_scales = scas;
        }

        public AnimationFrame(int modelCount) {
            m_rotations = new ArrayList<>();
            m_positions = new ArrayList<>();
            m_scales = new ArrayList<>();
            for (int i = 0; i < modelCount; i++)
            {
                m_rotations.add(new ComponentRotation());
                m_positions.add(new ComponentPosition());
                m_scales.add(new ComponentScale());
            }
        }

        public ComponentRotation getRotation(int model){
            if(model >= m_rotations.size())
                return new ComponentRotation();
            return m_rotations.get(model);
        }

        public ComponentPosition getPosition(int model){
            if(model >= m_positions.size())
                return new ComponentPosition();
            return m_positions.get(model);
        }

        public ComponentScale getScale(int model){
            if(model >= m_scales.size())
                return new ComponentScale();
            return m_scales.get(model);
        }


        public void addModel(){
            m_rotations.add(new ComponentRotation());
            m_positions.add(new ComponentPosition());
            m_scales.add(new ComponentScale());
        }
        public void removeModel(int id){
            m_rotations.remove(id);
            m_positions.remove(id);
            m_scales.remove(id);
        }

        public void activateTransformation(int model, GL2 gl, Matrix m){
            ComponentRotation rot = getRotation(model);
            ComponentPosition pos = getPosition(model);
            ComponentScale sca = getScale(model);
            //m.translatef(-pos.getX()/2, -pos.getY()/2, -pos.getZ()/2);
            pos.applyTranslation(m);
            rot.applyRotation(m);
            sca.applyScale(m);
            m.activateM(gl);
            //m.translatefM(gl, pos.getX()*1.5f, pos.getY()*1.5f, pos.getZ()*1.5f);
        }
    }
}
