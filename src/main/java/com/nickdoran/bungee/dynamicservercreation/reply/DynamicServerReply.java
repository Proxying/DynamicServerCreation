package com.nickdoran.bungee.dynamicservercreation.reply;

import com.nickdoran.bungee.dynamicservercreation.DynamicServer;

import java.util.ArrayList;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public class DynamicServerReply extends AbstractDSCReply {

    private ArrayList<DynamicServer> dynamicServers;

    public DynamicServerReply(ArrayList<DynamicServer> dynamicServers) {
        this.dynamicServers = dynamicServers;
    }

    public ArrayList<DynamicServer> getDynamicServers() {
        return dynamicServers;
    }
}
