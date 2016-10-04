package com.retro.engine.util.list;

import java.lang.reflect.Array;

/**
 * Created by Michael on 7/31/2016.
 */
public class UtilArray {

    public static <T> T[] concat(T[] a, T[] b){
        int aLen = a.length;
        int bLen = b.length;

        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static float[] concat(float[] a, float[] b){
        int aLen = a.length;
        int bLen = b.length;

        float[] c = new float[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
