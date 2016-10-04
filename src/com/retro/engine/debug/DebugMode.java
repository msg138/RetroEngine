package com.retro.engine.debug;

/**
 * Created by Michael on 7/15/2016.
 */

public enum DebugMode{

    // Enumeration of the possible debug modes
    DEBUG_DEBUG(101),
    DEBUG_ALL(100),
    DEBUG_MISC(99),
    DEBUG_NOTICE(50),
    DEBUG_ERROR(0);

    // The debug mode of the enumeration
    private int m_debugMode;

    /**
     * Creates a debugMode to be used when outputting in the debug class.
     * @param dm The number set to debug. The higher the more Verbose.
     */
    private DebugMode(int dm){
        m_debugMode = dm;
    }

    /**
     * Returns the debugmode of the enumeration
     * @return m_debugMode
     */
    public int getMode(){
        return m_debugMode;
    }
}