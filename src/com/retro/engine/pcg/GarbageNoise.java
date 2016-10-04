package com.retro.engine.pcg;

import com.retro.engine.util.vector.Vector3;

import java.util.Random;

/**
 * Created by Michael on 8/24/2016.
 */
public class GarbageNoise extends Noise {

    private Random m_r;
    // Develop a truly random with interpolation noise generator.

    public GarbageNoise(){
        m_r = new Random();
    }

    public float getTotal(Vector3 v){
        return 0;
    }


}
