package com.retro.engine.util.string;

/**
 * Created by Michael on 7/15/2016.
 */
public abstract class Formatter {

    // Identifier used when replacing data within a String
    protected String m_identifier;

    /**
     * Creates a new Formatter with the chosen identifier.
     * @param identifier The key to be replaced in strings with other information.
     */
    public Formatter(String identifier){
        m_identifier = identifier;
    }

    /**
     * Get the identifier of the formatter
     * @return m_identifier
     */
    public String getIdentifier(){
        return m_identifier;
    }

    /**
     * To be overridden in the extending formatter.
     * @param msg Message to be formatted
     * @return The formatted message, depends on the extending class.
     */
    public abstract String format(String msg);

}
