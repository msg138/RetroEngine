package com.retro.engine.Messaging;

/**
 * Created by Michael on 7/16/2016.
 */
public interface Message {

    public String getRawData();

    public String getRawSender();

    public Message getMessage();
}
