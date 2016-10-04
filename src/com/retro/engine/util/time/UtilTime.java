package com.retro.engine.util.time;

import java.util.Date;

/**
 * Created by Michael on 7/15/2016.
 */
public class UtilTime {

    /**
     * Returns a nicely formatted string with the time in it.
     * In the string $s = Seconds
     *               $m = minutes
     *               $h = hours
     * @param format The string to be formatted with the time
     * @return New string with the formatted information.
     */
    public static String getTimeFormatted(String format){
        String ret = format;
        Date d = new Date();
        {
            int ind = ret.indexOf("$s");
            if(ind != -1)
                ret = ret.substring(0, ind) + d.getSeconds() + ret.substring(ind+2);
        }
        {
            int ind = ret.indexOf("$m");
            if(ind != -1)
                ret = ret.substring(0, ind) + d.getMinutes() + ret.substring(ind+2);
        }
        {
            int ind = ret.indexOf("$h");
            if(ind != -1)
                ret = ret.substring(0, ind) + d.getHours() + ret.substring(ind+2);
        }
        return ret;
    }

    public static long nanoTime(){
        return java.lang.System.nanoTime();
    }
    public static long mill(){
        return java.lang.System.currentTimeMillis();
    }
}
