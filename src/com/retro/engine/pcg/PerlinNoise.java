package com.retro.engine.pcg;

import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 8/5/2016.
 */
public class PerlinNoise extends Noise {

    public PerlinNoise(){
        this(0,0,0,0,0);
    }
    public PerlinNoise(float persistence, float frequency, float amplitude, int octaves, long seed){
        super(persistence, frequency, amplitude, octaves, seed);
    }

    public float getHeight(Vector3 v){
        return getAmplitude() * getTotal(v);
    }

    protected float getTotal(Vector3 v){
        float t = 0.0f;
        float amplitude = 1;
        float freq = getFrequency();

        float x = v.getX();
        float y = v.getY();

        for(int k=0;k<getOctaves();k++)
        {
            t += getValue(y * freq + getSeed(), x * freq + getSeed()) * amplitude;
            amplitude *= getPersistence();
            freq *= 2;
        }
        return t;
    }
    protected float getTotal3(Vector3 v){
        float t = 0.0f;
        float amplitude = 1;
        float freq = getFrequency();

        float x = v.getX();
        float y = v.getY();
        float z = v.getZ();

        for(int k=0;k<getOctaves();k++)
        {
            t += getValue3(z * freq + getSeed(), y * freq + getSeed(), x * freq + getSeed()) * amplitude;
            amplitude *= getPersistence();
            freq *= 2;
        }
        return t;
    }
    private float getValue(float x, float y){
        int xint = (int)x;
        int yint = (int)y;
        float xfrac = x - xint;
        float yfrac = y - yint;

        float n01 = getNoise(xint - 1, yint - 1);
        float n02 = getNoise(xint + 1, yint - 1);
        float n03 = getNoise(xint - 1, yint + 1);
        float n04 = getNoise(xint + 1, yint + 1);
        float n05 = getNoise(xint - 1, yint);
        float n06 = getNoise(xint + 1, yint);
        float n07 = getNoise(xint, yint - 1);
        float n08 = getNoise(xint, yint + 1);
        float n09 = getNoise(xint, yint);

        float n12 = getNoise(xint + 2, yint - 1);
        float n14 = getNoise(xint + 2, yint + 1);
        float n16 = getNoise(xint + 2, yint);

        float n23 = getNoise(xint - 1, yint + 2);
        float n24 = getNoise(xint + 1, yint + 2);
        float n28 = getNoise(xint, yint + 2);

        float n34 = getNoise(xint + 2, yint + 2);

        float x0y0 = 0.0625f*(n01 + n02 + n03 + n04) + 0.125f*(n05 + n06 + n07 + n08) + 0.25f*(n09);
        float x1y0 = 0.0625f*(n07 + n12 + n08 + n14) + 0.125f*(n09 + n16 + n02 + n04) + 0.25f*(n06);
        float x0y1 = 0.0625f*(n05 + n06 + n23 + n24) + 0.125f*(n03 + n04 + n09 + n28) + 0.25f*(n08);
        float x1y1 = 0.0625f*(n09 + n16 + n28 + n34) + 0.125f*(n08 + n14 + n06 + n24) + 0.25f*(n04);

        //interpolate between those values according to the x and y fractions
        float v1 = getInterpolate(x0y0, x1y0, xfrac); //interpolate in x direction (y)
        float v2 = getInterpolate(x0y1, x1y1, xfrac); //interpolate in x direction (y+1)
        float fin = getInterpolate(v1, v2, yfrac);  //interpolate in y direction

        return fin;
    }
    private float getValue3(float x, float y, float z){
        int xint = (int)x;
        int yint = (int)y;
        int zint = (int)z;
        float xfrac = x - xint;
        float yfrac = y - yint;
        float zfrac = z - zint;

        float n01 = getNoise(xint - 1, yint - 1, zint - 1);
        float n02 = getNoise(xint + 1, yint - 1, zint - 1);
        float n03 = getNoise(xint - 1, yint + 1, zint - 1);
        float n04 = getNoise(xint + 1, yint + 1, zint - 1);
        float n05 = getNoise(xint - 1, yint, zint - 1);
        float n06 = getNoise(xint + 1, yint, zint);
        float n07 = getNoise(xint, yint - 1, zint - 1);
        float n08 = getNoise(xint, yint + 1, zint);
        float n09 = getNoise(xint, yint, zint - 1);

        float n12 = getNoise(xint + 2, yint - 1, zint - 1);
        float n14 = getNoise(xint + 2, yint + 1, zint - 1);
        float n16 = getNoise(xint + 2, yint, zint - 1);

        float n23 = getNoise(xint - 1, yint + 2, zint - 1);
        float n24 = getNoise(xint + 1, yint + 2, zint);
        float n28 = getNoise(xint, yint + 2, zint - 1);

        float n34 = getNoise(xint + 2, yint + 2, zint - 2);

        float x0y0 = 0.0625f*(n01 + n02 + n03 + n04) + 0.125f*(n05 + n06 + n07 + n08) + 0.25f*(n09);
        float x1y0 = 0.0625f*(n07 + n12 + n08 + n14) + 0.125f*(n09 + n16 + n02 + n04) + 0.25f*(n06);
        float x0y1 = 0.0625f*(n05 + n06 + n23 + n24) + 0.125f*(n03 + n04 + n09 + n28) + 0.25f*(n08);
        float x1y1 = 0.0625f*(n09 + n16 + n28 + n34) + 0.125f*(n08 + n14 + n06 + n24) + 0.25f*(n04);

        //interpolate between those values according to the x and y fractions
        float v1 = getInterpolate(x0y0, x1y0, xfrac); //interpolate in x direction (y)
        float v2 = getInterpolate(x0y1, x1y1, xfrac); //interpolate in x direction (y+1)
        float fin = getInterpolate(v1, v2, yfrac);  //interpolate in y direction
        float v3 = getInterpolate(v1, v2, zfrac); //interpolate in x direction (y+1)
        float f = getInterpolate(fin, v3, xfrac); //interpolate in x direction (y+1)

        return f;
    }

    private float getInterpolate(float x, float y, float a){
        float negA = 1.0f - a;
        float negASqr = negA * negA;
        float fac1 = 3.0f * (negASqr)-2.0f * (negASqr * negA);
        float aSqr = a * a;
        float fac2 = 3.0f * aSqr - 2.0f * (aSqr * a);

        return x * fac1 + y * fac2; //add the weighted factors
    }

    private float getNoise(int x, int y){
        int n = x + y * 57;
        n = (n << 13) ^ n;
        int t = (n * (n * n * 15731 + 78922) + 1376312588) & 0x7fffffff;
        return (float)(1.0 - (float)t * 0.93132257461547855625e-9);
    }
    private float getNoise(int x, int y, int z){
        int n = x + y * 57 + z*32;
        n = (n << 13) ^ n;
        int t = (n * (n * n * 15731 + 78921) + 137632589) & 0x7fffffff;
        return (float)(1.0 - (float)t * 0.93132257415478515625e-9);
    }


    @Override
    public NoiseSample getNoise2D(Vector3 v){
        return new NoiseSample(getAmplitude() * getTotal(v), v);
    }
    @Override
    public NoiseSample getNoise3D(Vector3 v){
        return new NoiseSample(getTotal3(v), v);
    }
}
