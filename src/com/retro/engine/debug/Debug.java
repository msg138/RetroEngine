package com.retro.engine.debug;

import com.retro.engine.util.string.UtilString;

/**
 * Created by Michael on 7/15/2016.
 */
public class Debug {

    private static String m_prefix = "[Debug] &t - ";
    private static DebugMode m_currentDebugMode = DebugMode.DEBUG_ALL;

    public static void out(Object output, DebugMode importance){

        String o = m_prefix + output.toString();

        o = UtilString.formatAll(o);

        if(importance.getMode() <= m_currentDebugMode.getMode())
        {
            java.lang.System.out.println(o);
            // TODO Output to file or something. like a log.
        }
    }

    public static void out(Object output){
        out(output, m_currentDebugMode);
    }

    public static void setDebugMode(DebugMode dm){
        m_currentDebugMode = dm;
    }
}
