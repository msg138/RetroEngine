package com.retro.engine.pcg;

import com.retro.engine.util.vector.Vector3;

/**
 * Created by Michael on 8/23/2016.
 */
public class SimplexNoise extends Noise {

    private static final int perm[] = {
            151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    };
    private static int hash(int i){
        return perm[i];
    }
    private static float grad(int hash, float x){
        int h = hash & 0x0F;
        float grad = 1.0f + (h & 7);
        if ((h& 8) != 0) grad = -grad;
        return (grad * x);
    }
    private static float grad(int hash, float x, float y){
        int h = hash & 0x3F;
        float u = h < 4 ? x : y;  // into 8 simple gradient directions,
        float v = h < 4 ? y : x;  // and compute the dot product with (x,y).
        return ((h & 1)!=0 ? -u : u) + ((h & 2)!=0 ? -2.0f*v : 2.0f*v);
    }
    private float noise(float x){
        float n0, n1;   // Noise contributions from the two "corners"

        // No need to skew the input space in 1D

        // Corners coordinates (nearest integer values):
        int i0 = (int)Math.floor(x);
        int i1 = i0 + 1;
        // Distances to corners (between 0 and 1):
        float x0 = x - i0;
        float x1 = x0 - 1.0f;

        // Calculate the contribution from the first corner
        float t0 = 1.0f - x0*x0;
//  if(t0 < 0.0f) t0 = 0.0f; // not possible
        t0 *= t0;
        n0 = t0 * t0 * grad(hash(i0), x0);

        // Calculate the contribution from the second corner
        float t1 = 1.0f - x1*x1;
//  if(t1 < 0.0f) t1 = 0.0f; // not possible
        t1 *= t1;
        n1 = t1 * t1 * grad(hash(i1%256), x1);

        // The maximum value of this noise is 8*(3/4)^4 = 2.53125
        // A factor of 0.395 scales to fit exactly within [-1,1]
        return 0.395f * (n0 + n1);
    }
    private float noise(float x, float y) {
        float n0, n1, n2;   // Noise contributions from the three corners

        // Skewing/Unskewing factors for 2D
        final float F2 = 0.366025403f;  // F2 = (sqrt(3) - 1) / 2
        final float G2 = 0.211324865f;  // G2 = (3 - sqrt(3)) / 6   = F2 / (1 + 2 * K)

        // Skew the input space to determine which simplex cell we're in
        float s = (x + y) * F2;  // Hairy factor for 2D
        float xs = x + s;
        float ys = y + s;
        int i = (int)Math.floor(xs);
        int j = (int)Math.floor(ys);

        // Unskew the cell origin back to (x,y) space
        float t = (i + j) * G2;
        float X0 = i - t;
        float Y0 = j - t;
        float x0 = x - X0;  // The x,y distances from the cell origin
        float y0 = y - Y0;

        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        int i1, j1;  // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) {   // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            i1 = 1;
            j1 = 0;
        } else {   // upper triangle, YX order: (0,0)->(0,1)->(1,1)
            i1 = 0;
            j1 = 1;
        }

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        float x1 = x0 - i1 + G2;            // Offsets for middle corner in (x,y) unskewed coords
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1.0f + 2.0f * G2;   // Offsets for last corner in (x,y) unskewed coords
        float y2 = y0 - 1.0f + 2.0f * G2;

        // Calculate the contribution from the first corner
        float t0 = 0.5f - x0*x0 - y0*y0;
        if (t0 < 0.0f) {
            n0 = 0.0f;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * grad(hash((i + hash(j%256))%256), x0, y0);
        }

        // Calculate the contribution from the second corner
        float t1 = 0.5f - x1*x1 - y1*y1;
        if (t1 < 0.0f) {
            n1 = 0.0f;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * grad(hash((i + hash(j%256))%256), x1, y1);
        }

        // Calculate the contribution from the third corner
        float t2 = 0.5f - x2*x2 - y2*y2;
        if (t2 < 0.0f) {
            n2 = 0.0f;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * grad(hash((i + hash(j%256))%256), x2, y2);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 45.23065f * (n0 + n1 + n2);
    }

    public float fractal(float x) {
        float output = 0.f;
        float frequency = getFrequency();
        float amplitude = 1;

        for (int i = 0; i < getOctaves(); i++) {
            output += (amplitude * noise(x * frequency));

            frequency *= 2;
            amplitude *= getPersistence();
        }

        return output;
    }

    public float fractal(float x, float y) {
        float output = 0.f;
        float frequency = getFrequency();
        float amplitude = 1;

        for (int i = 0; i < getOctaves(); i++) {
            output += (amplitude * noise(y * frequency + getSeed(), x * frequency + getSeed()));

            frequency *= 2;
            amplitude *= getPersistence();
        }

        return output;
    }

    @Override
    public NoiseSample getNoise2D(Vector3 v){
        return new NoiseSample(getAmplitude()*fractal(v.getX(), v.getY()), v);
    }

    public SimplexNoise(){
        super();
    }

    public SimplexNoise(float persistence, float frequency, float amplitude, int octaves, long seed){
        super(persistence, frequency, amplitude, octaves, seed);
    }

    @Override
    protected float getTotal(Vector3 v) {
        return 0;
    }
}
