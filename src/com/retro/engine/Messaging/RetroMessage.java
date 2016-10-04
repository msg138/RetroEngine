package com.retro.engine.Messaging;

import com.retro.engine.entity.Entity;

/**
 * Created by Michael on 7/16/2016.
 */
public class RetroMessage implements Message {

    private int m_messageID;

    private String m_rawData;
    private String m_rawSender;

    private Entity m_entity;

    public RetroMessage(int messageID, String rawData, Entity e){
        this(messageID, rawData, "ENTITY");
        m_entity = e;
    }

    public RetroMessage(int messageID, String rawData, String rawSender){
        m_rawData = rawData;
        m_rawSender = rawSender;

        m_messageID = messageID;

        m_entity = null;
    }

    public int getID(){
        return m_messageID;
    }

    public int getMessageID(){
        return getID();
    }

    public String getRawSender(){
        return m_rawSender;
    }

    public String getRawData(){
        return m_rawData;
    }

    public Message getMessage(){
        return this;
    }

    public boolean isEntity(){
        return m_entity != null;
    }

    public Entity getEntity(){
        return m_entity;
    }
}
