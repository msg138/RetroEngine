package com.retro.engine.pcg;

import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 8/5/2016.
 */
public abstract class Noise {

    private float m_persistence;
    private float m_frequency;
    private float m_amplitude;
    private int m_octaves;
    private long m_seed;

    public Noise(){
        this(0,0,0,0,0);
    }
    public Noise(float persistence, float frequency, float amplitude, int octaves, long seed){
        m_persistence = persistence;
        m_frequency = frequency;
        m_amplitude = amplitude;
        m_octaves = octaves;
        m_seed = seed;
    }

    public NoiseSettings getNoiseSettings(){
        NoiseSettings ret = new NoiseSettings();
        ret.setFloatA(getPersistence());
        ret.setFloatB(getFrequency());
        ret.setFloatC(getAmplitude());
        ret.setIntA(getOctaves());
        ret.setLongA(getSeed());
        return ret;
    }

    public void setNoiseSettings(NoiseSettings noise){
        setPersistence(noise.getFloatA());
        setFrequency(noise.getFloatB());
        setAmplitude(noise.getFloatC());
        setOctaves(noise.getIntA());
        setSeed(noise.getLongA());
    }

    public NoiseSample getNoise1D(Vector3 v){
        return new NoiseSample(0, v);
    }
    public NoiseSample getNoise2D(Vector3 v){
        return new NoiseSample(0, v);
    }
    public NoiseSample getNoise3D(Vector3 v){
        return new NoiseSample(0, v);
    }

    protected abstract float getTotal(Vector3 v);

    public void set(float persistence, float frequency, float amplitude, int octaves, long seed){
        m_persistence = persistence;
        m_frequency = frequency;
        m_amplitude = amplitude;
        m_octaves = octaves;
        m_seed = seed;
    }

    public float getPersistence(){
        return m_persistence;
    }
    public float getFrequency(){
        return m_frequency;
    }
    public float getAmplitude(){
        return m_amplitude;
    }
    public int getOctaves(){
        return m_octaves;
    }
    public long getSeed(){
        return m_seed;
    }

    public void setPersistence(float p){
        m_persistence = p;
    }
    public void setFrequency(float f){
        m_frequency = f;
    }
    public void setAmplitude(float a){
        m_amplitude = a;
    }
    public void setOctaves(int o){
        m_octaves = o;
    }
    public void setSeed(long s){
        m_seed = s;
    }



    public static class NoiseSample{

        private float m_value;
        private Vector3 m_derivative;

        public NoiseSample(float val, Vector3 deriv){
            m_value = val;
            m_derivative = deriv;
        }
        public float getValue(){
            return m_value;
        }
        public Vector3 getDerivative(){
            return m_derivative;
        }
    }
}
