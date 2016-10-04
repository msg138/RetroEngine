package com.retro.engine.util.string;

import com.retro.engine.util.time.UtilTime;
/**
 * Created by Michael on 7/15/2016.
 */
public class FormatterTime extends Formatter{

    /**
     * Creates a new Time Formatter, to put time into strings.
     * Uses identifier: &t
     */
    public FormatterTime(){
        super("&t");
    }

    /**
     * Replace the identifiers with the current time.
     * @param msg Message before formatting.
     * @return Message after formatting.
     */
    public String format(String msg){
        for(int a=msg.indexOf(getIdentifier()); a != -1; a = msg.indexOf(getIdentifier())){
            msg = msg.substring(0, a) + UtilTime.getTimeFormatted("[$h:$m:$s]") + msg.substring(a + getIdentifier().length());
        }

        return msg;
    }
}
